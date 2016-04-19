package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.listener.JFXListenerClass;
import fr.inria.diverse.torgen.inspectorguidget.processor.ClassListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.FXMLAnnotationProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.LambdaListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.ListenerProcessor;
import org.junit.Before;
import org.junit.Test;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TestFXMLAnnotationProcessor extends TestInspectorGuidget<ListenerProcessor<? extends CtElement>> implements JFXListenerClass {
	private Set<CtField<?>> fieldsFXML;
	private Set<CtMethod<?>> methodsFXML;
	private LambdaListenerProcessor lambdaProc;
	private ClassListenerProcessor classProc;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		fieldsFXML = new HashSet<>();
		methodsFXML = new HashSet<>();
		processors.forEach(p -> p.addJFXClassListener(this));
	}

	@Override
	public List<ListenerProcessor<? extends CtElement>> createProcessor() {
		return Collections.singletonList(new FXMLAnnotationProcessor());
	}

	@Test
	public void testFXMLAnnotationAttributes() {
		run("src/test/resources/java/fxml/FXMLAnnotationAttributes.java");
		assertEquals(2, fieldsFXML.size());
		assertEquals(0, methodsFXML.size());
	}

	@Override
	public void onJFXListenerClass(final CtClass<?> clazz) {
	}

	@Override
	public void onJFXListenerLambda(final CtLambda<?> lambda) {
	}

	@Override
	public void onJFXFXMLAnnotationOnField(final CtField<?> field) {
		fieldsFXML.add(field);
	}

	@Override
	public void onJFXFXMLAnnotationOnMethod(final CtMethod<?> method) {
		methodsFXML.add(method);
	}
}
