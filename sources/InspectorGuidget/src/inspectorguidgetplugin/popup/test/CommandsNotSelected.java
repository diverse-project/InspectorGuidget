package inspectorguidgetplugin.popup.test;

import inspectorguidgetplugin.popup.actions.GUICommands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import inspectorguidget.analyser.dataflow.Action;
import inspectorguidget.analyser.designsmells.Command;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.cu.SourcePosition;


public class CommandsNotSelected extends GUICommands{	
	
	public CommandsNotSelected() {
		super();
	}
		
	@Override
	protected void addMarkers(IProject project) {
		String projectName = project.getName();
		IJavaProject jProject = JavaCore.create(project);
		
		actions = new Command(listeners,factory);
		List<Action> candidates = actions.getCommands();//All command before the merge
		List<Action> commands = actions.getMergedCommands();		
		for (Action candidate : candidates){
			List<CtCodeElement> candStmts = candidate.getStatements();
			for (Action cmd : commands){
				List<CtCodeElement> cmdStmts = cmd.getStatements();
				if (candidate.getSource() == cmd.getSource() && candStmts != cmdStmts){	//check conditions too			
					File source = candidate.getSource().getPosition().getFile();
					List<CtExpression> conditions = candidate.getConditions();
					int initPosOfCommand;
					if (conditions.get(0).getPosition() != null){				
						initPosOfCommand = conditions.get(0).getPosition().getLine();
					}
					else {
							List<CtCodeElement> statements = candidate.getStatements();
							initPosOfCommand = statements.get(0).getPosition().getLine();
					}
					
					String absPath = source.getAbsolutePath();
					int begin = absPath.indexOf(projectName) + projectName.length() + 1; //+1 to remove the '/'
					String path = absPath.substring(begin);
					
					IResource r = project.findMember(path);
					IMarker m;
					try {
						m = r.createMarker(IMarker.PROBLEM);
						m.setAttribute(IMarker.MESSAGE, "Candidate is not selected here!");
						m.setAttribute(IMarker.LINE_NUMBER, initPosOfCommand);
						m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}	
	}

}



