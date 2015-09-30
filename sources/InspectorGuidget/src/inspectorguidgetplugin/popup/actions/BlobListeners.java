package inspectorguidgetplugin.popup.actions;

import helper.FileHelper;
import inspectorguidget.analyser.dataflow.Action;
import inspectorguidget.analyser.designsmells.BlobListener;
import inspectorguidget.analyser.designsmells.Command;
import inspectorguidgetplugin.Activator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.security.auth.login.Configuration;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
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

			/**
			 *  Recovery blobListeners by checking if there is more than one command per listener
			 */
			if (cmds != null && cmds.size() > 1) {
				File source = listener.getPosition().getFile();
				// Get the number of lines of code for a blob listener
				// int loc = getLOCListeners(listener);
				// Calculate the total number of lines of blobListeners
				// totalOfLines += loc;
				String absPath = source.getAbsolutePath();
				int begin = absPath.indexOf(projectName) + projectName.length() + 1; // +1 to remove the '/'
				IResource r = getResource(project, absPath.substring(begin));
				IMarker m;
				try {
					m = r.createMarker(IMarker.PROBLEM);
					m.setAttribute(IMarker.MESSAGE, "BlobListener detected here with " + cmds.size() + " commands"); // + " and " + loc + " LOC");
					m.setAttribute(IMarker.LINE_NUMBER, listener.getPosition().getLine());
					m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);

					infoMapping.put(m, listener); // store mapping

					BlobListenerView.addMarker(m); // update the view
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			logFile(project, listener, cmds);
		}
		// System.out.println("Total of lines code " + totalOfLines);
	}

	/**
	 * Log file that contains the info (e.g., relPath, lines, number of commands) about GUI listeners that have at least one command 
	 */
	public void logFile(IProject project, CtMethod<?> listener, List<Action> cmds){
		
		IPath pathProject = project.getFullPath();	
		File source = listener.getPosition().getFile();
		String absPath = source.getAbsolutePath();
		int begin = absPath.indexOf(project.getName()) + project.getName().length() + 1;
		String relPath = absPath.substring(begin);

		
		
		if (cmds != null && cmds.size() > 0) {
			int initLine = listener.getPosition().getLine();
			//int endLine = listener.getPosition().getEndLine(); //It does not work!
			int endLine = listener.getBody().getPosition().getEndLine();
			StringBuffer content = new StringBuffer();
			content.append(project.getName() + ";" + relPath + ";" + initLine + ";" + endLine  + ";" + cmds.size() + "\n");
			String fileName =  project.getName() + "-logfile" + ".txt";
			 
			//Get the dir setup in the preferences for blob listeners
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			String pathBlobFile = store.getString("pathBlobListeners");
			String fileNameBlob = "CheckedBlobListeners.txt";
			int end = pathBlobFile.indexOf(fileNameBlob);
			String dir = pathBlobFile.substring(0, end);			
			writeFile(dir, fileName, content.toString());
		}

	}
		
	/**
	 * Write 'content' in 'dir'/'file' 
	 */
	public static void writeFile(String dir, String file, String content){
		try{
			
			File newDir = new File(dir);
			if (!newDir.exists()){
				newDir.mkdirs();
			}
			
			FileWriter fw = new FileWriter(dir+file, true);		
			BufferedWriter output = new BufferedWriter(fw);
			output.write(content);
			output.flush();
			output.close();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}

	/**
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
			} 
			else{
				cmds.add(cmd);
			}
		}
		return res;
	}
		
	/*
	 * Calculate the number of physical lines of code for each blob listener
	 */
	 public int getLOCListeners(CtMethod method){
		 String[] lines = method.toString().split("\n");
		 int loc = 0;
		 int comments = 0;
		 for (String line : lines){
			 if(line.trim().startsWith("/") || line.trim().startsWith("*")){
				 comments++;
			 }
			 else{
				 loc++;
			 }
		 }
		 return loc;
	 }

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
