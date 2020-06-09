/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Iterator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class IterateBlock<T> {
    DatabaseMetaData.IteratorWithCleanup<T> iteratorWithCleanup;
    Iterator<T> javaIterator;
    boolean stopIterating = false;

    IterateBlock(DatabaseMetaData.IteratorWithCleanup<T> i) {
        this.iteratorWithCleanup = i;
        this.javaIterator = null;
    }

    IterateBlock(Iterator<T> i) {
        this.javaIterator = i;
        this.iteratorWithCleanup = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doForAll() throws SQLException {
        if (this.iteratorWithCleanup != null) {
            try {
                while (this.iteratorWithCleanup.hasNext()) {
                    this.forEach(this.iteratorWithCleanup.next());
                    if (!this.stopIterating) continue;
                    break;
                }
                Object var2_1 = null;
                this.iteratorWithCleanup.close();
                return;
            }
            catch (Throwable throwable) {
                Object var2_2 = null;
                this.iteratorWithCleanup.close();
                throw throwable;
            }
        }
        do {
            if (!this.javaIterator.hasNext()) return;
            this.forEach(this.javaIterator.next());
        } while (!this.stopIterating);
    }

    abstract void forEach(T var1) throws SQLException;

    public final boolean fullIteration() {
        if (this.stopIterating) return false;
        return true;
    }
}

