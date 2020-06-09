/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.util.Signal;

public class DecoderResult {
    protected static final Signal SIGNAL_UNFINISHED = Signal.valueOf(DecoderResult.class, (String)"UNFINISHED");
    protected static final Signal SIGNAL_SUCCESS = Signal.valueOf(DecoderResult.class, (String)"SUCCESS");
    public static final DecoderResult UNFINISHED = new DecoderResult((Throwable)SIGNAL_UNFINISHED);
    public static final DecoderResult SUCCESS = new DecoderResult((Throwable)SIGNAL_SUCCESS);
    private final Throwable cause;

    public static DecoderResult failure(Throwable cause) {
        if (cause != null) return new DecoderResult((Throwable)cause);
        throw new NullPointerException((String)"cause");
    }

    protected DecoderResult(Throwable cause) {
        if (cause == null) {
            throw new NullPointerException((String)"cause");
        }
        this.cause = cause;
    }

    public boolean isFinished() {
        if (this.cause == SIGNAL_UNFINISHED) return false;
        return true;
    }

    public boolean isSuccess() {
        if (this.cause != SIGNAL_SUCCESS) return false;
        return true;
    }

    public boolean isFailure() {
        if (this.cause == SIGNAL_SUCCESS) return false;
        if (this.cause == SIGNAL_UNFINISHED) return false;
        return true;
    }

    public Throwable cause() {
        if (!this.isFailure()) return null;
        return this.cause;
    }

    public String toString() {
        if (!this.isFinished()) return "unfinished";
        if (this.isSuccess()) {
            return "success";
        }
        String cause = this.cause().toString();
        return new StringBuilder((int)(cause.length() + 17)).append((String)"failure(").append((String)cause).append((char)')').toString();
    }
}

