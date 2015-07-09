package inspectorguidget.analyser.dataflow;

import inspectorguidget.analyser.cfg.BasicBlock;
import inspectorguidget.analyser.cfg.ControlFlowGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtParameterReference;

public class DefUse {
	
	/**
	 * Store lasts definitions before variables access.
	 */
	Map<CtVariableAccess<?>,Set<CtCodeElement>> reachingDef;
	
	
	/**
	 * Store used variables in a line of code.
	 */
	Map<CtCodeElement,List<CtVariableAccess<?>>> usedVar;	
	
	
	/**
	 * Store variable definitions at the last statements of blocks
	 */
	Map<BasicBlock,Map<CtVariable<?>,Set<CtCodeElement>>> blocksDef;
	
	
	List<CtParameter<?>> parameters;
	List<CtField<?>> attributes;
	ControlFlowGraph cfg;
	
	
	public DefUse(ControlFlowGraph cfg) {
		reachingDef = new IdentityHashMap<>();
		usedVar = new IdentityHashMap<>();
		blocksDef = new HashMap<>();
		parameters = new ArrayList<>();
		attributes = new ArrayList<>();
		this.cfg = cfg;
		//System.out.println("Method name "+ cfg.getMethod().getDeclaringType().getQualifiedName());
		
		init();
		process(cfg.getEntryBlock());
	}
	
	/**
	 * Find definitions of method's parameters and attributes
	 */
	private void init(){
		parameters = cfg.getExecutable().getParameters();
		CtElement parent = cfg.getExecutable().getParent();
		if(parent instanceof CtClass){
			attributes = ((CtClass<?>)parent).getFields(); //TODO: look at super classes
		}
	}
	
	/**
	 * Process the sequence of statements to updates definitions
	 */
	private void process(BasicBlock block, Map<CtVariable<?>,Set<CtCodeElement>> definitions){
		
		//Process statements
		for(CtCodeElement line : block.getElements()){
			if(line instanceof CtLocalVariable){
				CtLocalVariable<?> def = (CtLocalVariable<?>) line;
				//use
				CtExpression<?> rhs = def.getDefaultExpression();
				if(rhs != null){
					storeUseDef(rhs, definitions);
				}
				//def
				Set<CtCodeElement> localdef = new HashSet<CtCodeElement>();
				localdef.add(def);
				definitions.put(def, localdef);
			}
			else if(line instanceof CtAssignment){
				CtAssignment<?,?> assignment = (CtAssignment<?,?>) line;
				//use
				CtExpression<?> rhs = assignment.getAssignment();
				storeUseDef(rhs, definitions);
				//def
				CtExpression<?> assigned = assignment.getAssigned();
				if(assigned instanceof CtVariableAccess){
					CtVariable<?> lhs = ((CtVariableAccess<?>)assigned).getVariable().getDeclaration();
					Set<CtCodeElement> redef = new HashSet<CtCodeElement>();
					redef.add(line);
					definitions.put(lhs, redef);
				}
			}
			else if(line instanceof CtInvocation){
				//use
				CtInvocation<?> invoke = (CtInvocation<?>) line;
				storeUseDef(invoke, definitions);
			}
			else if(line instanceof CtReturn){
				//use
				CtReturn<?> ret = (CtReturn<?>) line;
				CtExpression<?> rhs = ret.getReturnedExpression();
				storeUseDef(rhs, definitions);
			}
		}
		
		//Store new definitions
		blocksDef.put(block, definitions);
	}
	
	/**
	 * Parent is the last visited block and child is yet visited
	 * but need to update its definitions
	 */
	private void updateDef(BasicBlock child, BasicBlock parent){
		Map<CtVariable<?>, Set<CtCodeElement>> parentDef = blocksDef.get(parent);
		
		for(CtCodeElement line : child.getElements()){
			if(line instanceof CtLocalVariable){
				CtLocalVariable<?> def = (CtLocalVariable<?>) line;
				//use
				CtExpression<?> rhs = def.getDefaultExpression();
				if(rhs != null){
					updateUseDef(rhs, parentDef);
				}
			}
			else if(line instanceof CtAssignment){
				CtAssignment<?,?> assignment = (CtAssignment<?,?>) line;
				//use
				CtExpression<?> rhs = assignment.getAssignment();
				updateUseDef(rhs, parentDef);
			}
			else if(line instanceof CtInvocation){
				//use
				CtInvocation<?> invoke = (CtInvocation<?>) line;
				updateUseDef(invoke, parentDef);
			}
			else if(line instanceof CtReturn){
				//use
				CtReturn<?> ret = (CtReturn<?>) line;
				CtExpression<?> rhs = ret.getReturnedExpression();
				updateUseDef(rhs, parentDef);
			}
		}
	}
	
	/**
	 * Entry point
	 * Breadth first exploration of the blocks of the cfg
	 */
	private void process(BasicBlock block){
		List<BasicBlock> toBeProcessed = new ArrayList<BasicBlock>();
		toBeProcessed.add(block);
		
		while(!toBeProcessed.isEmpty()){
			BasicBlock selected = toBeProcessed.get(0);
			
			//Retrieve definitions from visited parents
			List<BasicBlock> parents = selected.getParents();
			Map<CtVariable<?>, Set<CtCodeElement>> allDefinitions;
			if(parents.isEmpty()){
				allDefinitions = new HashMap<>();
			}
			else{
				List<Map<CtVariable<?>,Set<CtCodeElement>>> parentDef = new ArrayList<>();
				for(BasicBlock parent : parents){
					Map<CtVariable<?>, Set<CtCodeElement>> superDef = blocksDef.get(parent);
					if(superDef != null){
						parentDef.add(superDef);
					}
				}
				allDefinitions = mergeDef(parentDef);
			}

			//Process the block
			process(selected,allDefinitions);
			
			//Add children to the work list if not yet visited
			//and store used variables in conditional
			for(BasicBlock child : selected.getChildren()){
				if(blocksDef.get(child) == null){
					toBeProcessed.add(child);
					
					CtExpression<?> condition = selected.getCondition(child);
					if(condition != null){
						storeUseDef(condition, allDefinitions);
					}
				}
				else{
					updateDef(child,selected);
				}
			}
			
			toBeProcessed.remove(0);
		}
	}
	
	/**
	 * Merge definitions in a new map
	 */
	private Map<CtVariable<?>, Set<CtCodeElement>> mergeDef(List<Map<CtVariable<?>, Set<CtCodeElement>>> definitions){
		Map<CtVariable<?>, Set<CtCodeElement>> res = new HashMap<>();
		
		for(Map<CtVariable<?>, Set<CtCodeElement>> definition : definitions){
			for(Entry<CtVariable<?>, Set<CtCodeElement>> entry : definition.entrySet()){
				Set<CtCodeElement> defs = res.get(entry.getKey());
				if(defs == null){
					defs = new HashSet<CtCodeElement>();
					res.put(entry.getKey(), defs);
				}
				defs.addAll(entry.getValue());
			}
		}
		
		return res;
	}
	
	
	/**
	 * Find variables in the expression, find their last definitions and store these pairs
	 */
	private void storeUseDef(CtExpression<?> expr, Map<CtVariable<?>,Set<CtCodeElement>> definitions){
		List<CtVariableAccess<?>> varAccess = findUsedVar(expr);
		if(!varAccess.isEmpty()){
			usedVar.put(expr, varAccess);
		}
		for(CtVariableAccess<?> var : varAccess){
			CtVariable<?> decl = var.getVariable().getDeclaration();
			reachingDef.put(var, definitions.get(decl)); //store (use,def)
		}
	}
	
	/**
	 * Find variables access in the expression, and add new definitions
	 */
	private void updateUseDef(CtExpression<?> expr, Map<CtVariable<?>,Set<CtCodeElement>> definitions){
		List<CtVariableAccess<?>> varAccess = findUsedVar(expr);
		for(CtVariableAccess<?> var : varAccess){
			CtVariable<?> decl = var.getVariable().getDeclaration();
			
			Set<CtCodeElement> oldDef = reachingDef.get(var);
			if(oldDef != null){
				Set<CtCodeElement> newDef = definitions.get(decl);
				if(newDef != null)oldDef.addAll(newDef);
			}
		}
	}
	
	/**
	 * Get used variables in this expression.
	 * If the expression is a method call, the caller is included in used variables
	 */
	public List<CtVariableAccess<?>> findUsedVar(CtExpression<?> expr){
		List<CtVariableAccess<?>> res = new ArrayList<>();
		
		if( expr instanceof CtFieldAccess){
			CtFieldAccess<?> access = (CtFieldAccess<?>) expr;
			if(access.getTarget() != null){
				res.addAll(findUsedVar(access.getTarget()));
			}
			else{
				res.add(access);
			}
		}
		else if(expr instanceof CtBinaryOperator){
			CtBinaryOperator<?> op = (CtBinaryOperator<?>) expr;
			res.addAll(findUsedVar(op.getLeftHandOperand()));
			res.addAll(findUsedVar(op.getRightHandOperand()));
		}
		else if (expr instanceof CtUnaryOperator){//Added to get all conditions of a command in Command class
			CtUnaryOperator<?> unary = (CtUnaryOperator<?>) expr;
			res.addAll(findUsedVar(unary.getOperand()));
		}
		else if(expr instanceof CtInvocation){
			CtInvocation<?> invoke = (CtInvocation<?>) expr;
			res.addAll(findUsedVar(invoke.getTarget()));
			for(Object param : invoke.getArguments()){
				res.addAll(findUsedVar((CtExpression<?>)param));
			}
		}
		else if(expr instanceof CtVariableAccess){
			res.add((CtVariableAccess<?>) expr);
		}

		return res;
	}
	
//	/**
//	 * Find all super classes in the source code
//	 */
//	private List<CtClass<?>> getAllSuperClasses(CtClass<?> clazz){
//		//TODO: move this method in TypeHierarchy
//		List<CtClass<?>> res = new ArrayList<>();
//		res.add(clazz);
//		
//		CtTypeReference<?> superRef = clazz.getSuperclass();
//		if(superRef != null){
//			CtType<?> decl = superRef.getDeclaration();
//			if(decl instanceof CtClass){
//				res.addAll(getAllSuperClasses((CtClass<?>) decl));
//			}
//		}
//		
//		return res;
//	}
//	
//	/**
//	 * Get fields from the classes hierarchy 
//	 */
//	private List<CtField<?>> getSuperField(CtClass<?> clazz){
//		List<CtField<?>> res = new ArrayList<>();
//		
//		List<CtClass<?>> allClasses = getAllSuperClasses(clazz);
//		for(CtClass<?> cl : allClasses){
//			res.addAll(cl.getFields());
//		}
//		
//		return res;
//	}
//	
//	/**
//	 * Get fields from classes hierarchy & containing class if @clazz is nested
//	 */
//	private List<CtField<?>> getAllField(CtClass<?> clazz){
//		List<CtField<?>> res = new ArrayList<>();
//
//		CtElement topClass = clazz.getParent();
//		if(topClass instanceof CtClass){
//			res.addAll(getSuperField((CtClass<?>) topClass));
//		}
//		
//		res.addAll(getSuperField(clazz));
//		
//		return res;
//	}
//	
//	/**
//	 * return true is clazz is declared anonymous
//	 */
//	private boolean isAnonymousClass(CtClass<?> clazz){
//		boolean res = false;
//		
//		CtElement topClass = clazz.getParent();
//		if(topClass instanceof CtClass){
//			CtElement topTopClass = topClass.getParent();
//			if(topTopClass instanceof CtNewClass){
//				return true;
//			}
//		}
//		
//		return res;
//	}
	
	/**
	 * Two elements should have a different ID if their have the same
	 * hashcode but different location in the source code.
	 */
	private String getUniqueID(CtElement elem){
		if(elem.getPosition() == null) return "GeneratedStatement" + System.currentTimeMillis();
		return elem.getPosition().getFile().getName() + ":" +
				elem.getPosition().getSourceStart() + ":" +
				elem.getPosition().getSourceEnd() + ":" +
				elem.hashCode();
	}
	
	/*******
	 * API *
	 *******/
	
	/**
	 * Get variables used in this line
	 */
	public List<CtVariableAccess<?>> getUsedVariables(CtCodeElement line){
		if(line instanceof CtLocalVariable){
			CtLocalVariable<?> def = (CtLocalVariable<?>) line;
			CtExpression<?> rhs = def.getDefaultExpression();
			if(rhs != null){
				return findUsedVar(rhs);
			}
		}
		else if(line instanceof CtAssignment){
			CtAssignment<?,?> assignment = (CtAssignment<?,?>) line;
			CtExpression<?> rhs = assignment.getAssignment();
			return findUsedVar(rhs);
		}
		else if(line instanceof CtInvocation){
			CtInvocation<?> invoke = (CtInvocation<?>) line;
			return findUsedVar(invoke);
		}
		else if(line instanceof CtReturn){
			CtReturn<?> ret = (CtReturn<?>) line;
			CtExpression<?> rhs = ret.getReturnedExpression();
			return findUsedVar(rhs);
		}
		return null;
	}
	
	/**
	 * Get possibles definitions for this variable at this line.
	 * Can return null if not defined (i.e: access to final variables from nested classes) 
	 */
	public Set<CtCodeElement> getReachingDef(CtVariableAccess<?> var){
		return reachingDef.get(var);
	}
	
	/**
	 * Get attributes & methods parameters dependences for this variable access
	 */
	public Set<CtVariable<?>> getDeepDef(CtVariableAccess<?> var){
		Set<CtVariable<?>> res = new HashSet<>();

		Set<CtCodeElement> defs = getReachingDef(var);
		if(defs != null){
			for(CtCodeElement def : defs){
				List<CtVariableAccess<?>> topVars = getUsedVariables(def);
				if (topVars != null){
					for(CtVariableAccess<?> topVar : topVars){
						res.addAll(getDeepDef(topVar));
					}
				}
			}
		}
		else{
			CtVariable<?> decl = var.getVariable().getDeclaration();
			if(decl instanceof CtField || decl instanceof CtParameter){
				res.add(decl);
			}
		
			/*
			 * Workaround to fix a bug found in Spoon: 
			 * Getting declaration from a anonymous
			 */
			if (decl == null){
				if (var.getVariable() instanceof CtParameterReference){
					CtParameterReference<?> p = (CtParameterReference<?>) var.getVariable();
//					 String name = p.getDeclaringExecutable().getSimpleName(); 
					 String decType = p.getDeclaringExecutable().getDeclaringType().getSimpleName();
					 
					 if (decType.length() == 0){
						CtExecutable<?> exec = getDeclaringMethod(var);	
						if (exec != null){
							List<CtParameter<?>> params = exec.getParameters();
							for (CtParameter<?> param : params){
								if (var.getVariable().getSimpleName().equals(param.getSimpleName())){
									res.add(param);
									break;
								}
							}
						}
					 }
				}
				//else if (var.getVariable() instanceof CtFieldReference){
					//FIXME get declaration for variable found in this kind of expression: java.awt.event.KeyEvent.VK_ESCAPE
				//}
			}
			
		}
		
		return res;
	}
	//TODO: refactor car copy from ListenerRegistrationWrapper
	private CtExecutable<?> getDeclaringMethod(CtElement elem){
		
		if(elem == null){
			return null;
		}
		else if(elem.getParent() instanceof CtConstructor){
			return (CtExecutable<?>) elem.getParent();
		}
		else if(elem.getParent() instanceof CtMethod){
			return (CtExecutable<?>) elem.getParent();
		}
		else{
			return getDeclaringMethod(elem.getParent());
		}
	}


	
	/**
	 * Get attributes & methods parameters dependences from all conditions
	 * from the root block to this block
	 */
	public Set<CtVariable<?>> getDeepDef(BasicBlock block){
		Set<CtVariable<?>> res = new HashSet<>();
		
		List<BasicBlock> toBeProcessed = new ArrayList<BasicBlock>();
		toBeProcessed.add(block);
		List<BasicBlock> yetProcessed = new ArrayList<BasicBlock>();
		
		
		while(!toBeProcessed.isEmpty()){
			BasicBlock selected = toBeProcessed.get(0);
			
			for(BasicBlock parent : selected.getParents()){
				if(!yetProcessed.contains(parent)){
					toBeProcessed.add(parent);
					
					CtExpression<?> conditional = parent.getCondition(selected);
					res.addAll(getDeepDef(conditional));
				}
			}
			
			toBeProcessed.remove(0);
			yetProcessed.add(selected);
		}
		
		return res;
	}
	
	/**
	 * Return fields and parameters accessed in all conditional edges
	 * of the control flow graph
	 */
	public Set<CtVariable<?>> getAllDeepDef(){
		Set<CtVariable<?>> res = new HashSet<>();
		
		for(BasicBlock block : cfg.getAllNode()){
			
			for(BasicBlock child : block.getChildren()){
				CtExpression<?> condition = block.getCondition(child);
				res.addAll(getDeepDef(condition));
			}
		}
		
		return res;
	}
	
	/**
	 * Get all assigned fields in this block
	 */
	public Set<CtVariable<?>> getAssignedField(BasicBlock block){
		Set<CtVariable<?>> res = new HashSet<>();
		
		for(CtCodeElement line : block.getElements()){
			
			if(line instanceof CtAssignment){
//				CtAssignment<?,?> assignment = (CtAssignment<?,?>) line;
				CtExpression<?> leftPart = ((CtAssignment<?,?>) line).getAssigned();
				if(leftPart instanceof CtFieldAccess){
					CtField<?> field = ((CtFieldAccess<?>) leftPart).getVariable().getDeclaration();
					res.add(field);
				}
			}
			
		}
		
		return res;
	}
	
	/**
	 * Get all assigned fields in the control flow graph
	 */
	public Set<CtVariable<?>> getAllAssignedField(){
		Set<CtVariable<?>> res = new HashSet<>();
		
		for(BasicBlock block : cfg.getAllNode()){
			res.addAll(getAssignedField(block));
		}
		
		return res;
	}
	
	/**
	 * Get attributes & methods parameters dependences for variable access
	 * in this condition
	 */
	public Set<CtVariable<?>> getDeepDef(CtExpression<?> condition){
		Set<CtVariable<?>> res = new HashSet<>();
		
		List<CtVariableAccess<?>> vars = findUsedVar(condition);
		for(CtVariableAccess<?> var : vars){
			res.addAll(getDeepDef(var));
		}
		
		return res;
	}
	
	/**
	 * Return definitions of block @two that are not in block @one
	 * Because blocks can have severals parents, definitions are union-based
	 */
	public Map<CtVariable<?>, Set<CtCodeElement>> getDifferences(BasicBlock one, BasicBlock two){
		
		Map<CtVariable<?>, Set<CtCodeElement>> res = new IdentityHashMap<>();
		
		Map<CtVariable<?>, Set<CtCodeElement>> oldDef = blocksDef.get(one);
		Map<CtVariable<?>, Set<CtCodeElement>> newDef = blocksDef.get(two);
		
		for(Entry<CtVariable<?>, Set<CtCodeElement>> entry : newDef.entrySet()){
			CtVariable<?> newKey = entry.getKey();
			Set<CtCodeElement> newValue = entry.getValue();
			Set<CtCodeElement> oldValue = oldDef.get(newKey);
			
			if(oldValue == null || oldValue != newValue){
				res.put(newKey, newValue);
			}
		}
		
		return res;
	}
	
	/**
	 * Return definitions after the last statement of this block.
	 * Because blocks can have severals parents, definitions are union-based
	 */
	public Map<CtVariable<?>, Set<CtCodeElement>> getDefinitions(BasicBlock bloc){
		return blocksDef.get(bloc);
	}
	
	/**
	 * Return statements that are not used by another.
	 */
	public Set<CtCodeElement> getAllTerminalStatements(){
		
		Set<CtCodeElement> res = new HashSet<>();
		
		for(BasicBlock block : cfg.getAllNode()){
			for(CtCodeElement line : block.getElements()){
				
				//Add this line to candidates statements
				res.add(line);
				
				//Remove statements which are dependencies
				List<CtVariableAccess<?>> vars = getUsedVariables(line);
				if(vars != null && !vars.isEmpty()){
					for(CtVariableAccess<?> var : vars){
						Set<CtCodeElement> defs = reachingDef.get(var);
						if(defs != null){
							for(CtCodeElement def : defs){
								if(res.contains(def)) res.remove(def);
							}
						}
					}
				}
			}
		}
		
		return res;
	}
	
	/**
	 * Return conditions using @field (which is not redefined before the conditional in its method)
	 */
	public List<CtExpression<?>> getConditions(CtVariable<?> field){
		List<CtExpression<?>> res = new ArrayList<>();
		for(BasicBlock block : cfg.getAllNode()){
			for(BasicBlock child : block.getChildren()){
				CtExpression<?> condition = block.getCondition(child);
				if(getDeepDef(condition).contains(field)) res.add(condition);
			}
		}
		return res;
	}
	
	/**
	 * Get the source of this def-use chain
	 */
	public CtExecutable<?> getExecutable(){
		return cfg.getExecutable();
	}
	
	public String toString(){
		StringBuffer res  = new StringBuffer();
		
		res.append("digraph OutputGraph {\n");
		
//		List<CtParameter> params = cfg.getMethod().getParameters();
//		for(CtParameter param : params){
//			res.append("\""+param.hashCode()+"\""+"[shape=\"box\"][label=\""+param.toString().replaceAll("\"", "\\\\\"").replaceAll("\n", "\\\\l").replaceAll(";", " ")+"\"]\n");
//		}
//		
//		CtClass clazz = (CtClass) cfg.getMethod().getParent();
//		List<CtField> fields = getAllField(clazz);
//		for(CtField field : fields){
//			res.append("\""+field.hashCode()+"\""+"[shape=\"hexagon\"][label=\""+field.toString().replaceAll("\"", "\\\\\"").replaceAll("\n", "\\\\l").replaceAll(";", " ")+"\"]\n");
//		}
		
		for(BasicBlock block : cfg.getAllNode()){
			for(CtCodeElement line : block.getElements()){
				List<CtVariableAccess<?>> vars = getUsedVariables(line);
				res.append("\""+getUniqueID(line)+"\""+"[label=\""+line.toString().replaceAll("\"", "\\\\\"").replaceAll("\n", "\\\\l").replaceAll(";", " ")+"\"]\n");
				if(vars != null && !vars.isEmpty()){
					
					
					for(CtVariableAccess<?> var : vars){
						CtVariable<?> decl = var.getVariable().getDeclaration();
						Set<CtCodeElement> defs = reachingDef.get(var);
						
						if(defs == null && decl instanceof CtField){
							res.append("\""+getUniqueID(line)+"\""+"->"+"\""+getUniqueID(decl)+"\""+"\n");
							res.append("\""+getUniqueID(decl)+"\""+"[shape=\"hexagon\"][label=\""+decl.getSignature()  +"\"]\n");
						}
						else if(defs == null && decl instanceof CtParameter){
							res.append("\""+getUniqueID(line)+"\""+"->"+"\""+getUniqueID(decl)+"\""+"\n");
							res.append("\""+getUniqueID(decl)+"\""+"[shape=\"box\"][label=\""+decl.toString().replaceAll("\"", "\\\\\"").replaceAll("\n", "\\\\l").replaceAll(";", " ")+"\"]\n");
						}
						else if(defs != null){ //complain here if null & def in the source code?
							for(CtCodeElement def : defs){
								res.append("\""+getUniqueID(line)+"\""+"->"+"\""+getUniqueID(def)+"\""+"\n");
							}
						}
					}
				}
			}
		}
		
		res.append("}");
		
		return res.toString();
	}
}
