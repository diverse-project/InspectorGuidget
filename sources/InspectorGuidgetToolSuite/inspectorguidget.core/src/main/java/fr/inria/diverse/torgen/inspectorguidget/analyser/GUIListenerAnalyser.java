package fr.inria.diverse.torgen.inspectorguidget.analyser;

import fr.inria.diverse.torgen.inspectorguidget.processor.ClassListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.LambdaListenerProcessor;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

public class GUIListenerAnalyser extends InspectorGuidetAnalyser {
	private final @NotNull ClassListenerProcessor classProc;
	private final @NotNull LambdaListenerProcessor lambdaProc;

	public GUIListenerAnalyser() {
		super(Collections.emptyList());

		classProc = new ClassListenerProcessor();
		lambdaProc = new LambdaListenerProcessor();

		addProcessor(classProc);
		addProcessor(lambdaProc);
	}

	public @NotNull Map<CtClass<?>, Set<CtMethod<?>>> getClassListeners() {
		return Collections.unmodifiableMap(classProc.getAllListenerMethods());
	}

	public @NotNull Set<CtLambda<?>> getLambdaListeners() {
		return Collections.unmodifiableSet(lambdaProc.getAllListenerLambdas());
	}
}
