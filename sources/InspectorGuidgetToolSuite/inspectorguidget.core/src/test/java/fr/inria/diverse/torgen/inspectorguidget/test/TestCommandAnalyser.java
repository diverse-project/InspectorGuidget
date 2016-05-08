package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.analyser.Command;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonStructurePrinter;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static spoon.testing.Assert.assertThat;

public class TestCommandAnalyser {
	CommandAnalyser analyser;

	@Before
	public void setUp() {
		analyser = new CommandAnalyser();
	}

	@After
	public void tearsDown() {
		SpoonStructurePrinter printer = new SpoonStructurePrinter();
		printer.scan(Collections.singletonList(analyser.getModelBuilder().getFactory().Package().getRootPackage()));
	}

	@Test
	public void testEmptyClassListenerMethodNoCommand() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerEmptyClass.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(0L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}

	@Test
	public void testEmptyLambdaListenerMethodNoCommand() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerEmptyLambda.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(0L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}

	@Test
	public void testClassListenerMethodNoConditional() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerNoConditClass.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}

	@Test
	public void testLambdaListenerMethodNoConditional() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerNoConditLambda.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}

	@Test
	public void testClassListenerMethodNoCondMultipleMethodsButOneUsed() {
		analyser.addInputResource("src/test/resources/java/analysers/MouseInputListOneMethodUsed.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}

	@Test
	public void testClassListenerMethodNoCodeMultipleMethods() {
		analyser.addInputResource("src/test/resources/java/listeners/MouseInputListClass.java");
		analyser.run();
		assertEquals(7, analyser.getCommands().values().size());
		assertEquals(0L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}

	@Test
	public void testClassListenerOneMethodCondOneInstanceOf() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondOneInstanceOf.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(1L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}

	@Test
	public void testClassListenerOneMethodCondOneInstanceOfElse() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondOneInstanceOfElse.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(2L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}

	@Test
	public void testClassListenerCondInstanceOfReturn() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondInstanceOfReturn.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(3L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
		assertEquals(17, new ArrayList<>(analyser.getCommands().values()).get(0).get(0).getLineStart());
		assertEquals(17, new ArrayList<>(analyser.getCommands().values()).get(0).get(0).getLineEnd());
		assertEquals(21, new ArrayList<>(analyser.getCommands().values()).get(0).get(1).getLineStart());
		assertEquals(21, new ArrayList<>(analyser.getCommands().values()).get(0).get(1).getLineEnd());
		assertEquals(25, new ArrayList<>(analyser.getCommands().values()).get(0).get(2).getLineStart());
		assertEquals(25, new ArrayList<>(analyser.getCommands().values()).get(0).get(2).getLineEnd());
	}

	@Test
	public void testClassListenerSimpleDelegation() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerSimpleDelegation.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(2L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
		assertEquals(27, new ArrayList<>(analyser.getCommands().values()).get(0).get(0).getLineStart());
		assertEquals(27, new ArrayList<>(analyser.getCommands().values()).get(0).get(0).getLineEnd());
		assertEquals(12, new ArrayList<>(analyser.getCommands().values()).get(0).get(1).getLineStart());
		assertEquals(12, new ArrayList<>(analyser.getCommands().values()).get(0).get(1).getLineEnd());
	}

	@Test
	public void testClassListenerSwitch() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondSwitch.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(3L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
		assertEquals(18, new ArrayList<>(analyser.getCommands().values()).get(0).get(0).getLineStart());
		assertEquals(18, new ArrayList<>(analyser.getCommands().values()).get(0).get(0).getLineEnd());
		assertEquals(21, new ArrayList<>(analyser.getCommands().values()).get(0).get(1).getLineStart());
		assertEquals(21, new ArrayList<>(analyser.getCommands().values()).get(0).get(1).getLineEnd());
		assertEquals(24, new ArrayList<>(analyser.getCommands().values()).get(0).get(2).getLineStart());
		assertEquals(24, new ArrayList<>(analyser.getCommands().values()).get(0).get(2).getLineEnd());
	}

	@Test
	public void testClassNestedIf() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondSimpleNestedIf.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(2L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());

		Command cmd = new ArrayList<>(analyser.getCommands().values()).get(0).get(0);

		assertEquals(25, cmd.getLineStart());
		assertEquals(25, cmd.getLineEnd());
		assertEquals(3, cmd.getConditions().size());

		assertThat(cmd.getConditions().get(0)).isEqualTo("(e.getSource()) instanceof javax.swing.JButton");
		assertThat(cmd.getConditions().get(1)).isEqualTo("\"test\".equals(foo)");
		assertThat(cmd.getConditions().get(2)).isEqualTo("isItOkForYou()");

		cmd = new ArrayList<>(analyser.getCommands().values()).get(0).get(1);

		assertEquals(29, cmd.getLineStart());
		assertEquals(29, cmd.getLineEnd());
		assertEquals(3, cmd.getConditions().size());

		assertThat(cmd.getConditions().get(0)).isEqualTo("(e.getSource()) instanceof javax.swing.JMenuBar");
		assertThat(cmd.getConditions().get(1)).isEqualTo("\"test\".equals(foo)");
		assertThat(cmd.getConditions().get(2)).isEqualTo("isItOkForYou()");
	}

	@Test
	public void testClassListenerCondInstanceOfEmptyReturn() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondInstanceOfEmptyReturn.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(0L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
	}

	@Test
	@Ignore
	public void testClassListenerCondInstanceOfLocalVar() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondInstanceOfLocalVar.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(3L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
		assertEquals(20, new ArrayList<>(analyser.getCommands().values()).get(0).get(0).getLineStart());
		assertEquals(20, new ArrayList<>(analyser.getCommands().values()).get(0).get(0).getLineEnd());
		assertEquals(24, new ArrayList<>(analyser.getCommands().values()).get(0).get(1).getLineStart());
		assertEquals(24, new ArrayList<>(analyser.getCommands().values()).get(0).get(1).getLineEnd());
		assertEquals(28, new ArrayList<>(analyser.getCommands().values()).get(0).get(2).getLineStart());
		assertEquals(28, new ArrayList<>(analyser.getCommands().values()).get(0).get(2).getLineEnd());
	}

	@Test
	public void testClassListenerFragmentedCommand() {
		analyser.addInputResource("src/test/resources/java/analysers/ActionListenerCondFragmentedCommand.java");
		analyser.run();
		assertEquals(1, analyser.getCommands().values().size());
		assertEquals(2L, analyser.getCommands().values().stream().flatMap(c -> c.stream()).count());
		assertEquals(15, new ArrayList<>(analyser.getCommands().values()).get(0).get(0).getLineStart());
		assertEquals(21, new ArrayList<>(analyser.getCommands().values()).get(0).get(0).getLineEnd());
		assertEquals(15, new ArrayList<>(analyser.getCommands().values()).get(0).get(1).getLineStart());
		assertEquals(26, new ArrayList<>(analyser.getCommands().values()).get(0).get(1).getLineEnd());
	}
}
