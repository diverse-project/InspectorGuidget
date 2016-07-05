package fr.inria.diverse.torgen.inspectorguidget.processor;

import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonHelper;
import fr.inria.diverse.torgen.inspectorguidget.helper.WidgetHelper;
import fr.inria.diverse.torgen.inspectorguidget.listener.WidgetListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
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
	/** The widgets defined as fields of classes. */
	private final @NotNull Map<CtVariable<?>, List<CtVariableAccess<?>>> fields;
	/** The widgets created and directly added in a container. */
	private final @NotNull Map<CtTypeReference<?>, CtTypeReference<?>> references;
	/** Redefined widgets (e.g. reused local vars). */
	private final @NotNull Map<CtAssignment<?,?>, List<CtVariableAccess<?>>> varsReassigned;

	private Collection<CtTypeReference<?>> controlType;
	private CtTypeReference<?> collectionType;
	private final @NotNull Set<WidgetListener> widgetObs;
	private final boolean withConfigStat;
	/** A cache used to optimise the type references to analyse. */
	private final Map<String, Boolean> cacheTypeChecked;

	public WidgetProcessor() {
		this(false);
	}

	public WidgetProcessor(final boolean withConfigurationStatmts) {
		super();
		widgetObs = new HashSet<>();
		fields = new IdentityHashMap<>();
		references = new IdentityHashMap<>();
		withConfigStat = withConfigurationStatmts;
		cacheTypeChecked = new HashMap<>();
		varsReassigned = new HashMap<>();
	}

	public void addWidgetObserver(final @NotNull WidgetListener obs) {
		widgetObs.add(obs);
	}

	@Override
	public void init() {
		LOG.log(Level.INFO, "init processor " + getClass().getSimpleName());

		collectionType = getFactory().Type().createReference(Collection.class);
		controlType = WidgetHelper.INSTANCE.getWidgetTypes(getFactory());
	}

	@Override
	public void processingDone() {
		super.processingDone();
		cacheTypeChecked.clear();
	}

	@Override
	public boolean isToBeProcessed(CtTypeReference<?> type) {
		String ty = type.getQualifiedName();

		if(cacheTypeChecked.containsKey(ty)) {
			return cacheTypeChecked.get(ty);
		}else {
			boolean ok = isASubTypeOf(type, controlType);
			cacheTypeChecked.put(ty, ok);
			return ok;
		}
	}

	@Override
	public void process(final @NotNull CtTypeReference<?> element) {
		final CtElement parent = element.getParent();

		LOG.log(Level.INFO, () -> "PROCESSING " + element + " " + parent.getClass());

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
			CtField<?> decl = ((CtFieldReference<?>) parent).getDeclaration();

			if(decl!=null && WidgetHelper.INSTANCE.isTypeRefAWidget(decl.getType())) {
				addNotifyObserversOnField(decl, element);
			}
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
		if(parent instanceof CtTypeAccess<?>) {
			// A static method / attribute is used on a widget class.
			return;
		}
		if(parent instanceof CtClass<?>) {
			// A widget class is used.
			return;
		}

		if(parent instanceof CtLocalVariable<?> || parent instanceof CtLocalVariableReference<?> || parent instanceof CtVariableRead<?>) {
			// A widget var assigned to a local var; or simply used.
			return;
		}

		if(parent instanceof CtThisAccess<?>) {
			// Use of this on a widget object.
			return;
		}

		LOG.log(Level.WARNING, "CTypeReference parent not supported or ignored: " + parent.getClass() + " " + parent);
	}


	private void analyseMethodUse(final @NotNull CtMethod<?> meth, final CtTypeReference<?> element) {
		final ModifierKind visib = meth.getVisibility();
		if(visib == ModifierKind.PRIVATE) {
			meth.getParent(CtClass.class).getElements(new InvocationFilter(meth)).forEach(invok -> analyseWidgetInvocation(invok, element));
		}else if(visib == ModifierKind.PUBLIC) {
			meth.getFactory().Package().getRootPackage().getElements(new InvocationFilter(meth)).forEach(invok -> analyseWidgetInvocation(invok, element));
		}else if(visib == null || visib == ModifierKind.PROTECTED) {
			meth.getParent(CtPackage.class).getElements(new InvocationFilter(meth)).forEach(invok -> analyseWidgetInvocation(invok, element));
		}
	}


	private void analyseWidgetConstructorCall(final @NotNull CtConstructorCall<?> call, final CtTypeReference<?> element) {
		if(call.isParentInitialized()) {
			final CtElement parent = call.getParent();

			// When the creation of the widget is stored in a new local var, this var is considered as a widget.
			if(parent instanceof CtLocalVariable<?>) {
				addNotifyObserversOnField((CtLocalVariable<?>)parent, element);
			}
			// When the creation of the widget is stored in an already defined local var...
			else if(parent instanceof CtAssignment<?,?>) {
				CtAssignment<?, ?> assig = (CtAssignment<?, ?>) parent;
				// if the var is in fact a field and this last has been already added, then the reassignment is considered...
				if(assig.getAssigned() instanceof CtFieldWrite<?> && fields.containsKey(((CtFieldWrite<?>)assig.getAssigned()).getVariable().getDeclaration())) {
					addNotifyObserversOnReassignedVar(assig, element);
				}else {
				// otherwise the assignment is treated as it.
					analyseWidgetAssignment(assig, element);
				}
			} else {
				analyseWidgetUse(parent, element);
			}
		}
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


	private void analyseUseOfLocalVariable(final @NotNull CtLocalVariableReference<?> var, final @Nullable CtBlock<?> block, final CtTypeReference<?> refType) {
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
	private void analyseWidgetAssignment(final @NotNull CtAssignment<?,?> assign, final CtTypeReference<?> element) {
		final CtExpression<?> exp = assign.getAssigned();

		if(exp instanceof CtFieldWrite<?>) {
			addNotifyObserversOnField(((CtFieldWrite<?>) exp).getVariable().getDeclaration(), element);
		}
		else if(exp instanceof CtVariableWrite<?>) {
			addNotifyObserversOnField(((CtVariableWrite<?>)exp).getVariable().getDeclaration(), element);
		}
		else {
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
	private void analyseWidgetInvocation(final @NotNull CtInvocation<?> invok, final CtTypeReference<?> element) {
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


	private void addNotifyObserversOnContained(final CtInvocation<?> invok, final @Nullable CtTypeReference<?> element) {
		if(element!=null && references.putIfAbsent(element, element)==null) {
			widgetObs.forEach(o -> o.onWidgetCreatedForContainer(invok, element));
		}
	}

	private void addNotifyObserversOnField(final @Nullable CtVariable<?> field, final CtTypeReference<?> element) {
		if(field!=null && !fields.containsKey(field)) {
			final List<CtVariableAccess<?>> usages = extractUsagesOfWidgetField(field);
			fields.put(field, usages);
			widgetObs.forEach(o -> o.onWidgetAttribute(field, usages, element));
		}
	}

	private void addNotifyObserversOnReassignedVar(final @Nullable CtAssignment<?,?> assig, final CtTypeReference<?> element) {
		if(assig!=null && !varsReassigned.containsKey(assig)) {
			final List<CtVariableAccess<?>> usages = Collections.emptyList();// extractUsagesOfWidgetField(field);
			varsReassigned.put(assig, usages);
			widgetObs.forEach(o -> o.onWidgetCreatedInExistingVar(assig, usages, element));
		}
	}

	private List<CtVariableAccess<?>> extractUsagesOfWidgetField(final CtVariable<?> field) {
		if(withConfigStat) {
			return SpoonHelper.INSTANCE.extractUsagesOfField(field);
		}
		return Collections.emptyList();
	}

	public @NotNull Map<CtVariable<?>, List<CtVariableAccess<?>>> getFields() {
		return Collections.unmodifiableMap(fields);
	}
}
