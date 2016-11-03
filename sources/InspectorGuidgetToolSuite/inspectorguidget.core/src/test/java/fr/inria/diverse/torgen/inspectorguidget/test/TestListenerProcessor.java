package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.processor.ClassListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.InspectorGuidgetProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.LambdaListenerProcessor;
import org.junit.BeforeClass;
import org.junit.Test;
import spoon.reflect.declaration.CtElement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TestListenerProcessor extends TestInspectorGuidget<InspectorGuidgetProcessor<? extends CtElement>> {
	private LambdaListenerProcessor lambdaProc;
	private ClassListenerProcessor classProc;

	@BeforeClass
	public static void setUpBeforeClass() {
		InspectorGuidgetProcessor.LOG.addHandler(TestInspectorGuidget.HANDLER_FAIL);
	}

	@Override
	public List<InspectorGuidgetProcessor<? extends CtElement>> createProcessor() {
		lambdaProc = new LambdaListenerProcessor();
		classProc = new ClassListenerProcessor();
		return Arrays.asList(classProc, lambdaProc);
	}


	@Test
	public void testActionListenerAsLambda() {
		run("src/test/resources/java/listeners/ActionListenerLambda.java");
		assertEquals(1, lambdaProc.getAllListenerLambdas().size());
		assertEquals(0, classProc.getAllListenerMethods().size());
	}

	@Test
	public void testActionListenerAsLambdaInheritance() {
		run("src/test/resources/java/listeners/ActionListenerLambdaInheritance.java");
		assertEquals(1, lambdaProc.getAllListenerLambdas().size());
	}

	@Test
	public void testActionListenerAsLambdaInheritanceDefaultMethod() {
		run("src/test/resources/java/listeners/ListenerLambdaInheritanceDefault.java");
		assertEquals(1, lambdaProc.getAllListenerLambdas().size());
	}

	@Test
	public void testSwingCaretListenerAsLambda() {
		run("src/test/resources/java/listeners/CaretListenerLambda.java");
		assertEquals(1, lambdaProc.getAllListenerLambdas().size());
	}

	@Test
	public void testJFXHandlerAsClass() {
		run("src/test/resources/java/listeners/JFXEventHandlerClass.java");
		assertEquals(1, classProc.getAllListenerMethods().size());
		assertEquals(1, classProc.getAllListenerMethods().keySet().size());
		assertEquals(1, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}

	@Test
	public void testJFXEventHandlerAsLambda() {
		run("src/test/resources/java/listeners/JFXEventHandlerLambda.java");
		assertEquals(1, lambdaProc.getAllListenerLambdas().size());
	}

	@Test
	public void testAWTMouseListernerAsClassImplementingInterface() {
		run("src/test/resources/java/listeners/MouseListClass.java");
		assertEquals(1, classProc.getAllListenerMethods().size());
		assertEquals(1, classProc.getAllListenerMethods().keySet().size());
		assertEquals(5, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}


	@Test
	public void testSwingMouseInputListernerAsClassImplementingInterface() {
		run("src/test/resources/java/listeners/MouseInputListClass.java");
		assertEquals(1, classProc.getAllListenerMethods().size());
		assertEquals(1, classProc.getAllListenerMethods().keySet().size());
		assertEquals(7, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}

	@Test
	public void testSwingMouseListernerAsAnonClass() {
		run("src/test/resources/java/listeners/MouseListAnonClass.java");
		assertEquals(1, classProc.getAllListenerMethods().size());
		assertEquals(1, classProc.getAllListenerMethods().keySet().size());
		assertEquals(5, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}

	@Test
	public void testSwingMouseListernerAsClassWithInheritance() {
		run("src/test/resources/java/listeners/MouseListAnonClassWithInheritance.java");
		assertEquals(2, classProc.getAllListenerMethods().size());
		assertEquals(2, classProc.getAllListenerMethods().keySet().size());
		assertEquals(6, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}

	@Test
	public void testSwingMouseListernerAsAnonClassWithInheritance() {
		run("src/test/resources/java/listeners/MouseListClassInheritance.java");
		assertEquals(2, classProc.getAllListenerMethods().size());
		assertEquals(2, classProc.getAllListenerMethods().keySet().size());
		assertEquals(6, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}

	@Test
	public void testSwingMouseListernerTwoSameClasses() {
		run("src/test/resources/java/listeners/MouseListTwoSameClasses.java");
		assertEquals(2, classProc.getAllListenerMethods().size());
		assertEquals(2, classProc.getAllListenerMethods().keySet().size());
		assertEquals(10, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}

	@Test
	public void testActionListenerClassEmpty() {
		run("src/test/resources/java/analysers/ActionListenerEmptyClass.java");
		assertEquals(1, classProc.getAllListenerMethods().size());
		assertEquals(1, classProc.getAllListenerMethods().keySet().size());
		assertEquals(1, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}

	@Test
	public void testMouseListClassInheritanceAbsract() {
		run("src/test/resources/java/listeners/MouseListClassInheritanceAbsract.java");
		assertEquals(2, classProc.getAllListenerMethods().size());
		assertEquals(5, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}

	@Test
	public void testAbstractActionNotAListener() {
		run("src/test/resources/java/listeners/AbstractAction.java");
		assertEquals(0, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
		assertEquals(0, classProc.getAllListenerMethods().size());
	}

	@Test
	public void testAWTDragListener() {
		run("src/test/resources/java/listeners/AWTDragListener.java");
		assertEquals(4, classProc.getAllListenerMethods().size());
		assertEquals(12, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}


	@Test
	public void testClassWithMultipleListeners() {
		run("src/test/resources/java/listeners/MultipleListener.java");
		assertEquals(1, classProc.getAllListenerMethods().size());
		assertEquals(2L, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}
}
