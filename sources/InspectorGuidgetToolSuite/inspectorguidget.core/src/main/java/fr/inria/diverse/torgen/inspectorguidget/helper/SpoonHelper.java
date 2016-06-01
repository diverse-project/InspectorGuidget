package fr.inria.diverse.torgen.inspectorguidget.helper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.code.*;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class SpoonHelper {
	public static final SpoonHelper INSTANCE = new SpoonHelper();


	private SpoonHelper() {
		super();
	}


	/**
	 * @param elt The element from which the research starts.
	 * @param topParent The top parent that stops the research.
	 * @return All the conditional expressions from the given element 'elt' up to the given top parent.
	 */
	public @NotNull List<CtElement> getSuperConditionalExpressions(final @NotNull CtElement elt, final @NotNull CtElement topParent) {
		CtElement parent = elt.isParentInitialized() ? elt.getParent() : null;
		List<CtElement> conds = new ArrayList<>();

		// Exploring the parents to identify the conditional statements
		while(parent!=null) {
			if(parent instanceof CtIf) {
				conds.add(((CtIf) parent).getCondition());
			}else if(parent instanceof CtCase<?>) {
				conds.add(((CtCase<?>) parent).getCaseExpression());
				CtSwitch<?> switzh = parent.getParent(CtSwitch.class);
				if(switzh==null)
					System.err.println("Cannot find the switch statement from the case statement: " + parent);
				else
					conds.add(switzh.getSelector());
			}else if(parent instanceof CtWhile) {
				conds.add(((CtWhile) parent).getLoopingExpression());
			}else if(parent instanceof CtDo) {
				conds.add(((CtDo) parent).getLoopingExpression());
			}else if(parent instanceof CtFor) {
				conds.add(((CtFor) parent).getExpression());
			}

			parent = parent.isParentInitialized() ? parent.getParent() : null;
		}

		return conds;
	}


	public int getLinePosition(final CtElement elt) {
		if(elt==null)
			return -1;

		SourcePosition pos = elt.getPosition();
		CtElement parent = elt.isParentInitialized() ? elt.getParent() : null;

		while(pos==null && parent!=null) {
			pos = parent.getPosition();
			parent = parent.isParentInitialized() ? parent.getParent() : null;
		}

		if(pos==null)
			return -1;
		return pos.getLine();
	}

	public @NotNull String formatPosition(final @Nullable SourcePosition position) {
		if(position==null)
			return "";

		return "in " + position.getFile().getName()+":L"+position.getLine()+":"+position.getEndLine()
				+",C"+position.getColumn()+":"+position.getEndColumn();
	}


	public @NotNull Set<CtLocalVariable<?>> getAllLocalVarDeclaration(final @NotNull CtElement elt) {
		return elt.getElements(new LocalVariableAccessFilter()).stream().map(varRef -> {
			Set<CtLocalVariable<?>> localVars = getAllLocalVarDeclaration(varRef.getDeclaration());
			if(varRef.getDeclaration() instanceof CtLocalVariable<?>)
				localVars.add((CtLocalVariable<?>) varRef.getDeclaration());
			return localVars;
		}).flatMap(s -> s.stream()).collect(Collectors.toSet());
	}


	/**
	 * @param elt The element to test. May be null.
	 * @return True if the given element is a break or a return statement.
	 */
	public boolean isReturnBreakStatement(final @Nullable CtElement elt) {
		return elt instanceof CtBreak || elt instanceof CtReturn;
	}


	public CtExpression<Boolean> negBoolExpression(final @NotNull CtExpression<Boolean> exp) {
		final CtUnaryOperator<Boolean> neg = exp.getFactory().Core().createUnaryOperator();
		neg.setKind(UnaryOperatorKind.NOT);
		neg.setOperand(exp);
		return neg;
	}


	public CtExpression<Boolean> createEqExpressionFromSwitchCase(final @NotNull CtSwitch<?> switchStat, final @NotNull CtCase<?> caze) {
		final CtBinaryOperator<Boolean> exp = switchStat.getFactory().Core().createBinaryOperator();
		// A switch is an equality test against values
		exp.setKind(BinaryOperatorKind.EQ);
		// The tested object
		exp.setLeftHandOperand(switchStat.getSelector());
		// The tested constant
		exp.setRightHandOperand(caze.getCaseExpression());
		return exp;
	}
}
