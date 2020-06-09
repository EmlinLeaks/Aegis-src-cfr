/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import java.io.IOException;
import java.util.Arrays;

public final class AppendableCharSequence
implements CharSequence,
Appendable {
    private char[] chars;
    private int pos;

    public AppendableCharSequence(int length) {
        if (length < 1) {
            throw new IllegalArgumentException((String)("length: " + length + " (length: >= 1)"));
        }
        this.chars = new char[length];
    }

    private AppendableCharSequence(char[] chars) {
        if (chars.length < 1) {
            throw new IllegalArgumentException((String)("length: " + chars.length + " (length: >= 1)"));
        }
        this.chars = chars;
        this.pos = chars.length;
    }

    @Override
    public int length() {
        return this.pos;
    }

    @Override
    public char charAt(int index) {
        if (index <= this.pos) return this.chars[index];
        throw new IndexOutOfBoundsException();
    }

    public char charAtUnsafe(int index) {
        return this.chars[index];
    }

    @Override
    public AppendableCharSequence subSequence(int start, int end) {
        if (start != end) return new AppendableCharSequence((char[])Arrays.copyOfRange((char[])this.chars, (int)start, (int)end));
        return new AppendableCharSequence((int)Math.min((int)16, (int)this.chars.length));
    }

    @Override
    public AppendableCharSequence append(char c) {
        if (this.pos == this.chars.length) {
            char[] old = this.chars;
            this.chars = new char[old.length << 1];
            System.arraycopy((Object)old, (int)0, (Object)this.chars, (int)0, (int)old.length);
        }
        this.chars[this.pos++] = c;
        return this;
    }

    @Override
    public AppendableCharSequence append(CharSequence csq) {
        return this.append((CharSequence)csq, (int)0, (int)csq.length());
    }

    @Override
    public AppendableCharSequence append(CharSequence csq, int start, int end) {
        if (csq.length() < end) {
            throw new IndexOutOfBoundsException();
        }
        int length = end - start;
        if (length > this.chars.length - this.pos) {
            this.chars = AppendableCharSequence.expand((char[])this.chars, (int)(this.pos + length), (int)this.pos);
        }
        if (csq instanceof AppendableCharSequence) {
            AppendableCharSequence seq = (AppendableCharSequence)csq;
            char[] src = seq.chars;
            System.arraycopy((Object)src, (int)start, (Object)this.chars, (int)this.pos, (int)length);
            this.pos += length;
            return this;
        }
        int i = start;
        while (i < end) {
            this.chars[this.pos++] = csq.charAt((int)i);
            ++i;
        }
        return this;
    }

    public void reset() {
        this.pos = 0;
    }

    @Override
    public String toString() {
        return new String((char[])this.chars, (int)0, (int)this.pos);
    }

    public String substring(int start, int end) {
        int length = end - start;
        if (start > this.pos) throw new IndexOutOfBoundsException();
        if (length <= this.pos) return new String((char[])this.chars, (int)start, (int)length);
        throw new IndexOutOfBoundsException();
    }

    public String subStringUnsafe(int start, int end) {
        return new String((char[])this.chars, (int)start, (int)(end - start));
    }

    private static char[] expand(char[] array, int neededSpace, int size) {
        int newCapacity = array.length;
        do {
            if ((newCapacity <<= 1) >= 0) continue;
            throw new IllegalStateException();
        } while (neededSpace > newCapacity);
        char[] newArray = new char[newCapacity];
        System.arraycopy((Object)array, (int)0, (Object)newArray, (int)0, (int)size);
        return newArray;
    }
}

