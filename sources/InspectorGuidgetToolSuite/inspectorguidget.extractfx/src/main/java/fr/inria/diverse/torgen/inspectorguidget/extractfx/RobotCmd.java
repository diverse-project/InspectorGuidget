package fr.inria.diverse.torgen.inspectorguidget.extractfx;

import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;

public class RobotCmd extends Cmd<CtStatement> {
	public RobotCmd(final @NotNull CtStatement stat) {
		super(stat);
	}

	@Override
	public @NotNull String getText() {
		final String txt;

		if(exp instanceof CtInvocation<?>) {
			txt = getCallArgumentStringInvocation((CtInvocation<?>) exp);
		}else {
			txt = exp.toString();
		}

		return txt.replaceAll("[^A-Za-z0-9]", "") + getIDCmd();
	}

	public static @NotNull String getCallArgumentStringInvocation(final @NotNull CtInvocation<?> invok) {
		return invok.getExecutable().getSimpleName() + invok.getArguments().stream().map(arg -> arg.toString()).collect(Collectors.joining());
	}
}
