package fr.inria.diverse.torgen.inspectorguidget.analyser;

import spoon.reflect.code.CtStatement;

import java.util.List;

public class Command {
	private final List<CtStatement> statements;

	public Command(final List<CtStatement> stats) {
		super();
		statements = stats;
	}
}
