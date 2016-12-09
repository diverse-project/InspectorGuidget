package fr.inria.diverse.torgen.inspectorguidget.helper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

public final class LoggingHelper {
	public static final LoggingHelper INSTANCE = new LoggingHelper();

	public final Level loggingLevel = Level.SEVERE;

	private LoggingHelper() {
		super();
	}

	public void logException(final @NotNull Exception ex, final @NotNull Logger logger) {
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		logger.log(Level.SEVERE, sw.toString());
		sw.flush();
	}
}
