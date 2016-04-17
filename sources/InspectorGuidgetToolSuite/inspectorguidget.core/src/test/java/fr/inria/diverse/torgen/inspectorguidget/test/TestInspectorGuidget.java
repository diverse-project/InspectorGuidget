package fr.inria.diverse.torgen.inspectorguidget.test;

import org.junit.Before;
import spoon.compiler.SpoonCompiler;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.io.File;
import java.util.Collection;
import java.util.List;

public abstract class TestInspectorGuidget<T extends Processor<? extends CtElement>> {
	protected List<T> processors;
	protected SpoonCompiler modelBuilder;

	public static final boolean SHOW_MODEL = false;

	@Before
	public void setUp() {
		final StandardEnvironment evt = new StandardEnvironment();
		evt.setComplianceLevel(8);
		evt.setPreserveLineNumbers(true);
		modelBuilder = new JDTBasedSpoonCompiler(new FactoryImpl(new DefaultCoreFactory(), evt));
		processors = createProcessor();
	}

	public abstract List<T> createProcessor();

	public void run(final String srcPath) {
		modelBuilder.addInputSource(new File(srcPath));
		modelBuilder.build();

		if(SHOW_MODEL) {
			final Factory factory = modelBuilder.getFactory();
			System.out.println("interfaces:");
			factory.Interface().getAll().forEach(System.out::println);
			System.out.println("classes:");
			factory.Class().getAll().forEach(System.out::println);
			System.out.println("types:");
			factory.Type().getAll().forEach(System.out::println);
		}

		modelBuilder.process((Collection<Processor<? extends CtElement>>) processors);
	}
}
