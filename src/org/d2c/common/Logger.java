package org.d2c.common;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class Logger {

    /**
     * Text to put on the start of each line.
     */
    private final static String LOGGER_NL_TEXT = "D2C > ";

    /**
     * Log name
     */
    private static String LOG_NAME = "D2CLogger";

    /**
     * Is to print data?
     */
    private static boolean print = false;

    /**
     * Logger instance.
     */
    private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(getLogName());

    private static int DEBUG_LEVEL = 0;

    /**
     * Enable file handler
     */
    public static void enableFileHandler()
    {
        FileHandler fh;
        try {
            fh = new FileHandler(getLogName(), true);
            log.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
        } catch (SecurityException e) {
            log.log(Level.SEVERE, "SecurityException", e);
        } catch (IOException e) {
            log.log(Level.SEVERE, "IOException", e);
        }
    }

    /**
     * Get the log name.
     *
     * @return
     */
    public static String getLogName()
    {
        return LOG_NAME;
    }

    /**
     * Write a new log information entry.
     *
     * @param text Log text
     */
    public static void info(String text)
    {
        if (!print) {
            return;
        }

        log.info(LOGGER_NL_TEXT + text);
    }

    public static void error(String text)
    {
        if (!print) {
            return;
        }

        log.severe(LOGGER_NL_TEXT + text);
    }

    public static void enable()
    {
        print = true;
    }

    public static void disable()
    {
        print = false;
    }

    public static void config(int debugLevel)
    {
        DEBUG_LEVEL = debugLevel;

        // Console debug enable
        if (debugLevel > 0) {
            enable();
        }

        // File handle debug enable
        if (debugLevel > 1) {
            enableFileHandler();
        }
    }

    public static int getDebugLevel()
    {
        return DEBUG_LEVEL;
    }

}
