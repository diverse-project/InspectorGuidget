package inspectorguidget.eclipse.actions;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import fr.inria.diverse.torgen.inspectorguidget.Launcher;
import fr.inria.diverse.torgen.inspectorguidget.analyser.Command;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandWidgetBugsDetector;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandWidgetFinder;
import fr.inria.diverse.torgen.inspectorguidget.helper.Tuple;
import fr.inria.diverse.torgen.inspectorguidget.processor.WidgetProcessor;
import inspectorguidget.eclipse.views.GUIBugsView;
import spoon.reflect.declaration.CtElement;

public class DetectGUIBugsAction extends AbstractAction<CommandAnalyser> {
	/** Link Markers to their methods */
	static final Map<IMarker, Tuple<String, Command>> infoMapping = new HashMap<>();
	private CommandWidgetFinder finder;
	private CommandWidgetBugsDetector bugsanalyser;
	private WidgetProcessor widgetProc;

	public DetectGUIBugsAction() {
		super();
		spoon.Launcher.LOGGER.setLevel(Level.OFF);
	}
	
	@Override
	protected CommandAnalyser createAnalyser() {
		return new CommandAnalyser();
	}


	@Override
	protected void initAction(final IProgressMonitor monitor, final IProject project) {
		super.initAction(monitor, project);
		widgetProc = new WidgetProcessor(true);
		Launcher launcher = new Launcher(Collections.singletonList(widgetProc), analyser.getModelBuilder());
		launcher.process();
		finder = new CommandWidgetFinder(analyser.getCommands().values().parallelStream().flatMap(s -> s.stream()).collect(Collectors.toList()), widgetProc.getFields());
		finder.process();
		bugsanalyser = new CommandWidgetBugsDetector(finder.getResults());
		bugsanalyser.process();
		analysisTime = System.currentTimeMillis();
	}
	

	public static void clearMarkers() {
		GUIBugsView.getSingleton().clearMarkers();
		infoMapping.keySet().forEach(marker -> {
			try { marker.delete(); } 
			catch(Exception e) { e.printStackTrace();}
		});
		infoMapping.clear();
	}
	
	
	/** Attach a warning marker for each listeners */
	@Override
	protected void addMarkers(final IProject project) {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(GUIBugsView.ID);
		}catch(final PartInitException e1) {
			e1.printStackTrace();
		}

		bugsanalyser.getResults().forEach(tuple -> markCtElement(tuple, project));
	}
	
	
	private void markCtElement(final Tuple<String, Command> tuple, final IProject project) {
		final CtElement elt = tuple.b.getMainStatmtEntry().get().getStatmts().get(0);
		final String errorMessage = tuple.a;
		final String projectName = project.getName();
		final File source = elt.getPosition().getFile();
		final String absPath = source.getAbsolutePath();
		final int begin = absPath.indexOf(projectName) + projectName.length() + 1; 
		String path = absPath.substring(begin);
		IResource r = project.findMember(path);
		
		if(r==null) {
			int i = path.indexOf('/');
			if(i!=-1)
				path = path.substring(i);
			r = project.findMember(path);
		}
		
		if(r==null && path.startsWith("/"+project.getName())) {
			path = path.replaceFirst("/"+project.getName(), "");
			r = project.findMember(path);
		}
		
		if(r!=null) {
			IMarker m;
			try {
				m = r.createMarker(IMarker.PROBLEM);
				m.setAttribute(IMarker.MARKER, ClearMarkersAction.INSPECTOR_MARKER_NAME);
				m.setAttribute(IMarker.MESSAGE, errorMessage);
				m.setAttribute(IMarker.LINE_NUMBER, elt.getPosition().getLine());
				m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
				infoMapping.put(m, tuple);
				GUIBugsView.getSingleton().addMarker(m); // update the view
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
	
	

	/**
	 * Convert the marker to String "methodName;sourceFile;line"
	 */
	public static String getInfo(final IMarker marker) {
		String res = "";
//		CtElement method = infoMapping.get(marker);
//		
//		if(method != null) {
//			String sourceFile = method.getPosition().getFile().getName();
//			int line = method.getPosition().getLine();
//			String name;
//			CtType<?> parentType = method.getParent(CtType.class);
//			
//			if(parentType==null)
//				name = method.getSimpleName();
//			else
//				name = parentType.getQualifiedName() + "." + method.getSimpleName();
//			
//			res = name + ";" + sourceFile + ";" + line;
//		}

		return res;
	}
	
	public static String getLabel(final IMarker marker) {
		Tuple<String, Command> tuple = infoMapping.get(marker);
		return tuple==null ? "" : tuple.a;
	}
	
//	public static String getMethod(final IMarker marker) {
//		CtExecutable<?> exec = infoMapping.get(marker);
//		return exec==null ? "" : exec.getSimpleName();
//	}
}
