package fr.inria.diverse.torgen.inspectorguidget.extractfx;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;

public class RobotCmd extends Cmd<CtStatement> {
	public RobotCmd(final CtStatement stat) {
		super(stat);
	}

	@Override
	public String getText() {
		final String txt;

		if(exp instanceof CtInvocation<?>) {
			txt = ((CtInvocation<?>) exp).getExecutable().getSimpleName();
		}else {
			System.out.println(exp.getClass() + " " + exp);
			txt = exp.toString();
		}

		return txt.replaceAll("[^A-Za-z0-9]", "");
	}
}
