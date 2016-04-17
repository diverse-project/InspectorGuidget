package fr.inria.diverse.torgen.inspectorguidget.listener;

import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtClass;

public interface AWTListenerClass {
    void onAWTListenerClass(CtClass<?> clazz);

	void onAWTListenerLambda(CtLambda<?> lambda);
}
