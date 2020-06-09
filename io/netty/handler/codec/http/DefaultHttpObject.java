/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpObject;

public class DefaultHttpObject
implements HttpObject {
    private static final int HASH_CODE_PRIME = 31;
    private DecoderResult decoderResult = DecoderResult.SUCCESS;

    protected DefaultHttpObject() {
    }

    @Override
    public DecoderResult decoderResult() {
        return this.decoderResult;
    }

    @Deprecated
    @Override
    public DecoderResult getDecoderResult() {
        return this.decoderResult();
    }

    @Override
    public void setDecoderResult(DecoderResult decoderResult) {
        if (decoderResult == null) {
            throw new NullPointerException((String)"decoderResult");
        }
        this.decoderResult = decoderResult;
    }

    public int hashCode() {
        int result = 1;
        return 31 * result + this.decoderResult.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof DefaultHttpObject)) {
            return false;
        }
        DefaultHttpObject other = (DefaultHttpObject)o;
        return this.decoderResult().equals((Object)other.decoderResult());
    }
}

