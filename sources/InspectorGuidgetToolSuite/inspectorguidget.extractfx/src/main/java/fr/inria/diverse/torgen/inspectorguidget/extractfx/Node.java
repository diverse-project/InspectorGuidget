package fr.inria.diverse.torgen.inspectorguidget.extractfx;

import java.util.ArrayList;
import java.util.Collection;

public class Node {
	public final Cmd exp;
	public final Collection<Node> children;

	public Node(final Cmd nodeName) {
		super();
		exp = nodeName;
		children = new ArrayList<>();
	}

	public void accept(final VisitorNode v) {
		v.visitNode(this);
	}
}
