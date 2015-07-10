package inspectorguidget.analyser;

import java.util.ArrayList;

import java.util.List;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtMethod;

/* 
 * Process listeners with conditional statements
 */
public class ConditionalListeners {

	List<CtMethod<?>>	allCondListeners;

	public ConditionalListeners(List<CtMethod<?>> listeners) {
		this.allCondListeners = new ArrayList<>();
		allCondListeners.addAll(look4ConditionalMethods(listeners));
	}

	public ConditionalListeners() {
		super();
	}

	// Looking for methods of listeners that contain a condition block such as
	// if, while, for, switch, etc.
	private List<CtMethod<?>> look4ConditionalMethods(List<CtMethod<?>> methods) {
		List<CtMethod<?>> condMethods = new ArrayList<>();
		for (CtMethod<?> method : methods) {
			CtBlock<?> block = method.getBody();
			if (block != null) {// Listeners can have empty bodies
				List<CtCodeElement> elements = new ArrayList<>();
				elements.addAll(block.getStatements());
				if (isConditionalListener(elements)) {
					condMethods.add(method);
				}
			}
		}
		return condMethods;
	}

	public boolean isConditionalListener(List<CtCodeElement> elements) {

		for (CtCodeElement elem : elements) {
			if (isCondStatement(elem)) {
				return true;
			}
			List<CtCodeElement> listElements = processConditionalStmts(elem);
			if (!listElements.isEmpty()) {
				if (isConditionalListener(listElements)) {
					return true;
				}
			}
		}
		return false;
	}

	// Get statements in conditionals
	public List<CtCodeElement> processConditionalStmts(CtCodeElement line) {
		List<CtCodeElement> res = new ArrayList<>();

		if (line instanceof CtIf) {
			CtStatement thenStatement = ((CtIf) line).getThenStatement();
			CtStatement elseStatement = ((CtIf) line).getElseStatement();
			res.add(thenStatement);
			if (elseStatement != null) {
				res.add(elseStatement);
			}
		} else if (line instanceof CtConditional) {// Need to fix it! It cannot
													// be cast
			CtConditional<?> conditional = (CtConditional<?>) line;
			res.add(conditional.getThenExpression());
			if (conditional.getElseExpression() != null) {
				res.add(conditional.getElseExpression());
			}
		} else if (line instanceof CtDo) {
			CtDo ctDo = (CtDo) line;
			res.add(ctDo.getBody());

		} else if (line instanceof CtForEach) {
			CtForEach forEach = (CtForEach) line;
			res.add(forEach.getBody());

		} else if (line instanceof CtFor) {
			CtFor for_ = (CtFor) line;
			res.add(for_.getBody());
		} else if (line instanceof CtSwitch) {
			CtSwitch switch_ = (CtSwitch) line;
			List<CtCase<?>> cases = switch_.getCases();
			for (CtCase<?> case_ : cases) {
				if (case_ != null) {
					res.addAll(case_.getStatements());
				}
			}
		} else if (line instanceof CtTry) {
			CtTry try_ = (CtTry) line;
			res.addAll(try_.getBody().getStatements());

		} else if (line instanceof CtWhile) {
			CtWhile while_ = (CtWhile) line;
			res.add(while_.getBody());

		} else if (line instanceof CtBlock) {
			CtBlock<?> block = (CtBlock<?>) line;
			res.addAll(block.getStatements());
		} else if (line instanceof CtSynchronized) {
			CtSynchronized sync = (CtSynchronized) line;
			CtBlock<?> block = sync.getBlock();
			res.addAll(block.getStatements());
		}
		return res;
	}

	public boolean isCondStatement(CtCodeElement stmt) {
		if (stmt instanceof CtIf || stmt instanceof CtConditional || stmt instanceof CtDo || stmt instanceof CtForEach
				|| stmt instanceof CtFor || stmt instanceof CtSwitch || stmt instanceof CtWhile) {
			return true;
		}
		return false;
	}

	// Verify if the listener is contained by these statements
	public boolean isConditionalStatement(CtCodeElement stmt) {// Refactor and
																// change the
																// name: bad
																// name

		if (stmt instanceof CtIf || stmt instanceof CtConditional || stmt instanceof CtDo || stmt instanceof CtForEach
				|| stmt instanceof CtFor || stmt instanceof CtSwitch || stmt instanceof CtTry
				|| stmt instanceof CtWhile || stmt instanceof CtBlock) {
			return true;
		}
		return false;
	}

	public List<CtMethod<?>> getCondListeners() {
		return allCondListeners;
	}

	// Get statements in conditionals
	public List<CtStatement> processCondStatements(CtCodeElement line) {// Can
																		// be
																		// removed
																		// but
																		// firstly:
																		// refactor
																		// in
																		// VariablesProcessor
		List<CtStatement> res = new ArrayList<>();

		if (line instanceof CtIf) {

			CtStatement thenStatement = ((CtIf) line).getThenStatement();
			CtStatement elseStatement = ((CtIf) line).getElseStatement();
			try {
				// statements.add(ifCondition);
				res.add(thenStatement);
				res.add(elseStatement);
			} catch (NullPointerException e) {
				System.err.println("Caught NullPointerException: " + e.getMessage());
			}
		} else if (line instanceof CtConditional) {// Need to fix it! It cannot
													// be cast
			CtConditional<?> conditional = (CtConditional<?>) line;
			CtExpression<?> thenExpr = conditional.getThenExpression();
			res.add((CtStatement) thenExpr);
			res.add((CtStatement) conditional.getElseExpression());
			System.out.println("Conditional " + conditional);
		} else if (line instanceof CtDo) {
			CtDo ctDo = (CtDo) line;
			res.add(ctDo.getBody());

		} else if (line instanceof CtForEach) {
			CtForEach forEach = (CtForEach) line;
			res.add(forEach.getBody());

		} else if (line instanceof CtSwitch) {
			CtSwitch switch_ = (CtSwitch) line;
			List<CtCase<?>> cases = switch_.getCases();
			for (CtCase<?> case_ : cases) {
				res.addAll(case_.getStatements());
			}

		} else if (line instanceof CtTry) {
			CtTry try_ = (CtTry) line;
			res.addAll(try_.getBody().getStatements());

		} else if (line instanceof CtWhile) {
			CtWhile while_ = (CtWhile) line;
			res.add(while_.getBody());

		} else if (line instanceof CtBlock) {
			CtBlock<?> block = (CtBlock<?>) line;
			res.addAll(block.getStatements());
		} else if (line instanceof CtSynchronized) {
			CtSynchronized sync = (CtSynchronized) line;
			CtBlock<?> block = sync.getBlock();
			res.addAll(block.getStatements());
		}
		return res;
	}

	// //Is not used
	// public void processStatements(CtStatement statement){
	//
	// if (statement instanceof CtBlock){
	// CtBlock block = (CtBlock) statement;
	// List<CtStatement> res = block.getStatements();
	// }
	// else if (statement instanceof CtIf){
	// CtStatement thenPart = ((CtIf) statement).getThenStatement();
	// CtStatement elsePart = ((CtIf) statement).getElseStatement();
	// }
	// else if (statement instanceof CtTry){
	// CtTry try_ = (CtTry) statement;
	// List<CtStatement> res = try_.getBody().getStatements();
	//
	// }
	// else if (statement instanceof CtFor){
	// }
	// else if (statement instanceof CtWhile){
	// }
	// }
}
