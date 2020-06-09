/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ConciseFormatter
extends Formatter {
    private final DateFormat date = new SimpleDateFormat((String)System.getProperty((String)"net.md_5.bungee.log-date-format", (String)"HH:mm:ss"));

    @Override
    public String format(LogRecord record) {
        StringBuilder formatted = new StringBuilder();
        formatted.append((String)this.date.format((Object)Long.valueOf((long)record.getMillis())));
        formatted.append((String)" [");
        formatted.append((String)record.getLevel().getLocalizedName());
        formatted.append((String)"] ");
        formatted.append((String)this.formatMessage((LogRecord)record));
        formatted.append((char)'\n');
        if (record.getThrown() == null) return formatted.toString();
        StringWriter writer = new StringWriter();
        record.getThrown().printStackTrace((PrintWriter)new PrintWriter((Writer)writer));
        formatted.append((Object)writer);
        return formatted.toString();
    }
}

