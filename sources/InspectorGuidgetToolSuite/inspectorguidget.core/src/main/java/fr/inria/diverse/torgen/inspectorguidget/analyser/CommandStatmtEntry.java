package fr.inria.diverse.torgen.inspectorguidget.analyser;

import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtStatement;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An entry class used in the Command class to represent a statement that composed the code of the command.
 */
public class CommandStatmtEntry {
	final List<CtCodeElement> statmts;
	final Map<CtCodeElement, CommandStatmtEntry> methodCalls;
	final boolean mainEntry;

	public CommandStatmtEntry(final boolean main) {
		super();
		statmts = new ArrayList<>();
		methodCalls = new HashMap<>();
		mainEntry = main;
	}

	public CommandStatmtEntry(final boolean main, final @NotNull Collection<CtCodeElement> listStatmts) {
		this(main);
		addStatements(listStatmts);
	}

	public CommandStatmtEntry(final boolean main, final @NotNull List<CtStatement> listStatmts) {
		this(main);
		listStatmts.forEach(s -> addStatement(s));
	}

	public void addStatement(final @NotNull CtCodeElement statmt) {
		statmts.add(statmt);
	}

	public void addStatements(final @NotNull Collection<CtCodeElement> listStatmts) {
		statmts.addAll(listStatmts);
	}

	public void addStatementAt(final @NotNull CtCodeElement statmt, final int position) {
		statmts.add(position, statmt);
	}

	public void addMethodCallStatements(final @NotNull CtCodeElement statmt, final List<CtCodeElement> method) {
		if(!statmts.contains(statmt)) return;
		methodCalls.put(statmt, new CommandStatmtEntry(false, method));
	}

	public boolean isMainEntry() {
		return mainEntry;
	}

	public List<CtCodeElement> getAllStatmts() {
		List<CtCodeElement> stats = new ArrayList<>(statmts);
		stats.addAll(methodCalls.values().stream().map(m -> m.getAllStatmts()).flatMap(s -> s.stream()).collect(Collectors.toList()));
		return stats;
	}

	public List<CtCodeElement> getStatmts() {
		return Collections.unmodifiableList(statmts);
	}
}
