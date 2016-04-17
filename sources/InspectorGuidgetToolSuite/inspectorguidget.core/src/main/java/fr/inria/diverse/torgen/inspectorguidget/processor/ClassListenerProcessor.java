package fr.inria.diverse.torgen.inspectorguidget.processor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * This processor find listener methods in the source code
 */
public class ClassListenerProcessor extends ListenerProcessor<CtClass<?>> {
//	ListenersWrapper			wrapper;

//	/**
//	 * The @wrapper will be filled with found listener methods
//	 */
//	public ClassListenerProcessor(ListenersWrapper wrapper) {
//		this.wrapper = wrapper;
//	}


	/**
	 * Store each listener-related methods from @clazz
	 */
	@Override
	public void process(CtClass<?> clazz) {
		if(LOG.isLoggable(Level.ALL))
			LOG.log(Level.INFO, "process CtClass: " + clazz);

		final BooleanProperty isAdded = new SimpleBooleanProperty(false);

		// Case SWING
		swingListenersRef.stream().filter(ref -> clazz.isSubtypeOf(ref)).forEach(ref -> {
			isAdded.setValue(true);
			swingClassListeners.forEach(l -> l.onSwingListenerClass(clazz));
			processMethods(clazz, ref);
		});

		// Case AWT
		awtListenersRef.stream().filter(ref -> clazz.isSubtypeOf(ref)).forEach(ref -> {
			isAdded.setValue(true);
			awtClassListeners.forEach(l -> l.onAWTListenerClass(clazz));
			processMethods(clazz, ref);
		});

//		// Case SWT
//				for (CtTypeReference<?> ref : swtListenersRef) {
//					if (clazz.isSubtypeOf(ref)) {
//						isAdded.setValue(true);
//						processMethods(clazz, ref);
//					}
//				}

		// Case GENERIC
		if (!isAdded.getValue() && clazz.isSubtypeOf(eventListenerRef)) {
			processMethods(clazz, eventListenerRef);
		}
	}


//	/**
//	 * Build the result
//	 */
//	@Override
//	public void processingDone() {
//		wrapper.create(allListernerMethods, events);
//		System.out.println("done:");
//		swingClassListeners.forEach(System.out::println);
//		awtClassListeners.forEach(System.out::println);
//	}

	@Override
	public boolean isToBeProcessed(final CtClass<?> candidate) {
		return isListenerCass(candidate);
	}

	/**
	 * Store each method from cl that implements interf
	 */
	private void processMethods(final CtClass<?> cl, final CtTypeReference<?> interf) {
		for(final Method interfMeth : interf.getActualClass().getMethods()) {
			cl.getMethodsByName(interfMeth.getName()).forEach(method -> {
//				if(!Helper.identityContains(method, allListernerMethods)) { // TODO:
//					//																				// find
//					//																				// an
//					//																				// alternative
//					//					allListernerMethods.add(method);
//					//				}
//					registerEvent(method);
//				}
			});
		}
	}

	private void registerEvent(final CtMethod<?> met) {
		met.getParameters().stream().map(par -> par.getType()).filter(type -> type.isSubtypeOf(eventRef)).forEach(type -> events.add(type));
	}
}