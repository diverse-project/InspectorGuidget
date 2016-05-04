package fr.inria.diverse.torgen.inspectorguidget.processor;

import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonHelper;
import fr.inria.diverse.torgen.inspectorguidget.listener.WidgetListener;
import org.eclipse.jdt.annotation.NonNull;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.InvocationFilter;
import spoon.reflect.visitor.filter.VariableAccessFilter;

import java.util.*;
import java.util.logging.Level;

/**
 * Detects declaration of widgets.
 */
public class WidgetProcessor extends InspectorGuidgetProcessor<CtTypeReference<?>> {
	private Collection<CtTypeReference<?>> controlType;
	private Map<CtField<?>, CtField<?>> fields;
	/** The widgets created and directly added in a container. */
	private Map<CtTypeReference<?>, CtTypeReference<?>> references;
	private CtTypeReference<?> collectionType;
	private final Set<WidgetListener> widgetObs;

	public WidgetProcessor() {
		super();
		widgetObs = new HashSet<>();
	}

	public void addWidgetObserver(final @NonNull WidgetListener obs) {
		widgetObs.add(obs);
	}

	@Override
	public void init() {
		LOG.log(Level.INFO, "init processor " + getClass().getSimpleName());

		fields = new IdentityHashMap<>();
		references = new IdentityHashMap<>();
		controlType = new ArrayList<>();
		collectionType = getFactory().Type().createReference(Collection.class);

		controlType.add(getFactory().Type().createReference(javafx.scene.Node.class));
		controlType.add(getFactory().Type().createReference(javafx.scene.control.MenuItem.class));
		controlType.add(getFactory().Type().createReference(javafx.scene.control.Dialog.class));
		controlType.add(getFactory().Type().createReference(javafx.stage.Window.class));
		controlType.add(getFactory().Type().createReference(java.awt.Component.class));
	}

	@Override
	public boolean isToBeProcessed(CtTypeReference<?> type) {
		return isASubTypeOf(type, controlType);
	}

	@Override
	public void process(final CtTypeReference<?> element) {
		final CtElement parent = element.getParent();

		if(LOG.isLoggable(Level.INFO))
			LOG.log(Level.INFO, "PROCESSING " + element + " " + parent.getClass() + " " + parent.getParent().getClass());

		if(parent instanceof CtField<?>) {
			addNotifyObserversOnField((CtField<?>) parent, element);
			return;
		}
		if(parent instanceof CtExecutableReference<?> && parent.getParent() instanceof CtConstructorCall<?>) {
			analyseWidgetConstructorCall((CtConstructorCall<?>) parent.getParent(), element);
			return;
		}
		if(parent instanceof CtAssignment<?,?>) {
			analyseWidgetAssignment((CtAssignment<?, ?>) parent, element);
			return;
		}
		if(parent instanceof CtFieldReference<?>) {
			addNotifyObserversOnField(((CtFieldReference<?>) parent).getDeclaration(), element);
			return;
		}
		if(parent instanceof CtMethod<?>) {
			analyseMethodUse((CtMethod<?>) parent, element);
			return;
		}
		if(parent instanceof CtTypeReference<?>) {
			process((CtTypeReference<?>) parent);
			return;
		}
		if(parent instanceof CtExecutableReference<?>) {
			// A method is called on a widget, so ignored.
			return;
		}

		LOG.log(Level.WARNING, "CTypeReference parent not supported or ignored: " + parent.getClass() + " " + parent);
	}


	private void analyseMethodUse(final CtMethod<?> meth, final CtTypeReference<?> element) {
		meth.getFactory().Package().getRootPackage().getElements(new InvocationFilter(meth)).
				forEach(invok -> analyseWidgetInvocation(invok, element));
	}


	private void analyseWidgetConstructorCall(final CtConstructorCall<?> call, final CtTypeReference<?> element) {
		analyseWidgetUse(call.getParent(), element);
	}


	private void analyseWidgetUse(final CtElement elt, final CtTypeReference<?> refType) {
		if(elt instanceof CtAssignment<?, ?>) {
			analyseWidgetAssignment((CtAssignment<?, ?>) elt, refType);
			return;
		}
		if(elt instanceof CtInvocation<?>) {
			analyseWidgetInvocation((CtInvocation<?>) elt, refType);
			return;
		}

		if(elt instanceof CtLocalVariable<?>) {
			analyseUseOfLocalVariable(((CtLocalVariable<?>)elt).getReference(), elt.getParent(CtBlock.class), refType);
			return;
		}

		LOG.log(Level.WARNING, "Widget use not supported or ignored (" + SpoonHelper.INSTANCE.formatPosition(elt.getPosition()) +
				"): " + elt.getClass());
	}


	private void analyseUseOfLocalVariable(final CtLocalVariableReference<?> var, final CtBlock<?> block, final CtTypeReference<?> refType) {
		if(block==null) {
			LOG.log(Level.SEVERE, "No block ("+SpoonHelper.INSTANCE.formatPosition(var.getPosition())+"): " + var);
			return;
		}

		block.getElements(new VariableAccessFilter<>(var)).forEach(access -> analyseWidgetUse(access.getParent(), refType));
	}


	/**
	 * Object foo;
	 * foo = new JButton();
	 */
	private void analyseWidgetAssignment(final CtAssignment<?,?> assign, final CtTypeReference<?> element) {
		final CtExpression<?> exp = assign.getAssigned();

		if(exp instanceof CtFieldWrite<?>) {
			addNotifyObserversOnField(((CtFieldWrite<?>) exp).getVariable().getDeclaration(), element);
		}else {
			LOG.log(Level.WARNING, "Widget Assignment not supported or ignored: " + exp.getClass() + " " + exp);
		}
	}


	/**
	 * JPanel panel = new JPanel();
	 * panel.add(new JWindow());
	 * ****
	 * List<Object> foo = new ArrayList<>();
	 * foo.add(new JMenuItem());
	 */
	private void analyseWidgetInvocation(final CtInvocation<?> invok, final CtTypeReference<?> element) {
		final CtExpression<?> exp = invok.getTarget();

		if(exp==null) {
			LOG.log(Level.WARNING, "Cannot treat the widget invocation because of a null type: " + invok);
			return;
		}

		final CtTypeReference<?> type = exp.getType();

		if(type.isSubtypeOf(collectionType) && type.getParent() instanceof CtFieldReference<?>) {
			addNotifyObserversOnField(((CtFieldReference<?>) type.getParent()).getDeclaration(), element);
			return;
		}

		if(isASubTypeOf(type, controlType)) {
			addNotifyObserversOnContained(invok, element);
			return;
		}
		if(invok.getParent() instanceof CtAssignment<?,?>) {
			analyseWidgetAssignment((CtAssignment<?, ?>) invok.getParent(), element);
			return;
		}
		if(invok.getParent() instanceof CtInvocation<?> && invok.getParent() instanceof CtInvocation<?>) {
			// Calling a method on a collection.
			addNotifyObserversOnContained(invok, element);
			return;
		}

		LOG.log(Level.WARNING, "Widget invocation not supported or ignored: " + type.getSimpleName() + " " + invok);
	}


	private void addNotifyObserversOnContained(final CtInvocation<?> invok, final CtTypeReference<?> element) {
		if(element!=null && references.putIfAbsent(element, element)==null)
			widgetObs.forEach(o -> o.onWidgetCreatedForContainer(invok, element));
	}

	private void addNotifyObserversOnField(final CtField<?> field, final CtTypeReference<?> element) {
		if(field!=null && fields.putIfAbsent(field, field)==null)
			widgetObs.forEach(o -> o.onWidgetAttribute(field, element));
	}
}
