/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.proto.xmlrpc;

import com.mysql.fabric.FabricCommunicationException;
import java.util.List;

public interface XmlRpcMethodCaller {
    public List<?> call(String var1, Object[] var2) throws FabricCommunicationException;

    public void setHeader(String var1, String var2);

    public void clearHeader(String var1);
}

