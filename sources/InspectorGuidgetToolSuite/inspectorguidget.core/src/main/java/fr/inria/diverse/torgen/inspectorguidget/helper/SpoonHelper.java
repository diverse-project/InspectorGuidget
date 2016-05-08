package fr.inria.diverse.torgen.inspectorguidget.helper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.code.*;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;

import java.util.Optional;

public final class SpoonHelper {
	public static final SpoonHelper INSTANCE = new SpoonHelper();


	private SpoonHelper() {
		super();
	}


	public @NotNull String formatPosition(final @Nullable SourcePosition position) {
		if(position==null)
			return "";

		return "in " + position.getFile().getName()+":L"+position.getLine()+":"+position.getEndLine()
				+",C"+position.getColumn()+":"+position.getEndColumn();
	}


	/**
	 * @param elt The element to test. May be null.
	 * @return True if the given element is a break or a return statement.
	 */
	public boolean isReturnBreakStatement(final @Nullable CtElement elt) {
		return elt instanceof CtBreak || elt instanceof CtReturn;
	}


	/**
	 * @param var The variable access to analyse.
	 * @param blockParent The max root element to consider when going up the AST.
	 * @return The conditional statement in which the given variable access is part of the condition. Nothing otherwise.
	 */
	public @NotNull Optional<CtElement> getConditionalParent(final @Nullable CtVariableAccess<?> var, final @Nullable CtElement blockParent) {
		if(var==null) return Optional.empty();

		CtElement elt = var;
		CtElement parent = var.getParent();
		boolean isPartOfConditional = false;

		while(parent!=null && parent!=blockParent && !isPartOfConditional) {
			if(ConditionalFilter.isConditional(parent))
				if(IsTheCondition(parent, elt))
					isPartOfConditional = true;
				else
					parent = null;
			else {
				elt = parent;
				parent = parent.getParent();
			}
		}

		return isPartOfConditional ? Optional.of(parent) : Optional.empty();
	}


	/**
	 * @param conditionalStatmt The conditional statement to analyse.
	 * @param elt The element to check.
	 * @return True if elt is the condition of the given conditional statement. False otherwise.
	 */
	public boolean IsTheCondition(final @Nullable CtElement conditionalStatmt, final @Nullable CtElement elt) {
		if(conditionalStatmt==null || elt==null) return false;

		CtExpression<?> condition;

		if(conditionalStatmt instanceof CtIf)
			condition = ((CtIf)conditionalStatmt).getCondition();
		else if(conditionalStatmt instanceof CtConditional<?>)
			condition = ((CtConditional<?>)conditionalStatmt).getCondition();
		else if(conditionalStatmt instanceof CtSwitch<?>)
			condition = ((CtSwitch<?>)conditionalStatmt).getSelector();
		else
			condition = null;

		if(condition==null)
			System.out.println("Not a conditional statement: " + conditionalStatmt);

		return elt==condition;
	}
}
