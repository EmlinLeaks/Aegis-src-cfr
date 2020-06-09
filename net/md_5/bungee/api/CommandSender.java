/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api;

import java.util.Collection;
import net.md_5.bungee.api.chat.BaseComponent;

public interface CommandSender {
    public String getName();

    @Deprecated
    public void sendMessage(String var1);

    @Deprecated
    public void sendMessages(String ... var1);

    public void sendMessage(BaseComponent ... var1);

    public void sendMessage(BaseComponent var1);

    public Collection<String> getGroups();

    public void addGroups(String ... var1);

    public void removeGroups(String ... var1);

    public boolean hasPermission(String var1);

    public void setPermission(String var1, boolean var2);

    public Collection<String> getPermissions();
}

