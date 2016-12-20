package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.Launcher;
import fr.inria.diverse.torgen.inspectorguidget.analyser.Command;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandWidgetBugsDetector;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandWidgetFinder;
import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonStructurePrinter;
import fr.inria.diverse.torgen.inspectorguidget.processor.InspectorGuidgetProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.WidgetProcessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestWidgetFinder {
	private CommandAnalyser cmdAnalyser;
	private WidgetProcessor widgetProc;
	private CommandWidgetFinder finder;
	Map<Command, CommandWidgetFinder.WidgetFinderEntry> results;

	@BeforeClass
	public static void setUpBeforeClass() {
		InspectorGuidgetProcessor.LOG.addHandler(TestInspectorGuidget.HANDLER_FAIL);
	}

	@Before
	public void setUp() throws Exception {
		cmdAnalyser = new CommandAnalyser();
		widgetProc = new WidgetProcessor(true);
	}

	@After
	public void tearsDown() {
		if(TestInspectorGuidget.SHOW_MODEL) {
			SpoonStructurePrinter printer = new SpoonStructurePrinter();
			printer.scan(Collections.singletonList(cmdAnalyser.getModelBuilder().getFactory().Package().getRootPackage()));
		}
	}

	private void initTest(final String... paths) {
		Stream.of(paths).forEach(p -> cmdAnalyser.addInputResource(p));
		cmdAnalyser.run();

		Launcher launcher = new Launcher(Collections.singletonList(widgetProc), cmdAnalyser.getModelBuilder());
		launcher.process();

		finder = new CommandWidgetFinder(
			cmdAnalyser.getCommands().values().parallelStream().flatMap(s -> s.getCommands().stream()).collect(Collectors.toList()),
			widgetProc.getWidgetUsages());
		finder.process();
		results = finder.getResults();
	}

	@Test
	public void testAnonClassOnSingleFieldWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/AnonClassOnSingleFieldWidgetNoCond.java");
		assertEquals(1, results.size());
		assertEquals("b", new ArrayList<>(results.values()).get(0).getRegisteredWidgets().iterator().next().widgetVar.getSimpleName());
	}

	@Test
	public void testLambdaOnSingleFieldWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/LambdaOnSingleFieldWidgetNoCond.java");
		assertEquals(1, results.size());
		assertEquals(1L, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertEquals("b", new ArrayList<>(results.values()).get(0).getRegisteredWidgets().iterator().next().widgetVar.getSimpleName());
	}

	@Test
	public void testAnonClassOnSingleLocalVarWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/AnonClassOnSingleLocalVarWidgetNoCond.java");
		assertEquals(1, results.size());
		assertEquals(1L, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertEquals("b", new ArrayList<>(results.values()).get(0).getRegisteredWidgets().iterator().next().widgetVar.getSimpleName());
	}

	@Test
	public void testAnonClassOnSingleFieldWidgetEqualCond() {
		initTest("src/test/resources/java/widgetsIdentification/AnonClassOnFieldWidgetsEqualCond.java");
		assertEquals(1, results.size());
		assertEquals(2, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertEquals("b", new ArrayList<>(results.values()).get(0).getRegisteredWidgets().iterator().next().widgetVar.getSimpleName());
		assertEquals("a", new ArrayList<>(results.values()).get(0).getWidgetsUsedInConditions().iterator().next().widgetVar.getSimpleName());
	}

	@Test
	public void testLambdaOnSingleFieldWidgetEqualCond() {
		initTest("src/test/resources/java/widgetsIdentification/LambdaOnFieldWidgetsEqualCond.java");
		assertEquals(1, results.size());
		assertEquals(2, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertEquals("b", new ArrayList<>(results.values()).get(0).getRegisteredWidgets().iterator().next().widgetVar.getSimpleName());
		assertEquals("a", new ArrayList<>(results.values()).get(0).getWidgetsUsedInConditions().iterator().next().widgetVar.getSimpleName());
	}

	@Test
	public void testClassSingleWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/ClassSingleWidgetNoCond.java");
		assertEquals(1, results.size());
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertEquals("fooo", new ArrayList<>(results.values()).get(0).getRegisteredWidgets().iterator().next().widgetVar.getSimpleName());
	}

	@Test
	public void testClassInheritanceSingleWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/ClassInheritanceSingleWidgetNoCond.java");
		assertEquals(1, results.size());
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertEquals("fooo", new ArrayList<>(results.values()).get(0).getRegisteredWidgets().iterator().next().widgetVar.getSimpleName());
	}

	@Test
	public void testWidgetClassListener() {
		initTest("src/test/resources/java/widgetsIdentification/WidgetClassListener.java");
		assertEquals(1, results.size());
		assertEquals(0, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertTrue(new ArrayList<>(results.values()).get(0).getWidgetClasses().isPresent());
		assertEquals("Foo", new ArrayList<>(results.values()).get(0).getWidgetClasses().get().getSimpleName());
	}

	@Test
	public void testFalseNegativeWidgetClassListener() {
		initTest("src/test/resources/java/widgetsIdentification/FalsePositiveThisListener.java");
		assertEquals(1, results.size());
		assertEquals(0, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
	}


	@Test
	public void testClassListenerInheritance() {
		initTest("src/test/resources/java/widgetsIdentification/ClassListenerInheritance.java");
		CommandWidgetBugsDetector detector = new CommandWidgetBugsDetector(results);
		detector.process();

		assertEquals(2, results.size());

		List<Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry>> entries = results.entrySet().stream().sorted((a, b) ->
			a.getKey().getExecutable().getPosition().getLine() < b.getKey().getExecutable().getPosition().getLine() ? -1 :
			a.getKey().getExecutable().getPosition().getLine() == b.getKey().getExecutable().getPosition().getLine() ? 0 : 1)
			.collect(Collectors.toList());

		assertEquals(1, entries.get(0).getValue().getWidgetUsages(results.values()).size());
		assertEquals("fooo", entries.get(0).getValue().getRegisteredWidgets().iterator().next().widgetVar.getSimpleName());

		assertEquals(1, entries.get(1).getValue().getWidgetUsages(results.values()).size());
		assertEquals("bar", entries.get(1).getValue().getRegisteredWidgets().iterator().next().widgetVar.getSimpleName());
	}


	@Test
	public void testClassListenerExternal() {
		initTest("src/test/resources/java/widgetsIdentification/ClassListenerExternal.java");
		assertEquals(2, results.size());
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertEquals(1, new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testClassListenerExternalString() {
		initTest("src/test/resources/java/widgetsIdentification/ClassListenerExternalString.java");
		assertEquals(2, results.size());
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertEquals(1, new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testClassListenerExternalLocalVar() {
		initTest("src/test/resources/java/widgetsIdentification/ClassListenerExternalLocalVar.java");
		assertEquals(2, results.size());
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertEquals(1, new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testClassListenerExternal2() {
		initTest("src/test/resources/java/widgetsIdentification/ClassListenerExternal2.java");
		assertEquals(3, results.size());
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertEquals(1, new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size());
		assertEquals(1, new ArrayList<>(results.values()).get(2).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testMenuWidgetAndListener() {
		initTest("src/test/resources/java/widgetsIdentification/MenuWidgetAndListener.java");
		assertEquals(3, results.size());
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertEquals(1, new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size());
		assertEquals(1, new ArrayList<>(results.values()).get(2).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testAnotherExample() {
		initTest("src/test/resources/java/widgetsIdentification/AnotherExample.java");
		assertEquals(1, results.size());
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testUseSameStringVar() {
		initTest("src/test/resources/java/widgetsIdentification/UseSameStringVar.java");
		assertEquals(1, results.size());
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testAnotherExample2() {
		initTest("src/test/resources/java/widgetsIdentification/AnotherExample2.java");
		assertEquals(1, results.size());
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testAnotherExample3() {
		initTest("src/test/resources/java/widgetsIdentification/AnotherExample3.java");
		assertEquals(2, results.size());
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testAnotherExample3CorrectStatementsIndentification() {
		initTest("src/test/resources/java/widgetsIdentification/AnotherExample3.java");
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertEquals(1, new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testListenerRegisterOnInvocation() {
		initTest("src/test/resources/java/analysers/ListenerRegisterOnInvocation.java");
		assertEquals(1, results.size());
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testAnotherExample4() {
		initTest("src/test/resources/java/widgetsIdentification/AnotherExample4.java");
		assertEquals(2, results.size());
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertEquals(1, new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testFilterOutRegistrationWidgetUsingVars() {
		initTest("src/test/resources/java/widgetsIdentification/FilterOutRegistrationWidgetUsingVars.java");
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testFilterOutRegistrationWidgetUsingLiterals() {
		initTest("src/test/resources/java/widgetsIdentification/FilterOutRegistrationWidgetUsingLiterals.java");
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testFilterOutRegistrationWidgetUsingWidgetVars() {
		initTest("src/test/resources/java/widgetsIdentification/FilterOutRegistrationWidgetUsingWidgetVars.java");
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testInsertPSTricksCodeFrame() {
		initTest("src/test/resources/java/widgetsIdentification/InsertPSTricksCodeFrame.java");
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertEquals(1, new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size());
		assertEquals(1, new ArrayList<>(results.values()).get(2).getWidgetUsages(results.values()).size());
		assertEquals(1, new ArrayList<>(results.values()).get(3).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testAnotherExample5() {
		initTest("src/test/resources/java/widgetsIdentification/AnotherExample5.java");
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertEquals(1, new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testWidgetsWithSameName() {
		initTest("src/test/resources/java/widgetsIdentification/WidgetsWithSameName.java");
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertEquals(1, new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size());
	}

	@Test
	@Ignore
	public void testNonDeterministActionCmd() {
		initTest("src/test/resources/java/refactoring/ListenerTab.java");
		assertEquals(1, new ArrayList<>(results.values()).get(0).getWidgetUsages(results.values()).size());
		assertEquals(1, new ArrayList<>(results.values()).get(1).getWidgetUsages(results.values()).size());
	}

	@Test
	public void testNoMatchingWidgetButTwoCandidates() {
		initTest("src/test/resources/java/refactoring/RefactoringCommandNotPossible.java");
		List<Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry>> entries =
			results.entrySet().stream().sorted(Comparator.comparing(entry -> entry.getKey().getLineStart())).collect(Collectors.toList());
		assertEquals(0, entries.get(0).getValue().getWidgetUsages(results.values()).size());
		assertEquals(0, entries.get(1).getValue().getWidgetUsages(results.values()).size());
		assertEquals(1, entries.get(2).getValue().getWidgetUsages(results.values()).size());
		assertEquals(1, entries.get(3).getValue().getWidgetUsages(results.values()).size());
	}

	@Test
	public void testSuperSwitchActionListener() {
		initTest("src/test/resources/java/refactoring/SuperSwitchActionListener.java");
		List<Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry>> entries =
			results.entrySet().stream().sorted(Comparator.comparing(entry -> entry.getKey().getLineStart())).collect(Collectors.toList());
		assertEquals(1, entries.get(0).getValue().getWidgetUsages(results.values()).size());
		assertEquals(1, entries.get(1).getValue().getWidgetUsages(results.values()).size());
	}
}
