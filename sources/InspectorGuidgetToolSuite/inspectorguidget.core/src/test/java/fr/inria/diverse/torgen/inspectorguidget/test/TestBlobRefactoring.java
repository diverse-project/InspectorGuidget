package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.Launcher;
import fr.inria.diverse.torgen.inspectorguidget.analyser.Command;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandWidgetFinder;
import fr.inria.diverse.torgen.inspectorguidget.processor.WidgetProcessor;
import fr.inria.diverse.torgen.inspectorguidget.refactoring.ListenerCommandRefactor;
import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import spoon.compiler.Environment;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class TestBlobRefactoring {
	private CommandAnalyser cmdAnalyser;
	private WidgetProcessor widgetProc;
	private CommandWidgetFinder finder;
	ListenerCommandRefactor refactor;

	@Before
	public void setUp() throws Exception {
		cmdAnalyser = new CommandAnalyser();
		widgetProc = new WidgetProcessor(true);
	}

	private void initTest(final int startLine, final boolean asLambda, final String... paths) {
		initTest(Collections.singletonList(startLine), asLambda, paths);
	}

	private void initTest(final List<Integer> startLine, final boolean asLambda, final String... paths) {
		spoon.Launcher.LOGGER.setLevel(Level.OFF);
		Stream.of(paths).forEach(p -> cmdAnalyser.addInputResource(p));
		cmdAnalyser.run();

		Launcher launcher = new Launcher(Collections.singletonList(widgetProc), cmdAnalyser.getModelBuilder());
		launcher.process();

		finder = new CommandWidgetFinder(
			cmdAnalyser.getCommands().values().parallelStream().flatMap(s -> s.stream()).collect(Collectors.toList()),
			widgetProc.getWidgetUsages());
		finder.process();

		startLine.forEach(line -> {
			Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry> entry = finder.getResults().entrySet().stream().filter(e -> e.getKey().getLineStart()==line).findAny().get();
			refactor = new ListenerCommandRefactor(entry.getKey(), entry.getValue(), asLambda);
			refactor.execute();
		});
	}

	private String getFileCode(final String path) throws IOException {
		FileReader reader = new FileReader(path);
		BufferedReader buf = new BufferedReader(reader);
		String txt = buf.lines().collect(Collectors.joining("\n"));

		buf.close();
		reader.close();

		return txt;
	}

	String getRefactoredCode() {
		Environment env = cmdAnalyser.getEnvironment();
		env.useTabulations(true);
		env.setAutoImports(true);
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(env);
		printer.calculate(null, new ArrayList<>(cmdAnalyser.getModel().getAllTypes()));
		return printer.getResult().replace(" \n", "\n");
	}

	@Test
	public void testBRefactoredLambdaOneCmd() throws IOException {
		initTest(28, true, "src/test/resources/java/refactoring/B.java");
		assertEquals(getFileCode("src/test/resources/java/refactoring/BRefactoredLambdaOneCmd.java"), getRefactoredCode());
	}

	@Test
	public void testBRefactoredAnonOneCmd() throws IOException {
		initTest(28, false, "src/test/resources/java/refactoring/B.java");
		assertEquals(getFileCode("src/test/resources/java/refactoring/BRefactoredAnonOneCmd.java"), getRefactoredCode());
	}

	@Test
	public void testBRefactoredLambdaTwoCmds() throws IOException {
		initTest(Arrays.asList(24, 28), true, "src/test/resources/java/refactoring/B.java");
		assertEquals(getFileCode("src/test/resources/java/refactoring/BRefactoredLambdaTwoCmds.java"), getRefactoredCode());
	}

	@Test
	public void testARefactoredLambda() throws IOException {
		initTest(18, true, "src/test/resources/java/refactoring/A.java");
		assertEquals(getFileCode("src/test/resources/java/refactoring/ARefactoredLambda.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredExternalListenerClassOneCmd() throws IOException {
		initTest(11, true, "src/test/resources/java/refactoring/C.java");
		assertEquals(getFileCode("src/test/resources/java/refactoring/CRefactoredLambdaOneCmd.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredExternalListenerClassTwoCmds() throws IOException {
		initTest(Arrays.asList(11, 15), true, "src/test/resources/java/refactoring/C.java");
		assertEquals(getFileCode("src/test/resources/java/refactoring/CRefactoredLambdaTwoCmds.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredCmdsThatUseLocalVar() throws IOException {
		initTest(Arrays.asList(27, 31), true, "src/test/resources/java/refactoring/D.java");
		assertEquals(getFileCode("src/test/resources/java/refactoring/DRefactored.java"), getRefactoredCode());
	}
}
