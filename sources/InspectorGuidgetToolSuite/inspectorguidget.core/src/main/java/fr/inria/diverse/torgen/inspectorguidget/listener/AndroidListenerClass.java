package fr.inria.diverse.torgen.inspectorguidget.listener;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

import java.util.Set;

public interface AndroidListenerClass {
	/**
	 * Notified method when an Android listener class is analysed.
	 * @param clazz The Android listener class.
	 * @param methods All the methods of clazz that come from implemented listeners.
	 */
	void onAndroidListenerClass(CtClass<?> clazz, Set<CtMethod<?>> methods);
}
