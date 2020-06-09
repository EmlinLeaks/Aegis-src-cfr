/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.io;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

@GwtCompatible(emulated=true)
public abstract class BaseEncoding {
    private static final BaseEncoding BASE64 = new Base64Encoding((String)"base64()", (String)"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", (Character)Character.valueOf((char)'='));
    private static final BaseEncoding BASE64_URL = new Base64Encoding((String)"base64Url()", (String)"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_", (Character)Character.valueOf((char)'='));
    private static final BaseEncoding BASE32 = new StandardBaseEncoding((String)"base32()", (String)"ABCDEFGHIJKLMNOPQRSTUVWXYZ234567", (Character)Character.valueOf((char)'='));
    private static final BaseEncoding BASE32_HEX = new StandardBaseEncoding((String)"base32Hex()", (String)"0123456789ABCDEFGHIJKLMNOPQRSTUV", (Character)Character.valueOf((char)'='));
    private static final BaseEncoding BASE16 = new Base16Encoding((String)"base16()", (String)"0123456789ABCDEF");

    BaseEncoding() {
    }

    public String encode(byte[] bytes) {
        return this.encode((byte[])bytes, (int)0, (int)bytes.length);
    }

    public final String encode(byte[] bytes, int off, int len) {
        Preconditions.checkPositionIndexes((int)off, (int)(off + len), (int)bytes.length);
        StringBuilder result = new StringBuilder((int)this.maxEncodedSize((int)len));
        try {
            this.encodeTo((Appendable)result, (byte[])bytes, (int)off, (int)len);
            return result.toString();
        }
        catch (IOException impossible) {
            throw new AssertionError((Object)impossible);
        }
    }

    @GwtIncompatible
    public abstract OutputStream encodingStream(Writer var1);

    @GwtIncompatible
    public final ByteSink encodingSink(CharSink encodedSink) {
        Preconditions.checkNotNull(encodedSink);
        return new ByteSink((BaseEncoding)this, (CharSink)encodedSink){
            final /* synthetic */ CharSink val$encodedSink;
            final /* synthetic */ BaseEncoding this$0;
            {
                this.this$0 = baseEncoding;
                this.val$encodedSink = charSink;
            }

            public OutputStream openStream() throws IOException {
                return this.this$0.encodingStream((Writer)this.val$encodedSink.openStream());
            }
        };
    }

    private static byte[] extract(byte[] result, int length) {
        if (length == result.length) {
            return result;
        }
        byte[] trunc = new byte[length];
        System.arraycopy((Object)result, (int)0, (Object)trunc, (int)0, (int)length);
        return trunc;
    }

    public abstract boolean canDecode(CharSequence var1);

    public final byte[] decode(CharSequence chars) {
        try {
            return this.decodeChecked((CharSequence)chars);
        }
        catch (DecodingException badInput) {
            throw new IllegalArgumentException((Throwable)badInput);
        }
    }

    final byte[] decodeChecked(CharSequence chars) throws DecodingException {
        chars = this.padding().trimTrailingFrom((CharSequence)chars);
        byte[] tmp = new byte[this.maxDecodedSize((int)chars.length())];
        int len = this.decodeTo((byte[])tmp, (CharSequence)chars);
        return BaseEncoding.extract((byte[])tmp, (int)len);
    }

    @GwtIncompatible
    public abstract InputStream decodingStream(Reader var1);

    @GwtIncompatible
    public final ByteSource decodingSource(CharSource encodedSource) {
        Preconditions.checkNotNull(encodedSource);
        return new ByteSource((BaseEncoding)this, (CharSource)encodedSource){
            final /* synthetic */ CharSource val$encodedSource;
            final /* synthetic */ BaseEncoding this$0;
            {
                this.this$0 = baseEncoding;
                this.val$encodedSource = charSource;
            }

            public InputStream openStream() throws IOException {
                return this.this$0.decodingStream((Reader)this.val$encodedSource.openStream());
            }
        };
    }

    abstract int maxEncodedSize(int var1);

    abstract void encodeTo(Appendable var1, byte[] var2, int var3, int var4) throws IOException;

    abstract int maxDecodedSize(int var1);

    abstract int decodeTo(byte[] var1, CharSequence var2) throws DecodingException;

    abstract CharMatcher padding();

    public abstract BaseEncoding omitPadding();

    public abstract BaseEncoding withPadChar(char var1);

    public abstract BaseEncoding withSeparator(String var1, int var2);

    public abstract BaseEncoding upperCase();

    public abstract BaseEncoding lowerCase();

    public static BaseEncoding base64() {
        return BASE64;
    }

    public static BaseEncoding base64Url() {
        return BASE64_URL;
    }

    public static BaseEncoding base32() {
        return BASE32;
    }

    public static BaseEncoding base32Hex() {
        return BASE32_HEX;
    }

    public static BaseEncoding base16() {
        return BASE16;
    }

    @GwtIncompatible
    static Reader ignoringReader(Reader delegate, CharMatcher toIgnore) {
        Preconditions.checkNotNull(delegate);
        Preconditions.checkNotNull(toIgnore);
        return new Reader((Reader)delegate, (CharMatcher)toIgnore){
            final /* synthetic */ Reader val$delegate;
            final /* synthetic */ CharMatcher val$toIgnore;
            {
                this.val$delegate = reader;
                this.val$toIgnore = charMatcher;
            }

            public int read() throws IOException {
                int readChar;
                do {
                    if ((readChar = this.val$delegate.read()) == -1) return readChar;
                } while (this.val$toIgnore.matches((char)((char)readChar)));
                return readChar;
            }

            public int read(char[] cbuf, int off, int len) throws IOException {
                throw new java.lang.UnsupportedOperationException();
            }

            public void close() throws IOException {
                this.val$delegate.close();
            }
        };
    }

    static Appendable separatingAppendable(Appendable delegate, String separator, int afterEveryChars) {
        Preconditions.checkNotNull(delegate);
        Preconditions.checkNotNull(separator);
        Preconditions.checkArgument((boolean)(afterEveryChars > 0));
        return new Appendable((int)afterEveryChars, (Appendable)delegate, (String)separator){
            int charsUntilSeparator;
            final /* synthetic */ int val$afterEveryChars;
            final /* synthetic */ Appendable val$delegate;
            final /* synthetic */ String val$separator;
            {
                this.val$afterEveryChars = n;
                this.val$delegate = appendable;
                this.val$separator = string;
                this.charsUntilSeparator = this.val$afterEveryChars;
            }

            public Appendable append(char c) throws IOException {
                if (this.charsUntilSeparator == 0) {
                    this.val$delegate.append((CharSequence)this.val$separator);
                    this.charsUntilSeparator = this.val$afterEveryChars;
                }
                this.val$delegate.append((char)c);
                --this.charsUntilSeparator;
                return this;
            }

            public Appendable append(CharSequence chars, int off, int len) throws IOException {
                throw new java.lang.UnsupportedOperationException();
            }

            public Appendable append(CharSequence chars) throws IOException {
                throw new java.lang.UnsupportedOperationException();
            }
        };
    }

    @GwtIncompatible
    static Writer separatingWriter(Writer delegate, String separator, int afterEveryChars) {
        Appendable seperatingAppendable = BaseEncoding.separatingAppendable((Appendable)delegate, (String)separator, (int)afterEveryChars);
        return new Writer((Appendable)seperatingAppendable, (Writer)delegate){
            final /* synthetic */ Appendable val$seperatingAppendable;
            final /* synthetic */ Writer val$delegate;
            {
                this.val$seperatingAppendable = appendable;
                this.val$delegate = writer;
            }

            public void write(int c) throws IOException {
                this.val$seperatingAppendable.append((char)((char)c));
            }

            public void write(char[] chars, int off, int len) throws IOException {
                throw new java.lang.UnsupportedOperationException();
            }

            public void flush() throws IOException {
                this.val$delegate.flush();
            }

            public void close() throws IOException {
                this.val$delegate.close();
            }
        };
    }
}

