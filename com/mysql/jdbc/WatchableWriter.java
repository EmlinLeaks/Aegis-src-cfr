/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.WriterWatcher;
import java.io.CharArrayWriter;

class WatchableWriter
extends CharArrayWriter {
    private WriterWatcher watcher;

    WatchableWriter() {
    }

    @Override
    public void close() {
        super.close();
        if (this.watcher == null) return;
        this.watcher.writerClosed((WatchableWriter)this);
    }

    public void setWatcher(WriterWatcher watcher) {
        this.watcher = watcher;
    }
}

