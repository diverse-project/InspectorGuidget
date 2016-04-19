package fr.inria.diverse.torgen.inspectorguidget.processor;

import fr.inria.diverse.torgen.inspectorguidget.listener.JFXListenerClass;
import javafx.fxml.FXML;
import org.eclipse.jdt.annotation.NonNull;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

import java.util.HashSet;
import java.util.Set;

public class FXMLAnnotationProcessor extends ListenerProcessor<CtAnnotation<?>>{
	protected final Set<JFXListenerClass> jfxClassObservers;

	public FXMLAnnotationProcessor() {
		super();
		jfxClassObservers= new HashSet<>();
	}

	@Override
	public void process(final CtAnnotation<?> element) {
		CtElement elt = element.getAnnotatedElement();

		if(elt instanceof CtField) {
			CtField field =(CtField) elt;
			jfxClassObservers.forEach(o -> o.onJFXFXMLAnnotationOnField(field));
		} else if(elt instanceof CtMethod) {
			CtMethod method =(CtMethod) elt;
			jfxClassObservers.forEach(o -> o.onJFXFXMLAnnotationOnMethod(method));
		}
	}


	@Override
	public boolean isToBeProcessed(final CtAnnotation<?> candidate) {
		return FXML.class.getName().equals(candidate.getActualAnnotation().annotationType().getName());
	}

	public void addJFXClassListener(final @NonNull JFXListenerClass lis) {
		jfxClassObservers.add(lis);
	}
}
