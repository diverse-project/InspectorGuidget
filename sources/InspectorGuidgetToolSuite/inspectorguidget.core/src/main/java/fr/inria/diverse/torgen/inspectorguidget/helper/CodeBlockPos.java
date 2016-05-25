package fr.inria.diverse.torgen.inspectorguidget.helper;

public class CodeBlockPos {
	public final String file;
	public final int startLine;
	public final int endLine;

	public CodeBlockPos(String file, int startLine, int endLine) {
		this.file = file;
		this.startLine = startLine;
		this.endLine = endLine;
	}

	@Override
	public String toString() {
		return file + ";" + startLine + ";" + endLine;
	}
}
