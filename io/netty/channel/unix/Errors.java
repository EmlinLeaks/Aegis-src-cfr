/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.unix;

import io.netty.channel.unix.Errors;
import io.netty.channel.unix.ErrorsStaticallyReferencedJniMethods;
import io.netty.util.internal.EmptyArrays;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;

public final class Errors {
    public static final int ERRNO_ENOENT_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoENOENT();
    public static final int ERRNO_ENOTCONN_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoENOTCONN();
    public static final int ERRNO_EBADF_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEBADF();
    public static final int ERRNO_EPIPE_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEPIPE();
    public static final int ERRNO_ECONNRESET_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoECONNRESET();
    public static final int ERRNO_EAGAIN_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEAGAIN();
    public static final int ERRNO_EWOULDBLOCK_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEWOULDBLOCK();
    public static final int ERRNO_EINPROGRESS_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEINPROGRESS();
    public static final int ERROR_ECONNREFUSED_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errorECONNREFUSED();
    public static final int ERROR_EISCONN_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errorEISCONN();
    public static final int ERROR_EALREADY_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errorEALREADY();
    public static final int ERROR_ENETUNREACH_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errorENETUNREACH();
    private static final String[] ERRORS = new String[512];

    static void throwConnectException(String method, int err) throws IOException {
        if (err == ERROR_EALREADY_NEGATIVE) {
            throw new ConnectionPendingException();
        }
        if (err == ERROR_ENETUNREACH_NEGATIVE) {
            throw new NoRouteToHostException();
        }
        if (err == ERROR_EISCONN_NEGATIVE) {
            throw new AlreadyConnectedException();
        }
        if (err != ERRNO_ENOENT_NEGATIVE) throw new ConnectException((String)(method + "(..) failed: " + ERRORS[-err]));
        throw new FileNotFoundException();
    }

    public static NativeIoException newConnectionResetException(String method, int errnoNegative) {
        NativeIoException exception = new NativeIoException((String)method, (int)errnoNegative, (boolean)false);
        exception.setStackTrace((StackTraceElement[])EmptyArrays.EMPTY_STACK_TRACE);
        return exception;
    }

    public static NativeIoException newIOException(String method, int err) {
        return new NativeIoException((String)method, (int)err);
    }

    @Deprecated
    public static int ioResult(String method, int err, NativeIoException resetCause, ClosedChannelException closedCause) throws IOException {
        if (err == ERRNO_EAGAIN_NEGATIVE) return 0;
        if (err == ERRNO_EWOULDBLOCK_NEGATIVE) {
            return 0;
        }
        if (err == resetCause.expectedErr()) {
            throw resetCause;
        }
        if (err == ERRNO_EBADF_NEGATIVE) {
            throw closedCause;
        }
        if (err == ERRNO_ENOTCONN_NEGATIVE) {
            throw new NotYetConnectedException();
        }
        if (err != ERRNO_ENOENT_NEGATIVE) throw Errors.newIOException((String)method, (int)err);
        throw new FileNotFoundException();
    }

    public static int ioResult(String method, int err) throws IOException {
        if (err == ERRNO_EAGAIN_NEGATIVE) return 0;
        if (err == ERRNO_EWOULDBLOCK_NEGATIVE) {
            return 0;
        }
        if (err == ERRNO_EBADF_NEGATIVE) {
            throw new ClosedChannelException();
        }
        if (err == ERRNO_ENOTCONN_NEGATIVE) {
            throw new NotYetConnectedException();
        }
        if (err != ERRNO_ENOENT_NEGATIVE) throw new NativeIoException((String)method, (int)err, (boolean)false);
        throw new FileNotFoundException();
    }

    private Errors() {
    }

    static /* synthetic */ String[] access$000() {
        return ERRORS;
    }

    static {
        int i = 0;
        while (i < ERRORS.length) {
            Errors.ERRORS[i] = ErrorsStaticallyReferencedJniMethods.strError((int)i);
            ++i;
        }
    }
}

