/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.command;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.plugin.Command;

public class CommandPerms
extends Command {
    public CommandPerms() {
        super((String)"perms");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        HashSet<String> permissions = new HashSet<String>();
        for (String group : sender.getGroups()) {
            permissions.addAll(ProxyServer.getInstance().getConfigurationAdapter().getPermissions((String)group));
        }
        sender.sendMessage((String)ProxyServer.getInstance().getTranslation((String)"command_perms_groups", (Object[])new Object[]{Util.csv(sender.getGroups())}));
        Iterator<String> iterator = permissions.iterator();
        while (iterator.hasNext()) {
            String permission = iterator.next();
            sender.sendMessage((String)ProxyServer.getInstance().getTranslation((String)"command_perms_permission", (Object[])new Object[]{permission}));
        }
    }
}

