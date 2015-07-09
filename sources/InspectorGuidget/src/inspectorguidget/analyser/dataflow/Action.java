package inspectorguidget.analyser.dataflow;

import java.util.List;

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

/**
 * Represent an action from a listener
 */
public class Action {

	/**
	 * Where the action is located
	 */
	CtMethod<?>				source;

	/**
	 * The action
	 */
	CtCodeElement			actionStatement;
	List<CtCodeElement>		actionStatements;

	/**
	 * All conditions of the control flow from the beginning of the source
	 * method to the action statement
	 */
	List<CtExpression<?>>	conditions;

	public Action(CtCodeElement action, List<CtExpression<?>> conditions, CtMethod<?> method) {
		this.actionStatement = action;
		this.conditions = conditions;
		this.source = method;
	}

	// Multiple actions
	public Action(List<CtCodeElement> actions, List<CtExpression<?>> conditions, CtMethod<?> method) {
		this.actionStatements = actions;
		this.conditions = conditions;
		this.source = method;
	}

	/**
	 * The action executed
	 */
	public CtCodeElement getStatement() {
		return actionStatement;
	}

	public List<CtCodeElement> getStatements() {
		return actionStatements;
	}

	/**
	 * Conditions that must be true to execute this action
	 */
	public List<CtExpression<?>> getConditions() {
		return conditions;
	}

	/**
	 * The containing method of the statement
	 */
	public CtMethod<?> getSource() {
		return source;
	}

	// /**
	// * Return all methods from listeners that change the values of some
	// attributes
	// * presents in conditions of this action
	// */
	// private List<CtMethod> getWritingListener(){
	// return null; //TODO
	// }
	//
	// private List<CtMethod> getWritingListener(CtExpression expression){
	// return null; //TODO
	// }

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();

		CtClass<?> clazz = (CtClass<?>) source.getParent();

		res.append("[" + clazz.getQualifiedName() + "." + source.getSimpleName() + "]\n");
		if (actionStatement != null) {
			res.append(actionStatement.toString() + "\n");
		} else if (actionStatements != null) {
			res.append(actionStatements.toString() + "\n");
		}

		for (CtExpression<?> cond : conditions) {
			res.append("\t" + cond + "\n");
		}
		return res.toString();
	}
}
