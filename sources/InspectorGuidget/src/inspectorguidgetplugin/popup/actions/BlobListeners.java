package inspectorguidgetplugin.popup.actions;

import inspectorguidget.analyser.dataflow.Action;
import inspectorguidget.analyser.designsmells.Command;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import spoon.reflect.declaration.CtMethod;
import views.BlobListenerView;

/*
 * Responsible for recovering the BlobListeners (listeners that have more than one command)
 * and marking them in the plugin
 */
public class BlobListeners extends GUICommands {

	/**
	 * Link Markers to their methods
	 */
	static Map<IMarker, CtMethod<?>>	infoMapping;

	public BlobListeners() {
		super();
	}

	@Override
	protected void addMarkers(IProject project) {
		infoMapping = new HashMap<>();

		String projectName = project.getName();
		// IJavaProject jProject = JavaCore.create(project);
		actions = new Command(listeners, factory);
		Map<CtMethod<?>, List<Action>> candidatesBlob = gatherCommandsBySource(actions.getMergedCommands());

		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("views.BlobListenerView");
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// int totalOfLines = 0;
		for (CtMethod<?> listener : listeners.getListeners()) {
			List<Action> cmds = candidatesBlob.get(listener);

			// Recovery blobListeners by checking if there is more than one
			// command per listener
			if (cmds != null && cmds.size() > 1) {
				File source = listener.getPosition().getFile();
				// Get the number of lines of code for a blob listener
				// int loc = getLOCListeners(listener);
				// Calculate the total number of lines of blobListeners
				// totalOfLines += loc;
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
					m.setAttribute(IMarker.MESSAGE, "BlobListener detected here with " + cmds.size() + " commands"); // +
																														// " and "
																														// +
																														// loc
																														// +
																														// " LOC");
					m.setAttribute(IMarker.LINE_NUMBER, listener.getPosition().getLine());
					m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);

					infoMapping.put(m, listener); // store mapping

					BlobListenerView.addMarker(m); // update the view
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// System.out.println("Total of lines code " + totalOfLines);
	}

	/*
	 * Gather commands that are detected in the same method
	 */
	public Map<CtMethod<?>, List<Action>> gatherCommandsBySource(List<Action> commands) {
		Map<CtMethod<?>, List<Action>> res = new IdentityHashMap<>();

		for (Action cmd : commands) {
			List<Action> cmds = res.get(cmd.getSource());
			if (cmds == null) {
				cmds = new ArrayList<>();
				cmds.add(cmd);
				res.put(cmd.getSource(), cmds);
			} else {
				cmds.add(cmd);
			}
		}
		return res;
	}

	/*
	 * Calculate the number of physical lines of code for each blob listener
	 */
	// public int getLOCListeners(CtMethod method){
	// String[] lines = method.toString().split("\n");
	// int loc = 0;
	// int comments = 0;
	// for (String line : lines){
	// if(line.trim().startsWith("/") || line.trim().startsWith("*")){
	// comments++;
	// }
	// else{
	// loc++;
	// }
	// }
	// return loc;
	//
	// }

	/*
	 * Calculate the total of lines of code for all blob listeners
	 */
	// public int getTotalLoc(Map<CtMethod,Integer> size){
	// int totalLoc = 0;
	// for (Entry<CtMethod, Integer> entry : size.entrySet()){
	// totalLoc += entry.getValue();
	// }
	// return totalLoc;
	// }

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
