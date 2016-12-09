package fr.inria.diverse.torgen.inspectorguidget.helper;

import fr.inria.diverse.torgen.inspectorguidget.analyser.InspectorGuidetAnalyser;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExecArg {
	public static final String EXEC_CP_ARG = "-c";
	public static final String EXEC_SRC_ARG = "-s";

	public ExecArg() {
		super();
	}

	public void parse(final @Nullable String[] args, final @NotNull InspectorGuidetAnalyser launcher) {
		if(args == null || args.length == 0 || !EXEC_SRC_ARG.equals(args[0])) throw getArgumentException();

		int i = 1;
		while(i < args.length && !args[i].equals(EXEC_CP_ARG)) {
			launcher.addInputResource(args[i]);
			i++;
		}

		if(i < args.length && EXEC_CP_ARG.equals(args[i])) {
			i++;
			System.out.println(IntStream.range(i, args.length).mapToObj(j -> args[j]).collect(Collectors.toList()));
			launcher.setSourceClasspath(IntStream.range(i, args.length).mapToObj(j -> args[j]).toArray(String[]::new));
		}
	}


	private @NotNull IllegalArgumentException getArgumentException() {
		return new IllegalArgumentException("Arguments: -s path/to/scr/to/analyse -c path/to/optional/classpath/libs.jar");
	}
}
