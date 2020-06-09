/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.BadPacketException;
import net.md_5.bungee.protocol.OverflowPacketException;
import net.md_5.bungee.protocol.ProtocolConstants;

public abstract class DefinedPacket {
    public static void writeString(String s, ByteBuf buf) {
        if (s.length() > 32767) {
            throw new OverflowPacketException((String)String.format((String)"Cannot send string longer than Short.MAX_VALUE (got %s characters)", (Object[])new Object[]{Integer.valueOf((int)s.length())}));
        }
        byte[] b = s.getBytes((Charset)Charsets.UTF_8);
        DefinedPacket.writeVarInt((int)b.length, (ByteBuf)buf);
        buf.writeBytes((byte[])b);
    }

    public static String readString(ByteBuf buf) {
        int len = DefinedPacket.readVarInt((ByteBuf)buf);
        if (len > 32767) {
            throw new OverflowPacketException((String)String.format((String)"Cannot receive string longer than Short.MAX_VALUE (got %s characters)", (Object[])new Object[]{Integer.valueOf((int)len)}));
        }
        byte[] b = new byte[len];
        buf.readBytes((byte[])b);
        return new String((byte[])b, (Charset)Charsets.UTF_8);
    }

    public static void writeString(String s, int maxLength, ByteBuf buf) {
        if (s.length() > maxLength) {
            throw new OverflowPacketException((String)String.format((String)"Cannot send string longer than %s (got %s characters)", (Object[])new Object[]{Integer.valueOf((int)maxLength), Integer.valueOf((int)s.length())}));
        }
        byte[] b = s.getBytes((Charset)Charsets.UTF_8);
        DefinedPacket.writeVarInt((int)b.length, (ByteBuf)buf);
        buf.writeBytes((byte[])b);
    }

    public static String readString(ByteBuf buf, int maxLength) {
        int len = DefinedPacket.readVarInt((ByteBuf)buf);
        if (len > maxLength) {
            throw new OverflowPacketException((String)String.format((String)"Cannot receive string longer than %s (got %s characters)", (Object[])new Object[]{Integer.valueOf((int)maxLength), Integer.valueOf((int)len)}));
        }
        byte[] b = new byte[len];
        buf.readBytes((byte[])b);
        return new String((byte[])b, (Charset)Charsets.UTF_8);
    }

    public static void writeArray(byte[] b, ByteBuf buf) {
        if (b.length > 32767) {
            throw new OverflowPacketException((String)String.format((String)"Cannot send byte array longer than Short.MAX_VALUE (got %s bytes)", (Object[])new Object[]{Integer.valueOf((int)b.length)}));
        }
        DefinedPacket.writeVarInt((int)b.length, (ByteBuf)buf);
        buf.writeBytes((byte[])b);
    }

    public static byte[] toArray(ByteBuf buf) {
        byte[] ret = new byte[buf.readableBytes()];
        buf.readBytes((byte[])ret);
        return ret;
    }

    public static byte[] readArray(ByteBuf buf) {
        return DefinedPacket.readArray((ByteBuf)buf, (int)buf.readableBytes());
    }

    public static byte[] readArray(ByteBuf buf, int limit) {
        int len = DefinedPacket.readVarInt((ByteBuf)buf);
        if (len > limit) {
            throw new OverflowPacketException((String)String.format((String)"Cannot receive byte array longer than %s (got %s bytes)", (Object[])new Object[]{Integer.valueOf((int)limit), Integer.valueOf((int)len)}));
        }
        byte[] ret = new byte[len];
        buf.readBytes((byte[])ret);
        return ret;
    }

    public static int[] readVarIntArray(ByteBuf buf) {
        int len = DefinedPacket.readVarInt((ByteBuf)buf);
        int[] ret = new int[len];
        int i = 0;
        while (i < len) {
            ret[i] = DefinedPacket.readVarInt((ByteBuf)buf);
            ++i;
        }
        return ret;
    }

    public static void writeStringArray(List<String> s, ByteBuf buf) {
        DefinedPacket.writeVarInt((int)s.size(), (ByteBuf)buf);
        Iterator<String> iterator = s.iterator();
        while (iterator.hasNext()) {
            String str = iterator.next();
            DefinedPacket.writeString((String)str, (ByteBuf)buf);
        }
    }

    public static List<String> readStringArray(ByteBuf buf) {
        int len = DefinedPacket.readVarInt((ByteBuf)buf);
        ArrayList<String> ret = new ArrayList<String>((int)len);
        int i = 0;
        while (i < len) {
            ret.add((String)DefinedPacket.readString((ByteBuf)buf));
            ++i;
        }
        return ret;
    }

    public static int readVarInt(ByteBuf input) {
        return DefinedPacket.readVarInt((ByteBuf)input, (int)5);
    }

    public static int readVarInt(ByteBuf input, int maxBytes) {
        byte in;
        int out = 0;
        int bytes = 0;
        do {
            try {
                in = input.readByte();
            }
            catch (Exception ex) {
                throw new RuntimeException((String)"input.readByte() error");
            }
            out |= (in & 127) << bytes++ * 7;
            if (bytes <= maxBytes) continue;
            throw new RuntimeException((String)"bytes > maxBytes");
        } while ((in & 128) == 128);
        return out;
    }

    public static void writeVarInt(int value, ByteBuf output) {
        do {
            int part = value & 127;
            if ((value >>>= 7) != 0) {
                part |= 128;
            }
            output.writeByte((int)part);
        } while (value != 0);
    }

    public static int readVarInt(ByteBuf input, int maxBytes, ChannelHandlerContext chx, InetAddress address) {
        byte in;
        int out = 0;
        int bytes = 0;
        do {
            try {
                in = input.readByte();
                if (in < 0 && input.readableBytes() == 112) {
                    throw new BadPacketException((String)"invalid bytes");
                }
            }
            catch (Exception e) {
                throw new BadPacketException((String)"simple exception");
            }
            out |= (in & 127) << bytes++ * 7;
            if (bytes <= maxBytes) continue;
            throw new BadPacketException((String)"bytes > maxBytes");
        } while ((in & 128) == 128);
        return out;
    }

    public static int readVarShort(ByteBuf buf) {
        int low = buf.readUnsignedShort();
        int high = 0;
        if ((low & 32768) == 0) return (high & 255) << 15 | low;
        low &= 32767;
        high = (int)buf.readUnsignedByte();
        return (high & 255) << 15 | low;
    }

    public static void writeVarShort(ByteBuf buf, int toWrite) {
        int low = toWrite & 32767;
        int high = (toWrite & 8355840) >> 15;
        if (high != 0) {
            low |= 32768;
        }
        buf.writeShort((int)low);
        if (high == 0) return;
        buf.writeByte((int)high);
    }

    public static void writeUUID(UUID value, ByteBuf output) {
        output.writeLong((long)value.getMostSignificantBits());
        output.writeLong((long)value.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteBuf input) {
        return new UUID((long)input.readLong(), (long)input.readLong());
    }

    public void read(ByteBuf buf) {
        throw new UnsupportedOperationException((String)"Packet must implement read method");
    }

    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.read((ByteBuf)buf);
    }

    public void write(ByteBuf buf) {
        throw new UnsupportedOperationException((String)"Packet must implement write method");
    }

    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.write((ByteBuf)buf);
    }

    public abstract void handle(AbstractPacketHandler var1) throws Exception;

    public abstract boolean equals(Object var1);

    public abstract int hashCode();

    public abstract String toString();
}

