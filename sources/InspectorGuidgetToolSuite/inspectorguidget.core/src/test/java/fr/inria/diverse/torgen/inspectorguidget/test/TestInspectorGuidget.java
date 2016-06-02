package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonStructurePrinter;
import org.junit.Before;
import spoon.compiler.SpoonCompiler;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public abstract class TestInspectorGuidget<T extends Processor<? extends CtElement>> {
	protected Collection<T> processors;
	protected SpoonCompiler modelBuilder;

	public static final boolean SHOW_MODEL = true;

	@Before
	public void setUp() {
		final StandardEnvironment evt = new StandardEnvironment();
		evt.setComplianceLevel(8);
		modelBuilder = new JDTBasedSpoonCompiler(new FactoryImpl(new DefaultCoreFactory(), evt));
		processors = createProcessor();
	}

	protected abstract Collection<T> createProcessor();

	protected void run(final String... srcPath) {
		Arrays.stream(srcPath).forEach(src -> modelBuilder.addInputSource(new File(src)));
		modelBuilder.build();

		if(SHOW_MODEL) {
			SpoonStructurePrinter printer = new SpoonStructurePrinter();
			printer.scan(Collections.singletonList(modelBuilder.getFactory().Package().getRootPackage()));
		}

		modelBuilder.process((Collection<Processor<? extends CtElement>>) processors);
	}
}
