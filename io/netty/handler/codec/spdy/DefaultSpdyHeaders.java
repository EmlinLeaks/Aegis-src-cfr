/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.HeadersUtils;
import io.netty.handler.codec.ValueConverter;
import io.netty.handler.codec.spdy.DefaultSpdyHeaders;
import io.netty.handler.codec.spdy.SpdyHeaders;
import io.netty.util.AsciiString;
import io.netty.util.HashingStrategy;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DefaultSpdyHeaders
extends DefaultHeaders<CharSequence, CharSequence, SpdyHeaders>
implements SpdyHeaders {
    private static final DefaultHeaders.NameValidator<CharSequence> SpdyNameValidator = new DefaultHeaders.NameValidator<CharSequence>(){

        public void validateName(CharSequence name) {
            io.netty.handler.codec.spdy.SpdyCodecUtil.validateHeaderName((CharSequence)name);
        }
    };

    public DefaultSpdyHeaders() {
        this((boolean)true);
    }

    public DefaultSpdyHeaders(boolean validate) {
        super(AsciiString.CASE_INSENSITIVE_HASHER, validate ? HeaderValueConverterAndValidator.INSTANCE : CharSequenceValueConverter.INSTANCE, validate ? SpdyNameValidator : DefaultHeaders.NameValidator.NOT_NULL);
    }

    @Override
    public String getAsString(CharSequence name) {
        return HeadersUtils.getAsString(this, name);
    }

    @Override
    public List<String> getAllAsString(CharSequence name) {
        return HeadersUtils.getAllAsString(this, name);
    }

    @Override
    public Iterator<Map.Entry<String, String>> iteratorAsString() {
        return HeadersUtils.iteratorAsString((Iterable<Map.Entry<CharSequence, CharSequence>>)this);
    }

    @Override
    public boolean contains(CharSequence name, CharSequence value) {
        return this.contains((CharSequence)name, (CharSequence)value, (boolean)false);
    }

    @Override
    public boolean contains(CharSequence name, CharSequence value, boolean ignoreCase) {
        HashingStrategy<CharSequence> hashingStrategy;
        if (ignoreCase) {
            hashingStrategy = AsciiString.CASE_INSENSITIVE_HASHER;
            return this.contains(name, value, hashingStrategy);
        }
        hashingStrategy = AsciiString.CASE_SENSITIVE_HASHER;
        return this.contains(name, value, hashingStrategy);
    }
}

