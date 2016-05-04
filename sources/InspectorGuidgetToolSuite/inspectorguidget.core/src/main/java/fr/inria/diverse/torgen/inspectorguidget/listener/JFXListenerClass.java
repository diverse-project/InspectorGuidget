package fr.inria.diverse.torgen.inspectorguidget.listener;

import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

public interface JFXListenerClass {
    void onJFXListenerClass(CtClass<?> clazz);

	void onJFXListenerLambda(CtLambda<?> lambda);

	void onJFXFXMLAnnotationOnField(CtField<?> field);

	void onJFXFXMLAnnotationOnMethod(CtMethod<?> method);
}
