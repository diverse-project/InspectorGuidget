package fr.inria.diverse.torgen.inspectorguidget.helper;

public class CodeBlockPos {
	public final String x;
	public final int y;
	public final int z;

	public CodeBlockPos(String x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return x + ";" + y + ";" + z;
	}
}
