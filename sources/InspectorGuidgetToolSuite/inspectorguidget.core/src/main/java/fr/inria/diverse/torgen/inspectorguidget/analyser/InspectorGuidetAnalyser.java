package fr.inria.diverse.torgen.inspectorguidget.analyser;

import fr.inria.diverse.torgen.inspectorguidget.helper.ExecArg;
import fr.inria.diverse.torgen.inspectorguidget.helper.LoggingHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.SpoonAPI;
import spoon.compiler.Environment;
import spoon.processing.Processor;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.visitor.Filter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

public abstract class InspectorGuidetAnalyser implements SpoonAPI {
	public static final @NotNull Logger LOG = Logger.getLogger("InspectorGuidget analysers");

	static {
		LOG.setLevel(LoggingHelper.INSTANCE.loggingLevel);
	}

	protected final @NotNull JDTBasedSpoonCompiler modelBuilder;
	protected final @NotNull List<Processor<?>> processors;
	protected StandardEnvironment env;
	protected  Factory factory;

	public InspectorGuidetAnalyser(final @NotNull Collection<Processor<?>> procs) {
		super();
		processors = new ArrayList<>();
		procs.forEach(pr -> addProcessor(pr));
		modelBuilder = createCompiler();
	}

	public InspectorGuidetAnalyser(final @NotNull Collection<Processor<?>> procs, final @NotNull JDTBasedSpoonCompiler builder) {
		super();
		processors = new ArrayList<>();
		procs.forEach(pr -> addProcessor(pr));
		modelBuilder = builder;
	}

	@Override
	public CtModel getModel() {
		return modelBuilder.getFactory().getModel();
	}

	@Override
	public void run(final @Nullable String[] args) {
		if(args!=null) {
			new ExecArg().parse(args, this);
		}
		buildModel();
		process();
	}

	@Override
	public void run() {
		run(null);
	}


	public @NotNull JDTBasedSpoonCompiler getModelBuilder() {
		return modelBuilder;
	}

	public void setSourceClasspath(final String ... args) {
		modelBuilder.setSourceClasspath(args);
	}

	@Override
	public void addInputResource(final @NotNull String file) {
		modelBuilder.addInputSource(new File(file));
	}

	@Override
	public void setSourceOutputDirectory(final String path) {

	}

	@Override
	public void setSourceOutputDirectory(final File outputDirectory) {

	}

	@Override
	public void setOutputFilter(final Filter<CtType<?>> typeFilter) {

	}

	@Override
	public void setOutputFilter(final String... qualifedNames) {

	}

	@Override
	public void setBinaryOutputDirectory(final String path) {

	}

	@Override
	public void setBinaryOutputDirectory(final File outputDirectory) {

	}

	@Override
	public void addProcessor(final String name) {
	}

	@Override
	public <T extends CtElement> void addProcessor(final Processor<T> processor) {
		processors.add(processor);
	}

	@Override
	public CtModel buildModel() {
		modelBuilder.build();
		return modelBuilder.getFactory().getModel();
	}

	@Override
	public void process() {
		modelBuilder.process(processors);
	}

	@Override
	public void prettyprint() {
		System.out.println(this);
	}

	@Override
	public @Nullable Factory getFactory() {
		return factory;
	}

	@Override
	public @Nullable Environment getEnvironment() {
		return env;
	}

	@Override
	public @NotNull Factory createFactory() {
		factory = new FactoryImpl(new DefaultCoreFactory(), createEnvironment());;
		return factory;
	}

	@Override
	public @NotNull Environment createEnvironment() {
		env = new StandardEnvironment();
		env.setCommentEnabled(false);
		env.setComplianceLevel(8);
		env.setShouldCompile(false);
		return env;
	}

	@Override
	public @NotNull JDTBasedSpoonCompiler createCompiler() {
		return new JDTBasedSpoonCompiler(createFactory());
	}
}
