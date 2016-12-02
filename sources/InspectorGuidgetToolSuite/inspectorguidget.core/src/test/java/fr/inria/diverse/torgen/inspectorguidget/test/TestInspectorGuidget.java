package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonStructurePrinter;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.junit.Before;
import spoon.compiler.SpoonCompiler;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import static org.junit.Assert.fail;

public abstract class TestInspectorGuidget<T extends Processor<? extends CtElement>> {
	protected Collection<T> processors;
	protected SpoonCompiler modelBuilder;

	public static final boolean SHOW_MODEL = false;

	public static final Handler HANDLER_FAIL = new Handler() {
		@Override
		public void publish(final LogRecord record) {
			if(record.getLevel() == Level.SEVERE) {
				fail(record.getMessage());
			}
		}

		@Override
		public void flush() {
		}

		@Override
		public void close() throws SecurityException {
		}
	};

	@Before
	public void setUp() {
		final StandardEnvironment evt = new StandardEnvironment();
		evt.setComplianceLevel(8);
		modelBuilder = new JDTBasedSpoonCompiler(new FactoryImpl(new DefaultCoreFactory(), evt));
		processors = createProcessor();
//		Launcher.LOGGER.setLevel(Level.OFF);
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
