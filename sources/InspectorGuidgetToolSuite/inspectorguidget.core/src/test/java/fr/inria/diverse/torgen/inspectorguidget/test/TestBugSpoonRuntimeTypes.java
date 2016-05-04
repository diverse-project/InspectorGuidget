package fr.inria.diverse.torgen.inspectorguidget.test;


import org.junit.Test;
import spoon.compiler.SpoonCompiler;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.visitor.filter.InvocationFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.io.File;
import java.util.Collections;

class TestProcessor extends AbstractProcessor<CtMethod<?>> {
	@Override
	public void process(CtMethod<?> element) {
		element.getFactory().Package().getRootPackage().getElements(new InvocationFilter(element));
	}
}

public class TestBugSpoonRuntimeTypes {
	@Test
	public void testSpoon() {
		SpoonCompiler modelBuilder = new JDTBasedSpoonCompiler(new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment()));
		TestProcessor processor = new TestProcessor();
		modelBuilder.addInputSource(new File("src/test/resources/java/SpoonTestCode.java"));
		modelBuilder.build();
		modelBuilder.process(Collections.singletonList(processor));
	}
}
