/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.OutputStreamWatcher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class WatchableOutputStream
extends ByteArrayOutputStream {
    private OutputStreamWatcher watcher;

    WatchableOutputStream() {
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (this.watcher == null) return;
        this.watcher.streamClosed((WatchableOutputStream)this);
    }

    public void setWatcher(OutputStreamWatcher watcher) {
        this.watcher = watcher;
    }
}

