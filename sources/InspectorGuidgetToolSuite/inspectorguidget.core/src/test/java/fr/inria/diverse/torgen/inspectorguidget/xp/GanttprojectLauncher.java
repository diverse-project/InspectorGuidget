package fr.inria.diverse.torgen.inspectorguidget.xp;

import java.util.Arrays;
import java.util.List;

public class GanttprojectLauncher extends XPLauncher {

	public static void main(String args[]) {
		// git clone git@github.com:bardsoftware/ganttproject.git
		// git checkout a41c23c
		new GanttprojectLauncher().run();
	}

	@Override
	protected List<String> getInputResoures() {
		return Arrays.asList("/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/src",
			"/media/data/dev/repoAnalysisBlob/ganttproject/biz.ganttproject.core/src");
	}

	@Override
	protected String[] getSourceClassPath() {
		return new String[]{"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/jgoodies-looks-2.5.3.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/milton-client-2.6.2.4.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/endrick-cache-1.7.9.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/jxlayer.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/commons-net-ftpclient-3.0.1.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/balloontip-1.2.4.1.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/slf4j-simple-1.5.11.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/slf4j-api-1.5.11.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/httpclient-4.2.1.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/commons-codec-1.4.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/jdom-1.0.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/concurrentlinkedhashmap-lru-1.3.2.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/commons-logging.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/httpcore-4.2.1.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/jgoodies-common-1.7.0.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/eclipsito.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/milton-api-2.6.2.4.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/endrick-common-1.7.9.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/commons-csv.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/commons-io.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/AppleJavaExtensions.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/ganttproject-jxbusycomponent-1.2.2.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/swingx-1.6.4.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/ganttproject/lib/core/jcommander-1.17.jar",
			"/media/data/dev/repoAnalysisBlob/ganttproject/biz.ganttproject.core/lib/ganttproject-guava.jar"};
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
		return "/media/data/dev/refactor/ganttproject-refactored/src";
	}
}
