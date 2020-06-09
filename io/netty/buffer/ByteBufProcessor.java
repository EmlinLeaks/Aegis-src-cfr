/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBufProcessor;
import io.netty.util.ByteProcessor;

@Deprecated
public interface ByteBufProcessor
extends ByteProcessor {
    @Deprecated
    public static final ByteBufProcessor FIND_NUL = new ByteBufProcessor(){

        public boolean process(byte value) throws java.lang.Exception {
            if (value == 0) return false;
            return true;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_NON_NUL = new ByteBufProcessor(){

        public boolean process(byte value) throws java.lang.Exception {
            if (value != 0) return false;
            return true;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_CR = new ByteBufProcessor(){

        public boolean process(byte value) throws java.lang.Exception {
            if (value == 13) return false;
            return true;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_NON_CR = new ByteBufProcessor(){

        public boolean process(byte value) throws java.lang.Exception {
            if (value != 13) return false;
            return true;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_LF = new ByteBufProcessor(){

        public boolean process(byte value) throws java.lang.Exception {
            if (value == 10) return false;
            return true;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_NON_LF = new ByteBufProcessor(){

        public boolean process(byte value) throws java.lang.Exception {
            if (value != 10) return false;
            return true;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_CRLF = new ByteBufProcessor(){

        public boolean process(byte value) throws java.lang.Exception {
            if (value == 13) return false;
            if (value == 10) return false;
            return true;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_NON_CRLF = new ByteBufProcessor(){

        public boolean process(byte value) throws java.lang.Exception {
            if (value == 13) return true;
            if (value == 10) return true;
            return false;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_LINEAR_WHITESPACE = new ByteBufProcessor(){

        public boolean process(byte value) throws java.lang.Exception {
            if (value == 32) return false;
            if (value == 9) return false;
            return true;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_NON_LINEAR_WHITESPACE = new ByteBufProcessor(){

        public boolean process(byte value) throws java.lang.Exception {
            if (value == 32) return true;
            if (value == 9) return true;
            return false;
        }
    };
}

