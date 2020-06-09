/*
 * Decompiled with CFR <Could not determine version>.
 */
package xyz.yooniks.aegis;

import xyz.yooniks.aegis.AddressBlocker;

public final class Aegis {
    public static final String VERSION = "1.0.5 (1.8.x - 1.15.x)";
    private static Aegis instance;
    private final AddressBlocker addressBlocker = new AddressBlocker();

    private Aegis() {
        instance = this;
    }

    public AddressBlocker getAddressBlocker() {
        return this.addressBlocker;
    }

    public static Aegis getInstance() {
        if (instance != null) return instance;
        return new Aegis();
    }
}

