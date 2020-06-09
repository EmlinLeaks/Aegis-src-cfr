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

class EntityMap_1_13
extends EntityMap {
    static final EntityMap_1_13 INSTANCE = new EntityMap_1_13();

    EntityMap_1_13() {
        this.addRewrite((int)0, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)1, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)3, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)4, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)5, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)6, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)8, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)28, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)false);
        this.addRewrite((int)39, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)40, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)41, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)42, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)51, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)54, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)57, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)60, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)63, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)64, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)false);
        this.addRewrite((int)65, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)66, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)70, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)79, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)80, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)82, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)83, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)13, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_SERVER, (boolean)true);
        this.addRewrite((int)25, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_SERVER, (boolean)true);
    }

    @SuppressFBWarnings(value={"DLS_DEAD_LOCAL_STORE"})
    @Override
    public void rewriteClientbound(ByteBuf packet, int oldId, int newId, int protocolVersion) {
        super.rewriteClientbound((ByteBuf)packet, (int)oldId, (int)newId);
        int readerIndex = packet.readerIndex();
        int packetId = DefinedPacket.readVarInt((ByteBuf)packet);
        int packetIdLength = packet.readerIndex() - readerIndex;
        int jumpIndex = packet.readerIndex();
        switch (packetId) {
            case 64: {
                EntityMap_1_13.rewriteInt((ByteBuf)packet, (int)oldId, (int)newId, (int)(readerIndex + packetIdLength + 4));
                break;
            }
            case 79: {
                DefinedPacket.readVarInt((ByteBuf)packet);
                EntityMap_1_13.rewriteVarInt((ByteBuf)packet, (int)oldId, (int)newId, (int)packet.readerIndex());
                break;
            }
            case 70: {
                DefinedPacket.readVarInt((ByteBuf)packet);
                jumpIndex = packet.readerIndex();
            }
            case 53: {
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
            case 47: {
                short event = packet.readUnsignedByte();
                if (event == 1) {
                    DefinedPacket.readVarInt((ByteBuf)packet);
                    EntityMap_1_13.rewriteInt((ByteBuf)packet, (int)oldId, (int)newId, (int)packet.readerIndex());
                    break;
                }
                if (event != 2) break;
                int position = packet.readerIndex();
                EntityMap_1_13.rewriteVarInt((ByteBuf)packet, (int)oldId, (int)newId, (int)packet.readerIndex());
                packet.readerIndex((int)position);
                DefinedPacket.readVarInt((ByteBuf)packet);
                EntityMap_1_13.rewriteInt((ByteBuf)packet, (int)oldId, (int)newId, (int)packet.readerIndex());
                break;
            }
            case 63: {
                DefinedPacket.readVarInt((ByteBuf)packet);
                EntityMap_1_13.rewriteMetaVarInt((ByteBuf)packet, (int)(oldId + 1), (int)(newId + 1), (int)6, (int)protocolVersion);
                EntityMap_1_13.rewriteMetaVarInt((ByteBuf)packet, (int)oldId, (int)newId, (int)7, (int)protocolVersion);
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
        if (packetId == 40 && !BungeeCord.getInstance().getConfig().isIpForward()) {
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

