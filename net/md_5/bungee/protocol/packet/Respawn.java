/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class Respawn
extends DefinedPacket {
    private int dimension;
    private short difficulty;
    private long hashedSeed;
    private short gameMode;
    private String levelType;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.dimension = buf.readInt();
        if (protocolVersion < 477) {
            this.difficulty = buf.readUnsignedByte();
        }
        if (protocolVersion >= 573) {
            this.hashedSeed = buf.readLong();
        }
        this.gameMode = buf.readUnsignedByte();
        this.levelType = Respawn.readString((ByteBuf)buf);
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        buf.writeInt((int)this.dimension);
        if (protocolVersion < 477) {
            buf.writeByte((int)this.difficulty);
        }
        if (protocolVersion >= 573) {
            buf.writeLong((long)this.hashedSeed);
        }
        buf.writeByte((int)this.gameMode);
        Respawn.writeString((String)this.levelType, (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((Respawn)this);
    }

    public int getDimension() {
        return this.dimension;
    }

    public short getDifficulty() {
        return this.difficulty;
    }

    public long getHashedSeed() {
        return this.hashedSeed;
    }

    public short getGameMode() {
        return this.gameMode;
    }

    public String getLevelType() {
        return this.levelType;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setDifficulty(short difficulty) {
        this.difficulty = difficulty;
    }

    public void setHashedSeed(long hashedSeed) {
        this.hashedSeed = hashedSeed;
    }

    public void setGameMode(short gameMode) {
        this.gameMode = gameMode;
    }

    public void setLevelType(String levelType) {
        this.levelType = levelType;
    }

    @Override
    public String toString() {
        return "Respawn(dimension=" + this.getDimension() + ", difficulty=" + this.getDifficulty() + ", hashedSeed=" + this.getHashedSeed() + ", gameMode=" + this.getGameMode() + ", levelType=" + this.getLevelType() + ")";
    }

    public Respawn() {
    }

    public Respawn(int dimension, short difficulty, long hashedSeed, short gameMode, String levelType) {
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.hashedSeed = hashedSeed;
        this.gameMode = gameMode;
        this.levelType = levelType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Respawn)) {
            return false;
        }
        Respawn other = (Respawn)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getDimension() != other.getDimension()) {
            return false;
        }
        if (this.getDifficulty() != other.getDifficulty()) {
            return false;
        }
        if (this.getHashedSeed() != other.getHashedSeed()) {
            return false;
        }
        if (this.getGameMode() != other.getGameMode()) {
            return false;
        }
        String this$levelType = this.getLevelType();
        String other$levelType = other.getLevelType();
        if (this$levelType == null) {
            if (other$levelType == null) return true;
            return false;
        }
        if (this$levelType.equals((Object)other$levelType)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Respawn;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getDimension();
        result = result * 59 + this.getDifficulty();
        long $hashedSeed = this.getHashedSeed();
        result = result * 59 + (int)($hashedSeed >>> 32 ^ $hashedSeed);
        result = result * 59 + this.getGameMode();
        String $levelType = this.getLevelType();
        return result * 59 + ($levelType == null ? 43 : $levelType.hashCode());
    }
}

