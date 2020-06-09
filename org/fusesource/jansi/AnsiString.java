/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.fusesource.jansi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.fusesource.jansi.AnsiOutputStream;

public class AnsiString
implements CharSequence {
    private final CharSequence encoded;
    private final CharSequence plain;

    public AnsiString(CharSequence str) {
        assert (str != null);
        this.encoded = str;
        this.plain = this.chew((CharSequence)str);
    }

    private CharSequence chew(CharSequence str) {
        assert (str != null);
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        AnsiOutputStream out = new AnsiOutputStream((OutputStream)buff);
        try {
            out.write((byte[])str.toString().getBytes());
            out.flush();
            out.close();
            return new String((byte[])buff.toByteArray());
        }
        catch (IOException e) {
            throw new RuntimeException((Throwable)e);
        }
    }

    public CharSequence getEncoded() {
        return this.encoded;
    }

    public CharSequence getPlain() {
        return this.plain;
    }

    @Override
    public char charAt(int index) {
        return this.getEncoded().charAt((int)index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return this.getEncoded().subSequence((int)start, (int)end);
    }

    @Override
    public int length() {
        return this.getPlain().length();
    }

    public boolean equals(Object obj) {
        return this.getEncoded().equals((Object)obj);
    }

    public int hashCode() {
        return this.getEncoded().hashCode();
    }

    @Override
    public String toString() {
        return this.getEncoded().toString();
    }
}

