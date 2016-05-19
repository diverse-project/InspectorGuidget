package fr.inria.diverse.torgen.inspectorguidget.analyser;

import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtCodeElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Command {
	private final @NotNull CommandStatmtEntry EMPTY_CMD_ENTRY = new CommandStatmtEntry(false);

	private final @NotNull List<CommandStatmtEntry> statements;

	private final @NotNull List<CommandConditionEntry> conditions;

	public Command(final @NotNull CommandStatmtEntry stat, final @NotNull List<CommandConditionEntry> conds) {
		super();
		statements = new ArrayList<>();
		conditions = conds;
		statements.add(stat);
	}

	public Optional<CommandStatmtEntry> getMainStatmtEntry() {
		return statements.stream().filter(entry -> entry.isMainEntry()).findFirst();
	}

	public int getLineStart() {
		return getMainStatmtEntry().orElse(EMPTY_CMD_ENTRY).
				getStatmts().stream().map(s -> s.getPosition()).filter(p -> p!=null).mapToInt(p -> p.getLine()).min().orElse(-1);
	}

	public int getLineEnd() {
		return getMainStatmtEntry().orElse(EMPTY_CMD_ENTRY).
				getStatmts().stream().map(s -> s.getPosition()).filter(p -> p!=null).mapToInt(p -> p.getEndLine()).max().orElse(-1);
	}

	public List<CommandConditionEntry> getConditions() {
		return conditions;
	}

	public List<CommandStatmtEntry> getStatements() {
		return statements;
	}

	public Set<CtCodeElement> getAllStatmts() {
		return statements.stream().map(entry -> entry.getAllStatmts()).flatMap(s -> s.stream()).collect(Collectors.toSet());
	}
}
