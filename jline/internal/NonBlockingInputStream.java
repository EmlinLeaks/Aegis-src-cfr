/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.internal;

import java.io.IOException;
import java.io.InputStream;
import jline.internal.Log;

public class NonBlockingInputStream
extends InputStream
implements Runnable {
    private InputStream in;
    private int ch = -2;
    private boolean threadIsReading = false;
    private boolean isShutdown = false;
    private IOException exception = null;
    private boolean nonBlockingEnabled;

    public NonBlockingInputStream(InputStream in, boolean isNonBlockingEnabled) {
        this.in = in;
        this.nonBlockingEnabled = isNonBlockingEnabled;
        if (!isNonBlockingEnabled) return;
        Thread t = new Thread((Runnable)this);
        t.setName((String)"NonBlockingInputStreamThread");
        t.setDaemon((boolean)true);
        t.start();
    }

    public synchronized void shutdown() {
        if (this.isShutdown) return;
        if (!this.nonBlockingEnabled) return;
        this.isShutdown = true;
        this.notify();
    }

    public boolean isNonBlockingEnabled() {
        if (!this.nonBlockingEnabled) return false;
        if (this.isShutdown) return false;
        return true;
    }

    @Override
    public void close() throws IOException {
        this.in.close();
        this.shutdown();
    }

    @Override
    public int read() throws IOException {
        if (!this.nonBlockingEnabled) return this.in.read();
        return this.read((long)0L, (boolean)false);
    }

    public int peek(long timeout) throws IOException {
        if (!this.nonBlockingEnabled) throw new UnsupportedOperationException((String)"peek() cannot be called as non-blocking operation is disabled");
        if (!this.isShutdown) return this.read((long)timeout, (boolean)true);
        throw new UnsupportedOperationException((String)"peek() cannot be called as non-blocking operation is disabled");
    }

    public int read(long timeout) throws IOException {
        if (!this.nonBlockingEnabled) throw new UnsupportedOperationException((String)"read() with timeout cannot be called as non-blocking operation is disabled");
        if (!this.isShutdown) return this.read((long)timeout, (boolean)false);
        throw new UnsupportedOperationException((String)"read() with timeout cannot be called as non-blocking operation is disabled");
    }

    private synchronized int read(long timeout, boolean isPeek) throws IOException {
        if (this.exception != null) {
            assert (this.ch == -2);
            IOException toBeThrown = this.exception;
            if (isPeek) throw toBeThrown;
            this.exception = null;
            throw toBeThrown;
        }
        if (this.ch >= -1) {
            assert (this.exception == null);
        } else if ((timeout == 0L || this.isShutdown) && !this.threadIsReading) {
            this.ch = this.in.read();
        } else {
            boolean isInfinite;
            if (!this.threadIsReading) {
                this.threadIsReading = true;
                this.notify();
            }
            boolean bl = isInfinite = timeout <= 0L;
            while (isInfinite || timeout > 0L) {
                long start = System.currentTimeMillis();
                try {
                    this.wait((long)timeout);
                }
                catch (InterruptedException e) {
                    // empty catch block
                }
                if (this.exception != null) {
                    assert (this.ch == -2);
                    IOException toBeThrown = this.exception;
                    if (isPeek) throw toBeThrown;
                    this.exception = null;
                    throw toBeThrown;
                }
                if (this.ch >= -1) {
                    assert (this.exception == null);
                    break;
                }
                if (isInfinite) continue;
                timeout -= System.currentTimeMillis() - start;
            }
        }
        int ret = this.ch;
        if (isPeek) return ret;
        this.ch = -2;
        return ret;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0) throw new IndexOutOfBoundsException();
        if (len < 0) throw new IndexOutOfBoundsException();
        if (len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        int c = this.nonBlockingEnabled ? this.read((long)0L) : this.in.read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte)c;
        return 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        Log.debug((Object[])new Object[]{"NonBlockingInputStream start"});
        boolean needToShutdown = false;
        boolean needToRead = false;
        do {
            if (needToShutdown) {
                Log.debug((Object[])new Object[]{"NonBlockingInputStream shutdown"});
                return;
            }
            NonBlockingInputStream nonBlockingInputStream = this;
            // MONITORENTER : nonBlockingInputStream
            needToShutdown = this.isShutdown;
            needToRead = this.threadIsReading;
            try {
                if (!needToShutdown && !needToRead) {
                    this.wait((long)0L);
                }
            }
            catch (InterruptedException e) {
                // empty catch block
            }
            if (needToShutdown || !needToRead) continue;
            int charRead = -2;
            IOException failure = null;
            try {
                charRead = this.in.read();
            }
            catch (IOException e) {
                failure = e;
            }
            NonBlockingInputStream e = this;
            // MONITORENTER : e
            this.exception = failure;
            this.ch = charRead;
            this.threadIsReading = false;
            this.notify();
            // MONITOREXIT : e
        } while (true);
    }
}

