package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.analyser.Command;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandWidgetBugsDetector;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandWidgetFinder;
import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonStructurePrinter;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestWidgetFinder {
	private CommandAnalyser cmdAnalyser;
	private CommandWidgetFinder finder;

	@Before
	public void setUp() throws Exception {
		cmdAnalyser = new CommandAnalyser();
	}

	@After
	public void tearsDown() {
		if(TestInspectorGuidget.SHOW_MODEL) {
			SpoonStructurePrinter printer = new SpoonStructurePrinter();
			printer.scan(Collections.singletonList(cmdAnalyser.getModelBuilder().getFactory().Package().getRootPackage()));
		}
	}

	private void initTest(final String path) {
		cmdAnalyser.addInputResource(path);
		cmdAnalyser.run();
		finder = new CommandWidgetFinder(cmdAnalyser.getCommands().values().parallelStream().flatMap(s -> s.stream()).collect(Collectors.toList()));
		finder.process();
	}

	@Test
	public void testAnonClassOnSingleFieldWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/AnonClassOnSingleFieldWidgetNoCond.java");
		Map<Command, CommandWidgetFinder.WidgetFinderEntry> results = finder.getResults();

		assertEquals(1, results.size());
		assertEquals("b", new ArrayList<>(results.values()).get(0).getRegisteredWidgets().get(0).getSimpleName());
	}

	@Test
	@Ignore
	public void testLambdaOnSingleFieldWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/LambdaOnSingleFieldWidgetNoCond.java");
		Map<Command, CommandWidgetFinder.WidgetFinderEntry> results = finder.getResults();

		assertEquals(1, results.size());
		assertEquals("b", new ArrayList<>(results.values()).get(0).getRegisteredWidgets().get(0).getSimpleName());
	}

	@Test
	public void testAnonClassOnSingleLocalVarWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/AnonClassOnSingleLocalVarWidgetNoCond.java");
		Map<Command, CommandWidgetFinder.WidgetFinderEntry> results = finder.getResults();

		assertEquals(1, results.size());
		assertEquals(1L, new ArrayList<>(results.values()).get(0).getNbDistinctWidgets());
		assertEquals("b", new ArrayList<>(results.values()).get(0).getRegisteredWidgets().get(0).getSimpleName());
	}

	@Test
	public void testAnonClassOnSingleFieldWidgetEqualCond() {
		initTest("src/test/resources/java/widgetsIdentification/AnonClassOnFieldWidgetsEqualCond.java");
		Map<Command, CommandWidgetFinder.WidgetFinderEntry> results = finder.getResults();

		assertEquals(1, results.size());
		assertEquals(2, new ArrayList<>(results.values()).get(0).getNbDistinctWidgets());
		assertEquals("b", new ArrayList<>(results.values()).get(0).getRegisteredWidgets().get(0).getSimpleName());
		assertEquals("a", new ArrayList<>(results.values()).get(0).getWidgetsUsedInConditions().get(0).getSimpleName());
	}

	@Test
	public void testLambdaOnSingleFieldWidgetEqualCond() {
		initTest("src/test/resources/java/widgetsIdentification/LambdaOnFieldWidgetsEqualCond.java");
		Map<Command, CommandWidgetFinder.WidgetFinderEntry> results = finder.getResults();

		assertEquals(1, results.size());
		assertEquals(2, new ArrayList<>(results.values()).get(0).getNbDistinctWidgets());
		assertEquals("b", new ArrayList<>(results.values()).get(0).getRegisteredWidgets().get(0).getSimpleName());
		assertEquals("a", new ArrayList<>(results.values()).get(0).getWidgetsUsedInConditions().get(0).getSimpleName());
	}

	@Test
	public void testClassSingleWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/ClassSingleWidgetNoCond.java");
		Map<Command, CommandWidgetFinder.WidgetFinderEntry> results = finder.getResults();

		assertEquals(1, results.size());
		assertEquals(1, new ArrayList<>(results.values()).get(0).getNbDistinctWidgets());
		assertEquals("fooo", new ArrayList<>(results.values()).get(0).getRegisteredWidgets().get(0).getSimpleName());
	}

	@Test
	public void testClassInheritanceSingleWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/ClassInheritanceSingleWidgetNoCond.java");
		Map<Command, CommandWidgetFinder.WidgetFinderEntry> results = finder.getResults();

		assertEquals(1, results.size());
		assertEquals(1, new ArrayList<>(results.values()).get(0).getNbDistinctWidgets());
		assertEquals("fooo", new ArrayList<>(results.values()).get(0).getRegisteredWidgets().get(0).getSimpleName());
	}

	@Test
	public void testWidgetClassListener() {
		initTest("src/test/resources/java/widgetsIdentification/WidgetClassListener.java");
		Map<Command, CommandWidgetFinder.WidgetFinderEntry> results = finder.getResults();

		assertEquals(1, results.size());
		assertEquals(1, new ArrayList<>(results.values()).get(0).getNbDistinctWidgets());
		assertTrue(new ArrayList<>(results.values()).get(0).getWidgetClasses().isPresent());
		assertEquals("Foo", new ArrayList<>(results.values()).get(0).getWidgetClasses().get().getSimpleName());
	}

	@Test
	public void testFalseNegativeWidgetClassListener() {
		initTest("src/test/resources/java/widgetsIdentification/FalsePositiveThisListener.java");
		Map<Command, CommandWidgetFinder.WidgetFinderEntry> results = finder.getResults();

		assertEquals(1, results.size());
		assertEquals(0, new ArrayList<>(results.values()).get(0).getNbDistinctWidgets());
	}


	@Test
	public void testClassListenerInheritance() {
		initTest("src/test/resources/java/widgetsIdentification/ClassListenerInheritance.java");
		Map<Command, CommandWidgetFinder.WidgetFinderEntry> results = finder.getResults();

		CommandWidgetBugsDetector detector = new CommandWidgetBugsDetector(results);
		detector.process();

		assertEquals(2, results.size());

		List<Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry>> entries = results.entrySet().stream().sorted((a, b) ->
			a.getKey().getExecutable().getPosition().getLine() < b.getKey().getExecutable().getPosition().getLine() ? -1 :
			a.getKey().getExecutable().getPosition().getLine() == b.getKey().getExecutable().getPosition().getLine() ? 0 : 1)
			.collect(Collectors.toList());

		assertEquals(1, entries.get(0).getValue().getNbDistinctWidgets());
		assertEquals("fooo", entries.get(0).getValue().getRegisteredWidgets().get(0).getSimpleName());

		assertEquals(2, entries.get(1).getValue().getNbDistinctWidgets());
		List<String> names = entries.get(1).getValue().getRegisteredWidgets().stream().map(o -> o.getSimpleName()).sorted(String::compareTo).collect(Collectors.toList());
		assertEquals("bar", names.get(0));
		assertEquals("fooo", names.get(1));
	}


	@Test
	@Ignore
	public void testClassListenerExternal() {
		initTest("src/test/resources/java/widgetsIdentification/ClassListenerExternal.java");
		Map<Command, CommandWidgetFinder.WidgetFinderEntry> results = finder.getResults();

		assertEquals(1, results.size());
		assertEquals(2, new ArrayList<>(results.values()).get(0).getNbDistinctWidgets());
	}
}
