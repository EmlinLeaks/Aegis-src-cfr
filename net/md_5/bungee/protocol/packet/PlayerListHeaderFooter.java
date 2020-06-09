/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class PlayerListHeaderFooter
extends DefinedPacket {
    private String header;
    private String footer;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.header = PlayerListHeaderFooter.readString((ByteBuf)buf);
        this.footer = PlayerListHeaderFooter.readString((ByteBuf)buf);
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        PlayerListHeaderFooter.writeString((String)this.header, (ByteBuf)buf);
        PlayerListHeaderFooter.writeString((String)this.footer, (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((PlayerListHeaderFooter)this);
    }

    public String getHeader() {
        return this.header;
    }

    public String getFooter() {
        return this.footer;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    @Override
    public String toString() {
        return "PlayerListHeaderFooter(header=" + this.getHeader() + ", footer=" + this.getFooter() + ")";
    }

    public PlayerListHeaderFooter() {
    }

    public PlayerListHeaderFooter(String header, String footer) {
        this.header = header;
        this.footer = footer;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlayerListHeaderFooter)) {
            return false;
        }
        PlayerListHeaderFooter other = (PlayerListHeaderFooter)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$header = this.getHeader();
        String other$header = other.getHeader();
        if (this$header == null ? other$header != null : !this$header.equals((Object)other$header)) {
            return false;
        }
        String this$footer = this.getFooter();
        String other$footer = other.getFooter();
        if (this$footer == null) {
            if (other$footer == null) return true;
            return false;
        }
        if (this$footer.equals((Object)other$footer)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof PlayerListHeaderFooter;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $header = this.getHeader();
        result = result * 59 + ($header == null ? 43 : $header.hashCode());
        String $footer = this.getFooter();
        return result * 59 + ($footer == null ? 43 : $footer.hashCode());
    }
}

