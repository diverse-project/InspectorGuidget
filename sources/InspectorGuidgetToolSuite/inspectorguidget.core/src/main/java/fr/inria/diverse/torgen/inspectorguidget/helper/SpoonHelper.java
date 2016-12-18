package fr.inria.diverse.torgen.inspectorguidget.helper;

import fr.inria.diverse.torgen.inspectorguidget.filter.BasicFilter;
import fr.inria.diverse.torgen.inspectorguidget.filter.MyVariableAccessFilter;
import fr.inria.diverse.torgen.inspectorguidget.filter.VariableAccessFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCFlowBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;

public final class SpoonHelper {
	public static final SpoonHelper INSTANCE = new SpoonHelper();

	private final Set<String> logNames;


	private SpoonHelper() {
		super();
		logNames = new HashSet<>(Arrays.asList("log", "debug", "warning", "error", "severe", "fine", "finest", "finer", "info"));
	}


	public <T> Optional<CtCase<? super  T>> getNonEmptySwitchCase(final @Nullable CtCase<? super  T> ctcase) {
		if(ctcase==null) {
			return Optional.empty();
		}

		if(ctcase.getStatements().isEmpty()) {
			return getNonEmptySwitchCase(getNextSwitchCase(ctcase).orElse(null));
		}

		return Optional.of(ctcase);
	}


	public <T> Optional<CtCase<? super T>> getNextSwitchCase(final @NotNull CtCase<? super  T> ctcase) {
		final CtSwitch<T> swit = ctcase.isParentInitialized() ? (CtSwitch<T>) ctcase.getParent() : null;

		if(swit == null) {
			return Optional.empty();
		}

		List<CtCase<? super T>> cases = swit.getCases();
		final int index = cases.indexOf(ctcase);

		if(index == -1 || index + 1 >= cases.size()) {
			return Optional.empty();
		}

		return Optional.of(cases.get(index + 1));
	}


	public <T extends CtElement> boolean isRelevantCommandStatement(@NotNull T stat, @NotNull CtExecutable<?> exec) {
		return !(stat instanceof CtThrow) &&
			!SpoonHelper.INSTANCE.isReturnBreakStatement(stat) && !SpoonHelper.INSTANCE.isSuperCall(exec, stat) &&
			!SpoonHelper.INSTANCE.isLogStatement(stat);
	}


	public <T extends CtElement> boolean hasRelevantCommandStatements(@NotNull List<T> stats, @NotNull CtExecutable<?> exec) {
		// Getting all the variable used in the commands.
		// The declarations of these variables will not be considered as relevant and thus ignored.
		final Set<CtVariable<?>> vars = stats.parallelStream().map(s -> s.getElements(new VariableAccessFilter())).flatMap(s -> s.stream()).
			map(va -> va.getVariable()).filter(v -> v!=null).map(v -> v.getDeclaration()).collect(Collectors.toSet());

		return stats.isEmpty() || stats.parallelStream().anyMatch(stat -> isRelevantCommandStatement(stat, exec) &&
			// Ignoring the statements that declares the variables used in the command's statements.
			(!(stat instanceof CtVariable) || !vars.contains(stat)));
	}


	/**
	 * Checkes whether the given code statement contains a super call to the upper executable of exec.
	 * @param exec The overridden executable.
	 * @param stat The code statement to analyse.
	 * @return True whether the code statement contains a super call to the super methof of "exec". False otherwise.
	 */
	public boolean isSuperCall(final @NotNull CtExecutable<?> exec, final @NotNull CtElement stat) {
		final String execName = exec.getSimpleName();
		return stat instanceof CtInvocation<?> && ((CtInvocation<?>) stat).getTarget() instanceof CtSuperAccess<?> &&
			((CtInvocation<?>) stat).getExecutable().getSimpleName().equals(execName);
	}

	/**
	 * Checkes whether the given code statement is probably a log statement.
	 * @param stat The code statement to analyse.
	 * @return True whether the code statement is probably a log statement. False otherwise.
	 */
	public boolean isLogStatement(final @NotNull CtElement stat) {
		return stat instanceof CtInvocation<?> && logNames.contains(((CtInvocation<?>) stat).getExecutable().getSimpleName());
	}


	public @Nullable CtType<?> getMainTypeFromElt(final @Nullable CtElement elt) {
		if(elt == null) return null;
		return getMainType(elt.getParent(CtType.class));
	}


	public @Nullable CtType<?> getMainType(final @Nullable CtType<?> type) {
		if(type == null) return null;
		CtType<?> ty = getMainType(type.getParent(CtType.class));
		if(ty == null) {
			return type;
		}
		return ty;
	}


	public boolean isEmptyIfStatement(final @Nullable CtIf iff) {
		if(iff == null) return false;
		final BasicFilter<CtStatement> filter = new BasicFilter<CtStatement>(CtStatement.class) {
			@Override
			public boolean matches(final CtStatement element) {
				return !(element instanceof CtBlock) && !(element instanceof CtCFlowBreak) && !(element instanceof CtSynchronized) && !(element instanceof CtAssert);
			}
		};

		return iff.getThenStatement() == null || iff.getThenStatement().getElements(filter).isEmpty() &&
			(iff.getElseStatement() == null || iff.getElseStatement().getElements(filter).isEmpty());
	}


	public boolean isEmptySwitch(final @Nullable CtSwitch<?> sw) {
		return sw != null && sw.getCases().stream().allMatch(caz -> caz.getStatements().isEmpty());

	}


	/**
	 * Looks for a parent of the given type from a given element.
	 * @param elt The element from which the parent is looked for.
	 * @param typeParent The class of the parent to look for.
	 * @param butNot The research will stop as soon as the current parent is this element. Can be null.
	 * @return The found element or nothing.
	 */
	public <T extends CtElement> @NotNull Optional<CtElement> getParentOf(final @Nullable CtElement elt, final @NotNull Class<T> typeParent,
																		  final @Nullable CtElement butNot) {
		if(elt == null) return Optional.empty();

		try {
			CtElement parent = elt.getParent();

			while(!typeParent.isInstance(parent) && parent != butNot) {
				parent = parent.getParent();
			}

			if(parent == butNot || !typeParent.isInstance(parent)) return Optional.empty();

			return Optional.of(parent);

		}catch(final ParentNotInitializedException ex) {
			return Optional.empty();
		}
	}


	/**
	 * Checkes whether the given type has the given executable (by inheritance or not).
	 * Overriden methods are not considered (only leaf methods).
	 * @param ty The type to search in.
	 * @param exec The executable to look for.
	 * @return True if ty has exec.
	 */
	public boolean hasMethod(final @Nullable CtType<?> ty, final @Nullable CtExecutable<?> exec) {
		if(exec == null || ty == null) return false;

		// Checking whether the parent is the given type ty.
		try {
			if(exec.getParent() == ty) return true;
		}catch(ParentNotInitializedException ex) {
			return false;
		}

		// If the method is not the given type and, however, a method with its signature exists, this means
		// that exec is overriden here. So, we ignore this overriden method.
		final String over = exec.getSignature();
		if(ty.getMethods().parallelStream().anyMatch(m -> m.getSignature().equals(over))) return false;

		// Checking whether the super class has the method.
		// Finally, checking whether an interface has the method (a default method).
		final CtTypeReference<?> superCl = ty.getSuperclass();
		return superCl != null && hasMethod(superCl.getDeclaration(), exec) ||
			ty.getSuperInterfaces().parallelStream().anyMatch(interf -> hasMethod(interf.getDeclaration(), exec));
	}


	/**
	 * @param elt The element from which the research starts.
	 * @return All the conditional expressions from the given element 'elt' up to the given top parent.
	 */
	public @NotNull List<CtElement> getSuperConditionalExpressions(final @NotNull CtElement elt) {
		CtElement parent = elt.isParentInitialized() ? elt.getParent() : null;
		List<CtElement> conds = new ArrayList<>();

		// Exploring the parents to identify the conditional statements
		while(parent != null) {
			if(parent instanceof CtIf) {
				conds.add(((CtIf) parent).getCondition());
			}else {
				if(parent instanceof CtCase<?>) {
					conds.add(((CtCase<?>) parent).getCaseExpression());
					CtSwitch<?> switzh = parent.getParent(CtSwitch.class);
					if(switzh == null) System.err.println("Cannot find the switch statement from the case statement: " + parent);
					else conds.add(switzh.getSelector());
				}else {
					if(parent instanceof CtWhile) {
						conds.add(((CtWhile) parent).getLoopingExpression());
					}else {
						if(parent instanceof CtDo) {
							conds.add(((CtDo) parent).getLoopingExpression());
						}else {
							if(parent instanceof CtFor) {
								conds.add(((CtFor) parent).getExpression());
							}
						}
					}
				}
			}

			parent = parent.isParentInitialized() ? parent.getParent() : null;
		}

		return conds;
	}


	public int getLinePosition(final CtElement elt) {
		if(elt == null) return -1;

		SourcePosition pos = elt.getPosition();
		CtElement parent = elt.isParentInitialized() ? elt.getParent() : null;

		while(pos == null && parent != null) {
			pos = parent.getPosition();
			parent = parent.isParentInitialized() ? parent.getParent() : null;
		}

		if(pos == null) return -1;
		return pos.getLine();
	}

	public @NotNull String formatPosition(final @Nullable SourcePosition position) {
		if(position == null) return "";

		return "in " + position.getFile().getName() + ":L" + position.getLine() + ":" + position.getEndLine() + ",C" +
			position.getColumn() + ":" + position.getEndColumn();
	}


	public @NotNull Set<CtLocalVariable<?>> getAllLocalVarDeclaration(final @Nullable CtElement elt) {
		if(elt == null) {
			return new HashSet<>();
		}

		return elt.getElements(new BasicFilter<>(CtLocalVariableReference.class)).stream().map(varRef -> {
			final CtLocalVariable<?> varDecl = varRef.getDeclaration();
			Set<CtLocalVariable<?>> localVars = getAllLocalVarDeclaration(varDecl);
			if(varDecl != null) {
				localVars.add(varDecl);
			}
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
		neg.setPosition(exp.getPosition());
		return neg;
	}

	public CtExpression<Boolean> andBoolExpression(final @NotNull CtExpression<Boolean> exp1, final @NotNull CtExpression<Boolean> exp2, final boolean clone) {
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
		if(caze.getCaseExpression() == null) {// i.e. default case
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


	/**
	 * Returns the first parent statement or the element before this statement if the statement is a control flow statement.
	 * @param elt The element to analyse.
	 * @return The found element.
	 */
	public Optional<CtElement> getStatementParentNotCtrlFlow(@Nullable CtElement elt) {
		CtElement res = elt;
		boolean found = false;

		while(!found && res != null) {
			if(res instanceof CtStatement) {
				found = true;
			}else {
				if(res.isParentInitialized()) {
					CtElement parent = res.getParent();
					// FIXME use CtBodyHolder Spoon 5.5
					if(parent instanceof CtIf || parent instanceof CtSwitch || parent instanceof CtLoop || parent instanceof CtTry ||
						parent instanceof CtCatch || parent instanceof CtCase) {
						found = true;
					}else {
						res = parent;
					}
				}else {
					res = null;
				}
			}
		}

		return Optional.ofNullable(res);
	}

	public List<CtVariableAccess<?>> extractUsagesOfVar(final @NotNull CtVariable<?> var) {
		CtElement parent;

		if(var instanceof CtLocalVariable<?>) {
			parent = var.getParent(CtBlock.class);
		}else {
			if(var.getVisibility() == null) {
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
		}

		if(parent != null) {
			return parent.getElements(new MyVariableAccessFilter(var));
		}

		return Collections.emptyList();
	}
}
