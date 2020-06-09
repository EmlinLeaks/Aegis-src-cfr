/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class EncryptionResponse
extends DefinedPacket {
    private byte[] sharedSecret;
    private byte[] verifyToken;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.sharedSecret = EncryptionResponse.readArray((ByteBuf)buf, (int)128);
        this.verifyToken = EncryptionResponse.readArray((ByteBuf)buf, (int)128);
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        EncryptionResponse.writeArray((byte[])this.sharedSecret, (ByteBuf)buf);
        EncryptionResponse.writeArray((byte[])this.verifyToken, (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((EncryptionResponse)this);
    }

    public byte[] getSharedSecret() {
        return this.sharedSecret;
    }

    public byte[] getVerifyToken() {
        return this.verifyToken;
    }

    public void setSharedSecret(byte[] sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public void setVerifyToken(byte[] verifyToken) {
        this.verifyToken = verifyToken;
    }

    @Override
    public String toString() {
        return "EncryptionResponse(sharedSecret=" + Arrays.toString((byte[])this.getSharedSecret()) + ", verifyToken=" + Arrays.toString((byte[])this.getVerifyToken()) + ")";
    }

    public EncryptionResponse() {
    }

    public EncryptionResponse(byte[] sharedSecret, byte[] verifyToken) {
        this.sharedSecret = sharedSecret;
        this.verifyToken = verifyToken;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EncryptionResponse)) {
            return false;
        }
        EncryptionResponse other = (EncryptionResponse)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!Arrays.equals((byte[])this.getSharedSecret(), (byte[])other.getSharedSecret())) {
            return false;
        }
        if (Arrays.equals((byte[])this.getVerifyToken(), (byte[])other.getVerifyToken())) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof EncryptionResponse;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Arrays.hashCode((byte[])this.getSharedSecret());
        return result * 59 + Arrays.hashCode((byte[])this.getVerifyToken());
    }
}

