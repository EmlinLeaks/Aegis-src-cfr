/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class EncryptionRequest
extends DefinedPacket {
    private String serverId;
    private byte[] publicKey;
    private byte[] verifyToken;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.serverId = EncryptionRequest.readString((ByteBuf)buf);
        this.publicKey = EncryptionRequest.readArray((ByteBuf)buf);
        this.verifyToken = EncryptionRequest.readArray((ByteBuf)buf);
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        EncryptionRequest.writeString((String)this.serverId, (ByteBuf)buf);
        EncryptionRequest.writeArray((byte[])this.publicKey, (ByteBuf)buf);
        EncryptionRequest.writeArray((byte[])this.verifyToken, (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((EncryptionRequest)this);
    }

    public String getServerId() {
        return this.serverId;
    }

    public byte[] getPublicKey() {
        return this.publicKey;
    }

    public byte[] getVerifyToken() {
        return this.verifyToken;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public void setVerifyToken(byte[] verifyToken) {
        this.verifyToken = verifyToken;
    }

    @Override
    public String toString() {
        return "EncryptionRequest(serverId=" + this.getServerId() + ", publicKey=" + Arrays.toString((byte[])this.getPublicKey()) + ", verifyToken=" + Arrays.toString((byte[])this.getVerifyToken()) + ")";
    }

    public EncryptionRequest() {
    }

    public EncryptionRequest(String serverId, byte[] publicKey, byte[] verifyToken) {
        this.serverId = serverId;
        this.publicKey = publicKey;
        this.verifyToken = verifyToken;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EncryptionRequest)) {
            return false;
        }
        EncryptionRequest other = (EncryptionRequest)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$serverId = this.getServerId();
        String other$serverId = other.getServerId();
        if (this$serverId == null ? other$serverId != null : !this$serverId.equals((Object)other$serverId)) {
            return false;
        }
        if (!Arrays.equals((byte[])this.getPublicKey(), (byte[])other.getPublicKey())) {
            return false;
        }
        if (Arrays.equals((byte[])this.getVerifyToken(), (byte[])other.getVerifyToken())) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof EncryptionRequest;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $serverId = this.getServerId();
        result = result * 59 + ($serverId == null ? 43 : $serverId.hashCode());
        result = result * 59 + Arrays.hashCode((byte[])this.getPublicKey());
        return result * 59 + Arrays.hashCode((byte[])this.getVerifyToken());
    }
}

