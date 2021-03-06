package fr.inria.diverse.torgen.inspectorguidget.extractfx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;

public abstract class Cmd<T extends CtElement> {
	public final T exp;

	public Cmd(final @NotNull T express) {
		super();
		exp = express;
	}

	@Override
	public boolean equals(final @Nullable Object o) {
		if(this == o) return true;
		if(!(o instanceof Cmd)) return false;

		Cmd<?> cmd = (Cmd<?>) o;

		if(exp instanceof CtVariableAccess<?> && cmd.exp instanceof CtVariableAccess<?>) {
			return ((CtVariableAccess<?>) exp).getVariable().getDeclaration() == ((CtVariableAccess<?>) cmd.exp).getVariable().getDeclaration();
		}

		if(exp instanceof CtInvocation<?> && cmd.exp instanceof CtInvocation<?>) {
			final CtInvocation<?> invok1 = (CtInvocation<?>) exp;
			final CtInvocation<?> invok2 = (CtInvocation<?>) cmd.exp;
			if(invok1.getTarget() instanceof CtVariableAccess<?> && invok2.getTarget() instanceof CtVariableAccess<?>) {
				return ((CtVariableAccess<?>) invok1.getTarget()).getVariable().getDeclaration() == ((CtVariableAccess<?>) invok1.getTarget()).getVariable().getDeclaration();
			}
		}

		return exp.equals(cmd.exp);
	}

	@Override
	public int hashCode() {
		return exp != null ? exp.hashCode() : 0;
	}

	protected String getIDCmd() {
		final int hash = hashCode();
		return hash == 1 ? "" : "_" + String.valueOf(hash);
	}

	public abstract @NotNull String getText();
}
