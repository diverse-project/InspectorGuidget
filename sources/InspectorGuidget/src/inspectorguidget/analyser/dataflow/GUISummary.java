package inspectorguidget.analyser.dataflow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

public class GUISummary {
	
	List<MethodSummary> summaries;
	
	//Actions that are in an edge
	Set<Action> toBeDisplayed;

	public GUISummary(List<CtMethod<?>> listeners) {
		toBeDisplayed = new HashSet<>();
		summaries = new ArrayList<>();
		for(CtMethod<?> method : listeners){
			summaries.add(new MethodSummary(method));
		}
	}
	
	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();
		res.append("digraph g {\n");
		res.append("overlap=false\n");
		String edges = drawAllEdges(); //compute which action to display
		res.append(drawAllAction());
		res.append(edges);
		res.append("}\n");
		return res.toString();
	}
	
	private String drawAllAction(){ //TODO: refactor
		
		StringBuffer res = new StringBuffer();
		StringBuffer actions = new StringBuffer();
		StringBuffer fields = new StringBuffer();
		
		for(MethodSummary summary : summaries){
			
			for(Action action : summary.getActions()){
				if(toBeDisplayed.contains(action)){
					
					CtMethod<?> source = action.getSource();
					CtClass<?> clazz = (CtClass<?>) source.getParent();
					
					StringBuffer conditions = new StringBuffer();
					for(CtExpression<?> cond : action.getConditions()){
						conditions.append("<TR><TD PORT=\""+ clean2("P"+cond.hashCode()) +"\">"+ clean(cond.toString()) +"</TD></TR>\n");
					}

					//Draw action
					String node = "\""+action.hashCode() + "\"[shape=none, margin=0, label=<\n" +
							"<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n" +
							"<TR><TD BGCOLOR=\"white\">"+ clazz.getSimpleName()+"."+source.getSimpleName() +"</TD></TR>\n" +
							"<TR><TD BGCOLOR=\"lightgrey\" PORT=\"stmt\">"+ clean(action.getStatement().toString()) +"</TD></TR>\n" +
							conditions.toString() +
							"</TABLE>\n" +
							">];\n";
					actions.append(node);
				}
			}
			
			for(Action fieldAction : summary.getFieldAssignements()){
				if(toBeDisplayed.contains(fieldAction)){
					
					CtMethod<?> source = fieldAction.getSource();
					CtClass<?> clazz = (CtClass<?>) source.getParent();
					
					StringBuffer conditions = new StringBuffer();
					for(CtExpression<?> cond : fieldAction.getConditions()){
						conditions.append("<TR><TD PORT=\""+  clean2("P"+cond.hashCode()) +"\">"+ clean(cond.toString()) +"</TD></TR>\n");
					}

					//Draw action
					String node = "\""+fieldAction.hashCode() + "\"[shape=none, margin=0, label=<\n" +
							"<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n" +
							"<TR><TD BGCOLOR=\"white\">"+ clazz.getSimpleName()+"."+source.getSimpleName() +"</TD></TR>\n" +
							"<TR><TD BGCOLOR=\"coral\" PORT=\"stmt\">"+ clean(fieldAction.getStatement().toString()) +"</TD></TR>\n" +
							conditions.toString() +
							"</TABLE>\n" +
							">];\n";
					fields.append(node);
				}
			}
		}
		
		res.append("subgraph actions{\n");
		res.append(actions.toString());
		res.append("}\n");
		res.append("subgraph fields{\n");
		res.append(fields.toString());
		res.append("}\n");
		return res.toString();
	}
	
	private String drawAllEdges(){
		
		StringBuffer res = new StringBuffer();
		
		for(MethodSummary summary : summaries){
			
			for(Action fieldAction : summary.getFieldAssignements()){

				res.append(createEdges(fieldAction));
				
			}
		}
		return res.toString();
	}
	
	/*
	 * Draw all edges from the action to all others impacted actions
	 */
	private String createEdges(Action fieldAction){
		
		StringBuffer res = new StringBuffer(); 
		
		CtField<?> field = null;
		CtAssignment<?,?> assignment = (CtAssignment<?,?>) fieldAction.getStatement();
		CtExpression<?> leftPart = assignment.getAssigned();
		if(leftPart instanceof CtFieldAccess){
			field = ((CtFieldAccess<?>) leftPart).getVariable().getDeclaration();
		}
		
		for(MethodSummary summary : summaries){
			
			for(Action action : summary.getActions()){
				
				if(summary.isControlledBy(action,assignment)){
					toBeDisplayed.add(fieldAction);
					toBeDisplayed.add(action);
					
					List<CtExpression<?>> conditions = summary.getControllers(action,field);
					
					for(CtExpression<?> cond : conditions){
						//Draw the edge fieldAction:0 -> action:condID		
						String edge = "\""+fieldAction.hashCode()+"\":stmt -> \"" + action.hashCode()+"\":\""+ clean2("P"+cond.hashCode())+"\"\n";
						res.append(edge);
					}
				}
			}
			
			for(Action actionField : summary.getFieldAssignements()){ //TODO: refactor
							
				if(summary.isControlledBy(actionField,assignment)){
					toBeDisplayed.add(fieldAction);
					toBeDisplayed.add(actionField);
					
					List<CtExpression<?>> conditions = summary.getControllers(actionField,field);
					
					for(CtExpression<?> cond : conditions){
						String edge = "\""+fieldAction.hashCode()+"\":stmt -> \"" + actionField.hashCode()+"\":\""+ clean2("P"+cond.hashCode())+"\"\n";
						res.append(edge);
					}
				}
			}
			
		}
		return res.toString();
	}
	
	private String clean(String str){
		return str.replaceAll("\\u0022", "\\\\\"").replaceAll("\n", "\\\\l").replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;").replace("|", "&#124;");
	}
	
	private String clean2(String str){
		return str.replace('-', 'm');
	}
	
}
