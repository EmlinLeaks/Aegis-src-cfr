/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.nio.charset.Charset;

@GwtCompatible(emulated=true)
public final class Charsets {
    @GwtIncompatible
    public static final Charset US_ASCII = Charset.forName((String)"US-ASCII");
    @GwtIncompatible
    public static final Charset ISO_8859_1 = Charset.forName((String)"ISO-8859-1");
    public static final Charset UTF_8 = Charset.forName((String)"UTF-8");
    @GwtIncompatible
    public static final Charset UTF_16BE = Charset.forName((String)"UTF-16BE");
    @GwtIncompatible
    public static final Charset UTF_16LE = Charset.forName((String)"UTF-16LE");
    @GwtIncompatible
    public static final Charset UTF_16 = Charset.forName((String)"UTF-16");

    private Charsets() {
    }
}

