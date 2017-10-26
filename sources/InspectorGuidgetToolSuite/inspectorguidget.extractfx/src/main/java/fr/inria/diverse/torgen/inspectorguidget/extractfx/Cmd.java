package fr.inria.diverse.torgen.inspectorguidget.extractfx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

		return exp == cmd.exp;
	}

	@Override
	public int hashCode() {
		return exp != null ? exp.hashCode() : 0;
	}

	public abstract @NotNull String getText();
}
