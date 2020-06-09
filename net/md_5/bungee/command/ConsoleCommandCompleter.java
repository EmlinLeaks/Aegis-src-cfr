/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jline.console.completer.Completer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginManager;

public class ConsoleCommandCompleter
implements Completer {
    private final ProxyServer proxy;

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        int n;
        ArrayList<String> suggestions = new ArrayList<String>();
        this.proxy.getPluginManager().dispatchCommand((CommandSender)this.proxy.getConsole(), (String)buffer, suggestions);
        candidates.addAll(suggestions);
        int lastSpace = buffer.lastIndexOf((int)32);
        if (lastSpace == -1) {
            n = cursor - buffer.length();
            return n;
        }
        n = cursor - (buffer.length() - lastSpace - 1);
        return n;
    }

    public ConsoleCommandCompleter(ProxyServer proxy) {
        this.proxy = proxy;
    }
}

