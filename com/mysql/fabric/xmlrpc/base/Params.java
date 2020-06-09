/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.xmlrpc.base;

import com.mysql.fabric.xmlrpc.base.Param;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Params {
    protected List<Param> param;

    public List<Param> getParam() {
        if (this.param != null) return this.param;
        this.param = new ArrayList<Param>();
        return this.param;
    }

    public void addParam(Param p) {
        this.getParam().add((Param)p);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.param == null) return sb.toString();
        sb.append((String)"<params>");
        int i = 0;
        do {
            if (i >= this.param.size()) {
                sb.append((String)"</params>");
                return sb.toString();
            }
            sb.append((String)this.param.get((int)i).toString());
            ++i;
        } while (true);
    }
}

