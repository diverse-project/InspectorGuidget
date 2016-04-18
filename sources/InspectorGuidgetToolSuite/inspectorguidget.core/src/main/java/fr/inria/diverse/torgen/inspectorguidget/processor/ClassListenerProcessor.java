package fr.inria.diverse.torgen.inspectorguidget.processor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
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
	protected final Map<CtMethod<?>,CtMethod<?>> allListenerMethods;


	public ClassListenerProcessor() {
		super();
		allListenerMethods= new IdentityHashMap<>();
	}

	public Set<CtMethod<?>> getAllListenerMethods() {
		return allListenerMethods.keySet();
	}

	/**
	 * Store each listener-related methods from @clazz
	 */
	@Override
	public void process(CtClass<?> clazz) {
		if(LOG.isLoggable(Level.ALL))
			LOG.log(Level.INFO, "process CtClass: " + clazz);

		final BooleanProperty isAdded = new SimpleBooleanProperty(false);

		// Case SWING
		swingListenersRef.stream().filter(clazz::isSubtypeOf).forEach(ref -> {
			isAdded.setValue(true);
			swingClassListeners.forEach(l -> l.onSwingListenerClass(clazz));
			processMethods(clazz, ref);
		});

		// Case AWT
		awtListenersRef.stream().filter(clazz::isSubtypeOf).forEach(ref -> {
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
//		wrapper.create(allListernerLambdas, events);
//		System.out.println("done:");
//		swingClassListeners.forEach(System.out::println);
//		awtClassListeners.forEach(System.out::println);
//	}

	@Override
	public boolean isToBeProcessed(final CtClass<?> candidate) {
		return isListenerCass(candidate);
	}


	private CtTypeReference<?>[] getTypeRefFromClasses(Class<?>[] classes) {
		final ClassFactory facto = getFactory().Class();
		return Arrays.stream(classes).map(facto::createReference).toArray(CtTypeReference<?>[]::new);
	}


	/**
	 * Store each method from cl that implements interf
	 */
	private void processMethods(final CtClass<?> cl, final CtTypeReference<?> interf) {
		for(final Method interfMeth : interf.getActualClass().getMethods()) {
			final CtMethod<?> m = cl.getMethod(interfMeth.getName(), getTypeRefFromClasses(interfMeth.getParameterTypes()));

			if(m==null) {
				LOG.log(Level.SEVERE, "Cannot find the implemented method " + interfMeth + " from the interface: " + interf);
			}else {
				if(!allListenerMethods.containsKey(m)) {
					allListenerMethods.put(m, m);
				//  registerEvent(method);
				}
			}
		}
	}

	private void registerEvent(final CtMethod<?> met) {
		met.getParameters().stream().map(CtTypedElement::getType).filter(type -> type.isSubtypeOf(eventRef)).forEach(events::add);
	}
}