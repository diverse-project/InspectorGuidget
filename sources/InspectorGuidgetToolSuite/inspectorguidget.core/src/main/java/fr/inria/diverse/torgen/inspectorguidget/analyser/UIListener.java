package fr.inria.diverse.torgen.inspectorguidget.analyser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.declaration.CtExecutable;

/**
 * The concept of UI listener that contains UI commands and possibly a super UI listener
 */
public class UIListener {
	private @NotNull Optional<UIListener> superListener;
	private final @NotNull List<Command> cmds;
	private final @NotNull CtExecutable<?> executable;

	public UIListener(final @NotNull CtExecutable<?> exec) {
		super();
		superListener = Optional.empty();
		cmds = new ArrayList<>();
		executable = exec;
	}

	public void setSuperListener(final @Nullable UIListener superList) {
		superListener = Optional.ofNullable(superList);
	}

	public @NotNull CtExecutable<?> getExecutable() {
		return executable;
	}

	public void addCommand(final @NotNull Command cmd) {
		cmds.add(cmd);
	}

	public void removeCommandsIf(final @NotNull Predicate<? super Command> pred) {
		cmds.removeIf(pred);
	}

	public void removeAllCommands(final @NotNull List<Command> cmdToRm) {
		cmds.removeAll(cmdToRm);
	}

	public @NotNull List<Command> getCommands() {
		return Collections.unmodifiableList(cmds);
	}

	public Command getCommand(final int index) {
		return cmds.get(index);
	}

	public int getNbLocalCmds() {
		return cmds.size();
	}

	public int getNbTotalCmds() {
		return getNbLocalCmds() + getNbSuperCmds();
	}

	public int getNbSuperCmds() {
		return superListener.map(UIListener::getNbTotalCmds).orElse(0);
	}
}
