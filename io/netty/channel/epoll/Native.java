/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.epoll;

import io.netty.channel.epoll.EpollEventArray;
import io.netty.channel.epoll.NativeDatagramPacketArray;
import io.netty.channel.epoll.NativeStaticallyReferencedJniMethods;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.util.Locale;

public final class Native {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(Native.class);
    public static final int EPOLLIN;
    public static final int EPOLLOUT;
    public static final int EPOLLRDHUP;
    public static final int EPOLLET;
    public static final int EPOLLERR;
    public static final boolean IS_SUPPORTING_SENDMMSG;
    public static final boolean IS_SUPPORTING_TCP_FASTOPEN;
    public static final int TCP_MD5SIG_MAXKEYLEN;
    public static final String KERNEL_VERSION;

    public static FileDescriptor newEventFd() {
        return new FileDescriptor((int)Native.eventFd());
    }

    public static FileDescriptor newTimerFd() {
        return new FileDescriptor((int)Native.timerFd());
    }

    private static native int eventFd();

    private static native int timerFd();

    public static native void eventFdWrite(int var0, long var1);

    public static native void eventFdRead(int var0);

    static native void timerFdRead(int var0);

    static native void timerFdSetTime(int var0, int var1, int var2) throws IOException;

    public static FileDescriptor newEpollCreate() {
        return new FileDescriptor((int)Native.epollCreate());
    }

    private static native int epollCreate();

    @Deprecated
    public static int epollWait(FileDescriptor epollFd, EpollEventArray events, FileDescriptor timerFd, int timeoutSec, int timeoutNs) throws IOException {
        int ready;
        if (timeoutSec == 0 && timeoutNs == 0) {
            return Native.epollWait((FileDescriptor)epollFd, (EpollEventArray)events, (int)0);
        }
        if (timeoutSec == Integer.MAX_VALUE) {
            timeoutSec = 0;
            timeoutNs = 0;
        }
        if ((ready = Native.epollWait0((int)epollFd.intValue(), (long)events.memoryAddress(), (int)events.length(), (int)timerFd.intValue(), (int)timeoutSec, (int)timeoutNs)) >= 0) return ready;
        throw Errors.newIOException((String)"epoll_wait", (int)ready);
    }

    static int epollWait(FileDescriptor epollFd, EpollEventArray events, boolean immediatePoll) throws IOException {
        int n;
        if (immediatePoll) {
            n = 0;
            return Native.epollWait((FileDescriptor)epollFd, (EpollEventArray)events, (int)n);
        }
        n = -1;
        return Native.epollWait((FileDescriptor)epollFd, (EpollEventArray)events, (int)n);
    }

    static int epollWait(FileDescriptor epollFd, EpollEventArray events, int timeoutMillis) throws IOException {
        int ready = Native.epollWait((int)epollFd.intValue(), (long)events.memoryAddress(), (int)events.length(), (int)timeoutMillis);
        if (ready >= 0) return ready;
        throw Errors.newIOException((String)"epoll_wait", (int)ready);
    }

    public static int epollBusyWait(FileDescriptor epollFd, EpollEventArray events) throws IOException {
        int ready = Native.epollBusyWait0((int)epollFd.intValue(), (long)events.memoryAddress(), (int)events.length());
        if (ready >= 0) return ready;
        throw Errors.newIOException((String)"epoll_wait", (int)ready);
    }

    private static native int epollWait0(int var0, long var1, int var3, int var4, int var5, int var6);

    private static native int epollWait(int var0, long var1, int var3, int var4);

    private static native int epollBusyWait0(int var0, long var1, int var3);

    public static void epollCtlAdd(int efd, int fd, int flags) throws IOException {
        int res = Native.epollCtlAdd0((int)efd, (int)fd, (int)flags);
        if (res >= 0) return;
        throw Errors.newIOException((String)"epoll_ctl", (int)res);
    }

    private static native int epollCtlAdd0(int var0, int var1, int var2);

    public static void epollCtlMod(int efd, int fd, int flags) throws IOException {
        int res = Native.epollCtlMod0((int)efd, (int)fd, (int)flags);
        if (res >= 0) return;
        throw Errors.newIOException((String)"epoll_ctl", (int)res);
    }

    private static native int epollCtlMod0(int var0, int var1, int var2);

    public static void epollCtlDel(int efd, int fd) throws IOException {
        int res = Native.epollCtlDel0((int)efd, (int)fd);
        if (res >= 0) return;
        throw Errors.newIOException((String)"epoll_ctl", (int)res);
    }

    private static native int epollCtlDel0(int var0, int var1);

    public static int splice(int fd, long offIn, int fdOut, long offOut, long len) throws IOException {
        int res = Native.splice0((int)fd, (long)offIn, (int)fdOut, (long)offOut, (long)len);
        if (res < 0) return Errors.ioResult((String)"splice", (int)res);
        return res;
    }

    private static native int splice0(int var0, long var1, int var3, long var4, long var6);

    @Deprecated
    public static int sendmmsg(int fd, NativeDatagramPacketArray.NativeDatagramPacket[] msgs, int offset, int len) throws IOException {
        return Native.sendmmsg((int)fd, (boolean)Socket.isIPv6Preferred(), (NativeDatagramPacketArray.NativeDatagramPacket[])msgs, (int)offset, (int)len);
    }

    static int sendmmsg(int fd, boolean ipv6, NativeDatagramPacketArray.NativeDatagramPacket[] msgs, int offset, int len) throws IOException {
        int res = Native.sendmmsg0((int)fd, (boolean)ipv6, (NativeDatagramPacketArray.NativeDatagramPacket[])msgs, (int)offset, (int)len);
        if (res < 0) return Errors.ioResult((String)"sendmmsg", (int)res);
        return res;
    }

    private static native int sendmmsg0(int var0, boolean var1, NativeDatagramPacketArray.NativeDatagramPacket[] var2, int var3, int var4);

    static int recvmmsg(int fd, boolean ipv6, NativeDatagramPacketArray.NativeDatagramPacket[] msgs, int offset, int len) throws IOException {
        int res = Native.recvmmsg0((int)fd, (boolean)ipv6, (NativeDatagramPacketArray.NativeDatagramPacket[])msgs, (int)offset, (int)len);
        if (res < 0) return Errors.ioResult((String)"recvmmsg", (int)res);
        return res;
    }

    private static native int recvmmsg0(int var0, boolean var1, NativeDatagramPacketArray.NativeDatagramPacket[] var2, int var3, int var4);

    public static native int sizeofEpollEvent();

    public static native int offsetofEpollData();

    private static void loadNativeLibrary() {
        String name = SystemPropertyUtil.get((String)"os.name").toLowerCase((Locale)Locale.UK).trim();
        if (!name.startsWith((String)"linux")) {
            throw new IllegalStateException((String)"Only supported on Linux");
        }
        String staticLibName = "netty_transport_native_epoll";
        String sharedLibName = staticLibName + '_' + PlatformDependent.normalizedArch();
        ClassLoader cl = PlatformDependent.getClassLoader(Native.class);
        try {
            NativeLibraryLoader.load((String)sharedLibName, (ClassLoader)cl);
            return;
        }
        catch (UnsatisfiedLinkError e1) {
            try {
                NativeLibraryLoader.load((String)staticLibName, (ClassLoader)cl);
                logger.debug((String)"Failed to load {}", (Object)sharedLibName, (Object)e1);
                return;
            }
            catch (UnsatisfiedLinkError e2) {
                ThrowableUtil.addSuppressed((Throwable)e1, (Throwable)e2);
                throw e1;
            }
        }
    }

    private Native() {
    }

    static {
        try {
            Native.offsetofEpollData();
        }
        catch (UnsatisfiedLinkError ignore) {
            Native.loadNativeLibrary();
        }
        Socket.initialize();
        EPOLLIN = NativeStaticallyReferencedJniMethods.epollin();
        EPOLLOUT = NativeStaticallyReferencedJniMethods.epollout();
        EPOLLRDHUP = NativeStaticallyReferencedJniMethods.epollrdhup();
        EPOLLET = NativeStaticallyReferencedJniMethods.epollet();
        EPOLLERR = NativeStaticallyReferencedJniMethods.epollerr();
        IS_SUPPORTING_SENDMMSG = NativeStaticallyReferencedJniMethods.isSupportingSendmmsg();
        IS_SUPPORTING_TCP_FASTOPEN = NativeStaticallyReferencedJniMethods.isSupportingTcpFastopen();
        TCP_MD5SIG_MAXKEYLEN = NativeStaticallyReferencedJniMethods.tcpMd5SigMaxKeyLen();
        KERNEL_VERSION = NativeStaticallyReferencedJniMethods.kernelVersion();
    }
}

