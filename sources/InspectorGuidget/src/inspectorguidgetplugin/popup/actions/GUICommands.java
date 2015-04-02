package inspectorguidgetplugin.popup.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import inspectorguidget.analyser.processor.SimpleListenerProcessor;
import inspectorguidget.analyser.processor.wrapper.ListenersWrapper;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtExpression;
import views.CommandView;
import inspectorguidget.analyser.dataflow.Action;
import inspectorguidget.analyser.designsmells.Command;


public class GUICommands extends AbstractAction{
	
	/**
	 * Link Markers to their actions
	 */
	static HashMap<IMarker,Action> infoMapping;
	

	protected Command actions;
	protected ListenersWrapper listeners;
	protected List<Action> candidates;
	
	/**
	 * Constructor for detecting commands.
	 */
	public GUICommands() {
		super();
	}

	/**
	 * Attach a warning marker for each command
	 */
	protected void addMarkers(IProject project){
		
		infoMapping = new HashMap<IMarker, Action>();
		
		String projectName = project.getName();
		IJavaProject jProject = JavaCore.create(project);
		actions = new Command(listeners,factory);
		
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("views.CommandView");
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (Action cmd : actions.getMergedCommands()){
			File source = cmd.getSource().getPosition().getFile();
			List<CtExpression> conditions = cmd.getConditions();
			CtExpression lastCond = conditions.get((conditions.size()-1));
			
			int position;
			if (lastCond.getPosition() != null){
				position = lastCond.getPosition().getLine();
			}
			else{
				position = cmd.getStatements().get(0).getPosition().getLine();
			}
			
			String absPath = source.getAbsolutePath();
			int begin = absPath.indexOf(projectName) + projectName.length() + 1; //+1 to remove the '/'
			String path = absPath.substring(begin);
			
			IResource r = project.findMember(path);
			IMarker m;
			try {
				m = r.createMarker(IMarker.PROBLEM);
				m.setAttribute(IMarker.MESSAGE, "Command detected here: " + "" + position + "-" +
																				 cmd.getStatements().get(0).getPosition().getEndLine());
				m.setAttribute(IMarker.LINE_NUMBER, position);
				m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
				
				infoMapping.put(m, cmd); //store mapping
				
				CommandView.getSingleton().addMarker(m); //update the view
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Override
	protected List<AbstractProcessor> buildProcessors() {
		listeners = new ListenersWrapper();
		List<AbstractProcessor> processors = new ArrayList<AbstractProcessor>();
		processors.add(new SimpleListenerProcessor(listeners));
		return processors;
	}

	/**
	 * Convert the marker to String "methodName;sourceFile;line"
	 */
	public static String getInfo(IMarker marker){	
		String res = "";
		
		Action cmd = infoMapping.get(marker);
		
		String method = cmd.getSource().getSimpleName();
		
		CtExpression cond = cmd.getConditions().get(cmd.getConditions().size()-1);
		int line = cond.getPosition().getLine();
		
		String sourceFile = cmd.getSource().getPosition().getFile().getName();
		String name = cmd.getSource().getDeclaringType().getQualifiedName() + "." + cmd.getSource().getSimpleName(); 
		int posOf1stmt = cmd.getStatements().get(0).getPosition().getLine();
		int posOfLastStmt = cmd.getStatements().get(0).getPosition().getEndLine();
		res = name + ";" + sourceFile + ";" + line + ";" + "linesOfStatements: " + posOf1stmt + "-" + posOfLastStmt; 
		
		return res;
	}
	
	public static String getLabel(IMarker marker){
		Action cmd = infoMapping.get(marker);
		
		CtExpression cond = cmd.getConditions().get(cmd.getConditions().size()-1);
		if (cond.getPosition() != null){
			return cond.toString();
		}
		else {
			//return cmd.getStatements().get(0).toString();
			return cmd.getConditions().get(0).toString();
		}
	}
}
