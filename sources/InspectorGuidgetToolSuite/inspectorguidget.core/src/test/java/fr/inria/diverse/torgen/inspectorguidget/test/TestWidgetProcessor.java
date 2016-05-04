package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.listener.WidgetListener;
import fr.inria.diverse.torgen.inspectorguidget.processor.WidgetProcessor;
import org.junit.Before;
import org.junit.Test;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtTypeReference;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TestWidgetProcessor extends TestInspectorGuidget<WidgetProcessor> implements WidgetListener {
	private Set<CtField<?>> widgetAttrs;
	private Set<CtTypeReference<?>> widgetAddedToContainer;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		widgetAttrs= new HashSet<>();
		widgetAddedToContainer= new HashSet<>();
		processors.forEach(p -> p.addWidgetObserver(this));
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

	@Test
	public void testWidgetsConstructorObject() {
		run("src/test/resources/java/widgets/WidgetConstructorObject.java");
		assertEquals(1, widgetAttrs.size());
	}

	@Test
	public void testWidgetsConstructorListObject() {
		run("src/test/resources/java/widgets/WidgetConstructorListObject.java");
		assertEquals(1, widgetAttrs.size());
	}

	@Test
	public void testWidgetsConstructorContainer() {
		run("src/test/resources/java/widgets/WidgetConstructorContainer.java");
		assertEquals(1, widgetAttrs.size());
		assertEquals(1, widgetAddedToContainer.size());
	}

	@Test
	public void testWidgetsConstructorObjectUndirect() {
		run("src/test/resources/java/widgets/WidgetConstructorObjectUndirect.java");
		assertEquals(1, widgetAttrs.size());
	}

	@Override
	public void onWidgetAttribute(final CtField<?> widget, final CtTypeReference<?> element) {
		widgetAttrs.add(widget);
	}

	@Override
	public void onWidgetCreatedForContainer(CtInvocation<?> widgetInvoc, CtTypeReference<?> element) {
		widgetAddedToContainer.add(element);
	}
}
