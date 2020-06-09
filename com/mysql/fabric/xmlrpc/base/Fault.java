/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.xmlrpc.base;

import com.mysql.fabric.xmlrpc.base.Value;

public class Fault {
    protected Value value;

    public Value getValue() {
        return this.value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.value == null) return sb.toString();
        sb.append((String)"<fault>");
        sb.append((String)this.value.toString());
        sb.append((String)"</fault>");
        return sb.toString();
    }
}

