package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.analyser.Command;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandWidgetFinder;
import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonStructurePrinter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

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
	public void testClassSingleWidgetNoCond() {
		initTest("src/test/resources/java/widgetsIdentification/ClassSingleWidgetNoCond.java");
		Map<Command, CommandWidgetFinder.WidgetFinderEntry> results = finder.getResults();

		assertEquals(1, results.size());
		assertEquals(1, new ArrayList<>(results.values()).get(0).getNbDistinctWidgets());
		assertEquals("fooo", new ArrayList<>(results.values()).get(0).getRegisteredWidgets().get(0).getSimpleName());
	}
}
