/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.LastHttpContent;

public interface LastHttpContent
extends HttpContent {
    public static final LastHttpContent EMPTY_LAST_CONTENT = new LastHttpContent(){

        public ByteBuf content() {
            return io.netty.buffer.Unpooled.EMPTY_BUFFER;
        }

        public LastHttpContent copy() {
            return EMPTY_LAST_CONTENT;
        }

        public LastHttpContent duplicate() {
            return this;
        }

        public LastHttpContent replace(ByteBuf content) {
            return new io.netty.handler.codec.http.DefaultLastHttpContent((ByteBuf)content);
        }

        public LastHttpContent retainedDuplicate() {
            return this;
        }

        public HttpHeaders trailingHeaders() {
            return io.netty.handler.codec.http.EmptyHttpHeaders.INSTANCE;
        }

        public io.netty.handler.codec.DecoderResult decoderResult() {
            return io.netty.handler.codec.DecoderResult.SUCCESS;
        }

        @java.lang.Deprecated
        public io.netty.handler.codec.DecoderResult getDecoderResult() {
            return this.decoderResult();
        }

        public void setDecoderResult(io.netty.handler.codec.DecoderResult result) {
            throw new java.lang.UnsupportedOperationException((java.lang.String)"read only");
        }

        public int refCnt() {
            return 1;
        }

        public LastHttpContent retain() {
            return this;
        }

        public LastHttpContent retain(int increment) {
            return this;
        }

        public LastHttpContent touch() {
            return this;
        }

        public LastHttpContent touch(Object hint) {
            return this;
        }

        public boolean release() {
            return false;
        }

        public boolean release(int decrement) {
            return false;
        }

        public java.lang.String toString() {
            return "EmptyLastHttpContent";
        }
    };

    public HttpHeaders trailingHeaders();

    @Override
    public LastHttpContent copy();

    @Override
    public LastHttpContent duplicate();

    @Override
    public LastHttpContent retainedDuplicate();

    @Override
    public LastHttpContent replace(ByteBuf var1);

    @Override
    public LastHttpContent retain(int var1);

    @Override
    public LastHttpContent retain();

    @Override
    public LastHttpContent touch();

    @Override
    public LastHttpContent touch(Object var1);
}

