package org.d2c.common;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

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

        log.setUseParentHandlers(false);

        MyFormatter formatter = new MyFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        log.addHandler(handler);
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

    static class MyFormatter extends Formatter {
        //
        // Create a DateFormat to format the logger timestamp.
        //
        private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");

        public String format(LogRecord record)
        {
            StringBuilder builder = new StringBuilder(1000);
            builder.append(df.format(new Date(record.getMillis()))).append(" - ");
            builder.append("[").append(record.getLevel()).append("] - ");
            builder.append(formatMessage(record));
            builder.append("\n");
            return builder.toString();
        }

        public String getHead(Handler h)
        {
            return super.getHead(h);
        }

        public String getTail(Handler h)
        {
            return super.getTail(h);
        }
    }
}
