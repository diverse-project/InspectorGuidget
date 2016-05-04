package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.listener.AWTListenerClass;
import fr.inria.diverse.torgen.inspectorguidget.listener.JFXListenerClass;
import fr.inria.diverse.torgen.inspectorguidget.listener.SwingListenerClass;
import fr.inria.diverse.torgen.inspectorguidget.processor.ClassListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.LambdaListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.ListenerProcessor;
import org.junit.Before;
import org.junit.Test;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TestListenerProcessor extends TestInspectorGuidget<ListenerProcessor<? extends CtElement>>
		implements AWTListenerClass, SwingListenerClass, JFXListenerClass {
	private Set<CtClass<?>> clazzListener;
	private Set<CtLambda<?>> lambdaListener;
	private LambdaListenerProcessor lambdaProc;
	private ClassListenerProcessor classProc;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		clazzListener = new HashSet<>();
		lambdaListener= new HashSet<>();
		processors.forEach(p -> p.addAWTClassListener(this));
		processors.forEach(p -> p.addSwingClassListener(this));
		processors.forEach(p -> p.addJFXClassListener(this));
	}

	@Test
	public void testActionListenerAsLambda() {
		run("src/test/resources/java/listeners/ActionListenerLambda.java");
		assertEquals(1, lambdaListener.size());
	}

	@Test
	public void testActionListenerAsLambdaInheritance() {
		run("src/test/resources/java/listeners/ActionListenerLambdaInheritance.java");
		assertEquals(1, lambdaListener.size());
	}

	@Test
	public void testActionListenerAsLambdaInheritanceDefaultMethod() {
		run("src/test/resources/java/listeners/ListenerLambdaInheritanceDefault.java");
		assertEquals(1, lambdaListener.size());
	}

	@Test
	public void testSwingCaretListenerAsLambda() {
		run("src/test/resources/java/listeners/CaretListenerLambda.java");
		assertEquals(1, lambdaListener.size());
	}

	@Test
	public void testJFXHandlerAsClass() {
		run("src/test/resources/java/listeners/JFXEventHandlerClass.java");
		assertEquals(1, clazzListener.size());
		assertEquals(1, classProc.getAllListenerMethods().keySet().size());
		assertEquals(1, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}

	@Test
	public void testJFXEventHandlerAsLambda() {
		run("src/test/resources/java/listeners/JFXEventHandlerLambda.java");
		assertEquals(1, lambdaListener.size());
	}

	@Test
	public void testAWTMouseListernerAsClassImplementingInterface() {
		run("src/test/resources/java/listeners/MouseListClass.java");
		assertEquals(1, clazzListener.size());
		assertEquals(1, classProc.getAllListenerMethods().keySet().size());
		assertEquals(5, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}


	@Test
	public void testSwingMouseInputListernerAsClassImplementingInterface() {
		run("src/test/resources/java/listeners/MouseInputListClass.java");
		assertEquals(1, clazzListener.size());
		assertEquals(1, classProc.getAllListenerMethods().keySet().size());
		assertEquals(7, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}

	@Test
	public void testSwingMouseListernerAsAnonClass() {
		run("src/test/resources/java/listeners/MouseListAnonClass.java");
		assertEquals(1, clazzListener.size());
		assertEquals(1, classProc.getAllListenerMethods().keySet().size());
		assertEquals(5, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}

	@Test
	public void testSwingMouseListernerAsClassWithInheritance() {
		run("src/test/resources/java/listeners/MouseListAnonClassWithInheritance.java");
		assertEquals(2, clazzListener.size());
		assertEquals(2, classProc.getAllListenerMethods().keySet().size());
		assertEquals(6, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}

	@Test
	public void testSwingMouseListernerAsAnonClassWithInheritance() {
		run("src/test/resources/java/listeners/MouseListClassInheritance.java");
		assertEquals(2, clazzListener.size());
		assertEquals(2, classProc.getAllListenerMethods().keySet().size());
		assertEquals(6, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}

	@Test
	public void testSwingMouseListernerTwoSameClasses() {
		run("src/test/resources/java/listeners/MouseListTwoSameClasses.java");
		assertEquals(2, clazzListener.size());
		assertEquals(2, classProc.getAllListenerMethods().keySet().size());
		assertEquals(10, classProc.getAllListenerMethods().values().stream().flatMap(c -> c.stream()).collect(Collectors.toList()).size());
	}

	@Override
	public List<ListenerProcessor<? extends CtElement>> createProcessor() {
		lambdaProc = new LambdaListenerProcessor();
		classProc = new ClassListenerProcessor();
		return Arrays.asList(classProc, lambdaProc);
	}

	@Override
	public void onAWTListenerClass(final CtClass<?> clazz, Set<CtMethod<?>> methods) {
		clazzListener.add(clazz);
	}

	@Override
	public void onAWTListenerLambda(final CtLambda<?> lambda) {
		lambdaListener.add(lambda);
	}

	@Override
	public void onSwingListenerClass(final CtClass<?> clazz, Set<CtMethod<?>> methods) {
		clazzListener.add(clazz);
	}

	@Override
	public void onSwingListenerLambda(final CtLambda<?> lambda) {
		lambdaListener.add(lambda);
	}

	@Override
	public void onJFXListenerClass(final CtClass<?> clazz, Set<CtMethod<?>> methods) {
		clazzListener.add(clazz);
	}

	@Override
	public void onJFXListenerLambda(final CtLambda<?> lambda) {
		lambdaListener.add(lambda);
	}

	@Override
	public void onJFXFXMLAnnotationOnField(final CtField<?> field) {
	}

	@Override
	public void onJFXFXMLAnnotationOnMethod(final CtMethod<?> method) {
	}
}
