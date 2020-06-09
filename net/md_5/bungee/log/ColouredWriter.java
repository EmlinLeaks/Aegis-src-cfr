/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.log;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import jline.console.ConsoleReader;
import net.md_5.bungee.api.ChatColor;
import org.fusesource.jansi.Ansi;

public class ColouredWriter
extends Handler {
    private final Map<ChatColor, String> replacements = new EnumMap<ChatColor, String>(ChatColor.class);
    private final ChatColor[] colors = ChatColor.values();
    private final ConsoleReader console;

    public ColouredWriter(ConsoleReader console) {
        this.console = console;
        this.replacements.put((ChatColor)ChatColor.BLACK, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.RESET).fg((Ansi.Color)Ansi.Color.BLACK).boldOff().toString());
        this.replacements.put((ChatColor)ChatColor.DARK_BLUE, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.RESET).fg((Ansi.Color)Ansi.Color.BLUE).boldOff().toString());
        this.replacements.put((ChatColor)ChatColor.DARK_GREEN, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.RESET).fg((Ansi.Color)Ansi.Color.GREEN).boldOff().toString());
        this.replacements.put((ChatColor)ChatColor.DARK_AQUA, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.RESET).fg((Ansi.Color)Ansi.Color.CYAN).boldOff().toString());
        this.replacements.put((ChatColor)ChatColor.DARK_RED, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.RESET).fg((Ansi.Color)Ansi.Color.RED).boldOff().toString());
        this.replacements.put((ChatColor)ChatColor.DARK_PURPLE, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.RESET).fg((Ansi.Color)Ansi.Color.MAGENTA).boldOff().toString());
        this.replacements.put((ChatColor)ChatColor.GOLD, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.RESET).fg((Ansi.Color)Ansi.Color.YELLOW).boldOff().toString());
        this.replacements.put((ChatColor)ChatColor.GRAY, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.RESET).fg((Ansi.Color)Ansi.Color.WHITE).boldOff().toString());
        this.replacements.put((ChatColor)ChatColor.DARK_GRAY, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.RESET).fg((Ansi.Color)Ansi.Color.BLACK).bold().toString());
        this.replacements.put((ChatColor)ChatColor.BLUE, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.RESET).fg((Ansi.Color)Ansi.Color.BLUE).bold().toString());
        this.replacements.put((ChatColor)ChatColor.GREEN, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.RESET).fg((Ansi.Color)Ansi.Color.GREEN).bold().toString());
        this.replacements.put((ChatColor)ChatColor.AQUA, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.RESET).fg((Ansi.Color)Ansi.Color.CYAN).bold().toString());
        this.replacements.put((ChatColor)ChatColor.RED, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.RESET).fg((Ansi.Color)Ansi.Color.RED).bold().toString());
        this.replacements.put((ChatColor)ChatColor.LIGHT_PURPLE, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.RESET).fg((Ansi.Color)Ansi.Color.MAGENTA).bold().toString());
        this.replacements.put((ChatColor)ChatColor.YELLOW, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.RESET).fg((Ansi.Color)Ansi.Color.YELLOW).bold().toString());
        this.replacements.put((ChatColor)ChatColor.WHITE, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.RESET).fg((Ansi.Color)Ansi.Color.WHITE).bold().toString());
        this.replacements.put((ChatColor)ChatColor.MAGIC, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.BLINK_SLOW).toString());
        this.replacements.put((ChatColor)ChatColor.BOLD, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.UNDERLINE_DOUBLE).toString());
        this.replacements.put((ChatColor)ChatColor.STRIKETHROUGH, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.STRIKETHROUGH_ON).toString());
        this.replacements.put((ChatColor)ChatColor.UNDERLINE, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.UNDERLINE).toString());
        this.replacements.put((ChatColor)ChatColor.ITALIC, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.ITALIC).toString());
        this.replacements.put((ChatColor)ChatColor.RESET, (String)Ansi.ansi().a((Ansi.Attribute)Ansi.Attribute.RESET).toString());
    }

    public void print(String s) {
        ChatColor color;
        ChatColor[] arrchatColor = this.colors;
        int n = arrchatColor.length;
        for (int n2 = 0; n2 < n; s = s.replaceAll((String)("(?i)" + color.toString()), (String)this.replacements.get((Object)color)), ++n2) {
            color = arrchatColor[n2];
        }
        try {
            this.console.print((CharSequence)(Ansi.ansi().eraseLine((Ansi.Erase)Ansi.Erase.ALL).toString() + '\r' + s + Ansi.ansi().reset().toString()));
            this.console.drawLine();
            this.console.flush();
            return;
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Override
    public void publish(LogRecord record) {
        if (!this.isLoggable((LogRecord)record)) return;
        this.print((String)this.getFormatter().format((LogRecord)record));
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}

