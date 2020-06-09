/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class StatusResponse
extends DefinedPacket {
    private String response;

    @Override
    public void read(ByteBuf buf) {
        this.response = StatusResponse.readString((ByteBuf)buf);
    }

    @Override
    public void write(ByteBuf buf) {
        StatusResponse.writeString((String)this.response, (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((StatusResponse)this);
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "StatusResponse(response=" + this.getResponse() + ")";
    }

    public StatusResponse() {
    }

    public StatusResponse(String response) {
        this.response = response;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StatusResponse)) {
            return false;
        }
        StatusResponse other = (StatusResponse)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$response = this.getResponse();
        String other$response = other.getResponse();
        if (this$response == null) {
            if (other$response == null) return true;
            return false;
        }
        if (this$response.equals((Object)other$response)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof StatusResponse;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $response = this.getResponse();
        return result * 59 + ($response == null ? 43 : $response.hashCode());
    }
}

