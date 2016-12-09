package fr.inria.diverse.torgen.inspectorguidget.xp;

import fr.inria.diverse.torgen.inspectorguidget.Launcher;
import fr.inria.diverse.torgen.inspectorguidget.analyser.BlobListenerAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.analyser.Command;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandWidgetFinder;
import fr.inria.diverse.torgen.inspectorguidget.processor.WidgetProcessor;
import fr.inria.diverse.torgen.inspectorguidget.refactoring.ListenerCommandRefactor;
import org.apache.log4j.Level;
import spoon.compiler.Environment;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

public abstract class XPLauncher {
	private WidgetProcessor widgetProc;
	private CommandWidgetFinder finder;
	private BlobListenerAnalyser blobAnalyser;
	private final boolean genRefacClassesOnly = true;

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
		blobAnalyser.run();

		Launcher launcher = new Launcher(Collections.singletonList(widgetProc), blobAnalyser.getCmdAnalyser().getModelBuilder());
		launcher.process();

		finder = new CommandWidgetFinder(
			blobAnalyser.getCmdAnalyser().getCommands().values().parallelStream().flatMap(s -> s.stream()).collect(Collectors.toList()),
			widgetProc.getWidgetUsages());
		finder.process();

		final Set<CtType<?>> collectedTypes = new HashSet<>();
		blobAnalyser.getBlobs().entrySet().stream().
//			filter(e ->
//				(e.getKey().getSimpleName().equals("valueChanged") && e.getKey().getPosition().getLine()==329) ||
//					(e.getKey().getSimpleName().equals("actionPerformed") && e.getKey().getPosition().getLine()==361) ||
//					(e.getKey().getSimpleName().equals("actionPerformed") && e.getKey().getPosition().getLine()==321)
//					(e.getKey().getSimpleName().equals("actionPerformed") && e.getKey().getPosition().getLine()==130)
//		).
		map(e -> e.getValue()).flatMap(s -> s.stream()).forEach(cmd -> {
			System.out.println("Blob found in " + cmd);
			Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry> entry = finder.getResults().entrySet().stream().
				filter(e -> e.getKey()==cmd).findAny().get();
			ListenerCommandRefactor	refactor = new ListenerCommandRefactor(cmd, entry.getValue(), usingLambda(), genRefacClassesOnly);
			refactor.execute();
			collectedTypes.addAll(refactor.getRefactoredTypes());
		});

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

	protected abstract List<String> getInputResoures();

	protected abstract String[] getSourceClassPath();

	protected abstract int getCompilianceLevel();

	protected abstract boolean usingLambda();

	protected abstract String getOutputFolder();
}
