/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.jni;

public class NativeCodeException
extends Exception {
    public NativeCodeException(String message, int reason) {
        super((String)(message + " : " + reason));
    }
}

