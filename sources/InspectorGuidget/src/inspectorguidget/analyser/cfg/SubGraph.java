package inspectorguidget.analyser.cfg;

import java.util.List;

import spoon.reflect.code.CtCodeElement;

/**
 * Represent a part of the control flow graph with an entry node end an exit
 * node
 */
public class SubGraph {

	BasicBlock	entry;
	BasicBlock	exit;

	/**
	 * Creates disconnected entry and exit node
	 */
	public SubGraph(ControlFlowGraph cfg) {
		entry = new Connector(cfg);
		exit = new Connector(cfg);
	}

	/**
	 * Create a basic block. It is both entry and exit nodes of this SubGraph.
	 */
	public SubGraph(List<CtCodeElement> block, ControlFlowGraph cfg) {
		BasicBlock node = new BasicBlock(block, cfg);
		entry = node;
		exit = node;
	}

	public BasicBlock getEntry() {
		return entry;
	}

	public BasicBlock getExit() {
		return exit;
	}

	public void setEntry(BasicBlock node) {
		// BasicBlock.graph.remove(entry); TODO:remove this
		entry = node;
	}

	public void setExit(BasicBlock node) {
		// BasicBlock.graph.remove(exit); TODO:remove this
		exit = node;
	}

}
