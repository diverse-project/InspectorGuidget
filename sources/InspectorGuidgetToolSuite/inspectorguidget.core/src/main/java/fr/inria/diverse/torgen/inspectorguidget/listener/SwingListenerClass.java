package fr.inria.diverse.torgen.inspectorguidget.listener;

import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

import java.util.List;

public interface SwingListenerClass {
	/**
	 * Notified method when a swing listener class is analysed.
	 * @param clazz The swing listener class.
	 * @param methods All the methods of clazz that come from implemented listeners.
	 */
    void onSwingListenerClass(CtClass<?> clazz, List<CtMethod<?>> methods);

	void onSwingListenerLambda(CtLambda<?> lambda);
}
