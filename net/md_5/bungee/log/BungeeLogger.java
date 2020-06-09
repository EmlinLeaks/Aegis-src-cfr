/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.md_5.bungee.log;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import jline.console.ConsoleReader;
import net.md_5.bungee.log.ColouredWriter;
import net.md_5.bungee.log.ConciseFormatter;
import net.md_5.bungee.log.LogDispatcher;

public class BungeeLogger
extends Logger {
    private final Formatter formatter = new ConciseFormatter();
    private final LogDispatcher dispatcher = new LogDispatcher((BungeeLogger)this);

    @SuppressFBWarnings(value={"SC_START_IN_CTOR"})
    public BungeeLogger(String loggerName, String filePattern, ConsoleReader reader) {
        super((String)loggerName, null);
        this.setLevel((Level)Level.ALL);
        try {
            FileHandler fileHandler = new FileHandler((String)filePattern, (int)16777216, (int)8, (boolean)true);
            fileHandler.setFormatter((Formatter)this.formatter);
            this.addHandler((Handler)fileHandler);
            ColouredWriter consoleHandler = new ColouredWriter((ConsoleReader)reader);
            consoleHandler.setLevel((Level)Level.INFO);
            consoleHandler.setFormatter((Formatter)this.formatter);
            this.addHandler((Handler)consoleHandler);
        }
        catch (IOException ex) {
            System.err.println((String)"Could not register logger!");
            ex.printStackTrace();
        }
        this.dispatcher.start();
    }

    @Override
    public void log(LogRecord record) {
        this.dispatcher.queue((LogRecord)record);
    }

    void doLog(LogRecord record) {
        super.log((LogRecord)record);
    }
}

