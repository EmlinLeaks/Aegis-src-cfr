/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.connection;

import java.net.InetSocketAddress;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.Connection;

public interface Connection {
    public InetSocketAddress getAddress();

    @Deprecated
    public void disconnect(String var1);

    public void disconnect(BaseComponent ... var1);

    public void disconnect(BaseComponent var1);

    public boolean isConnected();

    public Unsafe unsafe();
}

