package inspectorguidget.eclipse.resolutions;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IMarkerResolution;

import fr.inria.diverse.torgen.inspectorguidget.Launcher;
import fr.inria.diverse.torgen.inspectorguidget.analyser.Command;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandWidgetFinder;
import fr.inria.diverse.torgen.inspectorguidget.analyser.UIListener;
import fr.inria.diverse.torgen.inspectorguidget.processor.WidgetProcessor;
import fr.inria.diverse.torgen.inspectorguidget.refactoring.ListenerCommandRefactor;
import inspectorguidget.eclipse.actions.AbstractAction;
import inspectorguidget.eclipse.actions.DetectBlobListenerAction;
import spoon.compiler.Environment;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.AbstractFilter;

public class BlobMarkerResolution implements IMarkerResolution {

	@Override
	public String getLabel() {
		return "Remove blob";
	}

	@Override
	public void run(final IMarker marker) {
		final Entry<CtExecutable<?>, UIListener> entry = DetectBlobListenerAction.getBlobCommands(marker);
		final WidgetProcessor widgetProc = new WidgetProcessor(true);
		
		System.out.println("Refactoring blob listener: " + entry);
		
		Launcher launcher = new Launcher(Collections.singletonList(widgetProc), DetectBlobListenerAction.currentAnalyser.getCmdAnalyser().getModelBuilder());
		launcher.process();
		
		System.out.println("Widgets processor ended");
		
		final CommandWidgetFinder finder = new CommandWidgetFinder(entry.getValue().getCommands(), widgetProc.getWidgetUsages());
		finder.process();
		
		System.out.println("Widgets finder ended");
		
		entry.getValue().getCommands().forEach(cmd -> {
			final ListenerCommandRefactor ref = new ListenerCommandRefactor(cmd, null, false, false);
			ref.execute();
			
			Environment env = widgetProc.getEnvironment();
			env.useTabulations(true);
			env.setAutoImports(true);
			DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(env);
			printer.calculate(null, cmd.getAllLocalStatmtsOrdered().stream().map(s -> s.getElements(new AbstractFilter<CtTypeReference<?>>() {})).
					flatMap(s -> s.stream()).map(tref -> tref.getDeclaration()).collect(Collectors.toList()));
			System.out.println(printer.getResult());
		});
	}
}
