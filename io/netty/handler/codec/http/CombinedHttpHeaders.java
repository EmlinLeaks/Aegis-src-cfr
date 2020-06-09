/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.ValueConverter;
import io.netty.handler.codec.http.CombinedHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.util.AsciiString;
import io.netty.util.HashingStrategy;
import io.netty.util.internal.StringUtil;

public class CombinedHttpHeaders
extends DefaultHttpHeaders {
    public CombinedHttpHeaders(boolean validate) {
        super(new CombinedHttpHeadersImpl(AsciiString.CASE_INSENSITIVE_HASHER, CombinedHttpHeaders.valueConverter((boolean)validate), CombinedHttpHeaders.nameValidator((boolean)validate)));
    }

    @Override
    public boolean containsValue(CharSequence name, CharSequence value, boolean ignoreCase) {
        return super.containsValue((CharSequence)name, (CharSequence)StringUtil.trimOws((CharSequence)value), (boolean)ignoreCase);
    }
}

