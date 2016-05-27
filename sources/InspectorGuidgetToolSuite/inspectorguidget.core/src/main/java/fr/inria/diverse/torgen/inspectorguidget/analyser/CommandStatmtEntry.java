package fr.inria.diverse.torgen.inspectorguidget.analyser;

import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtStatement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An entry class used in the Command class to represent a statement that composed the code of the command.
 */
public class CommandStatmtEntry {
	final List<CtCodeElement> statmts;
	final boolean mainEntry;

	public CommandStatmtEntry(final boolean main) {
		super();
		statmts = new ArrayList<>();
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

	public boolean isMainEntry() {
		return mainEntry;
	}

	public List<CtCodeElement> getStatmts() {
		return Collections.unmodifiableList(statmts);
	}

	public int getLineStart() {
		return getStatmts().stream().map(s -> s.getPosition()).filter(p -> p!=null).mapToInt(p -> p.getLine()).min().orElse(-1);
	}

	public int getLineEnd() {
		return getStatmts().stream().map(s -> s.getPosition()).filter(p -> p!=null).mapToInt(p -> p.getEndLine()).max().orElse(-1);
	}

	public boolean contains(final @NotNull CommandStatmtEntry entry) {
		return getLineStart()<=entry.getLineStart() && getLineEnd()>=entry.getLineEnd();
	}

	@Override
	public String toString() {
		return "CommandStatmtEntry{start: " + getLineStart() + ", end: " + getLineEnd() + "}";
	}
}
