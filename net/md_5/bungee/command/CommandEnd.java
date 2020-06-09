/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.command;

import com.google.common.base.Joiner;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CommandEnd
extends Command {
    public CommandEnd() {
        super((String)"end", (String)"bungeecord.command.end", (String[])new String[0]);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            BungeeCord.getInstance().stop();
            return;
        }
        BungeeCord.getInstance().stop((String)Joiner.on((char)' ').join((Object[])args));
    }
}

