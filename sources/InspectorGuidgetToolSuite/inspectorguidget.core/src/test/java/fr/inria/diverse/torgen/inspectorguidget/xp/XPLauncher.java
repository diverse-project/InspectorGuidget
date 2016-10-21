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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class XPLauncher {
	private WidgetProcessor widgetProc;
	private CommandWidgetFinder finder;
	private BlobListenerAnalyser blobAnalyser;

	public void run() {
		spoon.Launcher.LOGGER.setLevel(Level.OFF);

		blobAnalyser = new BlobListenerAnalyser();
		widgetProc = new WidgetProcessor(true);

		getInputResoures().forEach(p -> blobAnalyser.addInputResource(p));
//		cmdAnalyser.setSourceClasspath(getSourceClassPath());
		blobAnalyser.run();

		Launcher launcher = new Launcher(Collections.singletonList(widgetProc), blobAnalyser.getCmdAnalyser().getModelBuilder());
		launcher.process();

		finder = new CommandWidgetFinder(
			blobAnalyser.getCmdAnalyser().getCommands().values().parallelStream().flatMap(s -> s.stream()).collect(Collectors.toList()),
			widgetProc.getWidgetUsages());
		finder.process();

		blobAnalyser.getBlobs().forEach((exec,cmds) -> cmds.forEach(cmd -> {
			Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry> entry = finder.getResults().entrySet().stream().
				filter(e -> e.getKey()==cmd).findAny().get();

			//FIXME should memorize the classes that are modified to print them only
			ListenerCommandRefactor	refactor = new ListenerCommandRefactor(cmd, entry.getValue(), usingLambda());
			refactor.execute();
		}));

		Factory factory = blobAnalyser.getCmdAnalyser().getFactory();
		Environment env = factory.getEnvironment();
		env.useTabulations(true);
		env.setAutoImports(true);
		env.setShouldCompile(true);
		env.setComplianceLevel(getCompilianceLevel());

		blobAnalyser.getCmdAnalyser().getModel().getAllTypes().stream().filter(type -> type.getParent(CtType.class)==null).forEach(type -> {
			JavaOutputProcessor processor = new JavaOutputProcessor(new File("/home/foo/Bureau/foo"), new DefaultJavaPrettyPrinter(env));
			processor.setFactory(factory);
			processor.createJavaFile(type);
		});
	}

	protected abstract List<String> getInputResoures();

	protected abstract String[] getSourceClassPath();

	protected abstract int getCompilianceLevel();

	protected abstract boolean usingLambda();
}
