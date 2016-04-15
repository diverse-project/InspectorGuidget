package fr.inria.diverse.torgen.inspectorguidget.helper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LoggingHelper {
    public static final LoggingHelper INSTANCE = new LoggingHelper();

    public final Level loggingLevel = Level.ALL;

    private LoggingHelper() {
        super();
    }

    public void logException(final Exception ex, final Logger logger) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        logger.log(Level.SEVERE, sw.toString());
        sw.flush();
    }
}
