package inspectorguidget.eclipse.actions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import fr.inria.diverse.torgen.inspectorguidget.analyser.Command;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonHelper;
import inspectorguidget.eclipse.views.CommandView;

public class DetectGUICommandAction extends AbstractAction<CommandAnalyser> {
	/** Link Markers to their methods */
	private static final Map<IMarker, Command> INFO_MARKERS = new HashMap<>();

	public DetectGUICommandAction() {
		super();
	}
	
	@Override
	protected CommandAnalyser createAnalyser() {
		return new CommandAnalyser();
	}


	public static void clearMarkers() {
		CommandView.getSingleton().clearMarkers();
		INFO_MARKERS.keySet().forEach(marker -> {
			try { marker.delete(); } 
			catch(Exception e) { e.printStackTrace();}
		});
		INFO_MARKERS.clear();
	}
	
	
	/** Attach a warning marker for each listeners */
	@Override
	protected void addMarkers(final IProject project) {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(CommandView.ID);
		}catch(PartInitException e1) {
			e1.printStackTrace();
		}

		analyser.getCommands().values().stream().flatMap(s -> s.stream()).forEach(cmd -> markCtElement(cmd, project));
	}
	
	
	private void markCtElement(final Command cmd, final IProject project) {
		final String projectName = project.getName();
		final File source = cmd.getStatements().get(0).getPosition().getFile();
		// FIXME: little hack here
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
//				m.setAttribute(IMarker.MARKER, ClearMarkersAction.INSPECTOR_MARKER_NAME);
				m.setAttribute(IMarker.MESSAGE, "GUI command");
				
				int line;
				
				if(cmd.getConditions().isEmpty()) {
					System.err.println("NO CONDITION in command: " + cmd.getStatements());
					line = cmd.getLineEnd();
				}
				else line = SpoonHelper.INSTANCE.getLinePosition(cmd.getConditions().get(0));
				
				m.setAttribute(IMarker.LINE_NUMBER, line);
				m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
				INFO_MARKERS.put(m, cmd); // store mapping
				CommandView.getSingleton().addMarker(m); // update the view
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static Command getCommand(final IMarker marker) {
		return INFO_MARKERS.get(marker);
	}
	
	
	public static String getLabel(final IMarker marker) {
		Command cmd = INFO_MARKERS.get(marker);
		if(cmd==null || cmd.getConditions().isEmpty()) return "Command";
		return cmd.getConditions().get(0).toString();
	}
	

	/**
	 * Convert the marker to String "methodName;sourceFile;line"
	 */
	public static String getInfo(final IMarker marker) {
		String res = "";
		Command cmd = INFO_MARKERS.get(marker);
		
		if(cmd != null) {
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
		}

		return res;
	}
}
