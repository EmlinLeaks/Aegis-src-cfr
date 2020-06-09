/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.xmlrpc.base;

import com.mysql.fabric.xmlrpc.base.Value;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Data {
    protected List<Value> value;

    public List<Value> getValue() {
        if (this.value != null) return this.value;
        this.value = new ArrayList<Value>();
        return this.value;
    }

    public void addValue(Value v) {
        this.getValue().add((Value)v);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.value == null) return sb.toString();
        sb.append((String)"<data>");
        int i = 0;
        do {
            if (i >= this.value.size()) {
                sb.append((String)"</data>");
                return sb.toString();
            }
            sb.append((String)this.value.get((int)i).toString());
            ++i;
        } while (true);
    }
}

