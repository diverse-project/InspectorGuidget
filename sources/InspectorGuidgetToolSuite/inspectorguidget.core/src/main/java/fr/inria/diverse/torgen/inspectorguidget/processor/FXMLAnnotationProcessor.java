package fr.inria.diverse.torgen.inspectorguidget.processor;

import javafx.fxml.FXML;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

//TODO see whether AbstractManualProcessor improves the performance by precising the visit.
public class FXMLAnnotationProcessor extends InspectorGuidgetProcessor<CtAnnotation<?>> {
	public FXMLAnnotationProcessor() {
		super();
	}

	@Override
	public void process(final @NotNull CtAnnotation<?> element) {
		CtElement elt = element.getAnnotatedElement();

		if(elt instanceof CtField<?>) {
			CtField<?> field =(CtField<?>) elt;
			jfxClassObservers.forEach(o -> o.onJFXFXMLAnnotationOnField(field));
		} else if(elt instanceof CtMethod) {
			CtMethod<?> method =(CtMethod<?>) elt;
			jfxClassObservers.forEach(o -> o.onJFXFXMLAnnotationOnMethod(method));
		}
	}


	@Override
	public boolean isToBeProcessed(final @NotNull CtAnnotation<?> candidate) {
		return FXML.class.getName().equals(candidate.getActualAnnotation().annotationType().getName());
	}
}
