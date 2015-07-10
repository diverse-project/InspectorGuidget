package inspectorguidgetplugin.popup.actions;

import inspectorguidget.analyser.dataflow.Action;
import inspectorguidget.analyser.designsmells.BlobListener;
import inspectorguidget.analyser.designsmells.Command;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import spoon.reflect.declaration.CtMethod;
import views.BlobListener2View;

/*
 * Responsible for recovering the BlobListeners (listeners that have more than one command)
 * and also selecting them if they are not found in the listener registration
 *  marking them in the plugin
 */
public class BlobListener2 extends GUICommands {

	/**
	 * Link Markers to their methods
	 */
	static Map<IMarker, CtMethod<?>>	infoMapping;

	public BlobListener2() {
		super();
	}

	@Override
	protected void addMarkers(IProject project) {

		infoMapping = new HashMap<>();

		String projectName = project.getName();
		// IJavaProject jProject = JavaCore.create(project);
		actions = new Command(listeners, factory);
		List<Action> mergedComands = actions.getMergedCommands();
		BlobListener blob = new BlobListener(mergedComands);
		Map<CtMethod<?>, List<Action>> blobListeners = blob.getBlobListeners();
		if (blobListeners != null) {
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.showView("views.BlobListener2View");
			} catch (PartInitException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			for (Entry<CtMethod<?>, List<Action>> entry : blobListeners.entrySet()) {
				CtMethod<?> blobListener = entry.getKey();
				List<Action> commands = entry.getValue();
				File source = blobListener.getPosition().getFile();

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
					m.setAttribute(IMarker.MESSAGE, "BlobListener detected here with " + commands.size() + " commands");
					m.setAttribute(IMarker.LINE_NUMBER, blobListener.getPosition().getLine());
					m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);

					infoMapping.put(m, blobListener); // store mapping

					BlobListener2View.addMarker(m); // update the view
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
