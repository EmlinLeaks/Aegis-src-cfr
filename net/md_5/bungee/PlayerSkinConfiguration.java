/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee;

import net.md_5.bungee.api.SkinConfiguration;

public class PlayerSkinConfiguration
implements SkinConfiguration {
    static final SkinConfiguration SKIN_SHOW_ALL = new PlayerSkinConfiguration((byte)127);
    private final byte bitmask;

    @Override
    public boolean hasCape() {
        if ((this.bitmask >> 0 & 1) != 1) return false;
        return true;
    }

    @Override
    public boolean hasJacket() {
        if ((this.bitmask >> 1 & 1) != 1) return false;
        return true;
    }

    @Override
    public boolean hasLeftSleeve() {
        if ((this.bitmask >> 2 & 1) != 1) return false;
        return true;
    }

    @Override
    public boolean hasRightSleeve() {
        if ((this.bitmask >> 3 & 1) != 1) return false;
        return true;
    }

    @Override
    public boolean hasLeftPants() {
        if ((this.bitmask >> 4 & 1) != 1) return false;
        return true;
    }

    @Override
    public boolean hasRightPants() {
        if ((this.bitmask >> 5 & 1) != 1) return false;
        return true;
    }

    @Override
    public boolean hasHat() {
        if ((this.bitmask >> 6 & 1) != 1) return false;
        return true;
    }

    public PlayerSkinConfiguration(byte bitmask) {
        this.bitmask = bitmask;
    }
}

