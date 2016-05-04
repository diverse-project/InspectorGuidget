package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.listener.JFXListenerClass;
import fr.inria.diverse.torgen.inspectorguidget.processor.FXMLAnnotationProcessor;
import org.junit.Before;
import org.junit.Test;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TestFXMLAnnotationProcessor extends TestInspectorGuidget<FXMLAnnotationProcessor> implements JFXListenerClass {
	private Set<CtField<?>> fieldsFXML;
	private Set<CtMethod<?>> methodsFXML;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		fieldsFXML = new HashSet<>();
		methodsFXML = new HashSet<>();
		processors.forEach(p -> p.addJFXClassListener(this));
	}

	@Override
	public List<FXMLAnnotationProcessor> createProcessor() {
		return Collections.singletonList(new FXMLAnnotationProcessor());
	}

	@Test
	public void testFXMLAnnotationAttributes() {
		run("src/test/resources/java/fxml/FXMLAnnotationAttributes.java");
		assertEquals(2, fieldsFXML.size());
		assertEquals(0, methodsFXML.size());
	}

	@Override
	public void onJFXListenerClass(final CtClass<?> clazz, Set<CtMethod<?>> methods) {
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
