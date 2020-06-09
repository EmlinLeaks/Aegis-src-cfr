/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee;

import java.io.OutputStream;
import java.io.PrintStream;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import jline.console.ConsoleReader;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.command.ConsoleCommandSender;

public class BungeeCordLauncher {
    public static void main(String[] args) throws Exception {
        Security.setProperty((String)"networkaddress.cache.ttl", (String)"30");
        Security.setProperty((String)"networkaddress.cache.negative.ttl", (String)"10");
        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        parser.acceptsAll(Arrays.asList("help"), (String)"Show the help");
        parser.acceptsAll(Arrays.asList("v", "version"), (String)"Print version and exit");
        parser.acceptsAll(Arrays.asList("noconsole"), (String)"Disable console input");
        OptionSet options = parser.parse((String[])args);
        if (options.has((String)"help")) {
            parser.printHelpOn((OutputStream)System.out);
            return;
        }
        if (options.has((String)"version")) {
            System.out.println((String)BungeeCord.class.getPackage().getImplementationVersion());
            return;
        }
        if (BungeeCord.class.getPackage().getSpecificationVersion() != null && System.getProperty((String)"IReallyKnowWhatIAmDoingISwear") == null) {
            Date buildDate = new SimpleDateFormat((String)"yyyyMMdd").parse((String)BungeeCord.class.getPackage().getSpecificationVersion());
            Calendar deadline = Calendar.getInstance();
            deadline.add((int)3, (int)-8);
            if (buildDate.before((Date)deadline.getTime())) {
                System.err.println((String)"*** Warning, this build is outdated ***");
                System.err.println((String)"*** Please download a new build from http://ci.md-5.net/job/BungeeCord ***");
                System.err.println((String)"*** You will get NO support regarding this build ***");
                System.err.println((String)"*** Server will start in 10 seconds ***");
                Thread.sleep((long)TimeUnit.SECONDS.toMillis((long)10L));
            }
        }
        BungeeCord bungee = new BungeeCord();
        ProxyServer.setInstance((ProxyServer)bungee);
        bungee.getLogger().info((String)("Enabled BungeeCord version " + bungee.getVersion()));
        bungee.start();
        if (options.has((String)"noconsole")) return;
        while (bungee.isRunning) {
            String line = bungee.getConsoleReader().readLine((String)">");
            if (line == null) return;
            if (bungee.getPluginManager().dispatchCommand((CommandSender)ConsoleCommandSender.getInstance(), (String)line)) continue;
            bungee.getConsole().sendMessage((BaseComponent[])new ComponentBuilder((String)"Command not found").color((ChatColor)ChatColor.RED).create());
        }
    }
}

