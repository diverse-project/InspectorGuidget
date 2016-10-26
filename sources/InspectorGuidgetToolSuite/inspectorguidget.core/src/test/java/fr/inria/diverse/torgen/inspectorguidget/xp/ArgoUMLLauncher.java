package fr.inria.diverse.torgen.inspectorguidget.xp;

import java.util.Arrays;
import java.util.List;

public class ArgoUMLLauncher extends XPLauncher {

	public static void main(String args[]) {
		new ArgoUMLLauncher().run();
	}

	@Override
	protected List<String> getInputResoures() {
		return Arrays.asList("/media/data/dev/repoAnalysisBlob/argouml/src_new/",
					"/media/data/dev/repoAnalysisBlob/argouml/src/model/src/org/argouml/model");
	}

	@Override
	protected String[] getSourceClassPath() {
		return new String[]{"/media/data/dev/repoAnalysisBlob/argouml/lib/antlrall-2.7.2.jar",
						"/media/data/dev/repoAnalysisBlob/argouml/lib/log4j-1.2.6.jar",
						"/media/data/dev/repoAnalysisBlob/argouml/lib/gef-0.12.1.jar",
						"/media/data/dev/repoAnalysisBlob/argouml/lib/ocl-argo-1.1.jar",
						"/media/data/dev/repoAnalysisBlob/argouml/lib/toolbar-1.3.jar",
						"/media/data/dev/repoAnalysisBlob/argouml/lib/swidgets-0.1.4.jar",
						"/media/data/dev/repoAnalysisBlob/argouml/lib/commons-logging-1.0.2.jar"};
	}

	@Override
	protected int getCompilianceLevel() {
		return 4;
	}

	@Override
	protected boolean usingLambda() {
		return false;
	}

	@Override
	protected String getOutputFolder() {
		return "/home/foo/Bureau/foo/argoUMLRefactor";
	}
}
