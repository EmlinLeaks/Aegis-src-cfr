/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.util;

import com.mysql.jdbc.log.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class ReadAheadInputStream
extends InputStream {
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private InputStream underlyingStream;
    private byte[] buf;
    protected int endOfCurrentData;
    protected int currentPosition;
    protected boolean doDebug = false;
    protected Log log;

    private void fill(int readAtLeastTheseManyBytes) throws IOException {
        int n;
        this.checkClosed();
        this.endOfCurrentData = this.currentPosition = 0;
        int bytesToRead = Math.min((int)(this.buf.length - this.currentPosition), (int)readAtLeastTheseManyBytes);
        int bytesAvailable = this.underlyingStream.available();
        if (bytesAvailable > bytesToRead) {
            bytesToRead = Math.min((int)(this.buf.length - this.currentPosition), (int)bytesAvailable);
        }
        if (this.doDebug) {
            StringBuilder debugBuf = new StringBuilder();
            debugBuf.append((String)"  ReadAheadInputStream.fill(");
            debugBuf.append((int)readAtLeastTheseManyBytes);
            debugBuf.append((String)"), buffer_size=");
            debugBuf.append((int)this.buf.length);
            debugBuf.append((String)", current_position=");
            debugBuf.append((int)this.currentPosition);
            debugBuf.append((String)", need to read ");
            debugBuf.append((int)Math.min((int)(this.buf.length - this.currentPosition), (int)readAtLeastTheseManyBytes));
            debugBuf.append((String)" bytes to fill request,");
            if (bytesAvailable > 0) {
                debugBuf.append((String)" underlying InputStream reports ");
                debugBuf.append((int)bytesAvailable);
                debugBuf.append((String)" total bytes available,");
            }
            debugBuf.append((String)" attempting to read ");
            debugBuf.append((int)bytesToRead);
            debugBuf.append((String)" bytes.");
            if (this.log != null) {
                this.log.logTrace((Object)debugBuf.toString());
            } else {
                System.err.println((String)debugBuf.toString());
            }
        }
        if ((n = this.underlyingStream.read((byte[])this.buf, (int)this.currentPosition, (int)bytesToRead)) <= 0) return;
        this.endOfCurrentData = n + this.currentPosition;
    }

    private int readFromUnderlyingStreamIfNecessary(byte[] b, int off, int len) throws IOException {
        this.checkClosed();
        int avail = this.endOfCurrentData - this.currentPosition;
        if (this.doDebug) {
            StringBuilder debugBuf = new StringBuilder();
            debugBuf.append((String)"ReadAheadInputStream.readIfNecessary(");
            debugBuf.append((String)Arrays.toString((byte[])b));
            debugBuf.append((String)",");
            debugBuf.append((int)off);
            debugBuf.append((String)",");
            debugBuf.append((int)len);
            debugBuf.append((String)")");
            if (avail <= 0) {
                debugBuf.append((String)" not all data available in buffer, must read from stream");
                if (len >= this.buf.length) {
                    debugBuf.append((String)", amount requested > buffer, returning direct read() from stream");
                }
            }
            if (this.log != null) {
                this.log.logTrace((Object)debugBuf.toString());
            } else {
                System.err.println((String)debugBuf.toString());
            }
        }
        if (avail <= 0) {
            if (len >= this.buf.length) {
                return this.underlyingStream.read((byte[])b, (int)off, (int)len);
            }
            this.fill((int)len);
            avail = this.endOfCurrentData - this.currentPosition;
            if (avail <= 0) {
                return -1;
            }
        }
        int bytesActuallyRead = avail < len ? avail : len;
        System.arraycopy((Object)this.buf, (int)this.currentPosition, (Object)b, (int)off, (int)bytesActuallyRead);
        this.currentPosition += bytesActuallyRead;
        return bytesActuallyRead;
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        this.checkClosed();
        if ((off | len | off + len | b.length - (off + len)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        int totalBytesRead = 0;
        do {
            int bytesReadThisRound;
            if ((bytesReadThisRound = this.readFromUnderlyingStreamIfNecessary((byte[])b, (int)(off + totalBytesRead), (int)(len - totalBytesRead))) <= 0) {
                if (totalBytesRead != 0) return totalBytesRead;
                return bytesReadThisRound;
            }
            if ((totalBytesRead += bytesReadThisRound) < len) continue;
            return totalBytesRead;
        } while (this.underlyingStream.available() > 0);
        return totalBytesRead;
    }

    @Override
    public int read() throws IOException {
        this.checkClosed();
        if (this.currentPosition < this.endOfCurrentData) return this.buf[this.currentPosition++] & 255;
        this.fill((int)1);
        if (this.currentPosition < this.endOfCurrentData) return this.buf[this.currentPosition++] & 255;
        return -1;
    }

    @Override
    public int available() throws IOException {
        this.checkClosed();
        return this.underlyingStream.available() + (this.endOfCurrentData - this.currentPosition);
    }

    private void checkClosed() throws IOException {
        if (this.buf != null) return;
        throw new IOException((String)"Stream closed");
    }

    public ReadAheadInputStream(InputStream toBuffer, boolean debug, Log logTo) {
        this((InputStream)toBuffer, (int)4096, (boolean)debug, (Log)logTo);
    }

    public ReadAheadInputStream(InputStream toBuffer, int bufferSize, boolean debug, Log logTo) {
        this.underlyingStream = toBuffer;
        this.buf = new byte[bufferSize];
        this.doDebug = debug;
        this.log = logTo;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        if (this.underlyingStream == null) return;
        try {
            this.underlyingStream.close();
            Object var2_1 = null;
            this.underlyingStream = null;
            this.buf = null;
            this.log = null;
            return;
        }
        catch (Throwable throwable) {
            Object var2_2 = null;
            this.underlyingStream = null;
            this.buf = null;
            this.log = null;
            throw throwable;
        }
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public long skip(long n) throws IOException {
        this.checkClosed();
        if (n <= 0L) {
            return 0L;
        }
        long bytesAvailInBuffer = (long)(this.endOfCurrentData - this.currentPosition);
        if (bytesAvailInBuffer <= 0L) {
            this.fill((int)((int)n));
            bytesAvailInBuffer = (long)(this.endOfCurrentData - this.currentPosition);
            if (bytesAvailInBuffer <= 0L) {
                return 0L;
            }
        }
        long bytesSkipped = bytesAvailInBuffer < n ? bytesAvailInBuffer : n;
        this.currentPosition = (int)((long)this.currentPosition + bytesSkipped);
        return bytesSkipped;
    }
}

