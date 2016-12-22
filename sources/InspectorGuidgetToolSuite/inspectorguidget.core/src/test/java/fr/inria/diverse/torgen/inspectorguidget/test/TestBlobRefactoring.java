package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.Launcher;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandWidgetFinder;
import fr.inria.diverse.torgen.inspectorguidget.processor.WidgetProcessor;
import fr.inria.diverse.torgen.inspectorguidget.refactoring.ListenerCommandRefactor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

import static org.junit.Assert.assertEquals;
import static spoon.testing.Assert.assertThat;

public class TestBlobRefactoring {
	private CommandAnalyser cmdAnalyser;
	private WidgetProcessor widgetProc;
	private CommandWidgetFinder finder;
	ListenerCommandRefactor refactor;
	private boolean asField;

	@Before
	public void setUp() throws Exception {
		asField = false;
		cmdAnalyser = new CommandAnalyser();
		widgetProc = new WidgetProcessor(true);
	}

	private void initTest(final boolean asLambda, final String... paths) {
		initTest(null, asLambda, paths);
	}

	private void initTest(final int startLine, final boolean asLambda, final String... paths) {
		initTest(Collections.singletonList(startLine), asLambda, paths);
	}

	private void initTest(final List<Integer> startLine, final boolean asLambda, final String... paths) {
//		spoon.Launcher.LOGGER.setLevel(Level.OFF);
		ListenerCommandRefactor.LOG.setLevel(java.util.logging.Level.OFF);
		Stream.of(paths).forEach(p -> cmdAnalyser.addInputResource(p));
		cmdAnalyser.run();

//		SpoonStructurePrinter printer = new SpoonStructurePrinter();
//		printer.scan(Collections.singletonList(cmdAnalyser.getModelBuilder().getFactory().Package().getRootPackage()));

		Launcher launcher = new Launcher(Collections.singletonList(widgetProc), cmdAnalyser.getModelBuilder());
		launcher.process();

		finder = new CommandWidgetFinder(
			cmdAnalyser.getCommands().values().parallelStream().flatMap(s -> s.getCommands().stream()).collect(Collectors.toList()),
			widgetProc.getWidgetUsages());
		finder.process();

		final Collection<CommandWidgetFinder.WidgetFinderEntry> allEntries = finder.getResults().values();

		if(startLine==null) {
			finder.getResults().entrySet().forEach(entry -> {
				refactor = new ListenerCommandRefactor(entry.getKey(), entry.getValue(), asLambda, asField, false, allEntries);
				refactor.execute();
			});
		}else {
			startLine.forEach(line -> finder.getResults().entrySet().stream().filter(e -> e.getKey().getLineStart() == line).forEach(entry -> {
				refactor = new ListenerCommandRefactor(entry.getKey(), entry.getValue(), asLambda, asField, false, allEntries);
				refactor.execute();
			}));
		}
	}

	private String getFileCode(final String path) throws IOException {
		FileReader reader = new FileReader(path);
		BufferedReader buf = new BufferedReader(reader);
		String txt = buf.lines().collect(Collectors.joining("\n"));

		buf.close();
		reader.close();

		return txt;
	}

	private CtModel getExpectedModel(final String path) {
		spoon.Launcher launcher = new spoon.Launcher();
		final Environment env = launcher.getEnvironment();
		env.setComplianceLevel(8);
		launcher.addInputResource(path);
		launcher.buildModel();
		return launcher.getModel();
	}

	String getRefactoredCode() {
		Environment env = cmdAnalyser.getEnvironment();
		env.useTabulations(true);
		env.setAutoImports(true);
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(env);
		printer.calculate(null, new ArrayList<>(cmdAnalyser.getModel().getAllTypes()));
		return printer.getResult().replace(" \n", "\n").replaceAll("(\n)+", "\n");
	}

	@Test
	public void testBRefactoredLambdaOneCmd() throws IOException {
		initTest(28, true, "src/test/resources/java/refactoring/B.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/BRefactoredLambdaOneCmd.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/BRefactoredLambdaOneCmd.java"), getRefactoredCode());
	}

	@Test
	public void testBRefactoredAnonOneCmd() throws IOException {
		initTest(28, false, "src/test/resources/java/refactoring/B.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/BRefactoredAnonOneCmd.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/BRefactoredAnonOneCmd.java"), getRefactoredCode());
	}

	@Test
	public void testBRefactoredLambdaTwoCmds() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/B.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/BRefactoredLambdaTwoCmds.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/BRefactoredLambdaTwoCmds.java"), getRefactoredCode());
	}

	@Test
	public void testARefactoredLambda() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/A.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/ARefactoredLambda.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/ARefactoredLambda.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredExternalListenerClassOneCmd() throws IOException {
		initTest(11, true, "src/test/resources/java/refactoring/C.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/CRefactoredLambdaOneCmd.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/CRefactoredLambdaOneCmd.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredExternalListenerClassTwoCmds() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/C.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/CRefactoredLambdaTwoCmds.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/CRefactoredLambdaTwoCmds.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredCmdsThatUseLocalVar() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/D.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/DRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/DRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredSwitch() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/E.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/ERefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/ERefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredDispatch() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/F.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/FRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/FRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredElse() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/G.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/GRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/GRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredSharedVar() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/H.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/HRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/HRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredMultipleConditions() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/I.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/IRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/IRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredSameCommandForTwoWidgets() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/K.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/KRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/KRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredRemovePrivateActionCmdNames() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/J.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/JRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/JRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredReturnsReturns() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/L.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/LRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/LRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredExternalListenerWithInvocations() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/M.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/MRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/MRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredExternalListenerWithAttributes() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/N.java");
//		assertThat(cmdAnalyser.getModel().getRootPackage()).
//			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/NRefactored.java").getRootPackage());
		assertEquals(getFileCode("src/test/resources/java/refactoring/NRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredExternalListenerWithAttrReads() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/O.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/ORefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/ORefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredLocalWidgetRemoveActionNames() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/Q.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/QRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/QRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredUseAttributeFromAnotherListener() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/P.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/PRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/PRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredCopyExternalFields() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/R.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/RRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/RRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredThisInExternalListener() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/S.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/SRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/SRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredThisInLocalListener() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/T.java");
//		assertThat(cmdAnalyser.getModel().getRootPackage()).
//			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/TRefactored.java").getRootPackage());
		assertEquals(getFileCode("src/test/resources/java/refactoring/TRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredIfElseMultipleWidgets() throws IOException {
		initTest(true, "src/test/resources/java/listeners/MultipleListener.java");
//		assertThat(cmdAnalyser.getModel().getRootPackage()).
//			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/MultipleListenerRefactored.java").getRootPackage());
		assertEquals(getFileCode("src/test/resources/java/refactoring/MultipleListenerRefactored.java"), getRefactoredCode());
	}

	@Test
	@Ignore
	public void testTryCatchListener() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/U.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/MultipleListenerRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/MultipleListenerRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredComplexConditionalStatements() throws IOException {
		initTest(true, "src/test/resources/java/analysers/ComplexConditionalStatements.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/RefactoredComplexConditionalStatements.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/RefactoredComplexConditionalStatements.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredComplexBlobs() throws IOException {
		initTest(Arrays.asList(45, 52, 54, 56, 63, 65), true, "src/test/resources/java/refactoring/ComplexBlobs.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/RefactoredComplexBlobs.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/RefactoredComplexBlobs.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredComplexBlobsDisorder2() throws IOException {
		initTest(Arrays.asList(56, 63, 65, 45, 52, 54), true, "src/test/resources/java/refactoring/ComplexBlobs.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/RefactoredComplexBlobs.java").getRootPackage());
		//		assertEquals(getFileCode("src/test/resources/java/refactoring/RefactoredComplexBlobs.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredComplexBlobsDisorder3() throws IOException {
		initTest(Arrays.asList(65, 56, 63, 54, 45, 52), true, "src/test/resources/java/refactoring/ComplexBlobs.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/RefactoredComplexBlobs.java").getRootPackage());
		//		assertEquals(getFileCode("src/test/resources/java/refactoring/RefactoredComplexBlobs.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredComplexBlobsDisorder4() throws IOException {
		initTest(Arrays.asList(45, 65, 56, 52, 63, 54), true, "src/test/resources/java/refactoring/ComplexBlobs.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/RefactoredComplexBlobs.java").getRootPackage());
		//		assertEquals(getFileCode("src/test/resources/java/refactoring/RefactoredComplexBlobs.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredAdHocListenerReg() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/AdhocListenerReg.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/AdhocListenerRegRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/AdhocListenerRegRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredMultipleListenerReg() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/MultipleListenerReg.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/MultipleListenerRegRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/MultipleListenerRegRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredSwitchCasesSameLine() throws IOException {
		initTest(true, "src/test/resources/java/analysers/SwitchCasesSameLine.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/SwitchCasesSameLineRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/SwitchCasesSameLineRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredSuperSwitchActionListener() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/SuperSwitchActionListener.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/SuperSwitchActionListenerRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/SuperSwitchActionListenerRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredPartialListenerRefactoring1() throws IOException {
		initTest(Arrays.asList(41, 44), true, "src/test/resources/java/refactoring/PartialListenerRefactoring.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/PartialListenerRefactoringRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/PartialListenerRefactoringRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoringCommandNotPossible1() throws IOException {
		initTest(Collections.singletonList(37), true, "src/test/resources/java/refactoring/RefactoringCommandNotPossible.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/RefactoringCommandNotPossible.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/RefactoringCommandNotPossible.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoringCommandNotPossible2() throws IOException {
		initTest(Collections.singletonList(14), true, "src/test/resources/java/refactoring/RefactoringCommandNotPossible.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/RefactoringCommandNotPossible.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/RefactoringCommandNotPossible.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoringCommandNotPossible3() throws IOException {
		initTest(Arrays.asList(14, 37), true, "src/test/resources/java/refactoring/RefactoringCommandNotPossible.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/RefactoringCommandNotPossible.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/RefactoringCommandNotPossible.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredPartialListenerRefactoring2() throws IOException {
		initTest(Arrays.asList(14, 37, 41, 44), true, "src/test/resources/java/refactoring/PartialListenerRefactoring.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/PartialListenerRefactoringRefactored.java").getRootPackage());
		//		assertEquals(getFileCode("src/test/resources/java/refactoring/PartialListenerRefactoringRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoredPartialListenerRefactoring3() throws IOException {
		initTest(Arrays.asList(41, 14, 44, 37), true, "src/test/resources/java/refactoring/PartialListenerRefactoring.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/PartialListenerRefactoringRefactored.java").getRootPackage());
		//		assertEquals(getFileCode("src/test/resources/java/refactoring/PartialListenerRefactoringRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testRefactoringOnWidgetCreatedByFunction() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/Fooo.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/FoooRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/FoooRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testBreakAtEnd() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/BreakAtEnd.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/BreakAtEndRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/BreakAtEndRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testAdhocWidget() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/AdhocWidget.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/AdhocWidgetRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/AdhocWidgetRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testAdhocWidget2() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/AdhocWidget2.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/AdhocWidget2Refactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/AdhocWidget2Refactored.java"), getRefactoredCode());
	}

	@Test
	public void testAsFieldLambda() throws IOException {
		asField = true;
		initTest(true, "src/test/resources/java/refactoring/RegUnreg.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/RegUnregRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/RegUnregRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testAsFieldAnonClass() throws IOException {
		asField = true;
		initTest(false, "src/test/resources/java/refactoring/RegUnreg.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/RegUnregAnonRefactoredClass.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/RegUnregAnonRefactoredClass.java"), getRefactoredCode());
	}

	@Test
	public void testForcedAsFieldLambda() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/RegUnreg.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/RegUnregRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/RegUnregRefactored.java"), getRefactoredCode());
	}

	@Test
	public void testForcedAsFieldAnonClass() throws IOException {
		initTest(false, "src/test/resources/java/refactoring/RegUnreg.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/RegUnregAnonRefactoredClass.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/RegUnregAnonRefactoredClass.java"), getRefactoredCode());
	}

	@Test
	public void testRegistrationIf() throws IOException {
		initTest(true, "src/test/resources/java/refactoring/RegIf.java");
		assertThat(cmdAnalyser.getModel().getRootPackage()).
			isEqualTo(getExpectedModel("src/test/resources/java/refactoring/RegIfRefactored.java").getRootPackage());
//		assertEquals(getFileCode("src/test/resources/java/refactoring/RegIfRefactored.java"), getRefactoredCode());
	}
}
