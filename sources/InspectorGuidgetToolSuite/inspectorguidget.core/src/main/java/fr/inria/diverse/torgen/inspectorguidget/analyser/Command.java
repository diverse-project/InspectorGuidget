package fr.inria.diverse.torgen.inspectorguidget.analyser;

import fr.inria.diverse.torgen.inspectorguidget.helper.ClassMethodCallFilter;
import fr.inria.diverse.torgen.inspectorguidget.helper.NonAnonymClassFilter;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Command {
	private final @NotNull CtExecutable<?> executable;

	private final @NotNull CommandStatmtEntry EMPTY_CMD_ENTRY = new CommandStatmtEntry(false);

	private final @NotNull List<CommandStatmtEntry> statements;

	private final @NotNull List<CommandConditionEntry> conditions;

	public Command(final @NotNull CommandStatmtEntry stat, final @NotNull List<CommandConditionEntry> conds,
				   final CtExecutable<?> exec) {
		super();
		statements = new ArrayList<>();
		conditions = conds;
		statements.add(stat);
		executable = exec;
	}

	public @NotNull Optional<CommandStatmtEntry> getMainStatmtEntry() {
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

	public @NotNull List<CommandConditionEntry> getConditions() {
		return conditions;
	}

	public @NotNull List<CommandStatmtEntry> getStatements() {
		return statements;
	}

	public @NotNull Set<CtCodeElement> getAllStatmts() {
		return statements.stream().map(entry -> entry.getAllStatmts()).flatMap(s -> s.stream()).collect(Collectors.toSet());
	}

	public @NotNull CtExecutable<?> getExecutable() {
		return executable;
	}

	/**
	 * Identifies local methods of the GUI controller that are used by some statements in the main entry.
	 * The code of the identified local methods is added to the command.
	 */
	public void extractLocalDispatchCallWithoutGUIParam() {
		getMainStatmtEntry().ifPresent(main -> {
			final CtClass parent = executable.getParent(new NonAnonymClassFilter());
			final List<CtParameter<?>> params = executable.getParameters();

			// Getting all the statements that are invocation of a local method without GUI parameter.
			main.getStatmts().stream().map(stat -> stat.getElements(new ClassMethodCallFilter(params, parent, false))).flatMap(s -> s.stream()).
				forEach(stat -> main.addMethodCallStatements(stat, new CommandStatmtEntry(false, stat.getExecutable().getDeclaration().getBody().getStatements())));
		});
	}
}
