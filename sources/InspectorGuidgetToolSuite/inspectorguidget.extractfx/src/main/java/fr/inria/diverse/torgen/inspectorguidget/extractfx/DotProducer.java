package fr.inria.diverse.torgen.inspectorguidget.extractfx;

public class DotProducer implements Runnable {
	private final VisitorDotGen gen;
	private final VisitorDotRename rename;
	private final Node root;

	public DotProducer(final Node root) {
		super();
		this.root = root;
		gen = new VisitorDotGen();
		rename = new VisitorDotRename();
	}


	@Override
	public void run() {
		root.accept(rename);
		gen.execute(root);
	}
}
