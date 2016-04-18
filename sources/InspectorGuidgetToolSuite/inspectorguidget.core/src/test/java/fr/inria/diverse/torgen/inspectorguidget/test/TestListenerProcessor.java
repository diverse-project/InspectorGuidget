package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.listener.AWTListenerClass;
import fr.inria.diverse.torgen.inspectorguidget.processor.ClassListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.LambdaListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.ListenerProcessor;
import org.junit.Before;
import org.junit.Test;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TestListenerProcessor extends TestInspectorGuidget<ListenerProcessor<? extends CtElement>> implements AWTListenerClass {
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
	public void testSwingMouseListernerAsClassImplementingInterface() {
		run("src/test/resources/java/listeners/MouseListClass.java");
		assertEquals(1, clazzListener.size());
	}

	@Test
	public void testSwingMouseListernerAsAnonClass() {
		run("src/test/resources/java/listeners/MouseListAnonClass.java");
		assertEquals(1, clazzListener.size());
	}

	@Test
	public void testSwingMouseListernerAsClassWithInheritance() {
		run("src/test/resources/java/listeners/MouseListAnonClassWithInheritance.java");
		assertEquals(2, clazzListener.size());
	}

	@Test
	public void testSwingMouseListernerAsAnonClassWithInheritance() {
		run("src/test/resources/java/listeners/MouseListClassInheritance.java");
		assertEquals(2, clazzListener.size());
	}

	@Test
	public void testSwingMouseListernerTwoSameClasses() {
		run("src/test/resources/java/listeners/MouseListTwoSameClasses.java");
		assertEquals(2, clazzListener.size());
		assertEquals(10, classProc.getAllListenerMethods().size());
	}

	@Override
	public List<ListenerProcessor<? extends CtElement>> createProcessor() {
		lambdaProc = new LambdaListenerProcessor();
		classProc = new ClassListenerProcessor();
		return Arrays.asList(classProc, lambdaProc);
	}

	@Override
	public void onAWTListenerClass(final CtClass<?> clazz) {
		clazzListener.add(clazz);
	}

	@Override
	public void onAWTListenerLambda(final CtLambda<?> lambda) {
		lambdaListener.add(lambda);
	}
}
