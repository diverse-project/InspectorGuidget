package fr.inria.diverse.torgen.inspectorguidget.analyser;

import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;

/**
 * An entry class used in the Command class to represent a conditional statement that conditions the command.
 */
public class CommandConditionEntry {
	final CtCodeElement realStatmt;
	final CtExpression<Boolean> effectiveStatmt;

	public CommandConditionEntry(final @NotNull CtCodeElement realStatmt, final @NotNull CtExpression<Boolean> effectiveStatmt) {
		this.realStatmt = realStatmt;
		this.effectiveStatmt = effectiveStatmt;
	}

	public CommandConditionEntry(final @NotNull CtExpression<Boolean> statmt) {
		this(statmt, statmt);
	}

	/**
	 * @return The effective boolean expression that condition the command. For example, if the real statement is an 'else' one, then
	 * the effective statement may be the negation of the if condition. It means that the returns expression may have been created
	 * during the process and is not part of the AST.
	 */
	public CtExpression<Boolean> getEffectiveStatmt() {
		return effectiveStatmt;
	}

	/**
	 * @return The real statement pointed out by the command (can be an 'else', a 'case' ,or a real boolean expression for instance).
	 */
	public CtCodeElement getRealStatmt() {
		return realStatmt;
	}


	public boolean isSameCondition() {
		return realStatmt==effectiveStatmt;
	}
}
