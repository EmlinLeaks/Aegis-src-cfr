/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.xmlrpc.base;

import com.mysql.fabric.xmlrpc.base.Value;

public class Param {
    protected Value value;

    public Param() {
    }

    public Param(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return this.value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder((String)"<param>");
        sb.append((String)this.value.toString());
        sb.append((String)"</param>");
        return sb.toString();
    }
}

