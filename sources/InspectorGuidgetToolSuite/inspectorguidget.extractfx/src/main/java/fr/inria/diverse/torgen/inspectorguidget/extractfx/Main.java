package fr.inria.diverse.torgen.inspectorguidget.extractfx;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import spoon.Launcher;

public class Main {
	public static void main(final String[] args) {
		final Launcher launcher = new Launcher();
		final TestFXProcessor proc = new TestFXProcessor();
		launcher.addProcessor(proc);
		launcher.addInputResource("/home/ablouin/dev/latexdraw/latexdrawGit/latexdraw-core/net.sf.latexdraw/src/test/java");
		launcher.getModelBuilder().setSourceClasspath("/home/ablouin/.m2/repository/org/testfx/testfx-junit/4.0.7-alpha/testfx-junit-4.0.7-alpha.jar",
			"/home/ablouin/.m2/repository/org/testfx/testfx-core/4.0.7-alpha/testfx-core-4.0.7-alpha.jar",
			"/home/ablouin/.m2/repository/org/swinglabs/pdf-renderer/1.0.5/pdf-renderer-1.0.5.jar",
			"/home/ablouin/.m2/repository/org/mockito/mockito-core/2.10.0/mockito-core-2.10.0.jar",
			"/home/ablouin/.m2/repository/org/malai/malai.javafx/3.0-SNAPSHOT/malai.javafx-3.0-SNAPSHOT.jar",
			"/home/ablouin/.m2/repository/org/malai/malai.core/3.0-SNAPSHOT/malai.core-3.0-20171015.024021-124.jar",
			"/home/ablouin/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar",
			"/home/ablouin/.m2/repository/org/hamcrest/hamcrest-all/1.3/hamcrest-all-1.3.jar",
			"/home/ablouin/.m2/repository/org/antlr/antlr4-runtime/4.7/antlr4-runtime-4.7.jar",
			"/home/ablouin/.m2/repository/net/sf/jlibeps/jlibeps/0.1/jlibeps-0.1.jar",
			"/home/ablouin/.m2/repository/junit/junit/4.12/junit-4.12.jar",
			"/home/ablouin/.m2/repository/javax/inject/javax.inject/1/javax.inject-1.jar",
			"/home/ablouin/.m2/repository/com/google/inject/guice/4.1.0/guice-4.1.0.jar",
			"/home/ablouin/.m2/repository/com/google/guava/guava/21.0/guava-21.0.jar",
			"/home/ablouin/dev/latexdraw/latexdrawGit/latexdraw-core/net.sf.latexdraw/target/latexdraw.core-4.0.0-SNAPSHOT.jar");
		launcher.run();

		final @NotNull List<List<Cmd<?>>> exps = proc.getExps();
		final Node root = new Node(null);
		root.accept(new VisitorCFG(exps, root));
		final DotProducer visitorDot = new DotProducer(root);
		visitorDot.run();
	}
}
