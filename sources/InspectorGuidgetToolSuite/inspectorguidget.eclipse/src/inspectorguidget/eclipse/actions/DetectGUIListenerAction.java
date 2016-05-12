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

import fr.inria.diverse.torgen.inspectorguidget.analyser.GUIListenerAnalyser;
import inspectorguidget.eclipse.views.ListenerView;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;

public class DetectGUIListenerAction extends AbstractAction<GUIListenerAnalyser> {
	/** Link Markers to their methods */
	static final Map<IMarker, CtExecutable<?>> infoMapping = new HashMap<>();

	public DetectGUIListenerAction() {
		super();
	}
	
	@Override
	protected GUIListenerAnalyser createAnalyser() {
		return new GUIListenerAnalyser();
	}


	public static void clearMarkers() {
		ListenerView.getSingleton().clearMarkers();
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
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ListenerView.ID);
		}catch(PartInitException e1) {
			e1.printStackTrace();
		}

		analyser.getLambdaListeners().forEach(lambda -> markCtElement(lambda, project));
		analyser.getClassListeners().values().stream().flatMap(s -> s.stream()).forEach(method -> markCtElement(method, project));
	}
	
	
	private void markCtElement(final CtExecutable<?> elt, final IProject project) {
		final String projectName = project.getName();
		final File source = elt.getPosition().getFile();
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
				m.setAttribute(IMarker.MARKER, ClearMarkersAction.INSPECTOR_MARKER_NAME);
				m.setAttribute(IMarker.MESSAGE, "GUI listener");
				m.setAttribute(IMarker.LINE_NUMBER, elt.getPosition().getLine());
				m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
				infoMapping.put(m, elt); // store mapping
				ListenerView.getSingleton().addMarker(m); // update the view
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
		CtExecutable<?> method = infoMapping.get(marker);
		
		if(method != null) {
			String sourceFile = method.getPosition().getFile().getName();
			int line = method.getPosition().getLine();
			String name;
			CtType<?> parentType = method.getParent(CtType.class);
			
			if(parentType==null)
				name = method.getSimpleName();
			else
				name = ((CtTypeMember)method).getDeclaringType().getQualifiedName() + "." + method.getSimpleName();
			
			res = name + ";" + sourceFile + ";" + line;
		}

		return res;
	}

	public static String getMethod(final IMarker marker) {
		CtExecutable<?> exec = infoMapping.get(marker);
		return exec==null ? "" : exec.getSimpleName();
	}
}
