/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.md_5.bungee.entitymap;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.google.common.base.Throwables;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.md_5.bungee.entitymap.EntityMap_1_10;
import net.md_5.bungee.entitymap.EntityMap_1_11;
import net.md_5.bungee.entitymap.EntityMap_1_12;
import net.md_5.bungee.entitymap.EntityMap_1_12_1;
import net.md_5.bungee.entitymap.EntityMap_1_13;
import net.md_5.bungee.entitymap.EntityMap_1_14;
import net.md_5.bungee.entitymap.EntityMap_1_8;
import net.md_5.bungee.entitymap.EntityMap_1_9;
import net.md_5.bungee.entitymap.EntityMap_1_9_4;
import net.md_5.bungee.entitymap.EntityMap_Dummy;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public abstract class EntityMap {
    private final boolean[] clientboundInts = new boolean[256];
    private final boolean[] clientboundVarInts = new boolean[256];
    private final boolean[] serverboundInts = new boolean[256];
    private final boolean[] serverboundVarInts = new boolean[256];

    public static EntityMap getEntityMap(int version) {
        if (version == 573) {
            return EntityMap_Dummy.INSTANCE;
        }
        switch (version) {
            case 47: {
                return EntityMap_1_8.INSTANCE;
            }
            case 107: 
            case 108: 
            case 109: {
                return EntityMap_1_9.INSTANCE;
            }
            case 110: {
                return EntityMap_1_9_4.INSTANCE;
            }
            case 210: {
                return EntityMap_1_10.INSTANCE;
            }
            case 315: 
            case 316: {
                return EntityMap_1_11.INSTANCE;
            }
            case 335: {
                return EntityMap_1_12.INSTANCE;
            }
            case 338: 
            case 340: {
                return EntityMap_1_12_1.INSTANCE;
            }
            case 393: 
            case 401: 
            case 404: {
                return EntityMap_1_13.INSTANCE;
            }
            case 477: 
            case 480: 
            case 485: 
            case 490: 
            case 498: {
                return EntityMap_1_14.INSTANCE;
            }
        }
        throw new RuntimeException((String)("Version " + version + " has no entity map"));
    }

    protected void addRewrite(int id, ProtocolConstants.Direction direction, boolean varint) {
        if (direction == ProtocolConstants.Direction.TO_CLIENT) {
            if (varint) {
                this.clientboundVarInts[id] = true;
                return;
            }
            this.clientboundInts[id] = true;
            return;
        }
        if (varint) {
            this.serverboundVarInts[id] = true;
            return;
        }
        this.serverboundInts[id] = true;
    }

    public void rewriteServerbound(ByteBuf packet, int oldId, int newId) {
        EntityMap.rewrite((ByteBuf)packet, (int)oldId, (int)newId, (boolean[])this.serverboundInts, (boolean[])this.serverboundVarInts);
    }

    public void rewriteServerbound(ByteBuf packet, int oldId, int newId, int protocolVersion) {
        this.rewriteServerbound((ByteBuf)packet, (int)oldId, (int)newId);
    }

    public void rewriteClientbound(ByteBuf packet, int oldId, int newId) {
        EntityMap.rewrite((ByteBuf)packet, (int)oldId, (int)newId, (boolean[])this.clientboundInts, (boolean[])this.clientboundVarInts);
    }

    public void rewriteClientbound(ByteBuf packet, int oldId, int newId, int protocolVersion) {
        this.rewriteClientbound((ByteBuf)packet, (int)oldId, (int)newId);
    }

    protected static void rewriteInt(ByteBuf packet, int oldId, int newId, int offset) {
        int readId = packet.getInt((int)offset);
        if (readId == oldId) {
            packet.setInt((int)offset, (int)newId);
            return;
        }
        if (readId != newId) return;
        packet.setInt((int)offset, (int)oldId);
    }

    @SuppressFBWarnings(value={"DLS_DEAD_LOCAL_STORE"})
    protected static void rewriteVarInt(ByteBuf packet, int oldId, int newId, int offset) {
        int readId = DefinedPacket.readVarInt((ByteBuf)packet);
        int readIdLength = packet.readerIndex() - offset;
        if (readId != oldId) {
            if (readId != newId) return;
        }
        ByteBuf data = packet.copy();
        packet.readerIndex((int)offset);
        packet.writerIndex((int)offset);
        DefinedPacket.writeVarInt((int)(readId == oldId ? newId : oldId), (ByteBuf)packet);
        packet.writeBytes((ByteBuf)data);
        data.release();
    }

    protected static void rewriteMetaVarInt(ByteBuf packet, int oldId, int newId, int metaIndex) {
        EntityMap.rewriteMetaVarInt((ByteBuf)packet, (int)oldId, (int)newId, (int)metaIndex, (int)-1);
    }

    protected static void rewriteMetaVarInt(ByteBuf packet, int oldId, int newId, int metaIndex, int protocolVersion) {
        int type;
        int readerIndex = packet.readerIndex();
        block34 : do {
            short index;
            if ((index = packet.readUnsignedByte()) == 255) {
                packet.readerIndex((int)readerIndex);
                return;
            }
            type = DefinedPacket.readVarInt((ByteBuf)packet);
            if (protocolVersion >= 393) {
                switch (type) {
                    case 5: {
                        if (!packet.readBoolean()) continue block34;
                        DefinedPacket.readString((ByteBuf)packet);
                        continue block34;
                    }
                    case 15: {
                        int particleId = DefinedPacket.readVarInt((ByteBuf)packet);
                        if (protocolVersion >= 477) {
                            switch (particleId) {
                                case 3: 
                                case 23: {
                                    DefinedPacket.readVarInt((ByteBuf)packet);
                                    break;
                                }
                                case 14: {
                                    packet.skipBytes((int)16);
                                    break;
                                }
                                case 32: {
                                    EntityMap.readSkipSlot((ByteBuf)packet, (int)protocolVersion);
                                }
                            }
                            continue block34;
                        }
                        switch (particleId) {
                            case 3: 
                            case 20: {
                                DefinedPacket.readVarInt((ByteBuf)packet);
                                break;
                            }
                            case 11: {
                                packet.skipBytes((int)16);
                                break;
                            }
                            case 27: {
                                EntityMap.readSkipSlot((ByteBuf)packet, (int)protocolVersion);
                            }
                        }
                        continue block34;
                    }
                }
                if (type >= 6) {
                    --type;
                }
            }
            switch (type) {
                case 0: {
                    packet.skipBytes((int)1);
                    continue block34;
                }
                case 1: {
                    int position;
                    if (index == metaIndex) {
                        position = packet.readerIndex();
                        EntityMap.rewriteVarInt((ByteBuf)packet, (int)oldId, (int)newId, (int)position);
                        packet.readerIndex((int)position);
                    }
                    DefinedPacket.readVarInt((ByteBuf)packet);
                    continue block34;
                }
                case 2: {
                    packet.skipBytes((int)4);
                    continue block34;
                }
                case 3: 
                case 4: {
                    DefinedPacket.readString((ByteBuf)packet);
                    continue block34;
                }
                case 5: {
                    EntityMap.readSkipSlot((ByteBuf)packet, (int)protocolVersion);
                    continue block34;
                }
                case 6: {
                    packet.skipBytes((int)1);
                    continue block34;
                }
                case 7: {
                    packet.skipBytes((int)12);
                    continue block34;
                }
                case 8: {
                    packet.readLong();
                    continue block34;
                }
                case 9: {
                    if (!packet.readBoolean()) continue block34;
                    packet.skipBytes((int)8);
                    continue block34;
                }
                case 10: {
                    DefinedPacket.readVarInt((ByteBuf)packet);
                    continue block34;
                }
                case 11: {
                    if (!packet.readBoolean()) continue block34;
                    packet.skipBytes((int)16);
                    continue block34;
                }
                case 12: {
                    DefinedPacket.readVarInt((ByteBuf)packet);
                    continue block34;
                }
                case 13: {
                    try {
                        new NBTInputStream((InputStream)new ByteBufInputStream((ByteBuf)packet), (boolean)false).readTag();
                        continue block34;
                    }
                    catch (IOException ex) {
                        throw Throwables.propagate((Throwable)ex);
                    }
                }
                case 15: {
                    DefinedPacket.readVarInt((ByteBuf)packet);
                    DefinedPacket.readVarInt((ByteBuf)packet);
                    DefinedPacket.readVarInt((ByteBuf)packet);
                    continue block34;
                }
                case 16: {
                    int position;
                    if (index == metaIndex) {
                        position = packet.readerIndex();
                        EntityMap.rewriteVarInt((ByteBuf)packet, (int)(oldId + 1), (int)(newId + 1), (int)position);
                        packet.readerIndex((int)position);
                    }
                    DefinedPacket.readVarInt((ByteBuf)packet);
                    continue block34;
                }
                case 17: {
                    DefinedPacket.readVarInt((ByteBuf)packet);
                    continue block34;
                }
            }
            break;
        } while (true);
        if (protocolVersion < 393) throw new IllegalArgumentException((String)("Unknown meta type " + type + ": Using mods? refer to disable_entity_metadata_rewrite in waterfall.yml"));
        ++type;
        throw new IllegalArgumentException((String)("Unknown meta type " + type + ": Using mods? refer to disable_entity_metadata_rewrite in waterfall.yml"));
    }

    private static void readSkipSlot(ByteBuf packet, int protocolVersion) {
        if (protocolVersion >= 404) {
            if (!packet.readBoolean()) return;
        } else if (packet.readShort() == -1) return;
        if (protocolVersion >= 404) {
            DefinedPacket.readVarInt((ByteBuf)packet);
        }
        packet.skipBytes((int)(protocolVersion >= 393 ? 1 : 3));
        int position = packet.readerIndex();
        if (packet.readByte() == 0) return;
        packet.readerIndex((int)position);
        try {
            new NBTInputStream((InputStream)new ByteBufInputStream((ByteBuf)packet), (boolean)false).readTag();
            return;
        }
        catch (IOException ex) {
            throw Throwables.propagate((Throwable)ex);
        }
    }

    private static void rewrite(ByteBuf packet, int oldId, int newId, boolean[] ints, boolean[] varints) {
        int readerIndex = packet.readerIndex();
        int packetId = DefinedPacket.readVarInt((ByteBuf)packet);
        int packetIdLength = packet.readerIndex() - readerIndex;
        if (packetId < 0 || packetId > ints.length || packetId > varints.length) {
            packet.readerIndex((int)readerIndex);
            return;
        }
        if (ints[packetId]) {
            EntityMap.rewriteInt((ByteBuf)packet, (int)oldId, (int)newId, (int)(readerIndex + packetIdLength));
        } else if (varints[packetId]) {
            EntityMap.rewriteVarInt((ByteBuf)packet, (int)oldId, (int)newId, (int)(readerIndex + packetIdLength));
        }
        packet.readerIndex((int)readerIndex);
    }

    EntityMap() {
    }
}

