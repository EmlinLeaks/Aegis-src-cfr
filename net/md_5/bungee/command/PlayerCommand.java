/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.command;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Locale;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.command.PlayerCommand;

@Deprecated
public abstract class PlayerCommand
extends Command
implements TabExecutor {
    public PlayerCommand(String name) {
        super((String)name);
    }

    public PlayerCommand(String name, String permission, String ... aliases) {
        super((String)name, (String)permission, (String[])aliases);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        String lastArg = args.length > 0 ? args[args.length - 1].toLowerCase((Locale)Locale.ROOT) : "";
        return Iterables.transform(Iterables.filter(ProxyServer.getInstance().getPlayers(), new Predicate<ProxiedPlayer>((PlayerCommand)this, (String)lastArg){
            final /* synthetic */ String val$lastArg;
            final /* synthetic */ PlayerCommand this$0;
            {
                this.this$0 = this$0;
                this.val$lastArg = string;
            }

            public boolean apply(ProxiedPlayer player) {
                return player.getName().toLowerCase((Locale)Locale.ROOT).startsWith((String)this.val$lastArg);
            }
        }), new Function<ProxiedPlayer, String>((PlayerCommand)this){
            final /* synthetic */ PlayerCommand this$0;
            {
                this.this$0 = this$0;
            }

            public String apply(ProxiedPlayer player) {
                return player.getName();
            }
        });
    }
}

