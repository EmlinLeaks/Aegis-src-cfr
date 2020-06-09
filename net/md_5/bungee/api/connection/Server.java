/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.connection;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;

public interface Server
extends Connection {
    public ServerInfo getInfo();

    public void sendData(String var1, byte[] var2);
}

