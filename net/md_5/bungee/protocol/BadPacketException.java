/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol;

public class BadPacketException
extends RuntimeException {
    public BadPacketException(String message) {
        super((String)message);
    }

    public BadPacketException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }
}

