package fr.inria.diverse.torgen.inspectorguidget.analyser;

import org.jetbrains.annotations.NotNull;
import spoon.reflect.declaration.CtExecutable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BlobListenerAnalyser {
	private static int NB_CMDS = 3;

	private final @NotNull CommandAnalyser cmdAnalyser;
	private Map<CtExecutable<?>, List<Command>> blobs;

	public BlobListenerAnalyser() {
		super();
		cmdAnalyser = new CommandAnalyser();
	}


	public void run() {
		cmdAnalyser.run();
		blobs = cmdAnalyser.getCommands().entrySet().parallelStream().
					filter(entry -> entry.getValue().size()>= NB_CMDS).
					collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
	}

	public CommandAnalyser getCmdAnalyser() {
		return cmdAnalyser;
	}

	public Map<CtExecutable<?>, List<Command>> getBlobs() {
		return blobs;
	}

	public static void setNbCmdBlobs(final int nbCmds) {
		if(nbCmds>0)
			NB_CMDS = nbCmds;
	}
}
