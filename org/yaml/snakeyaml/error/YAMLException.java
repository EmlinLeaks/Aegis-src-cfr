/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.error;

public class YAMLException
extends RuntimeException {
    private static final long serialVersionUID = -4738336175050337570L;

    public YAMLException(String message) {
        super((String)message);
    }

    public YAMLException(Throwable cause) {
        super((Throwable)cause);
    }

    public YAMLException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }
}

