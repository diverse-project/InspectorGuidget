package fr.inria.diverse.torgen.inspectorguidget.xp;

import fr.inria.diverse.torgen.inspectorguidget.Launcher;
import fr.inria.diverse.torgen.inspectorguidget.analyser.BlobListenerAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.analyser.Command;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandWidgetFinder;
import fr.inria.diverse.torgen.inspectorguidget.analyser.UIListener;
import fr.inria.diverse.torgen.inspectorguidget.filter.BasicFilter;
import fr.inria.diverse.torgen.inspectorguidget.processor.WidgetProcessor;
import fr.inria.diverse.torgen.inspectorguidget.refactoring.ListenerCommandRefactor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NotNull;
import spoon.compiler.Environment;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;

public abstract class XPLauncher {
	protected WidgetProcessor widgetProc;
	protected CommandWidgetFinder finder;
	protected BlobListenerAnalyser blobAnalyser;
	protected final boolean genRefacClassesOnly = true;

	public void run() {
		spoon.Launcher.LOGGER.setLevel(Level.OFF);
		ListenerCommandRefactor.LOG.setLevel(java.util.logging.Level.OFF);

		try {
			FileHandler fh = new FileHandler("refactoring.log");
			fh.setFormatter(new SimpleFormatter());
			ListenerCommandRefactor.LOG.addHandler(fh);
		}catch(IOException e) {
			e.printStackTrace();
		}

		blobAnalyser = new BlobListenerAnalyser();
		widgetProc = new WidgetProcessor(true);

		getInputResoures().forEach(p -> blobAnalyser.addInputResource(p));
		blobAnalyser.getCmdAnalyser().setSourceClasspath(getSourceClassPath());
//		blobAnalyser.getCmdAnalyser().getEnvironment().setNoClasspath(true);
		blobAnalyser.run();

		List<Map.Entry<CtExecutable<?>, UIListener>> nonEmptyListeners = blobAnalyser.getCmdAnalyser().getCommands().entrySet().parallelStream().
			filter(entry -> entry.getValue().getNbTotalCmds() > 0).collect(Collectors.toList());

		System.out.println("Number of listener methods having at least one command: " + nonEmptyListeners.size());
		System.out.println("Total number of local commands: " +
			nonEmptyListeners.parallelStream().mapToInt(lis -> lis.getValue().getNbLocalCmds()).sum());
		System.out.println("Total number of LoCs of the listener methods having at least one command: " +
			nonEmptyListeners.parallelStream().mapToInt(exec ->
				exec.getKey().getPosition().getEndLine() - exec.getKey().getPosition().getLine()).sum());

		final String listenerInfos = nonEmptyListeners.parallelStream().map(entry -> getProjectName() + ";" +
			entry.getKey().getPosition().getFile().toString().replace(getInputResoures().get(0), "") + ";" +
			entry.getKey().getPosition().getLine() + ";" + entry.getKey().getPosition().getEndLine() + ";" +
			(entry.getKey().getBody()==null ? "1" : entry.getKey().getBody().getElements(new BasicFilter<>(CtStatement.class)).size()) + ";" +
			entry.getValue().getNbTotalCmds()).
			collect(Collectors.joining("\n"));

		try(final PrintWriter out = new PrintWriter("listeners-" + getProjectName() + ".csv")) {
			out.println(listenerInfos);
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}

		final long time = System.currentTimeMillis();

		Launcher launcher = new Launcher(Collections.singletonList(widgetProc), blobAnalyser.getCmdAnalyser().getModelBuilder());
		launcher.process();

		finder = new CommandWidgetFinder(
			blobAnalyser.getCmdAnalyser().getCommands().values().parallelStream().flatMap(s -> s.getCommands().stream()).collect(Collectors.toList()),
			widgetProc.getWidgetUsages());
		finder.process();

		final Set<CtType<?>> collectedTypes = new HashSet<>();
		final Collection<CommandWidgetFinder.WidgetFinderEntry> allEntries = finder.getResults().values();

		filterBlobsToRefactor().forEach(cmd -> {
			System.out.println("Blob found in " + cmd);
			Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry> entry = finder.getResults().entrySet().stream().
				filter(e -> e.getKey()==cmd).findAny().get();
			ListenerCommandRefactor	refactor = new ListenerCommandRefactor(cmd, entry.getValue(), usingLambda(), false, genRefacClassesOnly, allEntries);
			refactor.execute();
			collectedTypes.addAll(refactor.getRefactoredTypes());
		});

		System.out.println("Refactoring time: " + (System.currentTimeMillis() - time));

		Factory factory = blobAnalyser.getCmdAnalyser().getFactory();
		Environment env = factory.getEnvironment();
		env.useTabulations(true);
		env.setAutoImports(true);
		env.setShouldCompile(true);
		env.setComplianceLevel(getCompilianceLevel());

		if(genRefacClassesOnly) {
			collectedTypes.forEach(type -> {
				JavaOutputProcessor processor = new JavaOutputProcessor(new File(getOutputFolder()), new DefaultJavaPrettyPrinter(env));
				processor.setFactory(factory);
				processor.createJavaFile(type);
			});
		}else {
			blobAnalyser.getCmdAnalyser().getModel().getAllTypes().stream().filter(type -> type.getParent(CtType.class)==null).forEach(type -> {
				JavaOutputProcessor processor = new JavaOutputProcessor(new File(getOutputFolder()), new DefaultJavaPrettyPrinter(env));
				processor.setFactory(factory);
				processor.createJavaFile(type);
			});
		}
	}


	protected abstract String getProjectName();

	protected @NotNull List<Command> filterBlobsToRefactor() {
		return blobAnalyser.getBlobs().entrySet().stream().map(e -> e.getValue()).flatMap(s -> s.getCommands().stream()).collect(Collectors.toList());
	}

	protected abstract List<String> getInputResoures();

	protected abstract String[] getSourceClassPath();

	protected abstract int getCompilianceLevel();

	protected abstract boolean usingLambda();

	protected abstract String getOutputFolder();

	static class ListenerID {
		final String fileName;
		final int linePos;

		ListenerID(final String fileName, final int linePos) {
			this.fileName = fileName;
			this.linePos = linePos;
		}

		boolean match(final CtExecutable<?> exec) {
			final SourcePosition pos = exec.getPosition();
			return pos.getLine()==linePos && pos.getCompilationUnit().getFile().toString().endsWith(fileName);
		}
	}
}
