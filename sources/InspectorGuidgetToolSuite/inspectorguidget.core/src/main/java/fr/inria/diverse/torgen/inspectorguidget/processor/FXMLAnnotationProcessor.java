package fr.inria.diverse.torgen.inspectorguidget.processor;

import javafx.fxml.FXML;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

//TODO see whether AbstractManualProcessor improves the performance by precising the visit.
public class FXMLAnnotationProcessor extends InspectorGuidgetProcessor<CtAnnotation<?>> {
	private final @NotNull Set<CtField<?>> fieldAnnotations;
	private final @NotNull Set<CtMethod<?>> methodAnnotations;

	public FXMLAnnotationProcessor() {
		super();
		fieldAnnotations = new HashSet<>();
		methodAnnotations = new HashSet<>();
	}

	@Override
	public void process(final @NotNull CtAnnotation<?> element) {
		final CtElement elt = element.getAnnotatedElement();

		if(elt instanceof CtField<?>) {
			fieldAnnotations.add((CtField<?>) elt);
		}else if(elt instanceof CtMethod) {
			methodAnnotations.add((CtMethod<?>) elt);
		}
	}


	@Override
	public boolean isToBeProcessed(final @NotNull CtAnnotation<?> candidate) {
		return FXML.class.getName().equals(candidate.getActualAnnotation().annotationType().getName());
	}

	public @NotNull Set<CtField<?>> getFieldAnnotations() {
		return Collections.unmodifiableSet(fieldAnnotations);
	}

	public @NotNull Set<CtMethod<?>> getMethodAnnotations() {
		return Collections.unmodifiableSet(methodAnnotations);
	}
}
