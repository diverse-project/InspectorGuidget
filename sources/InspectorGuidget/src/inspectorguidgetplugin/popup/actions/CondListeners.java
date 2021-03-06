package inspectorguidgetplugin.popup.actions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import spoon.reflect.declaration.CtMethod;
import views.CondListenerView;

public class CondListeners extends GUIListeners {

	/**
	 * Link Markers to their methods
	 */
	static Map<IMarker, CtMethod<?>>	infoMapping;

	public CondListeners() {
		super();
	}

	/**
	 * Attach a warning marker for each listeners
	 */
	@Override
	protected void addMarkers(IProject project) {

		infoMapping = new HashMap<>();

		String projectName = project.getName();
		// IJavaProject jProject = JavaCore.create(project);

		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("views.CondListenerView");
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (CtMethod<?> method : listeners.getConditionalListeners()) {
			File source = method.getPosition().getFile();

			// FIXME: little hack here
			String absPath = source.getAbsolutePath();
			int begin = absPath.indexOf(projectName) + projectName.length() + 1; // +1
																					// to
																					// remove
																					// the
																					// '/'
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
			
			IMarker m;
			try {
				m = r.createMarker(IMarker.PROBLEM);
				m.setAttribute(IMarker.MESSAGE, "Listener with conditional statements spotted here!");
				m.setAttribute(IMarker.LINE_NUMBER, method.getPosition().getLine());
				m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);

				infoMapping.put(m, method); // store mapping

				CondListenerView.addMarker(m); // update the view
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Convert the marker to String "methodName;sourceFile;line"
	 */
	public static String getInfo(IMarker marker) {
		String res = "";

		CtMethod<?> method = infoMapping.get(marker);
		if (method != null) {
			String sourceFile = method.getPosition().getFile().getName();
			int line = method.getPosition().getLine();
			String name = method.getDeclaringType().getQualifiedName() + "." + method.getSimpleName();
			res = name + ";" + sourceFile + ";" + line;
		}

		return res;
	}

	public static String getMethod(IMarker marker) {
		return infoMapping.get(marker).getSimpleName();
	}

}
