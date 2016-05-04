package fr.inria.diverse.torgen.inspectorguidget.listener;

import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

import java.util.Set;

public interface JFXListenerClass {
	/**
	 * Notified method when a JFX listener class is analysed.
	 * @param clazz The JFX listener class.
	 * @param methods All the methods of clazz that come from implemented listeners.
	 */
	void onJFXListenerClass(CtClass<?> clazz, Set<CtMethod<?>> methods);

	void onJFXListenerLambda(CtLambda<?> lambda);

	void onJFXFXMLAnnotationOnField(CtField<?> field);

	void onJFXFXMLAnnotationOnMethod(CtMethod<?> method);
}
