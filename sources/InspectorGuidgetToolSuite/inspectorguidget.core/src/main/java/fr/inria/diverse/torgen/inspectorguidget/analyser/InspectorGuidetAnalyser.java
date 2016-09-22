package fr.inria.diverse.torgen.inspectorguidget.analyser;

import fr.inria.diverse.torgen.inspectorguidget.helper.ExecArg;
import fr.inria.diverse.torgen.inspectorguidget.helper.LoggingHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.SpoonAPI;
import spoon.compiler.Environment;
import spoon.compiler.SpoonCompiler;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

public abstract class InspectorGuidetAnalyser implements SpoonAPI {
	public static final @NotNull Logger LOG = Logger.getLogger("InspectorGuidget analysers");

	static {
		LOG.setLevel(LoggingHelper.INSTANCE.loggingLevel);
	}

	protected final @NotNull SpoonCompiler modelBuilder;
	protected final @NotNull List<Processor<?>> processors;

	public InspectorGuidetAnalyser(final @NotNull Collection<Processor<?>> procs) {
		super();
		processors = new ArrayList<>();
		procs.forEach(pr -> addProcessor(pr));
		modelBuilder = createCompiler();
	}

	public InspectorGuidetAnalyser(final @NotNull Collection<Processor<?>> procs, final @NotNull SpoonCompiler builder) {
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
	public void run(@Nullable String[] args) {
		if(args!=null)
			new ExecArg().parse(args, this);
		buildModel();
		process();
	}

	@Override
	public void run() {
		run(null);
	}


	public @NotNull SpoonCompiler getModelBuilder() {
		return modelBuilder;
	}

	public void setSourceClasspath(String ... args) {
		modelBuilder.setSourceClasspath(args);
	}

	@Override
	public void addInputResource(@NotNull String file) {
		modelBuilder.addInputSource(new File(file));
	}

	@Override
	public void setSourceOutputDirectory(String path) {

	}

	@Override
	public void setSourceOutputDirectory(File outputDirectory) {

	}

	@Override
	public void setOutputFilter(Filter<CtType<?>> typeFilter) {

	}

	@Override
	public void setOutputFilter(String... qualifedNames) {

	}

	@Override
	public void setBinaryOutputDirectory(String path) {

	}

	@Override
	public void setBinaryOutputDirectory(File outputDirectory) {

	}

	@Override
	public void addProcessor(String name) {
	}

	@Override
	public <T extends CtElement> void addProcessor(Processor<T> processor) {
		processors.add(processor);
	}

	@Override
	public void buildModel() {
		modelBuilder.build();
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
		return null;
	}

	@Override
	public @Nullable Environment getEnvironment() {
		return null;
	}

	@Override
	public @NotNull Factory createFactory() {
		return new FactoryImpl(new DefaultCoreFactory(), createEnvironment());
	}

	@Override
	public @NotNull Environment createEnvironment() {
		StandardEnvironment evt = new StandardEnvironment();
		evt.setCommentEnabled(false);
		evt.setComplianceLevel(8);
		return evt;
	}

	@Override
	public @NotNull SpoonCompiler createCompiler() {
		return new JDTBasedSpoonCompiler(createFactory());
	}
}
