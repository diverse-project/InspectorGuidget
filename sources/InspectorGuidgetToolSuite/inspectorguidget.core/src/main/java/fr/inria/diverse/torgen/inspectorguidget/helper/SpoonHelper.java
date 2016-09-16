package fr.inria.diverse.torgen.inspectorguidget.helper;

import fr.inria.diverse.torgen.inspectorguidget.filter.LocalVariableAccessFilter;
import fr.inria.diverse.torgen.inspectorguidget.filter.MyVariableAccessFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.code.*;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtVariable;

import java.util.*;
import java.util.stream.Collectors;

public final class SpoonHelper {
	public static final SpoonHelper INSTANCE = new SpoonHelper();


	private SpoonHelper() {
		super();
	}


//	public @NotNull List<CtExecutable<?>> getExecUsingSuperCall(final @NotNull List<CtExecutable<?>> execs) {
//		final SuperMethodInvocationFilter filter = new SuperMethodInvocationFilter();
//		return execs.parallelStream().filter(exec -> exec.getElements(filter).stream().
//								filter(supercall -> exec.getSimpleName().equals(supercall.getExecutable().getSimpleName())).findFirst().isPresent()
//			).collect(Collectors.toList());
//	}


	/**
	 * @param elt The element from which the research starts.
	 * @return All the conditional expressions from the given element 'elt' up to the given top parent.
	 */
	public @NotNull List<CtElement> getSuperConditionalExpressions(final @NotNull CtElement elt) {
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
		neg.setOperand(exp.clone());
		return neg;
	}

	public CtExpression<Boolean> andBoolExpression(final @NotNull CtExpression<Boolean> exp1, final @NotNull CtExpression<Boolean> exp2,
												   final boolean clone) {
		final CtBinaryOperator<Boolean> and = exp1.getFactory().Core().createBinaryOperator();
		and.setKind(BinaryOperatorKind.AND);

		if(clone) {
			and.setLeftHandOperand(exp1.clone());
			and.setRightHandOperand(exp2.clone());
		}else {
			and.setLeftHandOperand(exp1);
			and.setRightHandOperand(exp2);
		}
		return and;
	}


	public CtExpression<Boolean> createEqExpressionFromSwitchCase(final @NotNull CtSwitch<?> switchStat, final @NotNull CtCase<?> caze) {
		if(caze.getCaseExpression()==null) {// i.e. default case
			return switchStat.getCases().stream().filter(c -> c.getCaseExpression() != null).
				map(c -> negBoolExpression(createEqExpressionFromSwitchCase(switchStat, c))).reduce((a, b) -> andBoolExpression(a, b, false)).
				orElseGet(() -> switchStat.getFactory().Code().createLiteral(Boolean.TRUE));
		}

		CtBinaryOperator<Boolean> exp = switchStat.getFactory().Core().createBinaryOperator();
		// A switch is an equality test against values
		exp.setKind(BinaryOperatorKind.EQ);
		// The tested object
		exp.setLeftHandOperand(switchStat.getSelector().clone());
		// The tested constant
		exp.setRightHandOperand(caze.getCaseExpression().clone());

		return exp;
	}

//	/**
//	 * Shows the parents' class name and the position of these parents in the code of the given element.
//	 * @param element The element to scrutinise. Can be null.
//	 */
//	public void showParentsClassName(final @Nullable CtElement element) {
//		if(element==null) return;
//		CtElement elt = element;
//		CtElement parent;
//
//		while(elt.isParentInitialized() && elt.getParent()!=null) {
//			parent = elt.getParent();
//			System.out.print(parent.getClass().getSimpleName() + " " + formatPosition(parent.getPosition()) + " -> ");
//			elt = parent;
//		}
//		System.out.println();
//	}

	public List<CtVariableAccess<?>> extractUsagesOfVar(final @NotNull CtVariable<?> var) {
		CtElement parent;

		if(var instanceof CtLocalVariable<?>) {
			parent = var.getParent(CtBlock.class);
		}
		else if(var.getVisibility()==null) {
			parent = var.getParent(CtPackage.class);
			if(parent == null) parent = var.getParent(CtClass.class);
		}else {
			switch(var.getVisibility()) {
				case PRIVATE:
					parent = var.getParent(CtClass.class);
					break;
				case PROTECTED:
					parent = var.getParent(CtPackage.class);
					if(parent == null) parent = var.getParent(CtClass.class);
					break;
				case PUBLIC:
					parent = var.getFactory().Package().getRootPackage();
					break;
				default:
					parent = null;
					break;
			}
		}

		if(parent!=null) {
			return parent.getElements(new MyVariableAccessFilter(var));
		}
		//TODO find usages in method when the var is given as a parameter.

		return Collections.emptyList();
	}
}
