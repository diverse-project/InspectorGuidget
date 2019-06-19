package fr.inria.diverse.torgen.inspectorguidget;

import fr.inria.diverse.torgen.inspectorguidget.analyser.InspectorGuidetAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.processor.ClassListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.FXMLAnnotationProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.LambdaListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.WidgetProcessor;
import java.util.Arrays;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import spoon.processing.Processor;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

public class Launcher extends InspectorGuidetAnalyser {
	public static void main(final String[] args) {
		final Launcher launcher = new Launcher(Arrays.asList(new ClassListenerProcessor(), new LambdaListenerProcessor(),
											new FXMLAnnotationProcessor(), new WidgetProcessor()));
		launcher.run(args);
	}

	public Launcher(final @NotNull Collection<Processor<?>> procs) {
		super(procs);
	}


	public Launcher(final @NotNull Collection<Processor<?>> procs, final @NotNull JDTBasedSpoonCompiler builder) {
		super(procs, builder);
	}
}
