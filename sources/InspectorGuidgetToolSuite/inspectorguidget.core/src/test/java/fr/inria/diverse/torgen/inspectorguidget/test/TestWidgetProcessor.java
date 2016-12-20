package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.processor.InspectorGuidgetProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.WidgetProcessor;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestWidgetProcessor extends TestInspectorGuidget<WidgetProcessor> {
	private WidgetProcessor wproc;

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}

	@BeforeClass
	public static void setUpBeforeClass() {
		InspectorGuidgetProcessor.LOG.addHandler(TestInspectorGuidget.HANDLER_FAIL);
	}


	@Override
	public List<WidgetProcessor> createProcessor() {
		wproc = new WidgetProcessor(true);
		return Collections.singletonList(wproc);
	}

	@Test
	public void testWidgetsAsExplicitAttr() {
		run("src/test/resources/java/widgets/WidgetAsStdAttr.java");
		assertEquals(3, wproc.getWidgetUsages().size());
	}

	@Test
	public void testWidgetsAsExplicitAttrConstructor() {
		run("src/test/resources/java/widgets/WidgetAsStdAttr.java");
		assertEquals(1L, wproc.getWidgetUsages().stream().filter(u -> u.creation.isPresent()).count());
	}

	@Test
	public void testWidgetsAsListAttr() {
		run("src/test/resources/java/widgets/WidgetAsListAttr.java");
		assertEquals(0, wproc.getWidgetUsages().size());
	}

	@Test
	public void testWidgetsConstructorObject() {
		run("src/test/resources/java/widgets/WidgetConstructorObject.java");
		assertEquals(1, wproc.getWidgetUsages().size());
	}

	@Test
	public void testWidgetsConstructorListObject() {
		run("src/test/resources/java/widgets/WidgetConstructorListObject.java");
		assertEquals(0, wproc.getWidgetUsages().size());
	}

	@Test
	public void testWidgetsConstructorContainer() {
		run("src/test/resources/java/widgets/WidgetConstructorContainer.java");
		assertEquals(1, wproc.getWidgetUsages().size());
		assertTrue(new ArrayList<>(wproc.getWidgetUsages()).get(0).creation.isPresent());
		assertEquals(1, wproc.getRefWidgets().size());
	}

	@Test
	public void testWidgetsConstructorContainerUsage() {
		run("src/test/resources/java/widgets/WidgetConstructorContainer.java");
		assertEquals(1, new ArrayList<>(wproc.getWidgetUsages()).get(0).accesses.size());// The panel is used to add the window
	}

	@Test
	public void testWidgetsConstructorObjectUndirect() {
		run("src/test/resources/java/widgets/WidgetConstructorObjectUndirect.java");
		assertEquals(1, wproc.getWidgetUsages().size());
	}

	@Test
	public void testWidgetAsClass() {
		run("src/test/resources/java/widgets/WidgetAsClass.java");
		assertEquals(2, wproc.getWidgetUsages().size());
	}


	@Test
	public void testWidgetsConstructorObjectFunction() {
		run("src/test/resources/java/widgets/WidgetConstructorObjectFunction.java",
				"src/test/resources/java/widgets/WidgetConstructorObjectFunction2.java");
		assertEquals(4, wproc.getWidgetUsages().size());
	}

	@Test
	public void testWidgetsConstructorObjectFunction3() {
		run("src/test/resources/java/widgets/WidgetConstructorObjectFunction3.java");
		assertEquals(1, wproc.getWidgetUsages().size());
		assertEquals(1, wproc.getRefWidgets().size());
	}

	@Test
	public void testWidgetUsages() {
		run("src/test/resources/java/widgetsIdentification/ClassListenerExternal.java");
		assertEquals(2, wproc.getWidgetUsages().size());
		assertEquals(2, new ArrayList<>(wproc.getWidgetUsages()).get(0).accesses.size());
		assertEquals(2, new ArrayList<>(wproc.getWidgetUsages()).get(1).accesses.size());
	}

	@Test
	public void testWidgetAsLocalVarAddedToContainer() {
		run("src/test/resources/java/widgetsIdentification/ClassListenerExternal2.java");
		assertEquals(5, wproc.getWidgetUsages().size());
		assertEquals(0, wproc.getRefWidgets().size());
		List<WidgetProcessor.WidgetUsage> usages = wproc.getWidgetUsages().stream().filter(u -> u.creation.isPresent()).
			sorted((a, b) -> a.creation.get().getPosition().getLine() < b.creation.get().getPosition().getLine() ? -1 : 1).
			collect(Collectors.toList());
		assertEquals(27, usages.get(0).creation.get().getPosition().getLine());
		assertEquals(29, usages.get(1).creation.get().getPosition().getLine());
		assertEquals(35, usages.get(2).creation.get().getPosition().getLine());
		assertEquals(40, usages.get(3).creation.get().getPosition().getLine());
	}

	@Test
	public void testWidgetAsLocalVarAddedToContainerAccesses() {
		run("src/test/resources/java/widgetsIdentification/ClassListenerExternal2.java");
		List<WidgetProcessor.WidgetUsage> usages = wproc.getWidgetUsages().stream().filter(u -> u.creation.isPresent()).
			sorted((a, b) -> a.creation.get().getPosition().getLine() < b.creation.get().getPosition().getLine() ? -1 : 1).
			collect(Collectors.toList());
		assertEquals(4, usages.get(0).accesses.size());
		assertEquals(3, usages.get(1).accesses.size());
		assertEquals(3, usages.get(2).accesses.size());
		assertEquals(3, usages.get(3).accesses.size());
	}

	@Test
	public void testAnotherExample3CorrectStatementsIndentification() {
		run("src/test/resources/java/widgetsIdentification/AnotherExample3.java");
		List<WidgetProcessor.WidgetUsage> usages = wproc.getWidgetUsages().stream().filter(u -> u.creation.isPresent()).
			sorted((a, b) -> a.creation.get().getPosition().getLine() < b.creation.get().getPosition().getLine() ? -1 : 1).
			collect(Collectors.toList());
		assertEquals(4, wproc.getWidgetUsages().size());
		assertEquals(0, wproc.getRefWidgets().size());
		assertEquals(2, usages.get(0).accesses.size());
		assertEquals(2, usages.get(1).accesses.size());
		assertEquals(0, usages.get(2).accesses.size());
	}

	@Test
	public void testAnotherExample5() {
		run("src/test/resources/java/widgetsIdentification/AnotherExample5.java");
		assertEquals(2, wproc.getWidgetUsages().size());
	}


	@Test
	public void testWidgetsWithSameName() {
		run("src/test/resources/java/widgetsIdentification/WidgetsWithSameName.java");
		assertEquals(2, wproc.getWidgetUsages().size());
	}

	@Test
	public void testGoodNumberOfUsages() {
		run("src/test/resources/java/refactoring/SuperSwitchActionListener.java");
		assertEquals(2, wproc.getWidgetUsages().size());
	}

	@Test
	public void testWidgetCreatedInFunction() {
		run("src/test/resources/java/widgetsIdentification/WidgetCreatedInFunction.java");
		assertEquals(2, wproc.getWidgetUsages().size());
	}
}
