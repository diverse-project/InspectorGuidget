package inspectorguidget.analyser.dataflow;

import inspectorguidget.analyser.cfg.BasicBlock;
import inspectorguidget.analyser.cfg.ControlFlowGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ParentNotInitializedException;

/**
 * This class make a summary of a method by collecting all actions with their
 * related conditionals
 */
public class MethodSummary {

	List<Action>		actions;

	List<Action>		fieldAssignements;

	ControlFlowGraph	cfg;
	DefUse				defuse;

	public MethodSummary(CtMethod<?> method) {

		cfg = new ControlFlowGraph(method);
		defuse = new DefUse(cfg);

		fieldAssignements = new ArrayList<>();
		for (CtCodeElement actionStmt : findFieldSetters()) {
			Action action = new Action(actionStmt, gatherContainingCondition(actionStmt, method), method);
			fieldAssignements.add(action);
		}

		actions = new ArrayList<>();
		for (CtCodeElement actionStmt : findActions()) {
			Action action = new Action(actionStmt, gatherContainingCondition(actionStmt, method), method);
			actions.add(action);
		}

	}

	/**
	 * Find statements that are actions
	 * 
	 * (nodes with no outgoing edges from the def-use graph)
	 */
	private Set<CtCodeElement> findActions() {
		// doesn't consider field assignment as action
		Set<CtCodeElement> res = defuse.getAllTerminalStatements();
		res.removeAll(findFieldSetters());
		return res;
	}

	// /**
	// * Find conditions from beginning of the control flow to the action's
	// block
	// */
	// private List<CtExpression> gatherFlowConditions(CtCodeElement action){
	// //TODO: can have multi-paths
	//
	// //Find an execution path that contains action
	// List<List<BasicBlock>> paths = cfg.getExecutionPaths(); //TODO: compute
	// exec paths once for all actions
	// List<BasicBlock> candidatePath = null;
	// BasicBlock containerBlock = null;
	// for(List<BasicBlock> path : paths){
	// containerBlock = getContainingBlock(action, path);
	// if(containerBlock != null){
	// candidatePath = path;
	// break;
	// }
	// }
	//
	// //Gather condition to the action's block
	// List<CtExpression> res = new ArrayList<CtExpression>();
	// if(candidatePath != null){
	// BasicBlock current = candidatePath.get(0);
	// for(int i = 1; i < candidatePath.size(); i++){
	// BasicBlock next = candidatePath.get(i);
	// res.add(current.getCondition(next));
	//
	// if(next == containerBlock){
	// break;
	// }
	// else{
	// current = next;
	// }
	// }
	// }
	//
	// return res;
	// }

	/**
	 * Get expression from the containment hierarchy of the action
	 * (while,for,switch,conditional)
	 */
	private List<CtExpression<?>> gatherContainingCondition(CtCodeElement action, CtMethod<?> method) {

		List<CtExpression<?>> res = new ArrayList<>();

		CtElement parent = action;
		try {
			parent = action.getParent();

			while (parent != null && parent != method) {

				if (parent instanceof CtIf) { // TODO: get the negation too
					CtIf if_ = (CtIf) parent;
					res.add(if_.getCondition());
				} else if (parent instanceof CtWhile) {
					CtWhile while_ = (CtWhile) parent;
					res.add(while_.getLoopingExpression());
				} else if (parent instanceof CtFor) {
					CtFor for_ = (CtFor) parent;
					res.add(for_.getExpression());
				} else if (parent instanceof CtCase) {
					CtCase<?> case_ = (CtCase<?>) parent;
					res.add(case_.getCaseExpression());
				} else if (parent instanceof CtConditional) {// TODO: get the
																// negation too
					CtConditional<?> cond = (CtConditional<?>) parent;
					res.add(cond.getCondition());
				} else if (parent instanceof CtDo) {
					CtDo do_ = (CtDo) parent;
					res.add(do_.getLoopingExpression());
				}

				parent = parent.getParent();
			}
		} catch (ParentNotInitializedException e) {
			System.out.println("Parent init exeption: " + parent);
		}

		return res;
	}

	// /**
	// * Return the block that contains action
	// * Return null if it isn't the path
	// */
	// private BasicBlock getContainingBlock(CtCodeElement action,
	// List<BasicBlock> path){
	//
	// for(BasicBlock block : path){
	// if(block.getElements().contains(action)) return block;
	// }
	//
	// return null;
	// }

	/**
	 * Find field assignment statements
	 */
	private Set<CtCodeElement> findFieldSetters() { // TODO: refactor with
													// Defuse.getAssignedField()

		Set<CtCodeElement> res = new HashSet<>();

		for (BasicBlock block : cfg.getAllNode()) {
			for (CtCodeElement line : block.getElements()) {
				if (line instanceof CtAssignment) {
					CtAssignment<?, ?> assignment = (CtAssignment<?, ?>) line;
					if (assignment.getAssigned() instanceof CtFieldAccess) {
						res.add(line);
					}
				}
			}
		}

		return res;
	}

	public List<Action> getActions() {
		return actions;
	}

	public List<Action> getFieldAssignements() {
		return fieldAssignements;
	}

	/**
	 * Must return true if the field is used in @action's conditionals and not
	 * defined in the @action's method
	 */
	public boolean isControlledBy(Action action, CtAssignment<?, ?> fieldAssignment) {// TODO:
																						// move
																						// to
																						// Action

		CtField<?> field = null;
		CtExpression<?> leftPart = fieldAssignment.getAssigned();
		if (leftPart instanceof CtFieldAccess) {
			field = ((CtFieldAccess<?>) leftPart).getVariable().getDeclaration();
		}

		for (CtExpression<?> cond : action.getConditions()) {
			List<CtVariableAccess<?>> usedVars = defuse.findUsedVar(cond);
			for (CtVariableAccess<?> usedVar : usedVars) {

				// if(field != null && field ==
				// usedVar.getVariable().getDeclaration()){
				// Set<CtCodeElement> lastDef = defuse.getReachingDef(usedVar);
				// if(lastDef == null) return true;
				// }

				Set<CtVariable<?>> allDep = defuse.getDeepDef(usedVar);
				if (field != null && allDep.contains(field))
					return true;
			}
		}

		return false;
	}

	/**
	 * Return conditions that use @field
	 */
	public List<CtExpression<?>> getControllers(Action action, CtField<?> field) {// TODO:
																					// move
																					// to
																					// Action

		List<CtExpression<?>> res = new ArrayList<>();

		for (CtExpression<?> cond : action.getConditions()) {
			if (defuse.getDeepDef(cond).contains(field))
				res.add(cond);
		}

		return res;
	}
}
