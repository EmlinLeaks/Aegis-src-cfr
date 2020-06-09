/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.ReferenceCounted;

public interface InterfaceHttpData
extends Comparable<InterfaceHttpData>,
ReferenceCounted {
    public String getName();

    public HttpDataType getHttpDataType();

    @Override
    public InterfaceHttpData retain();

    @Override
    public InterfaceHttpData retain(int var1);

    @Override
    public InterfaceHttpData touch();

    @Override
    public InterfaceHttpData touch(Object var1);
}

