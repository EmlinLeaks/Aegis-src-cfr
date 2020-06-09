/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.md_5.bungee.entitymap;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.entitymap.EntityMap;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

class EntityMap_1_11
extends EntityMap {
    static final EntityMap_1_11 INSTANCE = new EntityMap_1_11();

    EntityMap_1_11() {
        this.addRewrite((int)0, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)1, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)3, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)4, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)5, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)6, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)8, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)27, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)false);
        this.addRewrite((int)37, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)38, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)39, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)40, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)47, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)49, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)52, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)54, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)57, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)58, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)false);
        this.addRewrite((int)59, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)60, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)64, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)72, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)73, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)74, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)75, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)10, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_SERVER, (boolean)true);
        this.addRewrite((int)20, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_SERVER, (boolean)true);
    }

    @SuppressFBWarnings(value={"DLS_DEAD_LOCAL_STORE"})
    @Override
    public void rewriteClientbound(ByteBuf packet, int oldId, int newId) {
        super.rewriteClientbound((ByteBuf)packet, (int)oldId, (int)newId);
        int readerIndex = packet.readerIndex();
        int packetId = DefinedPacket.readVarInt((ByteBuf)packet);
        int packetIdLength = packet.readerIndex() - readerIndex;
        int jumpIndex = packet.readerIndex();
        switch (packetId) {
            case 58: {
                EntityMap_1_11.rewriteInt((ByteBuf)packet, (int)oldId, (int)newId, (int)(readerIndex + packetIdLength + 4));
                break;
            }
            case 72: {
                DefinedPacket.readVarInt((ByteBuf)packet);
                EntityMap_1_11.rewriteVarInt((ByteBuf)packet, (int)oldId, (int)newId, (int)packet.readerIndex());
                break;
            }
            case 64: {
                DefinedPacket.readVarInt((ByteBuf)packet);
                jumpIndex = packet.readerIndex();
            }
            case 48: {
                int count = DefinedPacket.readVarInt((ByteBuf)packet);
                int[] ids = new int[count];
                for (int i = 0; i < count; ++i) {
                    ids[i] = DefinedPacket.readVarInt((ByteBuf)packet);
                }
                packet.readerIndex((int)jumpIndex);
                packet.writerIndex((int)jumpIndex);
                DefinedPacket.writeVarInt((int)count, (ByteBuf)packet);
                for (int id : ids) {
                    if (id == oldId) {
                        id = newId;
                    } else if (id == newId) {
                        id = oldId;
                    }
                    DefinedPacket.writeVarInt((int)id, (ByteBuf)packet);
                }
                break;
            }
            case 0: {
                DefinedPacket.readVarInt((ByteBuf)packet);
                DefinedPacket.readUUID((ByteBuf)packet);
                short type = packet.readUnsignedByte();
                if (type != 60 && type != 90 && type != 91) break;
                if (type == 60 || type == 91) {
                    ++oldId;
                    ++newId;
                }
                packet.skipBytes((int)26);
                int position = packet.readerIndex();
                int readId = packet.readInt();
                if (readId == oldId) {
                    packet.setInt((int)position, (int)newId);
                    break;
                }
                if (readId != newId) break;
                packet.setInt((int)position, (int)oldId);
                break;
            }
            case 5: {
                DefinedPacket.readVarInt((ByteBuf)packet);
                int idLength = packet.readerIndex() - readerIndex - packetIdLength;
                UUID uuid = DefinedPacket.readUUID((ByteBuf)packet);
                UserConnection player = BungeeCord.getInstance().getPlayerByOfflineUUID((UUID)uuid);
                if (player == null) break;
                int previous = packet.writerIndex();
                packet.readerIndex((int)readerIndex);
                packet.writerIndex((int)(readerIndex + packetIdLength + idLength));
                DefinedPacket.writeUUID((UUID)player.getUniqueId(), (ByteBuf)packet);
                packet.writerIndex((int)previous);
                break;
            }
            case 44: {
                short event = packet.readUnsignedByte();
                if (event == 1) {
                    DefinedPacket.readVarInt((ByteBuf)packet);
                    EntityMap_1_11.rewriteInt((ByteBuf)packet, (int)oldId, (int)newId, (int)packet.readerIndex());
                    break;
                }
                if (event != 2) break;
                int position = packet.readerIndex();
                EntityMap_1_11.rewriteVarInt((ByteBuf)packet, (int)oldId, (int)newId, (int)packet.readerIndex());
                packet.readerIndex((int)position);
                DefinedPacket.readVarInt((ByteBuf)packet);
                EntityMap_1_11.rewriteInt((ByteBuf)packet, (int)oldId, (int)newId, (int)packet.readerIndex());
                break;
            }
            case 57: {
                DefinedPacket.readVarInt((ByteBuf)packet);
                EntityMap_1_11.rewriteMetaVarInt((ByteBuf)packet, (int)(oldId + 1), (int)(newId + 1), (int)6);
                EntityMap_1_11.rewriteMetaVarInt((ByteBuf)packet, (int)oldId, (int)newId, (int)7);
            }
        }
        packet.readerIndex((int)readerIndex);
    }

    @Override
    public void rewriteServerbound(ByteBuf packet, int oldId, int newId) {
        super.rewriteServerbound((ByteBuf)packet, (int)oldId, (int)newId);
        int readerIndex = packet.readerIndex();
        int packetId = DefinedPacket.readVarInt((ByteBuf)packet);
        int packetIdLength = packet.readerIndex() - readerIndex;
        if (packetId == 27 && !BungeeCord.getInstance().getConfig().isIpForward()) {
            UUID uuid = DefinedPacket.readUUID((ByteBuf)packet);
            ProxiedPlayer player = BungeeCord.getInstance().getPlayer((UUID)uuid);
            if (player != null) {
                int previous = packet.writerIndex();
                packet.readerIndex((int)readerIndex);
                packet.writerIndex((int)(readerIndex + packetIdLength));
                DefinedPacket.writeUUID((UUID)((UserConnection)player).getPendingConnection().getOfflineId(), (ByteBuf)packet);
                packet.writerIndex((int)previous);
            }
        }
        packet.readerIndex((int)readerIndex);
    }
}

