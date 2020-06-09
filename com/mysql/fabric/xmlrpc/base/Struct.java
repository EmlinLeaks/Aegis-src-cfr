/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.xmlrpc.base;

import com.mysql.fabric.xmlrpc.base.Member;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Struct {
    protected List<Member> member;

    public List<Member> getMember() {
        if (this.member != null) return this.member;
        this.member = new ArrayList<Member>();
        return this.member;
    }

    public void addMember(Member m) {
        this.getMember().add((Member)m);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.member == null) return sb.toString();
        sb.append((String)"<struct>");
        int i = 0;
        do {
            if (i >= this.member.size()) {
                sb.append((String)"</struct>");
                return sb.toString();
            }
            sb.append((String)this.member.get((int)i).toString());
            ++i;
        } while (true);
    }
}

