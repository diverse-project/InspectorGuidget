package fr.inria.diverse.torgen.inspectorguidget.analyser;

import fr.inria.diverse.torgen.inspectorguidget.helper.CodeBlockPos;
import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;

public class Command {
	private final @NotNull CtExecutable<?> executable;

	protected static final @NotNull CommandStatmtEntry EMPTY_CMD_ENTRY = new CommandStatmtEntry(false);

	private final @NotNull List<CommandStatmtEntry> statements;

	private final @NotNull List<CommandConditionEntry> conditions;

	public Command(final @NotNull CommandStatmtEntry stat, final @NotNull List<CommandConditionEntry> conds, final @NotNull CtExecutable<?> exec) {
		super();
		statements = new ArrayList<>();
		conditions = conds;
		statements.add(stat);
		executable = exec;
		inferConditionsDependencies();
	}

	/**
	 * Analyses the conditions to identify the code statements that the conditions depend on (e.g. local var def).
	 */
	private void inferConditionsDependencies() {
		addAllStatements(
			conditions.stream().map(cond -> SpoonHelper.INSTANCE.getAllLocalVarDeclaration(cond.realStatmt).stream().
				map(localVar -> new CommandStatmtEntry(false, Collections.singletonList((CtCodeElement) localVar)))).
				flatMap(s -> s).collect(Collectors.toList())
		);
	}

	public @NotNull Optional<CommandStatmtEntry> getMainStatmtEntry() {
		return statements.stream().filter(entry -> entry.isMainEntry()).findFirst();
	}

	public int getLineStart() {
		return getMainStatmtEntry().orElse(EMPTY_CMD_ENTRY).
			getStatmts().stream().map(s -> s.getPosition()).filter(p -> p != null).mapToInt(p -> p.getLine()).min().orElse(-1);
	}

	public int getLineEnd() {
		return getMainStatmtEntry().orElse(EMPTY_CMD_ENTRY).
			getStatmts().stream().map(s -> s.getPosition()).filter(p -> p != null).mapToInt(p -> p.getEndLine()).max().orElse(-1);
	}

	public @NotNull List<CommandConditionEntry> getConditions() {
		return Collections.unmodifiableList(conditions);
	}

	public @NotNull List<CommandStatmtEntry> getStatements() {
		return Collections.unmodifiableList(statements);
	}

	public void addAllStatements(final int index, final @NotNull Collection<CommandStatmtEntry> entries) {
		statements.addAll(index, entries);
		optimiseStatementEntries();
	}

	public void addAllStatements(final @NotNull Collection<CommandStatmtEntry> entries) {
		statements.addAll(entries);
		optimiseStatementEntries();
	}

	private void optimiseStatementEntries() {
		int i = 0;
		while(i < statements.size()) {
			int j = 0;
			CommandStatmtEntry stat1 = statements.get(i);
			while(j < statements.size()) {
				CommandStatmtEntry stat2 = statements.get(j);
				if(stat1 != stat2 && (stat1.equals(stat2) || (stat1.containsLine(stat2) && !stat2.containsLine(stat1)) || stat1.containsElement(stat2))) {
					statements.remove(j);
				}else {
					j++;
				}
			}
			i++;
		}
	}


	/**
	 * Checks whether the given code element is already part of the command (i.e. one of its statements or of its conditions).
	 * @param elt The element to check.
	 * @return True if the given element is part of the command. False otherwise.
	 */
	public boolean hasStatement(final @Nullable CtElement elt) {
		return elt != null && Stream.concat(getConditions().stream().map(cond -> cond.realStatmt), getAllStatmts().stream()).anyMatch(stat -> stat == elt);
	}


	/**
	 * @return All the statements of the command (but not the conditions).
	 */
	public @NotNull List<CtElement> getAllStatmts() {
		return statements.stream().map(entry -> entry.getStatmts()).flatMap(s -> s.stream()).collect(Collectors.toList());
	}

	/**
	 * @return All the ordered and non-dispatched statements of the command (using their start line, conditions excluded).
	 */
	public @NotNull List<CtElement> getAllLocalStatmtsOrdered() {
		return statements.stream().filter(s -> !s.isDispatchedCode()).sorted((s1, s2) -> s1.getLineStart() < s2.getLineStart() ? -1 : 1).
			map(entry -> entry.getStatmts()).flatMap(s -> s.stream()).collect(Collectors.toList());
	}

	/**
	 * @return The executable (method or lambda) that contains the command.
	 */
	public @NotNull CtExecutable<?> getExecutable() {
		return executable;
	}

	public @NotNull List<CodeBlockPos> getOptimalCodeBlocks() {
		return Stream.concat(
				statements.stream().filter(stat -> !stat.getStatmts().isEmpty()).map(stat -> new CodeBlockPos(stat.getStatmts().get(0).getPosition().getCompilationUnit().getFile().toString(),
												stat.getLineStart(), stat.getLineEnd())),
				conditions.stream().map(stat -> new CodeBlockPos(stat.realStatmt.getPosition().getCompilationUnit().getFile().toString(),
												stat.realStatmt.getPosition().getLine(), stat.realStatmt.getPosition().getEndLine()))).
				collect(Collectors.groupingBy(triple -> triple.file)).values().parallelStream().
				map(triples -> triples.stream().sorted((o1, o2) -> o1.startLine < o2.startLine ? -1 : o1.startLine == o2.startLine ? 0 : 1).
				collect(Collectors.toList())).map(triples -> {
			int i = 0;
			CodeBlockPos ti;
			CodeBlockPos tj;

			while(i < triples.size() - 1) {
				int j = i + 1;
				ti = triples.get(i);
				while(j < triples.size()) {
					tj = triples.get(j);
					if(ti.endLine + 1 == tj.startLine || ti.endLine >= tj.startLine && ti.startLine <= tj.startLine) {
						triples.remove(j);
						triples.remove(i);
						ti = new CodeBlockPos(ti.file, ti.startLine, tj.endLine);
						if(triples.isEmpty()) triples.add(ti);
						else triples.add(i, ti);
						j = i + 1;
					}else j++;
				}
				i++;
			}

			return triples.stream().sorted((o1, o2) -> o1.startLine < o2.startLine ? -1 : o1.startLine == o2.startLine ? 0 : 1);
		}).flatMap(s -> s).collect(Collectors.toList());
	}

	public int getNbLines() {
//		Stream.concat(statements.stream().map(stat -> stat.getStatmts().get(0)),
//						conditions.stream().map(stat -> stat.realStatmt)).
//						filter(stat -> stat.getPosition() == null).findFirst().ifPresent(statnull -> System.out.println("NO POSITION: " + statnull));
		return Stream.concat(statements.stream().filter(stat -> !stat.getStatmts().isEmpty()).map(stat -> stat.getStatmts().get(0).getPosition()),
				conditions.stream().map(stat -> stat.realStatmt.getPosition())).mapToInt(pos-> pos.getEndLine()-pos.getLine()+1).sum();
	}


	@Override
	public boolean equals(final Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Command command = (Command) o;

		if(!executable.equals(command.executable)) return false;
		if(!statements.equals(command.statements)) return false;
		return conditions.equals(command.conditions);

	}

	@Override
	public int hashCode() {
		int result = executable.hashCode();
		result = 31 * result + statements.hashCode();
		result = 31 * result + conditions.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return (executable instanceof CtMethod<?> ? executable.getSignature() : executable.getClass().getSimpleName()) + ";" +
			getNbLines() + ";" + getOptimalCodeBlocks().stream().map(b -> b.toString()).collect(Collectors.joining(";"));
	}


	/**
	 * @return True if the command has at least one relevant statement (e.g. no a log.warning, return command). False otherwise.
	 */
	public boolean hasRelevantCommandStatement() {
		return !getStatements().isEmpty() && SpoonHelper.INSTANCE.hasRelevantCommandStatements(getAllLocalStatmtsOrdered(), getExecutable());
	}
}
