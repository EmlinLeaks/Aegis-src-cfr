/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.forge;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.forge.ForgeLogger;
import net.md_5.bungee.protocol.packet.PluginMessage;

final class ForgeLogger {
    static void logServer(LogDirection direction, String stateName, PluginMessage message) {
        String dir = direction == LogDirection.SENDING ? "Server -> Bungee" : "Server <- Bungee";
        String log = "[" + stateName + " " + dir + "][" + direction.name() + ": " + ForgeLogger.getNameFromDiscriminator((String)message.getTag(), (PluginMessage)message) + "]";
        BungeeCord.getInstance().getLogger().log((Level)Level.FINE, (String)log);
    }

    static void logClient(LogDirection direction, String stateName, PluginMessage message) {
        String dir = direction == LogDirection.SENDING ? "Client -> Bungee" : "Client <- Bungee";
        String log = "[" + stateName + " " + dir + "][" + direction.name() + ": " + ForgeLogger.getNameFromDiscriminator((String)message.getTag(), (PluginMessage)message) + "]";
        BungeeCord.getInstance().getLogger().log((Level)Level.FINE, (String)log);
    }

    private static String getNameFromDiscriminator(String channel, PluginMessage message) {
        byte discrim = message.getData()[0];
        if (channel.equals((Object)"FML|HS")) {
            switch (discrim) {
                case -2: {
                    return "Reset";
                }
                case -1: {
                    return "HandshakeAck";
                }
                case 0: {
                    return "ServerHello";
                }
                case 1: {
                    return "ClientHello";
                }
                case 2: {
                    return "ModList";
                }
                case 3: {
                    return "ModIdData";
                }
            }
            return "Unknown";
        }
        if (!channel.equals((Object)"FORGE")) return "UnknownChannel";
        switch (discrim) {
            case 1: {
                return "DimensionRegister";
            }
            case 2: {
                return "FluidIdMap";
            }
        }
        return "Unknown";
    }

    private ForgeLogger() {
    }
}

