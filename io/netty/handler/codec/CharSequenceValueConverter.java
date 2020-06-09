/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.AsciiString;
import io.netty.util.internal.PlatformDependent;
import java.text.ParseException;
import java.util.Date;

public class CharSequenceValueConverter
implements ValueConverter<CharSequence> {
    public static final CharSequenceValueConverter INSTANCE = new CharSequenceValueConverter();
    private static final AsciiString TRUE_ASCII = new AsciiString((CharSequence)"true");

    @Override
    public CharSequence convertObject(Object value) {
        if (!(value instanceof CharSequence)) return value.toString();
        return (CharSequence)value;
    }

    @Override
    public CharSequence convertInt(int value) {
        return String.valueOf((int)value);
    }

    @Override
    public CharSequence convertLong(long value) {
        return String.valueOf((long)value);
    }

    @Override
    public CharSequence convertDouble(double value) {
        return String.valueOf((double)value);
    }

    @Override
    public CharSequence convertChar(char value) {
        return String.valueOf((char)value);
    }

    @Override
    public CharSequence convertBoolean(boolean value) {
        return String.valueOf((boolean)value);
    }

    @Override
    public CharSequence convertFloat(float value) {
        return String.valueOf((float)value);
    }

    @Override
    public boolean convertToBoolean(CharSequence value) {
        return AsciiString.contentEqualsIgnoreCase((CharSequence)value, (CharSequence)TRUE_ASCII);
    }

    @Override
    public CharSequence convertByte(byte value) {
        return String.valueOf((int)value);
    }

    @Override
    public byte convertToByte(CharSequence value) {
        if (!(value instanceof AsciiString)) return Byte.parseByte((String)value.toString());
        if (value.length() != 1) return Byte.parseByte((String)value.toString());
        return ((AsciiString)value).byteAt((int)0);
    }

    @Override
    public char convertToChar(CharSequence value) {
        return value.charAt((int)0);
    }

    @Override
    public CharSequence convertShort(short value) {
        return String.valueOf((int)value);
    }

    @Override
    public short convertToShort(CharSequence value) {
        if (!(value instanceof AsciiString)) return Short.parseShort((String)value.toString());
        return ((AsciiString)value).parseShort();
    }

    @Override
    public int convertToInt(CharSequence value) {
        if (!(value instanceof AsciiString)) return Integer.parseInt((String)value.toString());
        return ((AsciiString)value).parseInt();
    }

    @Override
    public long convertToLong(CharSequence value) {
        if (!(value instanceof AsciiString)) return Long.parseLong((String)value.toString());
        return ((AsciiString)value).parseLong();
    }

    @Override
    public CharSequence convertTimeMillis(long value) {
        return DateFormatter.format((Date)new Date((long)value));
    }

    @Override
    public long convertToTimeMillis(CharSequence value) {
        Date date = DateFormatter.parseHttpDate((CharSequence)value);
        if (date != null) return date.getTime();
        PlatformDependent.throwException((Throwable)new ParseException((String)("header can't be parsed into a Date: " + value), (int)0));
        return 0L;
    }

    @Override
    public float convertToFloat(CharSequence value) {
        if (!(value instanceof AsciiString)) return Float.parseFloat((String)value.toString());
        return ((AsciiString)value).parseFloat();
    }

    @Override
    public double convertToDouble(CharSequence value) {
        if (!(value instanceof AsciiString)) return Double.parseDouble((String)value.toString());
        return ((AsciiString)value).parseDouble();
    }
}

