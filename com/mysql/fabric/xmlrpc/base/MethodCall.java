/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.xmlrpc.base;

import com.mysql.fabric.xmlrpc.base.Params;

public class MethodCall {
    protected String methodName;
    protected Params params;

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String value) {
        this.methodName = value;
    }

    public Params getParams() {
        return this.params;
    }

    public void setParams(Params value) {
        this.params = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder((String)"<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append((String)"<methodCall>");
        sb.append((String)("\t<methodName>" + this.methodName + "</methodName>"));
        if (this.params != null) {
            sb.append((String)this.params.toString());
        }
        sb.append((String)"</methodCall>");
        return sb.toString();
    }
}

