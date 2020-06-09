/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

public class CommandBungee
extends Command {
    public CommandBungee() {
        super((String)"bungee");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage((String)((Object)((Object)ChatColor.BLUE) + "This server is running " + (Object)((Object)ChatColor.GREEN) + ProxyServer.getInstance().getName() + (Object)((Object)ChatColor.BLUE) + " version (" + "1.0.5 (1.8.x - 1.15.x)" + ") " + (Object)((Object)ChatColor.GREEN) + ProxyServer.getInstance().getVersion() + (Object)((Object)ChatColor.BLUE) + " by " + (Object)((Object)ChatColor.GREEN) + "yooniks " + (Object)((Object)ChatColor.BLUE) + "(Fork of md_5 BungeeCord)\n" + (Object)((Object)ChatColor.BLUE) + "The plugin that we recommend to use to prevent exploits and crashers: " + (Object)((Object)ChatColor.YELLOW) + "https://www.spigotmc.org/resources/%E2%AD%90-casualprotector-the-most-powerful-anticrash-and-antibot-optimize-fps-and-tps-%E2%AD%90.59866/"));
    }
}

