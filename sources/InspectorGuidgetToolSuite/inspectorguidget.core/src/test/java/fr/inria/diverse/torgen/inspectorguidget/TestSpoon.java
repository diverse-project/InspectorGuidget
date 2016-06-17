package fr.inria.diverse.torgen.inspectorguidget;

import org.junit.Ignore;
import org.junit.Test;
import spoon.compiler.SpoonCompiler;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.io.File;
import java.util.Collections;


class TestProcessor extends AbstractProcessor<CtExecutable<?>> {
	@Override
	public void process(CtExecutable<?> element) {
//		System.out.println(element.getVariable().getDeclaration());
	}
}

public class TestSpoon {
	@Test
	@Ignore
	public void testSpoon() {
		final StandardEnvironment evt = new StandardEnvironment();
		evt.setComplianceLevel(8);
		SpoonCompiler modelBuilder = new JDTBasedSpoonCompiler(new FactoryImpl(new DefaultCoreFactory(), evt));
		TestProcessor processor = new TestProcessor();
		modelBuilder.addInputSource(new File("src/test/resources/java/widgetsIdentification/LambdaOnFieldWidgetsEqualCond.java"));
		modelBuilder.build();
		modelBuilder.process(Collections.singletonList(processor));
	}
}
