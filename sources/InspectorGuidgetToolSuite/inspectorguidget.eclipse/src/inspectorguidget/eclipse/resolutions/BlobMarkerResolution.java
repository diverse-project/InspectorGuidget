package inspectorguidget.eclipse.resolutions;

import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;

import fr.inria.diverse.torgen.inspectorguidget.Launcher;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandWidgetFinder;
import fr.inria.diverse.torgen.inspectorguidget.analyser.UIListener;
import fr.inria.diverse.torgen.inspectorguidget.filter.BasicFilter;
import fr.inria.diverse.torgen.inspectorguidget.processor.WidgetProcessor;
import fr.inria.diverse.torgen.inspectorguidget.refactoring.ListenerCommandRefactor;
import inspectorguidget.eclipse.actions.DetectBlobListenerAction;
import spoon.compiler.Environment;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

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
		final Collection<CommandWidgetFinder.WidgetFinderEntry> allEntries = finder.getResults().values();
		
		System.out.println("Widgets finder ended");
		
		entry.getValue().getCommands().forEach(cmd -> {
			final ListenerCommandRefactor ref = new ListenerCommandRefactor(cmd, null, false, false, false, allEntries);
			ref.execute();
			
			Environment env = widgetProc.getEnvironment();
			env.useTabulations(true);
			env.setAutoImports(true);
			DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(env);
			printer.calculate(null, cmd.getAllLocalStatmtsOrdered().stream().
					map(s -> s.getElements(new BasicFilter<CtTypeReference<?>>(CtTypeReference.class))).
					flatMap(s -> s.stream()).map(tref -> tref.getDeclaration()).collect(Collectors.toList()));
			System.out.println(printer.getResult());
		});
	}
}
