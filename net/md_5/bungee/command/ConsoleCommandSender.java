/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.command;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;

public final class ConsoleCommandSender
implements CommandSender {
    private static final ConsoleCommandSender instance = new ConsoleCommandSender();

    @Override
    public void sendMessage(String message) {
        ProxyServer.getInstance().getLogger().info((String)message);
    }

    @Override
    public void sendMessages(String ... messages) {
        String[] arrstring = messages;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String message = arrstring[n2];
            this.sendMessage((String)message);
            ++n2;
        }
    }

    @Override
    public void sendMessage(BaseComponent ... message) {
        this.sendMessage((String)BaseComponent.toLegacyText((BaseComponent[])message));
    }

    @Override
    public void sendMessage(BaseComponent message) {
        this.sendMessage((String)message.toLegacyText());
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public Collection<String> getGroups() {
        return Collections.emptySet();
    }

    @Override
    public void addGroups(String ... groups) {
        throw new UnsupportedOperationException((String)"Console may not have groups");
    }

    @Override
    public void removeGroups(String ... groups) {
        throw new UnsupportedOperationException((String)"Console may not have groups");
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public void setPermission(String permission, boolean value) {
        throw new UnsupportedOperationException((String)"Console has all permissions");
    }

    @Override
    public Collection<String> getPermissions() {
        return Collections.emptySet();
    }

    private ConsoleCommandSender() {
    }

    public static ConsoleCommandSender getInstance() {
        return instance;
    }
}

