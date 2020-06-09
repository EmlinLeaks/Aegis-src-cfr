/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class PluginMessage
extends DefinedPacket {
    public static final Function<String, String> MODERNISE = new Function<String, String>(){

        public String apply(String tag) {
            if (tag.equals((Object)"BungeeCord")) {
                return "bungeecord:main";
            }
            if (tag.equals((Object)"bungeecord:main")) {
                return "BungeeCord";
            }
            if (tag.indexOf((int)58) == -1) return "legacy:" + tag.toLowerCase((java.util.Locale)java.util.Locale.ROOT);
            return tag;
        }
    };
    public static final Predicate<PluginMessage> SHOULD_RELAY = new Predicate<PluginMessage>(){

        public boolean apply(PluginMessage input) {
            if (!(input.getTag().equals((Object)"REGISTER") || input.getTag().equals((Object)"minecraft:register") || input.getTag().equals((Object)"MC|Brand"))) {
                if (!input.getTag().equals((Object)"minecraft:brand")) return false;
            }
            if (input.getData().length >= 127) return false;
            return true;
        }
    };
    private String tag;
    private byte[] data;
    private boolean allowExtendedPacket = false;

    public PluginMessage(String tag, ByteBuf data, boolean allowExtendedPacket) {
        this((String)tag, (byte[])ByteBufUtil.getBytes((ByteBuf)data), (boolean)allowExtendedPacket);
    }

    public void setData(byte[] data) {
        this.data = Preconditions.checkNotNull(data, (Object)"Null data");
    }

    public void setData(ByteBuf buf) {
        Preconditions.checkNotNull(buf, (Object)"Null buffer");
        this.setData((byte[])ByteBufUtil.getBytes((ByteBuf)buf));
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.tag = protocolVersion >= 393 ? MODERNISE.apply((String)PluginMessage.readString((ByteBuf)buf)) : PluginMessage.readString((ByteBuf)buf);
        int maxSize = direction == ProtocolConstants.Direction.TO_SERVER ? 32767 : 1048576;
        Preconditions.checkArgument((boolean)(buf.readableBytes() < maxSize));
        this.data = new byte[buf.readableBytes()];
        buf.readBytes((byte[])this.data);
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        PluginMessage.writeString((String)(protocolVersion >= 393 ? MODERNISE.apply((String)this.tag) : this.tag), (ByteBuf)buf);
        buf.writeBytes((byte[])this.data);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((PluginMessage)this);
    }

    public DataInput getStream() {
        return new DataInputStream((InputStream)new ByteArrayInputStream((byte[])this.data));
    }

    public String getTag() {
        return this.tag;
    }

    public byte[] getData() {
        return this.data;
    }

    public boolean isAllowExtendedPacket() {
        return this.allowExtendedPacket;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setAllowExtendedPacket(boolean allowExtendedPacket) {
        this.allowExtendedPacket = allowExtendedPacket;
    }

    @Override
    public String toString() {
        return "PluginMessage(tag=" + this.getTag() + ", data=" + Arrays.toString((byte[])this.getData()) + ", allowExtendedPacket=" + this.isAllowExtendedPacket() + ")";
    }

    public PluginMessage() {
    }

    public PluginMessage(String tag, byte[] data, boolean allowExtendedPacket) {
        this.tag = tag;
        this.data = data;
        this.allowExtendedPacket = allowExtendedPacket;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PluginMessage)) {
            return false;
        }
        PluginMessage other = (PluginMessage)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$tag = this.getTag();
        String other$tag = other.getTag();
        if (this$tag == null ? other$tag != null : !this$tag.equals((Object)other$tag)) {
            return false;
        }
        if (!Arrays.equals((byte[])this.getData(), (byte[])other.getData())) {
            return false;
        }
        if (this.isAllowExtendedPacket() == other.isAllowExtendedPacket()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof PluginMessage;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $tag = this.getTag();
        result = result * 59 + ($tag == null ? 43 : $tag.hashCode());
        result = result * 59 + Arrays.hashCode((byte[])this.getData());
        return result * 59 + (this.isAllowExtendedPacket() ? 79 : 97);
    }
}

