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

class EntityMap_1_8
extends EntityMap {
    static final EntityMap_1_8 INSTANCE = new EntityMap_1_8();

    EntityMap_1_8() {
        this.addRewrite((int)4, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)10, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)11, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)12, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)13, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)14, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)15, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)16, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)17, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)18, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)20, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)21, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)22, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)23, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)24, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)25, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)26, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)false);
        this.addRewrite((int)27, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)false);
        this.addRewrite((int)28, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)29, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)30, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)32, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)37, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)44, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)67, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)73, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT, (boolean)true);
        this.addRewrite((int)2, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_SERVER, (boolean)true);
        this.addRewrite((int)11, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_SERVER, (boolean)true);
    }

    @SuppressFBWarnings(value={"DLS_DEAD_LOCAL_STORE"})
    @Override
    public void rewriteClientbound(ByteBuf packet, int oldId, int newId) {
        int readerIndex;
        block10 : {
            int packetIdLength;
            block12 : {
                short event;
                block16 : {
                    int packetId;
                    block15 : {
                        block13 : {
                            int changedId;
                            int readId;
                            block14 : {
                                block11 : {
                                    block9 : {
                                        super.rewriteClientbound((ByteBuf)packet, (int)oldId, (int)newId);
                                        readerIndex = packet.readerIndex();
                                        packetId = DefinedPacket.readVarInt((ByteBuf)packet);
                                        packetIdLength = packet.readerIndex() - readerIndex;
                                        if (packetId != 13) break block9;
                                        DefinedPacket.readVarInt((ByteBuf)packet);
                                        EntityMap_1_8.rewriteVarInt((ByteBuf)packet, (int)oldId, (int)newId, (int)packet.readerIndex());
                                        break block10;
                                    }
                                    if (packetId != 27) break block11;
                                    EntityMap_1_8.rewriteInt((ByteBuf)packet, (int)oldId, (int)newId, (int)(readerIndex + packetIdLength + 4));
                                    break block10;
                                }
                                if (packetId == 19) break block12;
                                if (packetId != 14) break block13;
                                DefinedPacket.readVarInt((ByteBuf)packet);
                                short type = packet.readUnsignedByte();
                                if (type != 60 && type != 90) break block10;
                                packet.skipBytes((int)14);
                                int position = packet.readerIndex();
                                changedId = readId = packet.readInt();
                                if (readId == oldId) {
                                    changedId = newId;
                                    packet.setInt((int)position, (int)changedId);
                                } else if (readId == newId) {
                                    changedId = oldId;
                                    packet.setInt((int)position, (int)changedId);
                                }
                                if (readId <= 0 || changedId > 0) break block14;
                                packet.writerIndex((int)(packet.writerIndex() - 6));
                                break block10;
                            }
                            if (changedId <= 0 || readId > 0) break block10;
                            packet.ensureWritable((int)6);
                            packet.writerIndex((int)(packet.writerIndex() + 6));
                            break block10;
                        }
                        if (packetId != 12) break block15;
                        DefinedPacket.readVarInt((ByteBuf)packet);
                        int idLength = packet.readerIndex() - readerIndex - packetIdLength;
                        UUID uuid = DefinedPacket.readUUID((ByteBuf)packet);
                        UserConnection player = BungeeCord.getInstance().getPlayerByOfflineUUID((UUID)uuid);
                        if (player != null) {
                            int previous = packet.writerIndex();
                            packet.readerIndex((int)readerIndex);
                            packet.writerIndex((int)(readerIndex + packetIdLength + idLength));
                            DefinedPacket.writeUUID((UUID)player.getUniqueId(), (ByteBuf)packet);
                            packet.writerIndex((int)previous);
                        }
                        break block10;
                    }
                    if (packetId != 66) break block10;
                    event = packet.readUnsignedByte();
                    if (event != 1) break block16;
                    DefinedPacket.readVarInt((ByteBuf)packet);
                    EntityMap_1_8.rewriteInt((ByteBuf)packet, (int)oldId, (int)newId, (int)packet.readerIndex());
                    break block10;
                }
                if (event != 2) break block10;
                int position = packet.readerIndex();
                EntityMap_1_8.rewriteVarInt((ByteBuf)packet, (int)oldId, (int)newId, (int)packet.readerIndex());
                packet.readerIndex((int)position);
                DefinedPacket.readVarInt((ByteBuf)packet);
                EntityMap_1_8.rewriteInt((ByteBuf)packet, (int)oldId, (int)newId, (int)packet.readerIndex());
                break block10;
            }
            int count = DefinedPacket.readVarInt((ByteBuf)packet);
            int[] ids = new int[count];
            for (int i = 0; i < count; ++i) {
                ids[i] = DefinedPacket.readVarInt((ByteBuf)packet);
            }
            packet.readerIndex((int)(readerIndex + packetIdLength));
            packet.writerIndex((int)(readerIndex + packetIdLength));
            DefinedPacket.writeVarInt((int)count, (ByteBuf)packet);
            for (int id : ids) {
                if (id == oldId) {
                    id = newId;
                } else if (id == newId) {
                    id = oldId;
                }
                DefinedPacket.writeVarInt((int)id, (ByteBuf)packet);
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
        if (packetId == 24 && !BungeeCord.getInstance().getConfig().isIpForward()) {
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

