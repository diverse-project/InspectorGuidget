package fr.inria.diverse.torgen.inspectorguidget.processor;

import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.Method;
import java.util.logging.Level;

public class LambdaListenerProcessor extends ListenerProcessor<CtLambda<?>>  {
	@Override
	public void process(final CtLambda<?> lambda) {
		if(LOG.isLoggable(Level.ALL))
			LOG.log(Level.INFO, "process CtLambda: " + lambda);

		boolean isAdded = false;
		final CtTypeInformation type = lambda.getType();

		// Case SWING
		for (CtTypeReference<?> ref : swingListenersRef) {
			if (type.isSubtypeOf(ref)) {
				isAdded = true;
				swingClassListeners.forEach(l -> l.onSwingListenerLambda(lambda));
				processMethods(lambda, ref);
			}
		}

		// Case AWT
		for (CtTypeReference<?> ref : awtListenersRef) {
			if (type.isSubtypeOf(ref)) {
				isAdded = true;
				awtClassListeners.forEach(l -> l.onAWTListenerLambda(lambda));
				processMethods(lambda, ref);
			}
		}

		//		// Case SWT
		//				for (CtTypeReference<?> ref : swtListenersRef) {
		//					if (clazz.isSubtypeOf(ref)) {
		//						isAdded = true;
		//						processMethods(clazz, ref);
		//					}
		//				}

		// Case GENERIC
		if (!isAdded) {
			if (type.isSubtypeOf(eventListenerRef)) {
				processMethods(lambda, eventListenerRef);
			}
		}
	}

	private void processMethods(final CtLambda<?> cl, final CtTypeReference<?> interf) {
		for(final Method m : interf.getActualClass().getMethods()) {
//			List<CtMethod<?>> methods = cl.getMethodsByName(m.getName());//FIXME
//			for (CtMethod<?> method : methods) {
//				//				if (!Helper.identityContains(method, allListernerMethods)) { // TODO:
//				//																				// find
//				//																				// an
//				//																				// alternative
//				//					allListernerMethods.add(method);
//				//				}
//				registerEvent(method);
//			}
		}
	}

	@Override
	public boolean isToBeProcessed(final CtLambda<?> candidate) {
		return isListenerCass(candidate.getType());
	}
}
