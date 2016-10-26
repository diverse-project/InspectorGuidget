package fr.inria.diverse.torgen.inspectorguidget.xp;

import java.util.Collections;
import java.util.List;

public class LaTeXDrawLauncher extends XPLauncher {

	public static void main(String args[]) {
		new LaTeXDrawLauncher().run();
	}

	@Override
	protected List<String> getInputResoures() {
		return Collections.singletonList("/media/data/dev/runtime-New_configuration/LaTeXDraw2.0.8_src");
	}

	@Override
	protected String[] getSourceClassPath() {
		return new String[]{"/media/data/dev/runtime-New_configuration/LaTeXDraw2.0.8_src/lib/jlibeps.jar",
			"/media/data/opt/eclipsek3/plugins/org.junit_4.12.0.v201504281640/junit.jar",
			"/media/data/opt/eclipsek3/plugins/org.hamcrest.core_1.3.0.v201303031735.jar",
			"/media/data/dev/runtime-New_configuration/LaTeXDraw2.0.8_src/lib/net.sourceforge.jiu.jar"};
	}

	@Override
	protected int getCompilianceLevel() {
		return 8;
	}

	@Override
	protected boolean usingLambda() {
		return true;
	}

	@Override
	protected String getOutputFolder() {
		return "/home/foo/Bureau/foo/latexdrawRefactor";
	}
}
