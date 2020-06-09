/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class Login
extends DefinedPacket {
    private int entityId;
    private short gameMode;
    private int dimension;
    private long hashedSeed;
    private short difficulty;
    private short maxPlayers;
    private String levelType;
    private int viewDistance;
    private boolean reducedDebugInfo;
    private boolean enableRespawnScreen;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.entityId = buf.readInt();
        this.gameMode = buf.readUnsignedByte();
        this.dimension = protocolVersion > 107 ? buf.readInt() : (int)buf.readByte();
        if (protocolVersion >= 573) {
            this.hashedSeed = buf.readLong();
        }
        if (protocolVersion < 477) {
            this.difficulty = buf.readUnsignedByte();
        }
        this.maxPlayers = buf.readUnsignedByte();
        this.levelType = Login.readString((ByteBuf)buf);
        if (protocolVersion >= 477) {
            this.viewDistance = Login.readVarInt((ByteBuf)buf);
        }
        if (protocolVersion >= 29) {
            this.reducedDebugInfo = buf.readBoolean();
        }
        if (protocolVersion < 573) return;
        this.enableRespawnScreen = buf.readBoolean();
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        buf.writeInt((int)this.entityId);
        buf.writeByte((int)this.gameMode);
        if (protocolVersion > 107) {
            buf.writeInt((int)this.dimension);
        } else {
            buf.writeByte((int)this.dimension);
        }
        if (protocolVersion >= 573) {
            buf.writeLong((long)this.hashedSeed);
        }
        if (protocolVersion < 477) {
            buf.writeByte((int)this.difficulty);
        }
        buf.writeByte((int)this.maxPlayers);
        Login.writeString((String)this.levelType, (ByteBuf)buf);
        if (protocolVersion >= 477) {
            Login.writeVarInt((int)this.viewDistance, (ByteBuf)buf);
        }
        if (protocolVersion >= 29) {
            buf.writeBoolean((boolean)this.reducedDebugInfo);
        }
        if (protocolVersion < 573) return;
        buf.writeBoolean((boolean)this.enableRespawnScreen);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((Login)this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public short getGameMode() {
        return this.gameMode;
    }

    public int getDimension() {
        return this.dimension;
    }

    public long getHashedSeed() {
        return this.hashedSeed;
    }

    public short getDifficulty() {
        return this.difficulty;
    }

    public short getMaxPlayers() {
        return this.maxPlayers;
    }

    public String getLevelType() {
        return this.levelType;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public boolean isReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    public boolean isEnableRespawnScreen() {
        return this.enableRespawnScreen;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void setGameMode(short gameMode) {
        this.gameMode = gameMode;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setHashedSeed(long hashedSeed) {
        this.hashedSeed = hashedSeed;
    }

    public void setDifficulty(short difficulty) {
        this.difficulty = difficulty;
    }

    public void setMaxPlayers(short maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setLevelType(String levelType) {
        this.levelType = levelType;
    }

    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
    }

    public void setReducedDebugInfo(boolean reducedDebugInfo) {
        this.reducedDebugInfo = reducedDebugInfo;
    }

    public void setEnableRespawnScreen(boolean enableRespawnScreen) {
        this.enableRespawnScreen = enableRespawnScreen;
    }

    @Override
    public String toString() {
        return "Login(entityId=" + this.getEntityId() + ", gameMode=" + this.getGameMode() + ", dimension=" + this.getDimension() + ", hashedSeed=" + this.getHashedSeed() + ", difficulty=" + this.getDifficulty() + ", maxPlayers=" + this.getMaxPlayers() + ", levelType=" + this.getLevelType() + ", viewDistance=" + this.getViewDistance() + ", reducedDebugInfo=" + this.isReducedDebugInfo() + ", enableRespawnScreen=" + this.isEnableRespawnScreen() + ")";
    }

    public Login() {
    }

    public Login(int entityId, short gameMode, int dimension, long hashedSeed, short difficulty, short maxPlayers, String levelType, int viewDistance, boolean reducedDebugInfo, boolean enableRespawnScreen) {
        this.entityId = entityId;
        this.gameMode = gameMode;
        this.dimension = dimension;
        this.hashedSeed = hashedSeed;
        this.difficulty = difficulty;
        this.maxPlayers = maxPlayers;
        this.levelType = levelType;
        this.viewDistance = viewDistance;
        this.reducedDebugInfo = reducedDebugInfo;
        this.enableRespawnScreen = enableRespawnScreen;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Login)) {
            return false;
        }
        Login other = (Login)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getEntityId() != other.getEntityId()) {
            return false;
        }
        if (this.getGameMode() != other.getGameMode()) {
            return false;
        }
        if (this.getDimension() != other.getDimension()) {
            return false;
        }
        if (this.getHashedSeed() != other.getHashedSeed()) {
            return false;
        }
        if (this.getDifficulty() != other.getDifficulty()) {
            return false;
        }
        if (this.getMaxPlayers() != other.getMaxPlayers()) {
            return false;
        }
        String this$levelType = this.getLevelType();
        String other$levelType = other.getLevelType();
        if (this$levelType == null ? other$levelType != null : !this$levelType.equals((Object)other$levelType)) {
            return false;
        }
        if (this.getViewDistance() != other.getViewDistance()) {
            return false;
        }
        if (this.isReducedDebugInfo() != other.isReducedDebugInfo()) {
            return false;
        }
        if (this.isEnableRespawnScreen() == other.isEnableRespawnScreen()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Login;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getEntityId();
        result = result * 59 + this.getGameMode();
        result = result * 59 + this.getDimension();
        long $hashedSeed = this.getHashedSeed();
        result = result * 59 + (int)($hashedSeed >>> 32 ^ $hashedSeed);
        result = result * 59 + this.getDifficulty();
        result = result * 59 + this.getMaxPlayers();
        String $levelType = this.getLevelType();
        result = result * 59 + ($levelType == null ? 43 : $levelType.hashCode());
        result = result * 59 + this.getViewDistance();
        result = result * 59 + (this.isReducedDebugInfo() ? 79 : 97);
        return result * 59 + (this.isEnableRespawnScreen() ? 79 : 97);
    }
}

