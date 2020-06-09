/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.forge;

import net.md_5.bungee.forge.ForgeClientHandshakeState;
import net.md_5.bungee.forge.IForgeClientPacketHandler;

enum ForgeClientHandshakeState implements IForgeClientPacketHandler<ForgeClientHandshakeState>
{
    START{

        public ForgeClientHandshakeState handle(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            net.md_5.bungee.forge.ForgeLogger.logClient((net.md_5.bungee.forge.ForgeLogger$LogDirection)net.md_5.bungee.forge.ForgeLogger$LogDirection.RECEIVED, (String)this.name(), (net.md_5.bungee.protocol.packet.PluginMessage)message);
            con.unsafe().sendPacket((net.md_5.bungee.protocol.DefinedPacket)message);
            con.getForgeClientHandler().setState((ForgeClientHandshakeState)HELLO);
            return HELLO;
        }

        public ForgeClientHandshakeState send(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            return HELLO;
        }
    }
    ,
    HELLO{

        public ForgeClientHandshakeState handle(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            net.md_5.bungee.forge.ForgeLogger.logClient((net.md_5.bungee.forge.ForgeLogger$LogDirection)net.md_5.bungee.forge.ForgeLogger$LogDirection.RECEIVED, (String)this.name(), (net.md_5.bungee.protocol.packet.PluginMessage)message);
            if (message.getData()[0] != 0) return this;
            con.unsafe().sendPacket((net.md_5.bungee.protocol.DefinedPacket)message);
            return this;
        }

        public ForgeClientHandshakeState send(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            if (message.getData()[0] == 1) {
                return this;
            }
            if (message.getData()[0] != 2) return this;
            if (con.getForgeClientHandler().getClientModList() != null) return WAITINGSERVERDATA;
            java.util.Map<String, String> clientModList = net.md_5.bungee.forge.ForgeUtils.readModList((net.md_5.bungee.protocol.packet.PluginMessage)message);
            con.getForgeClientHandler().setClientModList(clientModList);
            return WAITINGSERVERDATA;
        }
    }
    ,
    WAITINGSERVERDATA{

        public ForgeClientHandshakeState handle(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            net.md_5.bungee.forge.ForgeLogger.logClient((net.md_5.bungee.forge.ForgeLogger$LogDirection)net.md_5.bungee.forge.ForgeLogger$LogDirection.RECEIVED, (String)this.name(), (net.md_5.bungee.protocol.packet.PluginMessage)message);
            if (message.getData()[0] != 2) return this;
            con.unsafe().sendPacket((net.md_5.bungee.protocol.DefinedPacket)message);
            return this;
        }

        public ForgeClientHandshakeState send(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            return WAITINGSERVERCOMPLETE;
        }
    }
    ,
    WAITINGSERVERCOMPLETE{

        public ForgeClientHandshakeState handle(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            net.md_5.bungee.forge.ForgeLogger.logClient((net.md_5.bungee.forge.ForgeLogger$LogDirection)net.md_5.bungee.forge.ForgeLogger$LogDirection.RECEIVED, (String)this.name(), (net.md_5.bungee.protocol.packet.PluginMessage)message);
            if (message.getData()[0] == 3) {
                con.unsafe().sendPacket((net.md_5.bungee.protocol.DefinedPacket)message);
                return this;
            }
            con.unsafe().sendPacket((net.md_5.bungee.protocol.DefinedPacket)message);
            return this;
        }

        public ForgeClientHandshakeState send(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            return PENDINGCOMPLETE;
        }
    }
    ,
    PENDINGCOMPLETE{

        public ForgeClientHandshakeState handle(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            if (message.getData()[0] != -1) return this;
            net.md_5.bungee.forge.ForgeLogger.logClient((net.md_5.bungee.forge.ForgeLogger$LogDirection)net.md_5.bungee.forge.ForgeLogger$LogDirection.RECEIVED, (String)this.name(), (net.md_5.bungee.protocol.packet.PluginMessage)message);
            con.unsafe().sendPacket((net.md_5.bungee.protocol.DefinedPacket)message);
            return this;
        }

        public ForgeClientHandshakeState send(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            return COMPLETE;
        }
    }
    ,
    COMPLETE{

        public ForgeClientHandshakeState handle(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            if (message.getData()[0] != -1) return this;
            net.md_5.bungee.forge.ForgeLogger.logClient((net.md_5.bungee.forge.ForgeLogger$LogDirection)net.md_5.bungee.forge.ForgeLogger$LogDirection.RECEIVED, (String)this.name(), (net.md_5.bungee.protocol.packet.PluginMessage)message);
            con.unsafe().sendPacket((net.md_5.bungee.protocol.DefinedPacket)message);
            return this;
        }

        public ForgeClientHandshakeState send(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            return DONE;
        }
    }
    ,
    DONE{

        public ForgeClientHandshakeState handle(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            net.md_5.bungee.forge.ForgeLogger.logClient((net.md_5.bungee.forge.ForgeLogger$LogDirection)net.md_5.bungee.forge.ForgeLogger$LogDirection.RECEIVED, (String)this.name(), (net.md_5.bungee.protocol.packet.PluginMessage)message);
            return this;
        }

        public ForgeClientHandshakeState send(net.md_5.bungee.protocol.packet.PluginMessage message, net.md_5.bungee.UserConnection con) {
            return this;
        }
    };
    
}

