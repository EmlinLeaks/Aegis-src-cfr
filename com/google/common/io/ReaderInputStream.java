/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedBytes;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

@GwtIncompatible
final class ReaderInputStream
extends InputStream {
    private final Reader reader;
    private final CharsetEncoder encoder;
    private final byte[] singleByte = new byte[1];
    private CharBuffer charBuffer;
    private ByteBuffer byteBuffer;
    private boolean endOfInput;
    private boolean draining;
    private boolean doneFlushing;

    ReaderInputStream(Reader reader, Charset charset, int bufferSize) {
        this((Reader)reader, (CharsetEncoder)charset.newEncoder().onMalformedInput((CodingErrorAction)CodingErrorAction.REPLACE).onUnmappableCharacter((CodingErrorAction)CodingErrorAction.REPLACE), (int)bufferSize);
    }

    ReaderInputStream(Reader reader, CharsetEncoder encoder, int bufferSize) {
        this.reader = Preconditions.checkNotNull(reader);
        this.encoder = Preconditions.checkNotNull(encoder);
        Preconditions.checkArgument((boolean)(bufferSize > 0), (String)"bufferSize must be positive: %s", (int)bufferSize);
        encoder.reset();
        this.charBuffer = CharBuffer.allocate((int)bufferSize);
        this.charBuffer.flip();
        this.byteBuffer = ByteBuffer.allocate((int)bufferSize);
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }

    @Override
    public int read() throws IOException {
        if (this.read((byte[])this.singleByte) != 1) return -1;
        int n = UnsignedBytes.toInt((byte)this.singleByte[0]);
        return n;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        CoderResult result;
        Preconditions.checkPositionIndexes((int)off, (int)(off + len), (int)b.length);
        if (len == 0) {
            return 0;
        }
        int totalBytesRead = 0;
        boolean doneEncoding = this.endOfInput;
        block0 : do {
            if (this.draining) {
                if ((totalBytesRead += this.drain((byte[])b, (int)(off + totalBytesRead), (int)(len - totalBytesRead))) == len || this.doneFlushing) {
                    if (totalBytesRead <= 0) return -1;
                    int n = totalBytesRead;
                    return n;
                }
                this.draining = false;
                this.byteBuffer.clear();
            }
            do {
                if ((result = this.doneFlushing ? CoderResult.UNDERFLOW : (doneEncoding ? this.encoder.flush((ByteBuffer)this.byteBuffer) : this.encoder.encode((CharBuffer)this.charBuffer, (ByteBuffer)this.byteBuffer, (boolean)this.endOfInput))).isOverflow()) {
                    this.startDraining((boolean)true);
                    continue block0;
                }
                if (result.isUnderflow()) {
                    if (doneEncoding) {
                        this.doneFlushing = true;
                        this.startDraining((boolean)false);
                        continue block0;
                    }
                    if (this.endOfInput) {
                        doneEncoding = true;
                        continue;
                    }
                    this.readMoreChars();
                    continue;
                }
                if (result.isError()) break block0;
            } while (true);
            break;
        } while (true);
        result.throwException();
        return 0;
    }

    private static CharBuffer grow(CharBuffer buf) {
        char[] copy = Arrays.copyOf((char[])buf.array(), (int)(buf.capacity() * 2));
        CharBuffer bigger = CharBuffer.wrap((char[])copy);
        bigger.position((int)buf.position());
        bigger.limit((int)buf.limit());
        return bigger;
    }

    private void readMoreChars() throws IOException {
        if (ReaderInputStream.availableCapacity((Buffer)this.charBuffer) == 0) {
            if (this.charBuffer.position() > 0) {
                this.charBuffer.compact().flip();
            } else {
                this.charBuffer = ReaderInputStream.grow((CharBuffer)this.charBuffer);
            }
        }
        int limit = this.charBuffer.limit();
        int numChars = this.reader.read((char[])this.charBuffer.array(), (int)limit, (int)ReaderInputStream.availableCapacity((Buffer)this.charBuffer));
        if (numChars == -1) {
            this.endOfInput = true;
            return;
        }
        this.charBuffer.limit((int)(limit + numChars));
    }

    private static int availableCapacity(Buffer buffer) {
        return buffer.capacity() - buffer.limit();
    }

    private void startDraining(boolean overflow) {
        this.byteBuffer.flip();
        if (overflow && this.byteBuffer.remaining() == 0) {
            this.byteBuffer = ByteBuffer.allocate((int)(this.byteBuffer.capacity() * 2));
            return;
        }
        this.draining = true;
    }

    private int drain(byte[] b, int off, int len) {
        int remaining = Math.min((int)len, (int)this.byteBuffer.remaining());
        this.byteBuffer.get((byte[])b, (int)off, (int)remaining);
        return remaining;
    }
}

