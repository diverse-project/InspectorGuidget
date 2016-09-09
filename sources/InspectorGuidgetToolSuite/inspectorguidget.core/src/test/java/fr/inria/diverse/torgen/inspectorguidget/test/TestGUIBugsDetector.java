package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.Launcher;
import fr.inria.diverse.torgen.inspectorguidget.analyser.Command;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandWidgetBugsDetector;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandWidgetFinder;
import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonStructurePrinter;
import fr.inria.diverse.torgen.inspectorguidget.helper.Tuple;
import fr.inria.diverse.torgen.inspectorguidget.processor.WidgetProcessor;
import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestGUIBugsDetector {
	private CommandAnalyser cmdAnalyser;
	private WidgetProcessor widgetProc;
	private CommandWidgetFinder finder;
	Map<Command, CommandWidgetFinder.WidgetFinderEntry> results;
	List<Tuple<String, Command>> bugs;

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

		spoon.Launcher.LOGGER.setLevel(Level.OFF);
		Launcher launcher = new Launcher(Collections.singletonList(widgetProc), cmdAnalyser.getModelBuilder());
		launcher.process();

		finder = new CommandWidgetFinder(
			cmdAnalyser.getCommands().values().parallelStream().flatMap(s -> s.stream()).collect(Collectors.toList()),
			widgetProc.getWidgetUsages());
		finder.process();
		results = finder.getResults();
		CommandWidgetBugsDetector detector = new CommandWidgetBugsDetector(results);
		detector.process();
		bugs = detector.getResults();
	}

	@Test
	public void testAnonClassOnSingleFieldWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/AnonClassOnSingleFieldWidgetNoCond.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}

	@Test
	public void testLambdaOnSingleFieldWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/LambdaOnSingleFieldWidgetNoCond.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}

	@Test
	public void testAnonClassOnSingleLocalVarWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/AnonClassOnSingleLocalVarWidgetNoCond.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}

	@Test
	public void testAnonClassOnSingleFieldWidgetEqualCond() {
		initTest("src/test/resources/java/widgetsIdentification/AnonClassOnFieldWidgetsEqualCond.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}

	@Test
	@Ignore
	public void testLambdaOnSingleFieldWidgetEqualCond() {
		initTest("src/test/resources/java/widgetsIdentification/LambdaOnFieldWidgetsEqualCond.java");
		assertFalse(bugs.toString(), bugs.isEmpty());
	}

	@Test
	public void testClassSingleWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/ClassSingleWidgetNoCond.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}

	@Test
	public void testClassInheritanceSingleWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/ClassInheritanceSingleWidgetNoCond.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}

	@Test
	public void testWidgetClassListener() {
		initTest("src/test/resources/java/widgetsIdentification/WidgetClassListener.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}

	@Test
	public void testFalseNegativeWidgetClassListener() {
		initTest("src/test/resources/java/widgetsIdentification/FalsePositiveThisListener.java");
		assertFalse(bugs.toString(), bugs.isEmpty());
	}


	@Test
	public void testClassListenerInheritance() {
		initTest("src/test/resources/java/widgetsIdentification/ClassListenerInheritance.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}

	@Test
	public void testWidgetClassListener2widgets() {
		initTest("src/test/resources/java/widgetsIdentification/WidgetClassListener2widgets.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}


	@Test
	public void testClassListenerExternal() {
		initTest("src/test/resources/java/widgetsIdentification/ClassListenerExternal.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}

	@Test
	public void testClassListenerExternalString() {
		initTest("src/test/resources/java/widgetsIdentification/ClassListenerExternalString.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}

	@Test
	public void testClassListenerExternalLocalVar() {
		initTest("src/test/resources/java/widgetsIdentification/ClassListenerExternalLocalVar.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}

	@Test
	public void testClassListenerExternal2() {
		initTest("src/test/resources/java/widgetsIdentification/ClassListenerExternal2.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}

	@Test
	public void testMenuWidgetAndListener() {
		initTest("src/test/resources/java/widgetsIdentification/MenuWidgetAndListener.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}

	@Test
	public void testAnotherExample() {
		initTest("src/test/resources/java/widgetsIdentification/AnotherExample.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}

	@Test
	public void testUseSameStringVar() {
		initTest("src/test/resources/java/widgetsIdentification/UseSameStringVar.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}

	@Test
	public void testAnotherExample2() {
		initTest("src/test/resources/java/widgetsIdentification/AnotherExample2.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}

	@Test
	public void testAnotherExample3() {
		initTest("src/test/resources/java/widgetsIdentification/AnotherExample3.java");
		assertTrue(bugs.toString(), bugs.isEmpty());
	}
}
