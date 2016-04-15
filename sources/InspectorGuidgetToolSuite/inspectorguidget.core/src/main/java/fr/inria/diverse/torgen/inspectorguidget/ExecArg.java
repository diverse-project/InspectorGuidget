package fr.inria.diverse.torgen.inspectorguidget;

import fr.inria.diverse.torgen.inspectorguidget.Launcher;
import spoon.SpoonAPI;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExecArg {
    public static final String EXEC_CP_ARG = "-c";
    public static final String EXEC_SRC_ARG = "-s";

    private boolean srcParsed;
    private boolean cpParsed;

    public ExecArg() {
        super();
        srcParsed = false;
        cpParsed = false;
    }

    public void parse(final String[] args, final Launcher launcher) {
        if(args.length==0 || !EXEC_SRC_ARG.equals(args[0]))
            throw getArgumentException();

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



    private IllegalArgumentException getArgumentException() {
        return new IllegalArgumentException("Arguments: -s path/to/scr/to/analyse -c path/to/optional/classpath/libs.jar");
    }
}
