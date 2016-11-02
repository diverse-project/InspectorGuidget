package fr.inria.diverse.torgen.inspectorguidget.xp;

import java.util.Collections;
import java.util.List;

public class FreeColLauncher extends XPLauncher {

	public static void main(String args[]) {
		new FreeColLauncher().run();
	}

	@Override
	protected List<String> getInputResoures() {
		return Collections.singletonList("/media/data/dev/repoAnalysisBlob/FreeCol/src");
	}

	@Override
	protected String[] getSourceClassPath() {
		return new String[]{"/media/data/dev/repoAnalysisBlob/FreeCol/jars/commons-cli-1.1.jar",
			"/media/data/dev/repoAnalysisBlob/FreeCol/jars/cortado-0.6.0.jar",
			"/media/data/dev/repoAnalysisBlob/FreeCol/jars/jogg-0.0.7.jar",
			"/media/data/dev/repoAnalysisBlob/FreeCol/jars/jorbis-0.0.15.jar",
			"/media/data/dev/repoAnalysisBlob/FreeCol/jars/miglayout-core-4.2.jar",
			"/media/data/dev/repoAnalysisBlob/FreeCol/jars/miglayout-swing-4.2.jar"};
	}

	@Override
	protected int getCompilianceLevel() {
		return 6;
	}

	@Override
	protected boolean usingLambda() {
		return false;
	}

	@Override
	protected String getOutputFolder() {
		return "/media/data/dev/refactor/freecolRefactor";
	}
}
