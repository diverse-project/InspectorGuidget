package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.listener.JFXListenerClass;
import fr.inria.diverse.torgen.inspectorguidget.processor.WidgetProcessor;
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

public class TestWidgetProcessor extends TestInspectorGuidget<WidgetProcessor> implements JFXListenerClass {
	private Set<CtField<?>> widgetAttrs;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		widgetAttrs= new HashSet<>();
		processors.forEach(p -> p.addJFXClassListener(this));
	}

	@Override
	public List<WidgetProcessor> createProcessor() {
		return Collections.singletonList(new WidgetProcessor());
	}

	@Test
	public void testWidgetsAsExplicitAttr() {
		run("src/test/resources/java/widgets/WidgetAsStdAttr.java");
		assertEquals(3, widgetAttrs.size());
	}

	@Test
	public void testWidgetsAsListAttr() {
		run("src/test/resources/java/widgets/WidgetAsListAttr.java");
		assertEquals(1, widgetAttrs.size());
	}

	@Override
	public void onJFXListenerClass(final CtClass<?> clazz) {
	}

	@Override
	public void onJFXListenerLambda(final CtLambda<?> lambda) {
	}

	@Override
	public void onJFXFXMLAnnotationOnField(final CtField<?> field) {
	}

	@Override
	public void onJFXFXMLAnnotationOnMethod(final CtMethod<?> method) {
	}

	@Override
	public void onJFXWidgetAttribute(final CtField<?> widget) {
		widgetAttrs.add(widget);
	}
}
