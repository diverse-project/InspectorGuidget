package fr.inria.diverse.torgen.inspectorguidget.extractfx;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class VisitorCFG implements VisitorNode {
	public final @NotNull List<List<Cmd<?>>> allExps;
	private int currenti;
	private int currentj;
	private final Node rootNode;

	public VisitorCFG(final @NotNull List<List<Cmd<?>>> exps, final @NotNull Node root) {
		super();
		allExps = exps;
		currenti = 0;
		currentj = 0;
		rootNode = root;
	}

	@Override
	public void visitNode(final Node node) {
		if(allExps.size() <= currenti) return;
		final List<Cmd<?>> exps = allExps.get(currenti);
		if(exps.size() <= currentj) {
			currentj = 0;
			currenti++;
			rootNode.accept(this);
			return;
		}
		final Cmd<?> exp = exps.get(currentj);

		final Node expNode = node.children.stream().filter(child -> child.exp.equals(exp)).findAny().orElseGet(() -> {
			final Node n = new Node(exp);
			node.children.add(n);
			return n;
		});
		currentj++;
		expNode.accept(this);
	}
}
