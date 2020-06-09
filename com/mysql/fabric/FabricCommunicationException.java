/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric;

public class FabricCommunicationException
extends Exception {
    private static final long serialVersionUID = 1L;

    public FabricCommunicationException(Throwable cause) {
        super((Throwable)cause);
    }

    public FabricCommunicationException(String message) {
        super((String)message);
    }

    public FabricCommunicationException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }
}

