package fr.inria.diverse.torgen.inspectorguidget.extractfx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.stream.Collectors;

class VisitorDotGen implements VisitorNode {
	static final String ARROW = " -- ";

	public final StringBuilder builder;

	public VisitorDotGen() {
		super();
		builder = new StringBuilder();
	}

	public void execute(final Node node) {
		builder.append("graph CFG {\n");
		node.accept(this);
		builder.append("}\n");

		try {
			try(final FileWriter fw = new FileWriter(new File("examples/cfg.dot"));
				final BufferedWriter bw = new BufferedWriter(fw);
				final PrintWriter out = new PrintWriter(bw)) {
				out.println(Arrays.stream(builder.toString().split("\n")).distinct().collect(Collectors.joining("\n")));
			}
		}catch(final IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void visitNode(final Node node) {
		final String txt = node.exp == null ? "root" : node.getText();
		final String code = node.children.stream().map(child -> txt + ARROW + child.getText()).collect(Collectors.joining(";\n"));
		if(!code.isEmpty()) {
			builder.append(code).append(";\n");
		}
		node.children.forEach(child -> child.accept(this));
	}
}
