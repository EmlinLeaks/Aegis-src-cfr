/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.string;

import io.netty.buffer.ByteBufUtil;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.nio.charset.Charset;

public final class LineSeparator {
    public static final LineSeparator DEFAULT = new LineSeparator((String)StringUtil.NEWLINE);
    public static final LineSeparator UNIX = new LineSeparator((String)"\n");
    public static final LineSeparator WINDOWS = new LineSeparator((String)"\r\n");
    private final String value;

    public LineSeparator(String lineSeparator) {
        this.value = ObjectUtil.checkNotNull(lineSeparator, (String)"lineSeparator");
    }

    public String value() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LineSeparator)) {
            return false;
        }
        LineSeparator that = (LineSeparator)o;
        if (this.value != null) {
            boolean bl = this.value.equals((Object)that.value);
            return bl;
        }
        if (that.value != null) return false;
        return true;
    }

    public int hashCode() {
        if (this.value == null) return 0;
        int n = this.value.hashCode();
        return n;
    }

    public String toString() {
        return ByteBufUtil.hexDump((byte[])this.value.getBytes((Charset)CharsetUtil.UTF_8));
    }
}

