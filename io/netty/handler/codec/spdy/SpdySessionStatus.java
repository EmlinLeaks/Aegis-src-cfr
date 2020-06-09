/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

public class SpdySessionStatus
implements Comparable<SpdySessionStatus> {
    public static final SpdySessionStatus OK = new SpdySessionStatus((int)0, (String)"OK");
    public static final SpdySessionStatus PROTOCOL_ERROR = new SpdySessionStatus((int)1, (String)"PROTOCOL_ERROR");
    public static final SpdySessionStatus INTERNAL_ERROR = new SpdySessionStatus((int)2, (String)"INTERNAL_ERROR");
    private final int code;
    private final String statusPhrase;

    public static SpdySessionStatus valueOf(int code) {
        switch (code) {
            case 0: {
                return OK;
            }
            case 1: {
                return PROTOCOL_ERROR;
            }
            case 2: {
                return INTERNAL_ERROR;
            }
        }
        return new SpdySessionStatus((int)code, (String)("UNKNOWN (" + code + ')'));
    }

    public SpdySessionStatus(int code, String statusPhrase) {
        if (statusPhrase == null) {
            throw new NullPointerException((String)"statusPhrase");
        }
        this.code = code;
        this.statusPhrase = statusPhrase;
    }

    public int code() {
        return this.code;
    }

    public String statusPhrase() {
        return this.statusPhrase;
    }

    public int hashCode() {
        return this.code();
    }

    public boolean equals(Object o) {
        if (!(o instanceof SpdySessionStatus)) {
            return false;
        }
        if (this.code() != ((SpdySessionStatus)o).code()) return false;
        return true;
    }

    public String toString() {
        return this.statusPhrase();
    }

    @Override
    public int compareTo(SpdySessionStatus o) {
        return this.code() - o.code();
    }
}

