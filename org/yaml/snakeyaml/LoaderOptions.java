/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml;

public class LoaderOptions {
    private boolean allowDuplicateKeys = true;
    private boolean wrappedToRootException = false;

    public boolean isAllowDuplicateKeys() {
        return this.allowDuplicateKeys;
    }

    public void setAllowDuplicateKeys(boolean allowDuplicateKeys) {
        this.allowDuplicateKeys = allowDuplicateKeys;
    }

    public boolean isWrappedToRootException() {
        return this.wrappedToRootException;
    }

    public void setWrappedToRootException(boolean wrappedToRootException) {
        this.wrappedToRootException = wrappedToRootException;
    }
}

