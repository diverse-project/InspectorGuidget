package inspectorguidgetplugin.popup.actions;

import inspectorguidget.analyser.processor.SimpleListenerProcessor;
import inspectorguidget.analyser.processor.wrapper.ListenersWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;
import views.ListenerView;

public class GUIListeners extends AbstractAction {

	/**
	 * Link Markers to their methods
	 */
	static Map<IMarker, CtMethod<?>>	infoMapping;

	ListenersWrapper					listeners;

	/**
	 * Constructor for Action1.
	 */
	public GUIListeners() {
		super();
	}

	//
	/**
	 * Attach a warning marker for each listeners
	 */
	@Override
	protected void addMarkers(IProject project) {

		infoMapping = new HashMap<>();

		String projectName = project.getName();
		// IJavaProject jProject = JavaCore.create(project);

		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("views.ListenerView");
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (CtMethod<?> method : listeners.getListeners()) {
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
				m.setAttribute(IMarker.MESSAGE, "Listener spotted here!");
				m.setAttribute(IMarker.LINE_NUMBER, method.getPosition().getLine());
				m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);

				infoMapping.put(m, method); // store mapping

				ListenerView.addMarker(m); // update the view
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected List<AbstractProcessor<?>> buildProcessors() {
		listeners = new ListenersWrapper();
		List<AbstractProcessor<?>> processors = new ArrayList<>();
		processors.add(new SimpleListenerProcessor(listeners));
		return processors;
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
