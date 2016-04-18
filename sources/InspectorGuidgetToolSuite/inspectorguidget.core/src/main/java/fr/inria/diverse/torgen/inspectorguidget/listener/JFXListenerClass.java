package fr.inria.diverse.torgen.inspectorguidget.listener;

import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtClass;

public interface JFXListenerClass {
    void onJFXListenerClass(CtClass<?> clazz);

	void onJFXListenerLambda(CtLambda<?> lambda);
}
