/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import java.util.Arrays;

@Deprecated
public class ResourceLeakException
extends RuntimeException {
    private static final long serialVersionUID = 7186453858343358280L;
    private final StackTraceElement[] cachedStackTrace = this.getStackTrace();

    public ResourceLeakException() {
    }

    public ResourceLeakException(String message) {
        super((String)message);
    }

    public ResourceLeakException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public ResourceLeakException(Throwable cause) {
        super((Throwable)cause);
    }

    public int hashCode() {
        StackTraceElement[] trace = this.cachedStackTrace;
        int hashCode = 0;
        StackTraceElement[] arrstackTraceElement = trace;
        int n = arrstackTraceElement.length;
        int n2 = 0;
        while (n2 < n) {
            StackTraceElement e = arrstackTraceElement[n2];
            hashCode = hashCode * 31 + e.hashCode();
            ++n2;
        }
        return hashCode;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ResourceLeakException)) {
            return false;
        }
        if (o != this) return Arrays.equals((Object[])this.cachedStackTrace, (Object[])((ResourceLeakException)o).cachedStackTrace);
        return true;
    }
}

