package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.analyser.Command;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.analyser.InspectorGuidetAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.analyser.UIListener;
import fr.inria.diverse.torgen.inspectorguidget.helper.CodeBlockPos;
import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonStructurePrinter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtExecutable;
import spoon.testing.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestCommandAnalyser {
	CommandAnalyser analyser;

	@BeforeClass
	public static void setUpBeforeClass() {
		InspectorGuidetAnalyser.LOG.addHandler(TestInspectorGuidget.HANDLER_FAIL);
	}

	@Before
	public void setUp() {
		analyser = new CommandAnalyser();
	}

	@After
	public void tearsDown() {
		if(TestInspectorGuidget.SHOW_MODEL) {
			SpoonStructurePrinter printer = new SpoonStructurePrinter();
			printer.scan(Collections.singletonList(analyser.getModelBuilder().getFactory().Package().getRootPackage()));
		}
	}

	@Test
	public void testEmptyClassListenerMethodNoCommand() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerEmptyClass.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(0L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testEmptyLambdaListenerMethodNoCommand() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerEmptyLambda.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(0L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testClassListenerMethodNoConditional() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerNoConditClass.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testLambdaListenerMethodNoConditional() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerNoConditLambda.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testLambdaListenerNoBlockMethodNoConditional() {
		analyser.addInputResource("src/test/resources/java/widgetsIdentification/LambdaOnSingleFieldWidgetNoCond.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testClassListenerMethodNoCondMultipleMethodsButOneUsed() {
		analyser.addInputResource("src/test/resources/java/analysers/MouseInputListOneMethodUsed.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testClassListenerMethodNoCodeMultipleMethods() {
		analyser.addInputResource("src/test/resources/java/listeners/MouseInputListClass.java");
		analyser.run();
		assertEquals(7, analyser.getCommands().values().size());
		assertEquals(0L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testClassListenerOneMethodCondOneInstanceOf() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondOneInstanceOf.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testClassListenerOneMethodCondOneInstanceOfElse() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondOneInstanceOfElse.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(2L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testClassListenerCondInstanceOfReturn() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondInstanceOfReturn.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(3L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
		assertEquals(17, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineStart());
		assertEquals(18, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineEnd());
		assertEquals(21, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineStart());
		assertEquals(22, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineEnd());
		assertEquals(25, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(2).getLineStart());
		assertEquals(26, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(2).getLineEnd());
	}

	@Test
	public void testClassListenerSimpleDelegation() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerSimpleDelegation.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(2L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
		assertEquals(27, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineStart());
		assertEquals(27, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineEnd());
		assertEquals(12, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineStart());
		assertEquals(12, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineEnd());
	}

	@Test
	public void testClassListenerSwitch() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondSwitch.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(3L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
		assertEquals(18, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineStart());
		assertEquals(19, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineEnd());
		assertEquals(21, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineStart());
		assertEquals(22, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineEnd());
		assertEquals(24, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(2).getLineStart());
		assertEquals(25, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(2).getLineEnd());
	}

	@Test
	public void testClassListenerSwitchHasMainBlock() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondSwitch.java");
		analyser.run();
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertTrue(cmds.get(0).getMainStatmtEntry().isPresent());
		assertTrue(cmds.get(1).getMainStatmtEntry().isPresent());
		assertTrue(cmds.get(2).getMainStatmtEntry().isPresent());
	}

	@Test
	public void testClassNestedIf() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondSimpleNestedIf.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(2L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());

		Command cmd = new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0);

		assertEquals(25, cmd.getLineStart());
		assertEquals(26, cmd.getLineEnd());
		assertEquals(3, cmd.getConditions().size());

		Assert.assertThat(cmd.getConditions().get(0).effectiveStatmt).isEqualTo("(e.getSource()) instanceof javax.swing.JButton");
		Assert.assertThat(cmd.getConditions().get(1).effectiveStatmt).isEqualTo("\"test\".equals(foo)");
		Assert.assertThat(cmd.getConditions().get(2).effectiveStatmt).isEqualTo("isItOkForYou()");

		cmd = new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1);

		assertEquals(29, cmd.getLineStart());
		assertEquals(30, cmd.getLineEnd());
		assertEquals(3, cmd.getConditions().size());

		Assert.assertThat(cmd.getConditions().get(0).effectiveStatmt).isEqualTo("(e.getSource()) instanceof javax.swing.JMenuBar");
		Assert.assertThat(cmd.getConditions().get(1).effectiveStatmt).isEqualTo("\"test\".equals(foo)");
		Assert.assertThat(cmd.getConditions().get(2).effectiveStatmt).isEqualTo("isItOkForYou()");
	}

	@Test
	public void testGetOptimalCodeBlocks() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondSimpleNestedIf.java");
		analyser.run();

		Command cmd = new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0);
		List<CodeBlockPos> blocks = cmd.getOptimalCodeBlocks();

		assertEquals(2, blocks.size());
		assertEquals(21, blocks.get(0).startLine);
		assertEquals(22, blocks.get(0).endLine);
		assertEquals(24, blocks.get(1).startLine);
		assertEquals(26, blocks.get(1).endLine);
	}

	@Test
	public void testNbLinesCommand() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondSimpleNestedIf.java");
		analyser.run();

		Command cmd = new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0);
		assertEquals(4, cmd.getNbLines());
	}

	@Test
	@Ignore
	public void testClassListenerCondInstanceOfEmptyReturn() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondInstanceOfEmptyReturn.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(0L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testClassListenerCondInstanceOfLocalVar() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondInstanceOfLocalVar.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(3L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
		assertEquals(20, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineStart());
		assertEquals(21, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineEnd());
		assertEquals(24, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineStart());
		assertEquals(25, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineEnd());
		assertEquals(28, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(2).getLineStart());
		assertEquals(29, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(2).getLineEnd());
	}

	@Test
	public void testClassListenerFragmentedCommand() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondFragmentedCommand.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(2L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
		assertEquals(18, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineStart());
		assertEquals(21, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(0).getLineEnd());
		assertEquals(25, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineStart());
		assertEquals(26, new ArrayList<>(analyser.getCommands().values()).get(0).getCommand(1).getLineEnd());
	}

	@Test
	public void testRealComplexCommandExample1() {
		analyser.addInputResource("src/test/resources/java/analysers/RealComplexCommandExample1.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(4L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testRealComplexCommandExample1CommandPositions() {
		analyser.addInputResource("src/test/resources/java/analysers/RealComplexCommandExample1.java");
		analyser.run();
		UIListener list = new ArrayList<>(analyser.getCommands().values()).get(0);
		List<CodeBlockPos> blocks = list.getCommand(0).getOptimalCodeBlocks();
		assertEquals(32, blocks.get(0).startLine);
		assertEquals(33, blocks.get(0).endLine);
		assertEquals(35, blocks.get(1).startLine);
		assertEquals(36, blocks.get(1).endLine);
		assertEquals(38, blocks.get(2).startLine);
		assertEquals(43, blocks.get(2).endLine);
	}

	@Test
	public void testRealComplexCommandExample1CommandPositions2() {
		analyser.addInputResource("src/test/resources/java/analysers/RealComplexCommandExample1.java");
		analyser.run();
		UIListener list = new ArrayList<>(analyser.getCommands().values()).get(0);
		List<CodeBlockPos> blocks = list.getCommand(1).getOptimalCodeBlocks();

		assertEquals(32, blocks.get(0).startLine);
		assertEquals(33, blocks.get(0).endLine);
		assertEquals(35, blocks.get(1).startLine);
		assertEquals(36, blocks.get(1).endLine);
		assertEquals(46, blocks.get(2).startLine);
		assertEquals(46, blocks.get(2).endLine);
		assertEquals(48, blocks.get(3).startLine);
		assertEquals(56, blocks.get(3).endLine);
		assertEquals(58, blocks.get(4).startLine);
		assertEquals(58, blocks.get(4).endLine);
		assertEquals(60, blocks.get(5).startLine);
		assertEquals(64, blocks.get(5).endLine);
	}

	@Test
	public void testRealComplexCommandExample1CommandPositions3() {
		analyser.addInputResource("src/test/resources/java/analysers/RealComplexCommandExample1.java");
		analyser.run();
		UIListener cmds = new ArrayList<>(analyser.getCommands().values()).get(0);
		List<CodeBlockPos> blocks = cmds.getCommand(2).getOptimalCodeBlocks();

		assertEquals(32, blocks.get(0).startLine);
		assertEquals(33, blocks.get(0).endLine);
		assertEquals(35, blocks.get(1).startLine);
		assertEquals(36, blocks.get(1).endLine);
		assertEquals(46, blocks.get(2).startLine);
		assertEquals(46, blocks.get(2).endLine);
		assertEquals(48, blocks.get(3).startLine);
		assertEquals(56, blocks.get(3).endLine);
		assertEquals(67, blocks.get(4).startLine);
		assertEquals(71, blocks.get(4).endLine);
	}

	@Test
	public void testRealComplexCommandExample1CommandPositions4() {
		analyser.addInputResource("src/test/resources/java/analysers/RealComplexCommandExample1.java");
		analyser.run();
		UIListener cmds = new ArrayList<>(analyser.getCommands().values()).get(0);
		List<CodeBlockPos> blocks = cmds.getCommand(3).getOptimalCodeBlocks();

		assertEquals(32, blocks.get(0).startLine);
		assertEquals(33, blocks.get(0).endLine);
		assertEquals(35, blocks.get(1).startLine);
		assertEquals(36, blocks.get(1).endLine);
		assertEquals(46, blocks.get(2).startLine);
		assertEquals(46, blocks.get(2).endLine);
		assertEquals(48, blocks.get(3).startLine);
		assertEquals(56, blocks.get(3).endLine);
		assertEquals(58, blocks.get(4).startLine);
		assertEquals(58, blocks.get(4).endLine);
		assertEquals(74, blocks.get(5).startLine);
		assertEquals(79, blocks.get(5).endLine);
	}

	@Test
	public void testSimpleDispatch() {
		analyser.addInputResource("src/test/resources/java/analysers/SimpleDispatch.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertEquals(1L, cmds.size());
		assertEquals(11, cmds.get(0).getMainStatmtEntry().get().getStatmts().get(0).getPosition().getLine());
	}

	@Test
	public void testCommandsInIfElseIfBlocks() {
		analyser.addInputResource("src/test/resources/java/widgetsIdentification/ClassListenerExternal.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(2, new ArrayList<>(analyser.getCommands().values()).get(0).getNbLocalCmds());
	}

	@Test
	public void testCommandsInIfElseIfBlocksHasMainBlock() {
		analyser.addInputResource("src/test/resources/java/widgetsIdentification/ClassListenerExternal.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertTrue(analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList()).get(0).getMainStatmtEntry().isPresent());
		assertTrue(analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList()).get(1).getMainStatmtEntry().isPresent());

	}

	@Test
	public void testFilterOutRegistrationWidgetUsingVarsNbCmd() {
		analyser.addInputResource("src/test/resources/java/widgetsIdentification/FilterOutRegistrationWidgetUsingVars.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1, new ArrayList<>(analyser.getCommands().values()).get(0).getNbLocalCmds());
	}

	@Test
	public void testInsertPSTricksCodeFrameNbCmd() {
		analyser.addInputResource("src/test/resources/java/widgetsIdentification/InsertPSTricksCodeFrame.java");
		analyser.run();
		assertEquals(2, analyser.getCommands().values().size());
		assertEquals(2, new ArrayList<>(analyser.getCommands().values()).get(0).getNbLocalCmds());
		assertEquals(2, new ArrayList<>(analyser.getCommands().values()).get(1).getNbLocalCmds());
	}

	@Test
	public void testMultipleListenerMethodsNbCmd() {
		analyser.addInputResource("src/test/resources/java/listeners/MultipleListenerMethods.java");
		analyser.run();
		assertEquals(2, analyser.getCommands().values().size());
		assertEquals(1, Math.min(new ArrayList<>(analyser.getCommands().values()).get(0).getNbLocalCmds(), new ArrayList<>(analyser.getCommands().values()).get(1).getNbLocalCmds()));
		assertEquals(5, Math.max(new ArrayList<>(analyser.getCommands().values()).get(0).getNbLocalCmds(), new ArrayList<>(analyser.getCommands().values()).get(1).getNbLocalCmds()));
	}

	@Test
	public void testClassListenerSwitchDefault() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondSwitchDefault.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
		assertTrue(analyser.getCommands().values().iterator().next().getCommands().iterator().next().getConditions().get(0).effectiveStatmt instanceof CtBinaryOperator);
	}

	@Test
	public void testClassListenerSwitchDefault2() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondSwitchDefault2.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
		assertTrue(analyser.getCommands().values().iterator().next().getCommands().iterator().next().getConditions().get(0).effectiveStatmt instanceof CtLiteral);
	}

	@Test
	public void testSimpleDispatchMethodNoBody() {
		analyser.addInputResource("src/test/resources/java/analysers/SimpleDispatchMethodNoBody.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testSimpleDispatchMethodNoBody2() {
		analyser.addInputResource("src/test/resources/java/analysers/SimpleDispatchMethodNoBody2.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testSimpleDispatchMethodNoBody2HasMainBlock() {
		analyser.addInputResource("src/test/resources/java/analysers/SimpleDispatchMethodNoBody2.java");
		analyser.run();
		assertTrue(analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList()).get(0).getMainStatmtEntry().isPresent());
	}

	@Test
	public void testFinalBlockAllReturns() {
		analyser.addInputResource("src/test/resources/java/analysers/FinalBlockAllReturns.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
		assertEquals(10, analyser.getCommands().values().iterator().next().getCommands().iterator().next().getLineEnd());
	}

	@Test
	public void testFinalBlockNoReturn() {
		analyser.addInputResource("src/test/resources/java/analysers/FinalBlockNoReturn.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
		assertEquals(3, analyser.getCommands().values().iterator().next().getCommands().iterator().next().getNbLines());
	}

	@Test
	@Ignore
	public void testFinalBlockJustReturn() {
		analyser.addInputResource("src/test/resources/java/analysers/FinalBlockJustReturn.java");
		analyser.run();
		assertEquals(0L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testFinalBlockIsCatch() {
		analyser.addInputResource("src/test/resources/java/analysers/FinalBlockIsCatch.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testFinalBlockIsThrow() {
		analyser.addInputResource("src/test/resources/java/analysers/FinalBlockIsThrow.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testLambdaListenerHasMainCommandBlock() {
		analyser.addInputResource("src/test/resources/java/analysers/LambdaListenerHasMainCommandBlock.java");
		analyser.run();
		assertTrue(analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList()).get(0).getMainStatmtEntry().isPresent());
	}

	@Test
	public void testLocalVarOutsideListener() {
		analyser.addInputResource("src/test/resources/java/analysers/LocalVarOutsideListener.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(3L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertTrue(cmds.get(0).getMainStatmtEntry().isPresent());
		assertTrue(cmds.get(1).getMainStatmtEntry().isPresent());
		assertTrue(cmds.get(2).getMainStatmtEntry().isPresent());
	}

	@Test
	public void testSwitchCaseStrange() {
		analyser.addInputResource("src/test/resources/java/analysers/SwitchCaseStrange.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testActionListenerTwice() {
		analyser.addInputResource("src/test/resources/java/listeners/ActionListenerTwice.java");
		analyser.run();
		assertEquals(2, analyser.getCommands().values().size());
		assertEquals(2L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertTrue(cmds.get(0).getMainStatmtEntry().isPresent());
		assertTrue(cmds.get(1).getMainStatmtEntry().isPresent());
	}

	@Test
	public void testEmptyDispatch() {
		analyser.addInputResource("src/test/resources/java/analysers/EmptyDispatch.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
		Command cmd = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList()).get(0);
		assertEquals(11, cmd.getMainStatmtEntry().get().getLineStart());
		assertEquals(11, cmd.getMainStatmtEntry().get().getLineEnd());
	}

	@Test
	public void testSimpleDispatch2() {
		analyser.addInputResource("src/test/resources/java/analysers/SimpleDispatch2.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertEquals(9, cmds.get(0).getMainStatmtEntry().get().getStatmts().get(0).getPosition().getLine());
	}

	@Test
	public void testCorrectStatements() {
		analyser.addInputResource("src/test/resources/java/analysers/CommandStatements.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
		assertEquals(4, analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).iterator().next().getAllStatmts().size());
	}

	@Test
	public void testCorrectStatementsFromSwitch() {
		analyser.addInputResource("src/test/resources/java/refactoring/E.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(2L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
		assertEquals(4, analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).iterator().next().getAllStatmts().size());
	}

	@Test
	public void testNotSameStatementsCollectedInCommand() {
		analyser.addInputResource("src/test/resources/java/refactoring/I.java");
		analyser.run();
		assertEquals(1L, analyser.getCommands().values().iterator().next().getCommand(0).getAllStatmts().stream().filter(stat -> stat.getPosition().getLine()==23).count());
	}

	@Test
	public void testNestedStatementNotPartOfACommand() {
		analyser.addInputResource("src/test/resources/java/refactoring/I.java");
		analyser.run();
		assertEquals(1L, analyser.getCommands().values().iterator().next().getCommand(0).getAllStatmts().stream().filter(stat -> stat.getPosition().getLine()==26).count());
	}

	@Test
	public void testStrangeListener() {
		analyser.addInputResource("src/test/resources/java/analysers/NoIfStatement.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(2L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testClassWithMultipleListeners() {
		analyser.addInputResource("src/test/resources/java/listeners/MultipleListener.java");
		analyser.run();
		assertEquals(2, analyser.getCommands().values().size());
		assertEquals(3L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testNonDeterministActionCmd() {
		analyser.addInputResource("src/test/resources/java/refactoring/ListenerTab.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(2L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testSuperListenerLocalCmds() {
		analyser.addInputResource("src/test/resources/java/analysers/SuperActionListener.java");
		analyser.run();
		assertEquals(2, analyser.getCommands().values().size());
		assertEquals(3L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbLocalCmds()).sum());
	}

	@Test
	public void testIrrelevantCommandStatements() {
		analyser.addInputResource("src/test/resources/java/analysers/IrrelevantCommandStatements.java");
		analyser.run();
		assertEquals(2, analyser.getCommands().values().size());
		assertEquals(0L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testFalseDispatch() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerLambdaFalseDispatch.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertEquals(13, cmds.get(0).getMainStatmtEntry().get().getLineStart());
		assertEquals(13, cmds.get(0).getMainStatmtEntry().get().getLineEnd());
	}

	@Test
	public void testFalseDispatch2() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerLambdaFalseDispatch2.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertEquals(14, cmds.get(0).getMainStatmtEntry().get().getLineStart());
		assertEquals(16, cmds.get(0).getMainStatmtEntry().get().getLineEnd());
	}

	@Test
	public void testFalseDispatch3() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerFalseDispatch3.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testComplexConditionalStatements() {
		analyser.addInputResource("src/test/resources/java/analysers/ComplexConditionalStatements.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(2L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	@Ignore
	public void testComplexConditionalStatements3() {
		analyser.addInputResource("src/test/resources/java/analysers/ComplexConditionalStatements3.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(3L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testComplexConditionalStatements4() {
		analyser.addInputResource("src/test/resources/java/analysers/ComplexConditionalStatements4.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(2L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testComplexConditionalStatementsPosition() {
		analyser.addInputResource("src/test/resources/java/analysers/ComplexConditionalStatements.java");
		analyser.run();
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertEquals(27, cmds.get(1).getMainStatmtEntry().get().getLineStart());
		assertEquals(30, cmds.get(1).getMainStatmtEntry().get().getLineEnd());
	}

	@Test
	public void testComplexBlob() {
		analyser.addInputResource("src/test/resources/java/refactoring/ComplexBlobs.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(6L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testSwitchCaseWithLog() {
		analyser.addInputResource("src/test/resources/java/analysers/SwitchCaseWithLog.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testUndirectActionCmd() {
		analyser.addInputResource("src/test/resources/java/analysers/UndirectActionCmd.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(3L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testMixedSwitchAndIfStatements() {
		analyser.addInputResource("src/test/resources/java/analysers/MixedSwitchAndIf.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(3L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testMixedSwitchAndIfStatementsCmdPosition() {
		analyser.addInputResource("src/test/resources/java/analysers/MixedSwitchAndIf.java");
		analyser.run();
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).sorted(Comparator.comparing(cmd -> cmd.getLineStart())).collect(Collectors.toList());
		assertEquals(19, cmds.get(0).getMainStatmtEntry().get().getLineStart());
		assertEquals(20, cmds.get(0).getMainStatmtEntry().get().getLineEnd());
		assertEquals(23, cmds.get(1).getMainStatmtEntry().get().getLineStart());
		assertEquals(23, cmds.get(1).getMainStatmtEntry().get().getLineEnd());
		assertEquals(27, cmds.get(2).getMainStatmtEntry().get().getLineStart());
		assertEquals(28, cmds.get(2).getMainStatmtEntry().get().getLineEnd());
	}


	@Test
	public void testCommandComposedOfLocalVarAssignmentOnly() {
		analyser.addInputResource("src/test/resources/java/analysers/FollowedConditionals.java");
		analyser.run();
		Assertions.assertThat(analyser.getCommands().values()).hasSize(1);
		Assertions.assertThat(analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum()).isEqualTo(3L);
	}

	@Test
	public void testLogger() {
		analyser.addInputResource("src/test/resources/java/analysers/Logger.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(2L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testSwitchCasesSameLine() {
		analyser.addInputResource("src/test/resources/java/analysers/SwitchCasesSameLine.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(6L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}


	@Test
	public void testSwitchCasesStartLine() {
		analyser.addInputResource("src/test/resources/java/analysers/SwitchCasesSameLine.java");
		analyser.run();
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).sorted(Comparator.comparing(cmd -> cmd.getLineStart())).collect(Collectors.toList());
		assertEquals(47, cmds.get(0).getAllLocalStatmtsOrdered().get(0).getPosition().getLine());
		assertEquals(47, cmds.get(1).getAllLocalStatmtsOrdered().get(0).getPosition().getLine());
		assertEquals(47, cmds.get(2).getAllLocalStatmtsOrdered().get(0).getPosition().getLine());
		assertEquals(47, cmds.get(3).getAllLocalStatmtsOrdered().get(0).getPosition().getLine());
	}

	@Test
	public void testSharedInheritedCommandsEachListener() {
		analyser.addInputResource("src/test/resources/java/analysers/SharedInheritedCommands.java");
		analyser.run();
		assertEquals(3, analyser.getCommands().values().size());
		List<Map.Entry<CtExecutable<?>, UIListener>> listeners =
			analyser.getCommands().entrySet().stream().sorted(Comparator.comparing(entry -> entry.getKey().getPosition().getLine())).collect(Collectors.toList());
		assertEquals(1, listeners.get(0).getValue().getNbTotalCmds());
		assertEquals(2, listeners.get(1).getValue().getNbTotalCmds());
		assertEquals(3, listeners.get(2).getValue().getNbTotalCmds());
	}

	@Test
	public void testSharedInheritedCommandsTotalListeners() {
		analyser.addInputResource("src/test/resources/java/analysers/SharedInheritedCommands.java");
		analyser.run();
		assertEquals(4L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbLocalCmds()).sum());
	}

	@Test
	public void testSuperSwitchActionListenerGoodStatements() {
		analyser.addInputResource("src/test/resources/java/refactoring/SuperSwitchActionListener.java");
		analyser.run();
		List<Command> cmds = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList());
		assertEquals(2, cmds.get(0).getAllLocalStatmtsOrdered().size());
		assertEquals(2, cmds.get(1).getAllLocalStatmtsOrdered().size());
	}

	@Test
	public void testBreakAtEnd() {
		analyser.addInputResource("src/test/resources/java/refactoring/BreakAtEnd.java");
		analyser.run();
		Command cmd = analyser.getCommands().values().stream().flatMap(c -> c.getCommands().stream()).collect(Collectors.toList()).get(0);
		assertEquals(1, cmd.getStatements().size());
	}

	@Test
	public void testAssert() {
		analyser.addInputResource("src/test/resources/java/analysers/Assert.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(2L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}

	@Test
	public void testRecursiveAnalysis() {
		analyser.addInputResource("src/test/resources/java/analysers/RecursiveAnalysis.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().mapToLong(c -> c.getNbTotalCmds()).sum());
	}
}
