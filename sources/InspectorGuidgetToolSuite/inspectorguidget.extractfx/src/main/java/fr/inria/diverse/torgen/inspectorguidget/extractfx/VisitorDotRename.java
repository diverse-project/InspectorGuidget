package fr.inria.diverse.torgen.inspectorguidget.extractfx;

import java.util.HashMap;
import java.util.Map;

class VisitorDotRename implements VisitorNode {
	private final Map<String, Integer> counts;

	VisitorDotRename() {
		super();
		counts = new HashMap<>();
	}

	@Override
	public void visitNode(final Node node) {
		if(node.exp != null) {
			final String text = node.exp.getText();
			final Integer count = counts.getOrDefault(text, 1);
			node.setSuffix("_" + count);
			counts.put(text, count + 1);
		}
		node.children.forEach(child -> child.accept(this));
	}
}
