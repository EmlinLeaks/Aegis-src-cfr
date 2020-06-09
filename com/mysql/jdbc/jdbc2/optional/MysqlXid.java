/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jdbc2.optional;

import javax.transaction.xa.Xid;

public class MysqlXid
implements Xid {
    int hash = 0;
    byte[] myBqual;
    int myFormatId;
    byte[] myGtrid;

    public MysqlXid(byte[] gtrid, byte[] bqual, int formatId) {
        this.myGtrid = gtrid;
        this.myBqual = bqual;
        this.myFormatId = formatId;
    }

    public boolean equals(Object another) {
        int i;
        if (!(another instanceof Xid)) return false;
        Xid anotherAsXid = (Xid)another;
        if (this.myFormatId != anotherAsXid.getFormatId()) {
            return false;
        }
        byte[] otherBqual = anotherAsXid.getBranchQualifier();
        byte[] otherGtrid = anotherAsXid.getGlobalTransactionId();
        if (otherGtrid == null) return false;
        if (otherGtrid.length != this.myGtrid.length) return false;
        int length = otherGtrid.length;
        for (i = 0; i < length; ++i) {
            if (otherGtrid[i] == this.myGtrid[i]) continue;
            return false;
        }
        if (otherBqual == null) return false;
        if (otherBqual.length != this.myBqual.length) return false;
        length = otherBqual.length;
        i = 0;
        while (i < length) {
            if (otherBqual[i] != this.myBqual[i]) {
                return false;
            }
            ++i;
        }
        return true;
    }

    @Override
    public byte[] getBranchQualifier() {
        return this.myBqual;
    }

    @Override
    public int getFormatId() {
        return this.myFormatId;
    }

    @Override
    public byte[] getGlobalTransactionId() {
        return this.myGtrid;
    }

    public synchronized int hashCode() {
        if (this.hash != 0) return this.hash;
        int i = 0;
        while (i < this.myGtrid.length) {
            this.hash = 33 * this.hash + this.myGtrid[i];
            ++i;
        }
        return this.hash;
    }
}

