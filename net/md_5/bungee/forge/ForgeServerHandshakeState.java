/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.forge;

import net.md_5.bungee.forge.ForgeServerHandshakeState;
import net.md_5.bungee.forge.IForgeServerPacketHandler;

public enum ForgeServerHandshakeState implements IForgeServerPacketHandler<ForgeServerHandshakeState>
{
    START{

        public ForgeServerHandshakeState handle(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.netty.ChannelWrapper ch) {
            net.md_5.bungee.forge.ForgeLogger.logServer((net.md_5.bungee.forge.ForgeLogger$LogDirection)net.md_5.bungee.forge.ForgeLogger$LogDirection.RECEIVED, (String)this.name(), (net.md_5.bungee.protocol.packet.PluginMessage)message);
            ch.write((Object)message);
            return this;
        }

        public ForgeServerHandshakeState send(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            return HELLO;
        }
    }
    ,
    HELLO{

        public ForgeServerHandshakeState handle(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.netty.ChannelWrapper ch) {
            net.md_5.bungee.forge.ForgeLogger.logServer((net.md_5.bungee.forge.ForgeLogger$LogDirection)net.md_5.bungee.forge.ForgeLogger$LogDirection.RECEIVED, (String)this.name(), (net.md_5.bungee.protocol.packet.PluginMessage)message);
            if (message.getData()[0] == 1) {
                ch.write((Object)message);
            }
            if (message.getData()[0] != 2) return this;
            ch.write((Object)message);
            return this;
        }

        public ForgeServerHandshakeState send(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            return WAITINGCACK;
        }
    }
    ,
    WAITINGCACK{

        public ForgeServerHandshakeState handle(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.netty.ChannelWrapper ch) {
            net.md_5.bungee.forge.ForgeLogger.logServer((net.md_5.bungee.forge.ForgeLogger$LogDirection)net.md_5.bungee.forge.ForgeLogger$LogDirection.RECEIVED, (String)this.name(), (net.md_5.bungee.protocol.packet.PluginMessage)message);
            ch.write((Object)message);
            return this;
        }

        public ForgeServerHandshakeState send(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            if (message.getData()[0] == 3 && message.getTag().equals((Object)"FML|HS")) {
                con.getForgeClientHandler().setServerIdList((net.md_5.bungee.protocol.packet.PluginMessage)message);
                return this;
            }
            if (message.getData()[0] == -1 && message.getTag().equals((Object)"FML|HS")) {
                return this;
            }
            if (!message.getTag().equals((Object)"FORGE")) return this;
            return COMPLETE;
        }
    }
    ,
    COMPLETE{

        public ForgeServerHandshakeState handle(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.netty.ChannelWrapper ch) {
            net.md_5.bungee.forge.ForgeLogger.logServer((net.md_5.bungee.forge.ForgeLogger$LogDirection)net.md_5.bungee.forge.ForgeLogger$LogDirection.RECEIVED, (String)this.name(), (net.md_5.bungee.protocol.packet.PluginMessage)message);
            ch.write((Object)message);
            return this;
        }

        public ForgeServerHandshakeState send(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            return DONE;
        }
    }
    ,
    DONE{

        public ForgeServerHandshakeState handle(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.netty.ChannelWrapper ch) {
            net.md_5.bungee.forge.ForgeLogger.logServer((net.md_5.bungee.forge.ForgeLogger$LogDirection)net.md_5.bungee.forge.ForgeLogger$LogDirection.RECEIVED, (String)this.name(), (net.md_5.bungee.protocol.packet.PluginMessage)message);
            ch.write((Object)message);
            return this;
        }

        public ForgeServerHandshakeState send(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            return this;
        }
    };
    
}

