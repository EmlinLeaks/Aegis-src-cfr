/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.command;

import java.net.InetSocketAddress;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.PlayerCommand;

public class CommandIP
extends PlayerCommand {
    public CommandIP() {
        super((String)"ip", (String)"bungeecord.command.ip", (String[])new String[0]);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage((String)ProxyServer.getInstance().getTranslation((String)"username_needed", (Object[])new Object[0]));
            return;
        }
        ProxiedPlayer user = ProxyServer.getInstance().getPlayer((String)args[0]);
        if (user == null) {
            sender.sendMessage((String)ProxyServer.getInstance().getTranslation((String)"user_not_online", (Object[])new Object[0]));
            return;
        }
        sender.sendMessage((String)ProxyServer.getInstance().getTranslation((String)"command_ip", (Object[])new Object[]{args[0], user.getAddress()}));
    }
}

