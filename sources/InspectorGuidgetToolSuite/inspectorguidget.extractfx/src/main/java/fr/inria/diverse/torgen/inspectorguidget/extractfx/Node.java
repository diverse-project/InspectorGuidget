package fr.inria.diverse.torgen.inspectorguidget.extractfx;

import java.util.ArrayList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public class Node {
	public final Cmd<?> exp;
	public final @NotNull Collection<Node> children;
	private @NotNull String suffix;

	public Node(final Cmd<?> nodeName) {
		super();
		exp = nodeName;
		children = new ArrayList<>();
		suffix = "";
	}

	public void accept(final VisitorNode v) {
		v.visitNode(this);
	}

	public void setSuffix(final String suf) {
		suffix = suf == null ? "" : suf;
	}

	public String getText() {
		return exp.getText() + suffix;
	}
}
