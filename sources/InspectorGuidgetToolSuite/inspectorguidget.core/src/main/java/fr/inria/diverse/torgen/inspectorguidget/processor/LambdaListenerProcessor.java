package fr.inria.diverse.torgen.inspectorguidget.processor;

import fr.inria.diverse.torgen.inspectorguidget.helper.WidgetHelper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtLambda;
import spoon.reflect.reference.CtTypeReference;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class LambdaListenerProcessor extends InspectorGuidgetProcessor<CtLambda<?>>  {
	protected final @NotNull Set<CtLambda<?>> allListenerLambdas;

	public LambdaListenerProcessor() {
		super();
		allListenerLambdas = new HashSet<>();
	}

	public @NotNull Set<CtLambda<?>> getAllListenerLambdas() {
		return Collections.unmodifiableSet(allListenerLambdas);
	}

	@Override
	public void process(final @NotNull CtLambda<?> lambda) {
		LOG.log(Level.INFO, "process CtLambda: " + lambda);

		final BooleanProperty isAdded = new SimpleBooleanProperty(false);
		final CtTypeReference<?> type = lambda.getType();

		// Case SWING
		WidgetHelper.INSTANCE.getSwingListenersRef(getFactory()).stream().filter(type::isSubtypeOf).forEach(ref -> {
			isAdded.setValue(true);
			processMethods(lambda);
		});

		// Case AWT
		WidgetHelper.INSTANCE.getAWTListenersRef(getFactory()).stream().filter(type::isSubtypeOf).forEach(ref -> {
			isAdded.setValue(true);
			processMethods(lambda);
		});

		// Case JFX
		WidgetHelper.INSTANCE.getJFXListenersRef(getFactory()).stream().filter(type::isSubtypeOf).forEach(ref -> {
			isAdded.setValue(true);
			processMethods(lambda);
		});

		//		// Case SWT
		//				for (CtTypeReference<?> ref : swtListenersRef) {
		//					if (clazz.isSubtypeOf(ref)) {
		//						isAdded = true;
		//						processMethods(clazz, ref);
		//					}
		//				}

		// Case GENERIC
		if(!isAdded.getValue() && WidgetHelper.INSTANCE.isListenerClass(type, getFactory())) {
			processMethods(lambda);
		}
	}


	private void processMethods(final CtLambda<?> lambda) {
		allListenerLambdas.add(lambda);

//		final List<CtMethod<?>> ms = interf.
//				getDeclaration().getMethods().stream().
//				filter(m -> !m.isDefaultMethod() &&	!m.hasModifier(ModifierKind.STATIC)).collect(Collectors.toList());
//
//		if(ms.size()>1) {
//			LOG.log(Level.SEVERE, "More than one abstract method found in a lambda functional interface: " + ms);
//		}
//
//		if(ms.isEmpty()) {
//			LOG.log(Level.SEVERE, "No abstract method found in a lambda functional interface: " + lambda);
//			throw new ArrayIndexOutOfBoundsException("No abstract method found in a lambda functional interface: " + lambda);
//		}
//
//		System.out.println("got the lambda method: " + ms.get(0));
	}

	@Override
	public boolean isToBeProcessed(final @NotNull CtLambda<?> candidate) {
		return WidgetHelper.INSTANCE.isListenerClass(candidate.getType(), getFactory());
	}
}
