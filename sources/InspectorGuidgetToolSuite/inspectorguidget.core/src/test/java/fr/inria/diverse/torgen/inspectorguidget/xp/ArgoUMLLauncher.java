package fr.inria.diverse.torgen.inspectorguidget.xp;

import fr.inria.diverse.torgen.inspectorguidget.analyser.Command;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class ArgoUMLLauncher extends XPLauncher {

	public static void main(String args[]) {
		new ArgoUMLLauncher().run();
	}

	@Override
	protected List<String> getInputResoures() {
		return Collections.singletonList("/media/data/dev/repoAnalysisBlob/argouml/src_new/");
	}

	@Override
	protected String[] getSourceClassPath() {
		return new String[]{"/media/data/dev/repoAnalysisBlob/argouml/lib/antlrall-2.7.2.jar",
						"/media/data/dev/repoAnalysisBlob/argouml/lib/log4j-1.2.6.jar",
						"/media/data/dev/repoAnalysisBlob/argouml/lib/gef-0.12.1.jar",
						"/media/data/dev/repoAnalysisBlob/argouml/lib/ocl-argo-1.1.jar",
						"/media/data/dev/repoAnalysisBlob/argouml/lib/toolbar-1.3.jar",
						"/media/data/dev/repoAnalysisBlob/argouml/lib/swidgets-0.1.4.jar",
						"/media/data/dev/repoAnalysisBlob/argouml/lib/commons-logging-1.0.2.jar",
						"/media/data/dev/repoAnalysisBlob/argouml/src/model/build/classes"};
	}

	@Override
	protected String getProjectName() {
		return "argouml";
	}

	@Override
	protected @NotNull List<Command> filterBlobsToRefactor() {
		List<ListenerID> ids = Arrays.asList(
			new ListenerID("StylePanelFigMessage.java", 98),
			new ListenerID("StylePanelFigPackage.java", 87),
			new ListenerID("WizStep.java", 269),
			new ListenerID("UMLAddDialog.java", 253),
			new ListenerID("FindDialog.java", 402),
			new ListenerID("StylePanelFigText.java", 273),
			new ListenerID("StylePanelFigNodeModelElement.java", 152),
			new ListenerID("StylePanelFigUseCase.java", 96),
			new ListenerID("StylePanelFigInterface.java", 80),
//			new ListenerID("WizStepChoice.java", 143),
			new ListenerID("CriticBrowserDialog.java", 480),
//			new ListenerID("CriticBrowserDialog.java", 423),
			new ListenerID("StylePanelFigClass.java", 111)
		);

		return blobAnalyser.getBlobs().entrySet().stream().
			filter(e -> ids.stream().anyMatch(id -> id.match(e.getKey()))).
			map(e -> e.getValue()).flatMap(s -> s.getCommands().stream()).collect(Collectors.toList());
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
		return "/media/data/dev/refactor/argoUMLRefactor";
	}
}
