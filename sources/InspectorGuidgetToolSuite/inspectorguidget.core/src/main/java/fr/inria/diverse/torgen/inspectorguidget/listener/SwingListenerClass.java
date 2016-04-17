package fr.inria.diverse.torgen.inspectorguidget.listener;

import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtClass;

public interface SwingListenerClass {
    void onSwingListenerClass(CtClass<?> clazz);

	void onSwingListenerLambda(CtLambda<?> lambda);
}
