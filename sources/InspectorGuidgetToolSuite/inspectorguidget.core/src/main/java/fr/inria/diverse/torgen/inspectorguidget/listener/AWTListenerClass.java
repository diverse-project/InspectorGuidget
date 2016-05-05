package fr.inria.diverse.torgen.inspectorguidget.listener;

import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

import java.util.List;

public interface AWTListenerClass {
	/**
	 * Notified method when an AWT listener class is analysed.
	 * @param clazz The AWT listener class.
	 * @param methods All the methods of clazz that come from implemented listeners.
	 */
	void onAWTListenerClass(CtClass<?> clazz, List<CtMethod<?>> methods);

	void onAWTListenerLambda(CtLambda<?> lambda);
}
