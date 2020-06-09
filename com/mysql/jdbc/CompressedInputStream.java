/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ConnectionPropertiesImpl;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.log.Log;
import com.mysql.jdbc.log.NullLogger;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

class CompressedInputStream
extends InputStream {
    private byte[] buffer;
    private InputStream in;
    private Inflater inflater;
    private ConnectionPropertiesImpl.BooleanConnectionProperty traceProtocol;
    private Log log;
    private byte[] packetHeaderBuffer = new byte[7];
    private int pos = 0;

    public CompressedInputStream(Connection conn, InputStream streamFromServer) {
        this.traceProtocol = ((ConnectionPropertiesImpl)conn).traceProtocol;
        try {
            this.log = conn.getLog();
        }
        catch (SQLException e) {
            this.log = new NullLogger(null);
        }
        this.in = streamFromServer;
        this.inflater = new Inflater();
    }

    @Override
    public int available() throws IOException {
        if (this.buffer != null) return this.buffer.length - this.pos + this.in.available();
        return this.in.available();
    }

    @Override
    public void close() throws IOException {
        this.in.close();
        this.buffer = null;
        this.inflater.end();
        this.inflater = null;
        this.traceProtocol = null;
        this.log = null;
    }

    private void getNextPacketFromServer() throws IOException {
        byte[] uncompressedData = null;
        int lengthRead = this.readFully((byte[])this.packetHeaderBuffer, (int)0, (int)7);
        if (lengthRead < 7) {
            throw new IOException((String)"Unexpected end of input stream");
        }
        int compressedPacketLength = (this.packetHeaderBuffer[0] & 255) + ((this.packetHeaderBuffer[1] & 255) << 8) + ((this.packetHeaderBuffer[2] & 255) << 16);
        int uncompressedLength = (this.packetHeaderBuffer[4] & 255) + ((this.packetHeaderBuffer[5] & 255) << 8) + ((this.packetHeaderBuffer[6] & 255) << 16);
        boolean doTrace = this.traceProtocol.getValueAsBoolean();
        if (doTrace) {
            this.log.logTrace((Object)("Reading compressed packet of length " + compressedPacketLength + " uncompressed to " + uncompressedLength));
        }
        if (uncompressedLength > 0) {
            uncompressedData = new byte[uncompressedLength];
            byte[] compressedBuffer = new byte[compressedPacketLength];
            this.readFully((byte[])compressedBuffer, (int)0, (int)compressedPacketLength);
            this.inflater.reset();
            this.inflater.setInput((byte[])compressedBuffer);
            try {
                this.inflater.inflate((byte[])uncompressedData);
            }
            catch (DataFormatException dfe) {
                throw new IOException((String)"Error while uncompressing packet from server.");
            }
        } else {
            if (doTrace) {
                this.log.logTrace((Object)"Packet didn't meet compression threshold, not uncompressing...");
            }
            uncompressedLength = compressedPacketLength;
            uncompressedData = new byte[uncompressedLength];
            this.readFully((byte[])uncompressedData, (int)0, (int)uncompressedLength);
        }
        if (doTrace) {
            if (uncompressedLength > 1024) {
                this.log.logTrace((Object)("Uncompressed packet: \n" + StringUtils.dumpAsHex((byte[])uncompressedData, (int)256)));
                byte[] tempData = new byte[256];
                System.arraycopy((Object)uncompressedData, (int)(uncompressedLength - 256), (Object)tempData, (int)0, (int)256);
                this.log.logTrace((Object)("Uncompressed packet: \n" + StringUtils.dumpAsHex((byte[])tempData, (int)256)));
                this.log.logTrace((Object)"Large packet dump truncated. Showing first and last 256 bytes.");
            } else {
                this.log.logTrace((Object)("Uncompressed packet: \n" + StringUtils.dumpAsHex((byte[])uncompressedData, (int)uncompressedLength)));
            }
        }
        if (this.buffer != null && this.pos < this.buffer.length) {
            if (doTrace) {
                this.log.logTrace((Object)"Combining remaining packet with new: ");
            }
            int remaining = this.buffer.length - this.pos;
            byte[] newBuffer = new byte[remaining + uncompressedData.length];
            System.arraycopy((Object)this.buffer, (int)this.pos, (Object)newBuffer, (int)0, (int)remaining);
            System.arraycopy((Object)uncompressedData, (int)0, (Object)newBuffer, (int)remaining, (int)uncompressedData.length);
            uncompressedData = newBuffer;
        }
        this.pos = 0;
        this.buffer = uncompressedData;
    }

    private void getNextPacketIfRequired(int numBytes) throws IOException {
        if (this.buffer != null) {
            if (this.pos + numBytes <= this.buffer.length) return;
        }
        this.getNextPacketFromServer();
    }

    @Override
    public int read() throws IOException {
        try {
            this.getNextPacketIfRequired((int)1);
            return this.buffer[this.pos++] & 255;
        }
        catch (IOException ioEx) {
            return -1;
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read((byte[])b, (int)0, (int)b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0) throw new IndexOutOfBoundsException();
        if (off > b.length) throw new IndexOutOfBoundsException();
        if (len < 0) throw new IndexOutOfBoundsException();
        if (off + len > b.length) throw new IndexOutOfBoundsException();
        if (off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len <= 0) {
            return 0;
        }
        try {
            this.getNextPacketIfRequired((int)len);
        }
        catch (IOException ioEx) {
            return -1;
        }
        int remainingBufferLength = this.buffer.length - this.pos;
        int consummedBytesLength = Math.min((int)remainingBufferLength, (int)len);
        System.arraycopy((Object)this.buffer, (int)this.pos, (Object)b, (int)off, (int)consummedBytesLength);
        this.pos += consummedBytesLength;
        return consummedBytesLength;
    }

    private final int readFully(byte[] b, int off, int len) throws IOException {
        if (len < 0) {
            throw new IndexOutOfBoundsException();
        }
        int n = 0;
        while (n < len) {
            int count = this.in.read((byte[])b, (int)(off + n), (int)(len - n));
            if (count < 0) {
                throw new EOFException();
            }
            n += count;
        }
        return n;
    }

    @Override
    public long skip(long n) throws IOException {
        long count = 0L;
        long i = 0L;
        while (i < n) {
            int bytesRead = this.read();
            if (bytesRead == -1) {
                return count;
            }
            ++count;
            ++i;
        }
        return count;
    }
}

