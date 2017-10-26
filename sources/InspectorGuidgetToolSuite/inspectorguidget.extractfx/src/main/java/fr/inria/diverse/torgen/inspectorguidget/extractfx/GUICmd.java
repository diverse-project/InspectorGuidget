package fr.inria.diverse.torgen.inspectorguidget.extractfx;

import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtVariableAccess;

public class GUICmd extends Cmd<CtExpression<?>> {
	public GUICmd(final @NotNull CtExpression<?> express) {
		super(express);
	}

	@Override
	public @NotNull String getText() {
		final String txt;
		if(exp instanceof CtInvocation<?> && ((CtInvocation<?>) exp).getTarget() instanceof CtVariableAccess<?>) {
			txt = ((CtVariableAccess<?>)((CtInvocation<?>) exp).getTarget()).getVariable().getSimpleName();
		}else {
			if(exp instanceof CtVariableAccess<?>) {
				txt = ((CtVariableAccess<?>)exp).getVariable().getSimpleName();
			}else {
				if(exp instanceof CtLambda<?>) {
					final CtLambda<?> lambda = (CtLambda<?>) exp;
					txt = lambda.getExpression().getElements((CtInvocation<?> invok) -> TestFXProcessor.isRobotInvocation(invok)).stream().
						map(invok -> invok.getExecutable().getSimpleName()).collect(Collectors.joining());
				}else {
					txt = exp.toString();
				}
			}
		}

		return txt.replaceAll("[^A-Za-z0-9]", "") + getIDCmd();
	}
}
