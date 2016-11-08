package fr.inria.diverse.torgen.inspectorguidget.xp;

import java.util.Arrays;
import java.util.List;

public class JPhotoTaggerLauncher extends XPLauncher {

	public static void main(String args[]) {
		// git clone git@github.com:ebaumann/jphototagger.git
		// git checkout Version_0.34.5
		//remove the file RepositoryConnectedTestClassRunner.java
		new JPhotoTaggerLauncher().run();
	}

	@Override
	protected List<String> getInputResoures() {
		return Arrays.asList("/media/data/dev/repoAnalysisBlob/jphototagger/Program/src",
			"/media/data/dev/repoAnalysisBlob/jphototagger/Lib/src",
			"/media/data/dev/repoAnalysisBlob/jphototagger/API/src",
			"/media/data/dev/repoAnalysisBlob/jphototagger/Domain/src",
			"/media/data/dev/repoAnalysisBlob/jphototagger/XMP/src",
			"/media/data/dev/repoAnalysisBlob/jphototagger/Image/src",
			"/media/data/dev/repoAnalysisBlob/jphototagger/Iptc/src");
	}

	@Override
	protected String[] getSourceClassPath() {
		return new String[]{"/media/data/dev/repoAnalysisBlob/jphototagger/Libraries/src/mapdb-src.jar",
			"/media/data/dev/repoAnalysisBlob/jphototagger/Libraries/src/metadata-extractor-src.jar",
			"/media/data/dev/repoAnalysisBlob/jphototagger/Libraries/ImgrRdr.jar",
			"/media/data/dev/repoAnalysisBlob/jphototagger/Libraries/beansbinding.jar",
			"/media/data/dev/repoAnalysisBlob/jphototagger/Libraries/mapdb.jar",
			"/media/data/dev/repoAnalysisBlob/jphototagger/Libraries/eventbus.jar",
			"/media/data/dev/repoAnalysisBlob/jphototagger/Libraries/jgoodies-looks.jar",
			"/media/data/dev/repoAnalysisBlob/jphototagger/Libraries/metadata-extractor.jar",
			"/media/data/dev/repoAnalysisBlob/jphototagger/Libraries/swingx-core.jar",
			"/media/data/dev/repoAnalysisBlob/jphototagger/Libraries/org-openide-util-lookup.jar",
			"/media/data/dev/repoAnalysisBlob/jphototagger/Libraries/lucene-core.jar",
			"/media/data/dev/repoAnalysisBlob/jphototagger/Libraries/XMPCore.jar",
			"/media/data/dev/repoAnalysisBlob/jphototagger/Libraries/jgoodies-common.jar",
			"/media/data/dev/repoAnalysisBlob/jphototagger/Libraries/hsqldb.jar"
		};
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
		return "/media/data/dev/refactor/jphototagger-refactored/src";
	}
}
