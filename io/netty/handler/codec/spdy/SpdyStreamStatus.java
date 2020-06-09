/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

public class SpdyStreamStatus
implements Comparable<SpdyStreamStatus> {
    public static final SpdyStreamStatus PROTOCOL_ERROR = new SpdyStreamStatus((int)1, (String)"PROTOCOL_ERROR");
    public static final SpdyStreamStatus INVALID_STREAM = new SpdyStreamStatus((int)2, (String)"INVALID_STREAM");
    public static final SpdyStreamStatus REFUSED_STREAM = new SpdyStreamStatus((int)3, (String)"REFUSED_STREAM");
    public static final SpdyStreamStatus UNSUPPORTED_VERSION = new SpdyStreamStatus((int)4, (String)"UNSUPPORTED_VERSION");
    public static final SpdyStreamStatus CANCEL = new SpdyStreamStatus((int)5, (String)"CANCEL");
    public static final SpdyStreamStatus INTERNAL_ERROR = new SpdyStreamStatus((int)6, (String)"INTERNAL_ERROR");
    public static final SpdyStreamStatus FLOW_CONTROL_ERROR = new SpdyStreamStatus((int)7, (String)"FLOW_CONTROL_ERROR");
    public static final SpdyStreamStatus STREAM_IN_USE = new SpdyStreamStatus((int)8, (String)"STREAM_IN_USE");
    public static final SpdyStreamStatus STREAM_ALREADY_CLOSED = new SpdyStreamStatus((int)9, (String)"STREAM_ALREADY_CLOSED");
    public static final SpdyStreamStatus INVALID_CREDENTIALS = new SpdyStreamStatus((int)10, (String)"INVALID_CREDENTIALS");
    public static final SpdyStreamStatus FRAME_TOO_LARGE = new SpdyStreamStatus((int)11, (String)"FRAME_TOO_LARGE");
    private final int code;
    private final String statusPhrase;

    public static SpdyStreamStatus valueOf(int code) {
        if (code == 0) {
            throw new IllegalArgumentException((String)"0 is not a valid status code for a RST_STREAM");
        }
        switch (code) {
            case 1: {
                return PROTOCOL_ERROR;
            }
            case 2: {
                return INVALID_STREAM;
            }
            case 3: {
                return REFUSED_STREAM;
            }
            case 4: {
                return UNSUPPORTED_VERSION;
            }
            case 5: {
                return CANCEL;
            }
            case 6: {
                return INTERNAL_ERROR;
            }
            case 7: {
                return FLOW_CONTROL_ERROR;
            }
            case 8: {
                return STREAM_IN_USE;
            }
            case 9: {
                return STREAM_ALREADY_CLOSED;
            }
            case 10: {
                return INVALID_CREDENTIALS;
            }
            case 11: {
                return FRAME_TOO_LARGE;
            }
        }
        return new SpdyStreamStatus((int)code, (String)("UNKNOWN (" + code + ')'));
    }

    public SpdyStreamStatus(int code, String statusPhrase) {
        if (code == 0) {
            throw new IllegalArgumentException((String)"0 is not a valid status code for a RST_STREAM");
        }
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
        if (!(o instanceof SpdyStreamStatus)) {
            return false;
        }
        if (this.code() != ((SpdyStreamStatus)o).code()) return false;
        return true;
    }

    public String toString() {
        return this.statusPhrase();
    }

    @Override
    public int compareTo(SpdyStreamStatus o) {
        return this.code() - o.code();
    }
}

