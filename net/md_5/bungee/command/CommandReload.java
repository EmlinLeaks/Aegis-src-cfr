/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.command;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.ProxyReloadEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.conf.Configuration;

public class CommandReload
extends Command {
    public CommandReload() {
        super((String)"greload", (String)"bungeecord.command.reload", (String[])new String[0]);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        BungeeCord.getInstance().config.load();
        BungeeCord.getInstance().reloadMessages();
        BungeeCord.getInstance().stopListeners();
        BungeeCord.getInstance().startListeners();
        BungeeCord.getInstance().getPluginManager().callEvent(new ProxyReloadEvent((CommandSender)sender));
        sender.sendMessage((String)(ChatColor.BOLD.toString() + ChatColor.RED.toString() + "BungeeCord has been reloaded. This is NOT advisable and you will not be supported with any issues that arise! Please restart BungeeCord ASAP."));
    }
}

