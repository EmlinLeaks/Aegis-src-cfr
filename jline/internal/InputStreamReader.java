/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;

public class InputStreamReader
extends Reader {
    private InputStream in;
    private static final int BUFFER_SIZE = 8192;
    private boolean endOfInput = false;
    String encoding;
    CharsetDecoder decoder;
    ByteBuffer bytes = ByteBuffer.allocate((int)8192);

    public InputStreamReader(InputStream in) {
        super((Object)in);
        this.in = in;
        this.encoding = System.getProperty((String)"file.encoding", (String)"ISO8859_1");
        this.decoder = Charset.forName((String)this.encoding).newDecoder().onMalformedInput((CodingErrorAction)CodingErrorAction.REPLACE).onUnmappableCharacter((CodingErrorAction)CodingErrorAction.REPLACE);
        this.bytes.limit((int)0);
    }

    public InputStreamReader(InputStream in, String enc) throws UnsupportedEncodingException {
        super((Object)in);
        if (enc == null) {
            throw new NullPointerException();
        }
        this.in = in;
        try {
            this.decoder = Charset.forName((String)enc).newDecoder().onMalformedInput((CodingErrorAction)CodingErrorAction.REPLACE).onUnmappableCharacter((CodingErrorAction)CodingErrorAction.REPLACE);
        }
        catch (IllegalArgumentException e) {
            throw (UnsupportedEncodingException)new UnsupportedEncodingException((String)enc).initCause((Throwable)e);
        }
        this.bytes.limit((int)0);
    }

    public InputStreamReader(InputStream in, CharsetDecoder dec) {
        super((Object)in);
        dec.averageCharsPerByte();
        this.in = in;
        this.decoder = dec;
        this.bytes.limit((int)0);
    }

    public InputStreamReader(InputStream in, Charset charset) {
        super((Object)in);
        this.in = in;
        this.decoder = charset.newDecoder().onMalformedInput((CodingErrorAction)CodingErrorAction.REPLACE).onUnmappableCharacter((CodingErrorAction)CodingErrorAction.REPLACE);
        this.bytes.limit((int)0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        Object object = this.lock;
        // MONITORENTER : object
        this.decoder = null;
        if (this.in != null) {
            this.in.close();
            this.in = null;
        }
        // MONITOREXIT : object
        return;
    }

    public String getEncoding() {
        if (this.isOpen()) return this.encoding;
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int read() throws IOException {
        int n;
        Object object = this.lock;
        // MONITORENTER : object
        if (!this.isOpen()) {
            throw new IOException((String)"InputStreamReader is closed.");
        }
        char[] buf = new char[4];
        if (this.read((char[])buf, (int)0, (int)4) != -1) {
            n = Character.codePointAt((char[])buf, (int)0);
            return n;
        }
        n = -1;
        // MONITOREXIT : object
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int read(char[] buf, int offset, int length) throws IOException {
        boolean needInput;
        Object object = this.lock;
        // MONITORENTER : object
        if (!this.isOpen()) {
            throw new IOException((String)"InputStreamReader is closed.");
        }
        if (offset < 0) throw new IndexOutOfBoundsException();
        if (offset > buf.length - length) throw new IndexOutOfBoundsException();
        if (length < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (length == 0) {
            // MONITOREXIT : object
            return 0;
        }
        CharBuffer out = CharBuffer.wrap((char[])buf, (int)offset, (int)length);
        CoderResult result = CoderResult.UNDERFLOW;
        boolean bl = needInput = !this.bytes.hasRemaining();
        while (out.hasRemaining()) {
            if (needInput) {
                try {
                    if (this.in.available() == 0 && out.position() > offset) {
                        break;
                    }
                }
                catch (IOException e) {
                    // empty catch block
                }
                int to_read = this.bytes.capacity() - this.bytes.limit();
                int off = this.bytes.arrayOffset() + this.bytes.limit();
                int was_red = this.in.read((byte[])this.bytes.array(), (int)off, (int)to_read);
                if (was_red == -1) {
                    this.endOfInput = true;
                    break;
                }
                if (was_red == 0) break;
                this.bytes.limit((int)(this.bytes.limit() + was_red));
                needInput = false;
            }
            if (!(result = this.decoder.decode((ByteBuffer)this.bytes, (CharBuffer)out, (boolean)false)).isUnderflow()) break;
            if (this.bytes.limit() == this.bytes.capacity()) {
                this.bytes.compact();
                this.bytes.limit((int)this.bytes.position());
                this.bytes.position((int)0);
            }
            needInput = true;
        }
        if (result == CoderResult.UNDERFLOW && this.endOfInput) {
            result = this.decoder.decode((ByteBuffer)this.bytes, (CharBuffer)out, (boolean)true);
            this.decoder.flush((CharBuffer)out);
            this.decoder.reset();
        }
        if (result.isMalformed()) {
            throw new MalformedInputException((int)result.length());
        }
        if (result.isUnmappable()) {
            throw new UnmappableCharacterException((int)result.length());
        }
        if (out.position() - offset == 0) {
            return -1;
        }
        int n = out.position() - offset;
        // MONITOREXIT : object
        return n;
    }

    private boolean isOpen() {
        if (this.in == null) return false;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean ready() throws IOException {
        Object object = this.lock;
        // MONITORENTER : object
        if (this.in == null) {
            throw new IOException((String)"InputStreamReader is closed.");
        }
        try {
            if (this.bytes.hasRemaining()) return true;
            if (this.in.available() > 0) return true;
            boolean bl = false;
            // MONITOREXIT : object
            return bl;
        }
        catch (IOException e) {
            // MONITOREXIT : object
            return false;
        }
    }
}

