package caffeinatedpinkie.lightningtweaks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Simple class to conveniently log messages for this mod.
 *
 * @author CaffeinatedPinkie
 */
public class LoggerLT {
    public static Logger logger = LogManager.getLogger(LightningTweaks.MODID);

    /**
     * Runs a specified {@link Runnable} and then logs the time it took to complete,
     * using the given {@link String}.
     *
     * @param runnable    an arbitrary series of commands
     * @param processName the name of the process being run
     */
    public static void log(Runnable runnable, String processName) {
	long startTime = System.currentTimeMillis();
	runnable.run();
	log(processName + " in " + (System.currentTimeMillis() - startTime) + " ms!");
    }

    /**
     * Logs a message with {@link #logger}, the mod {@link Logger}, if
     * {@link ConfigLT#verbose} is true.
     *
     * @param message the message to be logged
     */
    public static void log(String message) {
	if (ConfigLT.verbose)
	    logger.info(message);
    }

    /**
     * Logs a warning message with {@link #logger}, the mod {@link Logger}, if
     * {@link ConfigLT#verbose} is true.
     *
     * @param message the message to be logged
     * @param e       the {@link Exception} thrown that caused this message
     */
    public static void warn(String message, Exception e) {
	if (ConfigLT.verbose) {
	    StackTraceElement[] stackTrace = e.getStackTrace();
	    logger.warn(message + " Caused by: " + e);
	    if (stackTrace.length > 0)
		logger.warn("\t@ " + stackTrace[0]);
	}
    }
}
