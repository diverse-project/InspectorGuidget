package fr.inria.diverse.torgen.inspectorguidget.xp;

import fr.inria.diverse.torgen.inspectorguidget.analyser.Command;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class FreeColLauncher extends XPLauncher {

	public static void main(String args[]) {
		// git clone git://git.code.sf.net/p/freecol/git freecol-git
		// git checkout v0.11.6
		// Modify the constructor of CaptureGoodsDialog to let the Java code loadable by Spoon.
		new FreeColLauncher().run();
	}

	@Override
	protected List<String> getInputResoures() {
		return Collections.singletonList("/media/data/dev/repoAnalysisBlob/freecol-git/src");
	}

	@Override
	protected @NotNull List<Command> filterBlobsToRefactor() {
		List<ListenerID> ids = Arrays.asList(
//			new ListenerID("ReportPanel.java", 140),
			new ListenerID("TradeRoutePanel.java", 306),
			new ListenerID("EuropePanel.java", 840),
			new ListenerID("ColorCellEditor.java", 99),
			new ListenerID("ColonyPanel.java", 953),
			new ListenerID("NewPanel.java", 502),
			new ListenerID("StartGamePanel.java", 296),
//			new ListenerID("UnitLabel.java", 369),
//			new ListenerID("ReportCompactColonyPanel.java", 1116),
			new ListenerID("ServerListPanel.java", 187)
		);

		return blobAnalyser.getBlobs().entrySet().stream().
			filter(e -> ids.stream().anyMatch(id -> id.match(e.getKey()))).
			map(e -> e.getValue()).flatMap(s -> s.getCommands().stream()).collect(Collectors.toList());
	}

	@Override
	protected String[] getSourceClassPath() {
		return new String[]{"/media/data/dev/repoAnalysisBlob/freecol-git/jars/commons-cli-1.1.jar",
			"/media/data/dev/repoAnalysisBlob/freecol-git/jars/cortado-0.6.0.jar",
			"/media/data/dev/repoAnalysisBlob/freecol-git/jars/jogg-0.0.17.jar",
			"/media/data/dev/repoAnalysisBlob/freecol-git/jars/jorbis-0.0.17.jar",
			"/media/data/dev/repoAnalysisBlob/freecol-git/jars/miglayout-core-4.2.jar",
			"/media/data/dev/repoAnalysisBlob/freecol-git/jars/miglayout-swing-4.2.jar"};
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
		return "/media/data/dev/refactor/freecol-refactored/src";
	}
}
