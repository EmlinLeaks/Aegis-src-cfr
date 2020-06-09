/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mojang.brigadier;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class StringReader
implements ImmutableStringReader {
    private static final char SYNTAX_ESCAPE = '\\';
    private static final char SYNTAX_QUOTE = '\"';
    private final String string;
    private int cursor;

    public StringReader(StringReader other) {
        this.string = other.string;
        this.cursor = other.cursor;
    }

    public StringReader(String string) {
        this.string = string;
    }

    @Override
    public String getString() {
        return this.string;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    @Override
    public int getRemainingLength() {
        return this.string.length() - this.cursor;
    }

    @Override
    public int getTotalLength() {
        return this.string.length();
    }

    @Override
    public int getCursor() {
        return this.cursor;
    }

    @Override
    public String getRead() {
        return this.string.substring((int)0, (int)this.cursor);
    }

    @Override
    public String getRemaining() {
        return this.string.substring((int)this.cursor);
    }

    @Override
    public boolean canRead(int length) {
        if (this.cursor + length > this.string.length()) return false;
        return true;
    }

    @Override
    public boolean canRead() {
        return this.canRead((int)1);
    }

    @Override
    public char peek() {
        return this.string.charAt((int)this.cursor);
    }

    @Override
    public char peek(int offset) {
        return this.string.charAt((int)(this.cursor + offset));
    }

    public char read() {
        return this.string.charAt((int)this.cursor++);
    }

    public void skip() {
        ++this.cursor;
    }

    public static boolean isAllowedNumber(char c) {
        if (c >= '0') {
            if (c <= '9') return true;
        }
        if (c == '.') return true;
        if (c == '-') return true;
        return false;
    }

    public void skipWhitespace() {
        while (this.canRead()) {
            if (!Character.isWhitespace((char)this.peek())) return;
            this.skip();
        }
    }

    public int readInt() throws CommandSyntaxException {
        int start = this.cursor;
        while (this.canRead() && StringReader.isAllowedNumber((char)this.peek())) {
            this.skip();
        }
        String number = this.string.substring((int)start, (int)this.cursor);
        if (number.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedInt().createWithContext((ImmutableStringReader)this);
        }
        try {
            return Integer.parseInt((String)number);
        }
        catch (NumberFormatException ex) {
            this.cursor = start;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext((ImmutableStringReader)this, (Object)number);
        }
    }

    public long readLong() throws CommandSyntaxException {
        int start = this.cursor;
        while (this.canRead() && StringReader.isAllowedNumber((char)this.peek())) {
            this.skip();
        }
        String number = this.string.substring((int)start, (int)this.cursor);
        if (number.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedLong().createWithContext((ImmutableStringReader)this);
        }
        try {
            return Long.parseLong((String)number);
        }
        catch (NumberFormatException ex) {
            this.cursor = start;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidLong().createWithContext((ImmutableStringReader)this, (Object)number);
        }
    }

    public double readDouble() throws CommandSyntaxException {
        int start = this.cursor;
        while (this.canRead() && StringReader.isAllowedNumber((char)this.peek())) {
            this.skip();
        }
        String number = this.string.substring((int)start, (int)this.cursor);
        if (number.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedDouble().createWithContext((ImmutableStringReader)this);
        }
        try {
            return Double.parseDouble((String)number);
        }
        catch (NumberFormatException ex) {
            this.cursor = start;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext((ImmutableStringReader)this, (Object)number);
        }
    }

    public float readFloat() throws CommandSyntaxException {
        int start = this.cursor;
        while (this.canRead() && StringReader.isAllowedNumber((char)this.peek())) {
            this.skip();
        }
        String number = this.string.substring((int)start, (int)this.cursor);
        if (number.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedFloat().createWithContext((ImmutableStringReader)this);
        }
        try {
            return Float.parseFloat((String)number);
        }
        catch (NumberFormatException ex) {
            this.cursor = start;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidFloat().createWithContext((ImmutableStringReader)this, (Object)number);
        }
    }

    public static boolean isAllowedInUnquotedString(char c) {
        if (c >= '0') {
            if (c <= '9') return true;
        }
        if (c >= 'A') {
            if (c <= 'Z') return true;
        }
        if (c >= 'a') {
            if (c <= 'z') return true;
        }
        if (c == '_') return true;
        if (c == '-') return true;
        if (c == '.') return true;
        if (c == '+') return true;
        return false;
    }

    public String readUnquotedString() {
        int start = this.cursor;
        while (this.canRead()) {
            if (!StringReader.isAllowedInUnquotedString((char)this.peek())) return this.string.substring((int)start, (int)this.cursor);
            this.skip();
        }
        return this.string.substring((int)start, (int)this.cursor);
    }

    public String readQuotedString() throws CommandSyntaxException {
        if (!this.canRead()) {
            return "";
        }
        if (this.peek() != '\"') {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedStartOfQuote().createWithContext((ImmutableStringReader)this);
        }
        this.skip();
        StringBuilder result = new StringBuilder();
        boolean escaped = false;
        while (this.canRead()) {
            char c = this.read();
            if (escaped) {
                if (c != '\"' && c != '\\') {
                    this.setCursor((int)(this.getCursor() - 1));
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidEscape().createWithContext((ImmutableStringReader)this, (Object)String.valueOf((char)c));
                }
                result.append((char)c);
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '\"') {
                return result.toString();
            }
            result.append((char)c);
        }
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedEndOfQuote().createWithContext((ImmutableStringReader)this);
    }

    public String readString() throws CommandSyntaxException {
        if (!this.canRead()) return this.readUnquotedString();
        if (this.peek() != '\"') return this.readUnquotedString();
        return this.readQuotedString();
    }

    public boolean readBoolean() throws CommandSyntaxException {
        int start = this.cursor;
        String value = this.readString();
        if (value.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedBool().createWithContext((ImmutableStringReader)this);
        }
        if (value.equals((Object)"true")) {
            return true;
        }
        if (value.equals((Object)"false")) {
            return false;
        }
        this.cursor = start;
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidBool().createWithContext((ImmutableStringReader)this, (Object)value);
    }

    public void expect(char c) throws CommandSyntaxException {
        if (!this.canRead()) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol().createWithContext((ImmutableStringReader)this, (Object)String.valueOf((char)c));
        if (this.peek() != c) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol().createWithContext((ImmutableStringReader)this, (Object)String.valueOf((char)c));
        }
        this.skip();
    }
}

