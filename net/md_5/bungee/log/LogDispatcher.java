/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.log;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.LogRecord;
import net.md_5.bungee.log.BungeeLogger;

public class LogDispatcher
extends Thread {
    private final BungeeLogger logger;
    private final BlockingQueue<LogRecord> queue = new LinkedBlockingQueue<LogRecord>();

    public LogDispatcher(BungeeLogger logger) {
        super((String)"BungeeCord Logger Thread");
        this.logger = logger;
    }

    @Override
    public void run() {
        Object record;
        while (!this.isInterrupted()) {
            try {
                record = this.queue.take();
            }
            catch (InterruptedException ex) {
                continue;
            }
            this.logger.doLog((LogRecord)record);
        }
        record = this.queue.iterator();
        while (record.hasNext()) {
            LogRecord record2 = (LogRecord)record.next();
            this.logger.doLog((LogRecord)record2);
        }
    }

    public void queue(LogRecord record) {
        if (this.isInterrupted()) return;
        this.queue.add((LogRecord)record);
    }
}

