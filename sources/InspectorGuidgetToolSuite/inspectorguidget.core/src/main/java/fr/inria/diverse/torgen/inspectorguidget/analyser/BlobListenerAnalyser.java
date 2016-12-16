package fr.inria.diverse.torgen.inspectorguidget.analyser;

import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import spoon.SpoonAPI;
import spoon.compiler.Environment;
import spoon.compiler.SpoonCompiler;
import spoon.processing.Processor;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Filter;

public class BlobListenerAnalyser implements SpoonAPI {
	private static int NB_CMDS = 3;

	private final @NotNull CommandAnalyser cmdAnalyser;
	private Map<CtExecutable<?>, UIListener> blobs;

	public BlobListenerAnalyser() {
		super();
		cmdAnalyser = new CommandAnalyser();
	}


	@Override
	public void run(final String[] args) {
		buildModel();
		process();
	}

	@Override
	public void addInputResource(final String file) {
		cmdAnalyser.addInputResource(file);
	}

	@Override
	public void setSourceOutputDirectory(final String path) {
		cmdAnalyser.setSourceOutputDirectory(path);
	}

	@Override
	public void setSourceOutputDirectory(final File outputDirectory) {
		cmdAnalyser.setSourceOutputDirectory(outputDirectory);
	}

	@Override
	public void setOutputFilter(final Filter<CtType<?>> typeFilter) {
		cmdAnalyser.setOutputFilter(typeFilter);
	}

	@Override
	public void setOutputFilter(final String... qualifedNames) {
		cmdAnalyser.setOutputFilter(qualifedNames);
	}

	@Override
	public void setBinaryOutputDirectory(final String path) {
		cmdAnalyser.setBinaryOutputDirectory(path);
	}

	@Override
	public void setBinaryOutputDirectory(final File outputDirectory) {
		cmdAnalyser.setBinaryOutputDirectory(outputDirectory);
	}

	@Override
	public void addProcessor(final String name) {
		cmdAnalyser.addProcessor(name);
	}

	@Override
	public <T extends CtElement> void addProcessor(final Processor<T> processor) {
		cmdAnalyser.addProcessor(processor);
	}

	@Override
	public void buildModel() {
		cmdAnalyser.buildModel();
	}

	@Override
	public void process() {
		cmdAnalyser.process();
		blobs = cmdAnalyser.getCommands().entrySet().parallelStream().
			filter(entry -> entry.getValue().getNbTotalCmds() >= NB_CMDS).
			collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
	}

	@Override
	public void prettyprint() {
		cmdAnalyser.prettyprint();
	}

	@Override
	public void run() {
		run(null);
	}

	@Override
	public Factory getFactory() {
		return cmdAnalyser.getFactory();
	}

	@Override
	public Environment getEnvironment() {
		return cmdAnalyser.getEnvironment();
	}

	@Override
	public Factory createFactory() {
		return cmdAnalyser.createFactory();
	}

	@Override
	public Environment createEnvironment() {
		return cmdAnalyser.createEnvironment();
	}

	@Override
	public SpoonCompiler createCompiler() {
		return cmdAnalyser.createCompiler();
	}

	@Override
	public CtModel getModel() {
		return cmdAnalyser.getModel();
	}

	public @NotNull CommandAnalyser getCmdAnalyser() {
		return cmdAnalyser;
	}

	public Map<CtExecutable<?>, UIListener> getBlobs() {
		return blobs;
	}

	public static void setNbCmdBlobs(final int nbCmds) {
		if(nbCmds > 0) {
			NB_CMDS = nbCmds;
		}
	}
}
