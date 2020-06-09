/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.xmlrpc.base;

import com.mysql.fabric.xmlrpc.base.Fault;
import com.mysql.fabric.xmlrpc.base.Params;

public class MethodResponse {
    protected Params params;
    protected Fault fault;

    public Params getParams() {
        return this.params;
    }

    public void setParams(Params value) {
        this.params = value;
    }

    public Fault getFault() {
        return this.fault;
    }

    public void setFault(Fault value) {
        this.fault = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder((String)"<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append((String)"<methodResponse>");
        if (this.params != null) {
            sb.append((String)this.params.toString());
        }
        if (this.fault != null) {
            sb.append((String)this.fault.toString());
        }
        sb.append((String)"</methodResponse>");
        return sb.toString();
    }
}

