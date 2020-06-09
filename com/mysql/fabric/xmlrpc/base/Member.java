/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.xmlrpc.base;

import com.mysql.fabric.xmlrpc.base.Value;

public class Member {
    protected String name;
    protected Value value;

    public Member() {
    }

    public Member(String name, Value value) {
        this.setName((String)name);
        this.setValue((Value)value);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public Value getValue() {
        return this.value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append((String)"<member>");
        sb.append((String)("<name>" + this.name + "</name>"));
        sb.append((String)this.value.toString());
        sb.append((String)"</member>");
        return sb.toString();
    }
}

