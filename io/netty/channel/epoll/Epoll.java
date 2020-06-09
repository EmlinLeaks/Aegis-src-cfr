/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.channel.epoll.Native;
import io.netty.channel.unix.FileDescriptor;
import io.netty.util.internal.SystemPropertyUtil;

public final class Epoll {
    private static final Throwable UNAVAILABILITY_CAUSE;

    public static boolean isAvailable() {
        if (UNAVAILABILITY_CAUSE != null) return false;
        return true;
    }

    public static void ensureAvailability() {
        if (UNAVAILABILITY_CAUSE == null) return;
        throw (Error)new UnsatisfiedLinkError((String)"failed to load the required native library").initCause((Throwable)UNAVAILABILITY_CAUSE);
    }

    public static Throwable unavailabilityCause() {
        return UNAVAILABILITY_CAUSE;
    }

    private Epoll() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        Throwable cause = null;
        if (SystemPropertyUtil.getBoolean((String)"io.netty.transport.noNative", (boolean)false)) {
            cause = new UnsupportedOperationException((String)"Native transport was explicit disabled with -Dio.netty.transport.noNative=true");
        } else {
            FileDescriptor epollFd = null;
            FileDescriptor eventFd = null;
            try {
                epollFd = Native.newEpollCreate();
                eventFd = Native.newEventFd();
            }
            catch (Throwable t) {
                cause = t;
            }
            finally {
                if (epollFd != null) {
                    try {
                        epollFd.close();
                    }
                    catch (Exception exception) {}
                }
                if (eventFd != null) {
                    try {
                        eventFd.close();
                    }
                    catch (Exception exception) {}
                }
            }
        }
        UNAVAILABILITY_CAUSE = cause;
    }
}

