package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.listener.WidgetListener;
import fr.inria.diverse.torgen.inspectorguidget.processor.WidgetProcessor;
import org.junit.Before;
import org.junit.Test;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtTypeReference;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class TestWidgetProcessor extends TestInspectorGuidget<WidgetProcessor> implements WidgetListener {
	private Map<CtVariable<?>, List<CtVariableAccess<?>>> widgetAttrs;
	private Map<CtAssignment<?,?>, List<CtVariableAccess<?>>> widgetReassigned;
	private Set<CtTypeReference<?>> widgetAddedToContainer;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		widgetAttrs = new HashMap<>();
		widgetAddedToContainer = new HashSet<>();
		widgetReassigned = new HashMap<>();
		processors.forEach(p -> p.addWidgetObserver(this));
	}


	@Override
	public List<WidgetProcessor> createProcessor() {
		return Collections.singletonList(new WidgetProcessor(true));
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

	@Test
	public void testWidgetAsClass() {
		run("src/test/resources/java/widgets/WidgetAsClass.java");
		assertEquals(1, widgetAttrs.size());
	}


	@Test
	public void testWidgetsConstructorObjectFunction() {
		run("src/test/resources/java/widgets/WidgetConstructorObjectFunction.java",
				"src/test/resources/java/widgets/WidgetConstructorObjectFunction2.java");
		assertEquals(4, widgetAttrs.size());
	}

	@Test
	public void testWidgetsConstructorObjectFunction3() {
		run("src/test/resources/java/widgets/WidgetConstructorObjectFunction3.java");
		assertEquals(1, widgetAttrs.size());
		assertEquals(1, widgetAddedToContainer.size());
	}

	@Test
	public void testWidgetUsages() {
		run("src/test/resources/java/widgetsIdentification/ClassListenerExternal.java");
		assertEquals(2, widgetAttrs.size());
		assertEquals(2, new ArrayList<>(widgetAttrs.values()).get(0).size());
		assertEquals(2, new ArrayList<>(widgetAttrs.values()).get(1).size());
	}

	@Test
	public void testWidgetAsLocalVarAddedToContainer() {
		run("src/test/resources/java/widgetsIdentification/ClassListenerExternal2.java");
		assertEquals(4, widgetAttrs.size() + widgetReassigned.size());
	}

	@Override
	public void onWidgetAttribute(final CtVariable<?> widget, final List<CtVariableAccess<?>> usages, final CtTypeReference<?> element) {
		widgetAttrs.put(widget, usages);
	}

	@Override
	public void onWidgetCreatedInExistingVar(final CtAssignment<?, ?> assig, final List<CtVariableAccess<?>> usages, final CtTypeReference<?> element) {
		widgetReassigned.put(assig, usages);
	}

	@Override
	public void onWidgetCreatedForContainer(CtInvocation<?> widgetInvoc, CtTypeReference<?> element) {
		widgetAddedToContainer.add(element);
	}
}
