package inspectorguidget.analyser.designsmells;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import inspectorguidget.analyser.ComponentsAction;
import inspectorguidget.analyser.ConditionalListeners;
import inspectorguidget.analyser.ListenerType;
import inspectorguidget.analyser.cfg.BasicBlock;
import inspectorguidget.analyser.cfg.CfgBuilder;
import inspectorguidget.analyser.cfg.ControlFlowGraph;
import inspectorguidget.analyser.cfg.SubGraph;
import inspectorguidget.analyser.processor.ClassesProcessor;
import inspectorguidget.analyser.processor.StatementProcessor;
import inspectorguidget.analyser.processor.wrapper.ListenersWrapper;
import spoon.processing.ProcessingManager;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.support.QueueProcessingManager;
import inspectorguidget.analyser.dataflow.Action;
import inspectorguidget.analyser.dataflow.DefUse;
import inspectorguidget.analyser.dataflow.MethodSummary;

/*
 * This class analyse listeners with conditional blocks and 
 * recovery all conditionals that handle the event source of a listener 
 * Also, filter those commands to commands that refer only to widgets  
 *  
 */
public class Command {
	ListenersWrapper wrapper;
	List<MethodSummary> summaries;
	//VariablesProcessor attributes;
	ControlFlowGraph cfg;
	DefUse defuse;
	ListenerType evType;
	SubGraph graph;
	//List<Action> candidateCommands; //Commands with conditions that manage at least one listener event.
	List<Action> commandsWithAllConds;//Commands that manage widgets attributes in their conditional statements. Storing all conditions surrounded by a command, for example, all non executed conditions
	List<Action> commands;//Filter commandsWithAllConds by removing non executed conditions of a command (e.g., previous else statements)
	List<Action> mergedCommands;
	Factory factory;	
	public Command(ListenersWrapper procWrapper,Factory factory) {
		this.wrapper = procWrapper;
		List<CtMethod> listeners = wrapper.getListeners();
		List<CtMethod> allCondListeners = wrapper.getConditionalListeners();
		this.factory = factory;
		//To measure the LOC size of listeners
		//int totalLOCListeners = getTotalLoc(getLOCListeners(listeners));
		//int totalLOCondListeners = getTotalLoc(getLOCListeners(allCondListeners));
		//System.out.println("GUIListeners size "+ totalLOCListeners);
		//System.out.println("GUICondListeners size "+ totalLOCondListeners);
		
		//candidateCommands = new ArrayList<Action>();
		commandsWithAllConds = new ArrayList<Action>();
		commands = new ArrayList<Action>();
		for(CtMethod method : allCondListeners){
			//summaries = new ArrayList<MethodSummary>();
			//summaries.add(new MethodSummary(method));			
			cfg = new ControlFlowGraph(method);
			defuse = new DefUse(cfg);	
			graph = CfgBuilder.build(method,cfg);
			evType = new ListenerType(factory);
			//candidateCommands.addAll(looking4Commands(method));
			commands.addAll(looking4Commands(method));
		}	
		//commands = findCommandRefWidgets(candidateCommands);
		mergedCommands = mergeCommands(commands);
		//List<Action> instanceOfCommands = getInstanceOfCommands(commands);		
		System.out.println("#Listeners " + listeners.size());
		System.out.println("#CondListeners " + allCondListeners.size());	
		System.out.println("#All commands " + commands.size());
		System.out.println("#Merged commands " + mergedCommands.size());
	}

	public Command() {
		// TODO Auto-generated constructor stub
	}

	private List<Action> looking4Commands(CtMethod method){
		BasicBlock block = cfg.getEntryBlock();
		Map<List<CtCodeElement>, List<CtExpression>> cmds = getElementsInConditionals(block);
		
		List<Action> actions = new ArrayList<Action>();
		for (Entry<List<CtCodeElement>, List<CtExpression>> cmd : cmds.entrySet()){	
			for  (CtCodeElement element : cmd.getKey()) {
				Action action = new Action(element, cmd.getValue(), method);
				actions.add(action); 
			}
		}
		List<Action>  listCommands = gatherCommands(actions);
		return listCommands;
	}
	
	//Traverse the CFG starting from the entry node and get all elements that are into eventRef conditionals  
	private Map<List<CtCodeElement>, List<CtExpression>> getElementsInConditionals(BasicBlock block){
		
		Map<List<CtCodeElement>, List<CtExpression>> res = new IdentityHashMap<List<CtCodeElement>, List<CtExpression>>();
		
		BasicBlock entryNode = block;
		List<BasicBlock> queue = new ArrayList<BasicBlock>();
		List<BasicBlock> visited = new ArrayList<BasicBlock>();
		queue.add(entryNode);
		
		while (!queue.isEmpty()){		
			BasicBlock parent = queue.iterator().next();
			queue.remove(parent);	
			visited.add(parent);
			for (BasicBlock child : parent.getChildren()){
				if (!queue.contains(child) && !visited.contains(child)){
					if (!child.getElements().isEmpty()){
						List<CtCodeElement> elements = child.getElements();	
						List<CtExpression> evtConditions = looking4Conditionals(elements);	
						if(!evtConditions.isEmpty()){
							res.put(child.getElements(), evtConditions);	
						}
					}
					queue.add(child);
				}
			}	
		}
		return res;	
	}	
	
	//Looking for conditions that refer to event source and widget type
	private List<CtExpression> looking4Conditionals(List<CtCodeElement> elements){
	
		List<CtExpression> res = new ArrayList<>();
		List<CtExpression> allConditions = gatherContainingCondition(elements, cfg.getExecutable());
		List<CtExpression> condRefEvents = new ArrayList<>();	
		
		//Verify if the elements has conditions
		List<CtExpression> allCondRefEvents = new ArrayList<>();	
		if (!allConditions.isEmpty()) {				
			for (CtExpression cond : allConditions){
				if (isConditionEventRef(cond) || isCondRefWidgets(cond)){
					allCondRefEvents.add(cond);
				}	 
			}			
			//It means that has at least one condition that manage the event or widget
			if (!allCondRefEvents.isEmpty()){
				condRefEvents.addAll(allConditions);
			}
//			//Commands can have nested conditions that not refer to events
			Iterator<CtExpression> it = allConditions.iterator();
			while (it.hasNext() && allCondRefEvents.size() > 0){
				CtExpression condition = it.next();
				condRefEvents.add(condition);
				allCondRefEvents.remove(condition);
				
			}
		}	
		return condRefEvents;
	}	

	
	//Verify if a condition refers to listener events of a method 
	private boolean isConditionEventRef(CtExpression condition){		
		List<CtParameter> parameters = defuse.getExecutable().getParameters();
		List<CtTypeReference> typesParam = new ArrayList<>();
		for (CtParameter parameter : parameters){//get parameters'type of a method
			typesParam.add(parameter.getType());		
		}
		
		Set<CtVariable> variables = defuse.getDeepDef(condition);//TODO: improve to check firstly type of vars
		if (!variables.isEmpty()){
			for (CtVariable var : variables){	
				CtTypeReference typeVar = var.getType();
				if (typeVar != null){
					if (evType.isEventRef(typeVar) && typesParam.contains(typeVar)){
						return true;
					}
				}
			}
		}
		else{
			List<CtTypeReference> typeRefs = getDeepDef2(condition);
			for (CtTypeReference typeRef : typeRefs){	
				if (evType.isEventRef(typeRef) && typesParam.contains(typeRef)){
					return true;
				}
			}
		}		
		return false;
	}
	
	//Workaround to force to get definitions for some variables that is not possible get the declaration/defs by using defuse.getDeepDef(condition): bug in defuse
	private List<CtTypeReference> getDeepDef2(CtExpression condition){
		List<CtTypeReference> res = new ArrayList<CtTypeReference>();
		
		List<CtVariableAccess> vars = defuse.findUsedVar(condition);
		for (CtVariableAccess var : vars){	
//			if (var.getVariable() instanceof CtFieldReference){//conds such as KeyEvent.VK_ESCAPE, where getDeepDef(var) does not found the var declaration
//				CtFieldReference fieldRef = (CtFieldReference) var.getVariable();//FIXME
//				CtTypeReference fieldType = fieldRef.getDeclaringType();
//				res.add(fieldType);
//			}
//			if (var.getVariable() instanceof CtVariable){
//				CtVariable variable = (CtVariable) var.getVariable();
//				res.add(var.getType());//maybe i cannot
//				
//			}
//			else 
			if (var.getVariable() instanceof CtParameterReference){
				CtParameterReference param = (CtParameterReference) var.getVariable();
				res.add(param.getType());
			}
			else {
				Set<CtExpression> defs = getVarDefs(var);
				for (CtExpression def : defs){
					res.addAll(getDeepDef2(def));
				}
			}
		}
		return res;	
	}
	
	//Checking if an expression from a condition refers to a widget type  
	private boolean isCondRefWidgets(CtExpression expr){
		ComponentsAction access = new ComponentsAction();
			if (expr instanceof CtInvocation){//"Copy to clipboard".equals(label);(label.contains("Copy")
				CtInvocation invoke = (CtInvocation) expr;	
				if (invoke.getExecutable() != null && access.isGetProperty(invoke.getExecutable().getSimpleName())){
						return true;
				}
				else if (invoke.getTarget() != null && isCondRefWidgets(invoke.getTarget())){
					return true;
				}
				else {
					List<CtExpression> arguments = invoke.getArguments();	
					for (CtExpression arg : arguments){
						if (arg != null){
							if (isCondRefWidgets(arg)){
								return true;
							}
						}
					}	
				}
			}
			else if (expr instanceof CtVariableAccess){
				CtVariableAccess varAccess = (CtVariableAccess) expr;
				if (evType.isComponent(varAccess.getType())){
					return true;
				}
				else{
					Set<CtExpression> defs = getVarDefs(varAccess);
					if (!defs.isEmpty()){
						for (CtExpression def : defs){
							if (isCondRefWidgets(def)){
								return true;	
							}
						}
					}
				}
			}
			else if (expr instanceof CtBinaryOperator){
				//Verify if is isDeclaredInSourceCode
				CtBinaryOperator op = (CtBinaryOperator) expr;
				if (op.getKind().name().equals("INSTANCEOF")){	
					if (evType.isComponentRef(op.getRightHandOperand())){
						return true;
					}
				}
				else if (isCondRefWidgets(op.getLeftHandOperand()) || isCondRefWidgets(op.getRightHandOperand())){
						return true;
				} 
			}
			else if (expr instanceof CtUnaryOperator){
				CtUnaryOperator unary = (CtUnaryOperator) expr;	
				if (evType.isComponentRef(unary.getOperand())){
					return true;
				}
				else if (isCondRefWidgets(unary.getOperand())){
						return true;
				} 
			}
			
		return false;
		
	}
	
	public List<CtExpression> gatherContainingCondition(List<CtCodeElement> elements, CtExecutable method){
		List<CtExpression> res = new ArrayList<CtExpression>();		
		CtCodeElement action = elements.get(0);	
		CtElement parent = action;
		if (action != null){
			try{
				parent = action.getParent();
				while (parent != null && parent != method){
					res.addAll(exploreConditionals(action, (CtCodeElement) parent));//optimize to explore only action's parents
					parent = parent.getParent();
				}
			}
			catch(ParentNotInitializedException e){
				System.out.println("Parent init exeption: " + parent);
			}
		}
		return res;
	}
	
	
    public List<CtExpression> exploreConditionals(CtCodeElement action, CtCodeElement parent){
    	
    	CtUnaryOperator<Boolean> negCondition = factory.Core().createUnaryOperator();
    	List<CtExpression> res = new ArrayList<CtExpression>(); 
			if(parent instanceof CtIf){
	            CtIf ctif = (CtIf) parent;
	            CtStatement thenpart = ctif.getThenStatement();
	            
	            if (isContainedBy(action,thenpart)) {    	
	            	 res.add(ctif.getCondition());
	            	 return res;
	            }
	            else{
	                List<CtExpression> childCond = exploreConditionals(action, thenpart);
	                if(!childCond.isEmpty()){
	                    res.add(ctif.getCondition());
	                    res.addAll(exploreConditionals(action, thenpart));
	                }
	            }
	            CtStatement elsepart = ctif.getElseStatement();
	            if(isContainedBy(action,elsepart)){
	                negCondition.setOperand(ctif.getCondition());
					negCondition.setKind(UnaryOperatorKind.NOT);
	                res.add(negCondition);
					return res;
	            }
	            else{
	                List<CtExpression> childCond = exploreConditionals(action, elsepart);
	                if(!childCond.isEmpty()){
	                    res.add(ctif.getCondition());
	                	res.addAll(exploreConditionals(action, elsepart));
	                }
	            }
	        } 
	        else if(parent instanceof CtWhile){
				CtWhile while_ = (CtWhile) parent;
				res.add(while_.getLoopingExpression());
			}
			else if(parent instanceof CtFor){
				CtFor for_ = (CtFor) parent;
				res.add(for_.getExpression());
			}
			else if(parent instanceof CtCase){
				CtCase case_ = (CtCase) parent;
				res.add(case_.getCaseExpression());
			}
			else if(parent instanceof CtConditional){//TODO: get the negation too
				CtConditional cond = (CtConditional) parent;
				res.add(cond.getCondition());
			}
			else if(parent instanceof CtDo){
				CtDo do_ = (CtDo) parent;
				res.add(do_.getLoopingExpression());
			}
			else if (parent instanceof CtSwitch){
				CtSwitch switch_ = (CtSwitch) parent;
				res.add(switch_.getSelector());
			}
    	
        return res; 
    }

    //Verify if an action is contained in CtStatement 
  	public boolean isContainedBy (CtCodeElement action, CtCodeElement statement){
  		
  		ConditionalListeners cl = new ConditionalListeners();
  		if (cl.isConditionalStatement(statement)){
  			List<CtCodeElement> lines = cl.processConditionalStmts(statement);
  			for (CtCodeElement elem : lines){
  				if (isContainedBy(action, elem)){
  					return true;
  				}
  			}
  		}
  		else if (statement == action){
  			return true;
  		}
  		return false;
  		

  			
  	}
    //Gather elements of commands that are found into same conditional statement
	public List<Action> gatherCommands(List<Action> actions){
		
		List<Action> cmds = new ArrayList<Action>();
		List<Action> tmp = new ArrayList<Action>(actions);
		List<Action> linked = new ArrayList<Action>();
		
		for (Action action : actions){
			List<CtCodeElement> statements = new ArrayList<>();
			if(!linked.contains(action)) {
				tmp.remove(action);//Action must not interact to itself					
				statements.add(action.getStatement());
				
				Iterator<Action> elements = tmp.iterator();
				while (elements.hasNext()){
					Action element = elements.next();	
					if (!action.getConditions().isEmpty() && !element.getConditions().isEmpty()){						
						
						if (action.getConditions() == element.getConditions() ||
							action.getConditions().toString().equals(element.getConditions().toString())){	
							statements.add(element.getStatement());	
							linked.add(element);
							elements.remove();
						}
					}
				}
//				if (!isAdded(cmds, action)){				
//					cmds.add(new Action(statements, action.getConditions(), action.getSource()));
//				}
				if (!isStopStatement(statements)){
					cmds.add(new Action(statements, action.getConditions(), action.getSource()));
				}
			}
		}
		return cmds;
	}	
		
	//Refactor: comes from ComponentsPropertiesProcessor
	private Set<CtExpression> getVarDefs(CtVariableAccess var){
		Set<CtExpression> res = new HashSet<CtExpression>();

		Set<CtCodeElement> defs = defuse.getReachingDef(var);
		CtVariable dec = var.getVariable().getDeclaration();
		if(defs != null){
			for(CtCodeElement def : defs){
				res.addAll(processLine(def));
			}
		}
		else if (dec != null && dec.getDefaultExpression() != null){			
				res.add(dec.getDefaultExpression());
		}
		
		return res;
	}
		
	//Refactor: comes from ComponentsPropertiesProcessor
	private List<CtExpression> processLine(CtCodeElement line){
	ConditionalListeners condStmt = new ConditionalListeners();
	
		List<CtExpression> res = new ArrayList<>();
		if (line instanceof CtExecutable){
			for (CtCodeElement lMethod : ((CtExecutable) line).getBody().getStatements()){		
				processLine(lMethod);	
			}
		}
		else if (condStmt.isConditionalStatement(line)){
			List<CtCodeElement> lines = condStmt.processConditionalStmts(line);
			for (CtCodeElement lConditional : lines){
				processLine(lConditional);			
			}
		}
		else if (line instanceof CtExpression){//CtInvocation, CtVariableAccess, CtFieldAccess, CtAssignment, etc.
			CtExpression expr = (CtExpression) line;
			res.add(expr);	
		}
		else if (line instanceof CtVariable){
			CtVariable var = (CtVariable) line;
			if (var.getDefaultExpression() != null){
				res.add(var.getDefaultExpression());
			}
		}
		return res;
	}
	
	//Merge commands that represent the same command
	public List<Action> mergeCommands(List<Action> cmds){		
		List<Action> mergedCmds = new ArrayList<Action>();
		for (Action cmd : cmds){
			cfg = new ControlFlowGraph(cmd.getSource());
			defuse = new DefUse(cfg);
			List<CtExpression> conditions = cmd.getConditions();
			CtExpression surroundedCond = null;
			for (CtExpression cond : conditions){
				if (isCondRefWidgets(cond) || isConditionEventRef(cond)){//Get only the conditions that refer a widget/event, also remove the negation condition	
					surroundedCond = cond;
					break;
				}
			}
			List<CtCodeElement> statements  = getSurroundedBlock(cmd, surroundedCond);
			List<CtExpression> list = new ArrayList<CtExpression>();
			list.add(surroundedCond);
			Action action = new Action(statements, list, cmd.getSource());
			if (!isAdded(mergedCmds, action)){
				mergedCmds.add(action);
			}
		
		}
		List<Action> res = removeNestedCommands(mergedCmds);
		return res;
	}
	
	public List<Action> removeNestedCommands(List<Action> actions){
		List<Action> res = new ArrayList<Action>();
		
//		IdentityHashMap<CtMethod, List<Action>> cmds = gatherCommandsBySource(actions);
//		for (Entry<CtMethod, List<Action>> entry: cmds.entrySet()){
//			IdentityHashMap<Action, List<Action>> cmdsANDNested = new IdentityHashMap<Action, List<Action>>();
//			
//			//Compare the each command with in a method
//			for(Action cmd: entry.getValue()){
//				int cmdPosLine = cmd.getStatements().get(0).getPosition().getLine();
//				List<Action> nested = new ArrayList<Action>();
//				Iterator<Action> it = entry.getValue().iterator();
//				while(it.hasNext()){
//					Action tmp = it.next();
//					if (cmd != tmp){//do not compare the same command
//						int tmpPosLine = tmp.getStatements().get(0).getPosition().getLine();
//						if (cmd.getSource() == tmp.getSource() &&
//							tmpPosLine > cmdPosLine &&//tmpLine should be greater cmdLine to be nested
//							isNested(tmp.getStatements(), cmd.getStatements())){//tmp is contained by command					
//							 nested.add(tmp);
//						}
//					}
//				}
//				//Getting the cmd and its nested cmds
//				cmdsANDNested.put(cmd, nested);		
//		
//			}
//			res.addAll(findProperCommand(cmdsANDNested));
			
		//}
		IdentityHashMap<Action, List<Action>> cmdsANDNested = new IdentityHashMap<Action, List<Action>>();
		for (Action cmd : actions){//want to know all nested for this command	
		int cmdPosLine = cmd.getStatements().get(0).getPosition().getLine();
			List<Action> nested = new ArrayList<Action>();
			Iterator<Action> it = actions.iterator();
			while(it.hasNext()){
				Action tmp = it.next();
				if (cmd != tmp){//not compare the same command
				int tmpPosLine = tmp.getStatements().get(0).getPosition().getLine();
					if (cmd.getSource() == tmp.getSource() &&
						tmpPosLine > cmdPosLine &&//tmpLine should be greater cmdLine to be nested: to avoid nested cmds that share only one and same stmt
						isNested(tmp.getStatements(), cmd.getStatements())){//tmp is contained by command					
						 nested.add(tmp);
					}
				}
			}
			//Getting the cmd and its nested cmds
			cmdsANDNested.put(cmd, nested);				
		}
		res.addAll(findProperCommand(cmdsANDNested));
		return res;
	}

	//Verify if statements1 is contained into statements2: TODO: Refactor by merging with isContainedBy
	public boolean isNested(List<CtCodeElement> candidate, List<CtCodeElement> cmdStmts){
		ConditionalListeners cl = new ConditionalListeners();
		boolean isNested = false;
		for (CtCodeElement cand : candidate){//Check all statements of the candidate to nested
			if (!cl.isConditionalStatement(cand)){
				for (CtCodeElement stmt : cmdStmts){
					if(isContainedBy(cand,stmt)){//Check if the cand is contained into the stmt
						isNested = true;
					}
					else{
						return false;
					} 
				}
			}
			else {//process the conditional stmts to get only elements
				List<CtCodeElement> elements = cl.processConditionalStmts(cand);
				isNested = isNested(elements, cmdStmts);
				if (!isNested){
					return false;
				}
			}
		}
		return isNested;
	}
	
	//Check all nested command and get the most pertinent command
	public List<Action> findProperCommand(IdentityHashMap<Action,List<Action>> cmdsANDNested){
		IdentityHashMap<Action,List<Action>> cmdANDparents = new  IdentityHashMap<Action,List<Action>>();
		
		//For each nested get parents		
		for (Entry<Action, List<Action>> entry : cmdsANDNested.entrySet()){;
			Action parent = entry.getKey();
			for (Action child : entry.getValue()){
				List<Action> parents = cmdANDparents.get(child);
				if (parents == null){
					parents = new ArrayList<Action>();
					parents.add(parent);
					cmdANDparents.put(child, parents);
				}
				else{
					parents.add(parent);
				}	
			}
		}
		
		List<Action> candidate = new ArrayList<Action>();
		Set<Action> notCandidate = new HashSet<Action>();
		//Get the proper commands by filter their nested commands
		for (Entry<Action, List<Action>> entry : cmdsANDNested.entrySet()){
			Action cmd = entry.getKey();
			List<Action> nested = entry.getValue();
			candidate.add(cmd);
			if (nested.size() == 1){//only one nested command
				notCandidate.addAll(nested);
			}
			else if (nested.size() > 1){//more one nested command
				List<Action> cmds = new ArrayList<Action>();
				cmds.add(cmd);
				cmds.addAll(entry.getValue());
				if(isAllNested(cmds,cmdANDparents)){
					notCandidate.addAll(nested);
				}
				else{//All commands under cmd are nested
					notCandidate.add(cmd);
				}
			}
			
		}
		if (!notCandidate.isEmpty()){
			candidate.removeAll(notCandidate);
		}
		return candidate;
	}

	//Check if the commands are all nested
	public boolean isAllNested(List<Action> cmds, IdentityHashMap<Action, List<Action>> cmdsANDparents2){
		
		for (Action cmd : cmds){
			//List<Action> parents = new ArrayList<Action>();
			List<Action> parents = cmdsANDparents2.get(cmd);	 
			if (parents != null){
				List<Action> cmds2 = new ArrayList<Action>(cmds);
				cmds2.remove(cmd);
				//If all parents are the same
				if(parents.containsAll(cmds2)){//If some cmd has all parents in the list cmds2, it means all cmds are nested
					return true;
				}
			}
		}
		return false;
	}
	
	//Return the block surrounded by this condition 
	private List<CtCodeElement> getSurroundedBlock(Action cmd, CtExpression cond) {
		List<CtCodeElement> res = new ArrayList<CtCodeElement>();
		CtUnaryOperator<Boolean> negation = factory.Core().createUnaryOperator();
		
		List<CtCodeElement> stmts = cmd.getStatements();
		CtCodeElement stmt1 = stmts.get(0);		
		CtElement parent = stmt1;
		if (stmt1 != null){			
			try{
				parent = stmt1.getParent();
				while (parent != null && parent != cmd.getSource()){
					if (parent instanceof CtIf){
						CtIf if_ = (CtIf) parent;
						negation.setOperand(if_.getCondition());
						negation.setKind(UnaryOperatorKind.NOT);
						if (if_.getCondition() == cond){
							CtStatement thenPart = if_.getThenStatement();			
							res.add(thenPart);
							return res;
						}else if (negation.toString().equals(cond.toString())) {//Handle the else part as a command
							CtStatement elsePart = if_.getElseStatement();			
							res.add(elsePart);
							return res;	
						}
					}
					 else if(parent instanceof CtWhile){
							CtWhile while_ = (CtWhile) parent;
							if (while_.getLoopingExpression() == cond){
								res.add(while_.getBody());
								return res;
							}		
					}
					else if(parent instanceof CtFor){
						CtFor for_ = (CtFor) parent;
						if (for_.getExpression() == cond){
							res.add(for_.getBody());
							return res;
						}
					}
					else if(parent instanceof CtCase){
						CtCase case_ = (CtCase) parent;
						if (case_.getCaseExpression() == cond){
							res.addAll(case_.getStatements());
							return res ;
						}
					}
					else if(parent instanceof CtConditional){
						CtConditional conditional = (CtConditional) parent;
						if (conditional.getCondition() == cond){
							res.add(conditional.getThenExpression());
							return res;
						}
					}
					else if(parent instanceof CtDo){
						CtDo do_ = (CtDo) parent;
						if (do_.getLoopingExpression() == cond){
							res.add(do_.getBody());
							return res;
						}
					}
					else if (parent instanceof CtSwitch){
						CtSwitch switch_ = (CtSwitch) parent;
						if (switch_.getSelector() == cond){
							res.addAll(switch_.getCases());
							return res;
						}
					}
					parent = parent.getParent();
				}
			}
			catch(ParentNotInitializedException e){
				System.out.println("Parent init exeption: " + parent);
			}
		}
		return null;
	}
	
	private boolean isAdded(List<Action> actions, Action action) {
		for (Action a : actions){
			if (a.toString().equals(action.toString())){
				return true;
			}
		}
		return false;
	}
	
	//Check if the statements of a command have only return statement
	private boolean isStopStatement(List<CtCodeElement> elements) {
		
//		if (elements.size() == 1 && elements.toString().contains("return")){
//			if (elements.get(0) instanceof CtReturn){
//					CtReturn ret = (CtReturn) elements.get(0);
//					if (ret.getReturnedExpression() == null){
//						return true;
//					}
//			}	
//		}
		if (elements.size() == 1){
			if (elements.toString().contains("return")){
				if (elements.get(0) instanceof CtReturn){
					CtReturn ret = (CtReturn) elements.get(0);
					if (ret.getReturnedExpression() == null){
						return true;
					}
				}	
			}
			else if (elements.toString().contains("break")){ //|| elements.toString().contains("continue")){
				return true;
			}
			
		}
		return false;
	}
		
	/*
	 * Refine commands by recovering only commands that contain at least one keyword "instanceof" in their conditionals
	 * That means, commands that can listen to more one widget type
	 */
	public List<Action> getInstanceOfCommands(List<Action> cmds) {
		List<Action> candidates = new ArrayList<>();
		
		for ( Action action : cmds){
			List<CtExpression> conditions = action.getConditions();
			for (CtExpression cond : conditions){				
				if (cond instanceof CtBinaryOperator){	
					CtBinaryOperator operator = (CtBinaryOperator) cond;					
					if (operator.toString().contains("instanceof")){						
						candidates.add(action);
					}
				}
			}
		}
		return candidates;
	}	
	//Get commands surrounded by only executed conditions 
	public List<Action> getCommands(){
		return commands;
	}
	
	//Get commands surrounded by only executed conditions 
	public List<Action> getMergedCommands(){
		return mergedCommands;
	}
	//Get commands surrounded by all conditions including non executed conditions
	public List<Action> getCommandsWithAllConds(){
		return commandsWithAllConds;
	}
	
//	public Map<CtMethod, Integer> getLOCListeners(List<CtMethod>  methods){
//		Map<CtMethod,Integer> sizes = new IdentityHashMap<CtMethod, Integer>();
//		for (CtMethod method : methods){
//			ProcessingManager processorManager = new QueueProcessingManager(factory);
//			StatementProcessor statementCounter = new StatementProcessor();
//			processorManager.addProcessor(statementCounter);
//			processorManager.process(method);
//			int loc = statementCounter.getCount();
//			sizes.put(method, loc);
//		}
//		return sizes;
//	}
//	public int getTotalLoc(Map<CtMethod,Integer> size){
//		int totalLoc = 0;
//		for (Entry<CtMethod, Integer> entry : size.entrySet()){
//			totalLoc += entry.getValue();
//		}
//		return totalLoc;	
//	}
}	
//	/**
//	 * Return true if the declaration of the type is in the analyzed source code
//	 */
//	private boolean isDeclaredInsourceCode(CtTypeReference type){
//		if(type != null) {
//			CtSimpleType dec = type.getDeclaration();
//			if (dec != null){
//				return true;
//			}	
//		}
//		return false;
//	}
//	private void print(List<Action> cmds) {		
//		
//		Map<CtMethod, List<Action>> cmds4Method = new IdentityHashMap<>();	
//		for(Action cmd : cmds){	
//			CtMethod source = cmd.getSource();
//			List<Action> actions = new ArrayList<>();
//			actions.add(cmd);
//			
//			List<Action> action = cmds4Method.get(source);
//			if (action == null){
//				cmds4Method.put(source, actions);
//			}
//			else {
//				action.addAll(actions);
//			}
//		}
//		
//		for (Entry <CtMethod, List<Action>> entry : cmds4Method.entrySet()){
//				StringBuffer content = new StringBuffer();
//				CtClass clazz = (CtClass) entry.getKey().getParent();
//				String fileName =  clazz.getQualifiedName() + "."+ entry.getKey().getSimpleName() + ".txt";
//				String dir = config.getOutputFolder()+"/commands/";
//				
//				content.append(entry.getKey()+ "\n");
//				
//				for (Action entry2 : entry.getValue()) {
//					content.append("COMMAND" + "\n");
//					content.append(entry2);
//					content.append("----------------"+ "\n");
//				}
//				writeFile(dir, fileName, content.toString());
//		}
//		
//	}
	
//	/**
//	 * Write 'content' in 'dir'/'file' 
//	 */
//	public static void writeFile(String dir, String file, String content){
//		try{
//			File newDir = new File(dir);
//			newDir.mkdirs();
//			FileWriter fw;
//			if (dir.contains(file)){
//				fw = new FileWriter(dir+"/"+file, true);
//			}
//			else {
//				fw = new FileWriter(dir+"/"+file, false);	
//			}
//			
//			BufferedWriter output = new BufferedWriter(fw);
//			output.write(content);
//			output.flush();
//			output.close();
//		}
//		catch(Exception e){
//			System.out.println(e);
//		}
//	}
	
	//To test after filtering: instanceOfCommands, etcs
//	private void printCommands(List<Action> commands) {		
//		System.out.println("FILTER");
//		System.out.println("--------------");
//		for(Action cmd : commands){	
//			System.out.println("ClassName "+ cmd.getSource().getDeclaringType().getQualifiedName());
//			System.out.println("block "+ cmd.getStatements());
//			System.out.println("condition "+ cmd.getConditions());
//			System.out.println("--------------");
//		}
//	}

//To test after filtering: instanceOfCommands, etcs
//	private void printCommands(Set<Action> commands) {		
//		System.out.println("FILTER");
//		System.out.println("--------------");
//		for(Action cmd : commands){	
//			System.out.println(cmd.getSource().getDeclaringType().getQualifiedName());
//			System.out.println(cmd.getStatements());
//			System.out.println(cmd.getConditions());
//			System.out.println("--------------");
//		}
//	}

	
//	public List<Action> findCommandRefWidgets(List<Action> candidates){
//	List<Action> res = new ArrayList<Action>();
//	for (Action cand : candidates){
//		cfg = new ControlFlowGraph(cand.getSource());
//		defuse = new DefUse(cfg);
//		List<CtExpression> condRefWidget = new ArrayList<CtExpression>();
//		for (CtExpression condition : cand.getConditions()){
//			if ( isCondRefWidgets(condition)){//Should verify if the widget condition handles the listener event
//				condRefWidget.add(condition);
//				//Storing only executed conditions
//				res.add(new Action(cand.getStatements(), condRefWidget, cand.getSource()));
//				//Also storing non executed conditions (e.g. previous else statements)
//				commandsWithAllConds.add(cand);
//				break;
//			}
//			else {
//				condRefWidget.add(condition);
//			}
//		}
//	}
//	return res;
//}
	
	/*
	 * Gather commands that are detected in the same method
	 */
//	private IdentityHashMap<CtMethod,List<Action>> gatherCommandsBySource(List<Action> commands){		
//		IdentityHashMap<CtMethod, List<Action>> res = new IdentityHashMap<CtMethod, List<Action>>();	
//		
//		for (Action cmd : commands){
//			List<Action> cmds = res.get(cmd.getSource());
//			if (cmds == null ){
//				cmds = new ArrayList<Action>();
//				cmds.add(cmd);
//				res.put(cmd.getSource(), cmds);
//			}
//			else{
//				cmds.add(cmd);
//			}
//		}
//		return res;	
//	}
	


		
		



		
		
	


	

