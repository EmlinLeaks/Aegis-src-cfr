/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.Headers;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface SpdyHeaders
extends Headers<CharSequence, CharSequence, SpdyHeaders> {
    public String getAsString(CharSequence var1);

    public List<String> getAllAsString(CharSequence var1);

    public Iterator<Map.Entry<String, String>> iteratorAsString();

    public boolean contains(CharSequence var1, CharSequence var2, boolean var3);
}

