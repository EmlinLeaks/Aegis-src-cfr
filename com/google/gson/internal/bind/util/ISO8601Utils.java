/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson.internal.bind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class ISO8601Utils {
    private static final String UTC_ID = "UTC";
    private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone((String)"UTC");

    public static String format(Date date) {
        return ISO8601Utils.format((Date)date, (boolean)false, (TimeZone)TIMEZONE_UTC);
    }

    public static String format(Date date, boolean millis) {
        return ISO8601Utils.format((Date)date, (boolean)millis, (TimeZone)TIMEZONE_UTC);
    }

    public static String format(Date date, boolean millis, TimeZone tz) {
        int offset;
        GregorianCalendar calendar = new GregorianCalendar((TimeZone)tz, (Locale)Locale.US);
        calendar.setTime((Date)date);
        int capacity = "yyyy-MM-ddThh:mm:ss".length();
        capacity += millis ? ".sss".length() : 0;
        StringBuilder formatted = new StringBuilder((int)(capacity += tz.getRawOffset() == 0 ? "Z".length() : "+hh:mm".length()));
        ISO8601Utils.padInt((StringBuilder)formatted, (int)calendar.get((int)1), (int)"yyyy".length());
        formatted.append((char)'-');
        ISO8601Utils.padInt((StringBuilder)formatted, (int)(calendar.get((int)2) + 1), (int)"MM".length());
        formatted.append((char)'-');
        ISO8601Utils.padInt((StringBuilder)formatted, (int)calendar.get((int)5), (int)"dd".length());
        formatted.append((char)'T');
        ISO8601Utils.padInt((StringBuilder)formatted, (int)calendar.get((int)11), (int)"hh".length());
        formatted.append((char)':');
        ISO8601Utils.padInt((StringBuilder)formatted, (int)calendar.get((int)12), (int)"mm".length());
        formatted.append((char)':');
        ISO8601Utils.padInt((StringBuilder)formatted, (int)calendar.get((int)13), (int)"ss".length());
        if (millis) {
            formatted.append((char)'.');
            ISO8601Utils.padInt((StringBuilder)formatted, (int)calendar.get((int)14), (int)"sss".length());
        }
        if ((offset = tz.getOffset((long)calendar.getTimeInMillis())) == 0) {
            formatted.append((char)'Z');
            return formatted.toString();
        }
        int hours = Math.abs((int)(offset / 60000 / 60));
        int minutes = Math.abs((int)(offset / 60000 % 60));
        formatted.append((char)(offset < 0 ? (char)'-' : '+'));
        ISO8601Utils.padInt((StringBuilder)formatted, (int)hours, (int)"hh".length());
        formatted.append((char)':');
        ISO8601Utils.padInt((StringBuilder)formatted, (int)minutes, (int)"mm".length());
        return formatted.toString();
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    public static Date parse(String date, ParsePosition pos) throws ParseException {
        fail = null;
        try {
            offset = pos.getIndex();
            year = ISO8601Utils.parseInt((String)date, (int)offset, (int)(offset += 4));
            if (ISO8601Utils.checkOffset((String)date, (int)offset, (char)'-')) {
                // empty if block
            }
            month = ISO8601Utils.parseInt((String)date, (int)(++offset), (int)(offset += 2));
            if (ISO8601Utils.checkOffset((String)date, (int)offset, (char)'-')) {
                // empty if block
            }
            day = ISO8601Utils.parseInt((String)date, (int)(++offset), (int)(offset += 2));
            hour = 0;
            minutes = 0;
            seconds = 0;
            milliseconds = 0;
            hasT = ISO8601Utils.checkOffset((String)date, (int)offset, (char)'T');
            if (!hasT && date.length() <= offset) {
                calendar = new GregorianCalendar((int)year, (int)(month - 1), (int)day);
                pos.setIndex((int)offset);
                return calendar.getTime();
            }
            if (hasT) {
                hour = ISO8601Utils.parseInt((String)date, (int)(++offset), (int)(offset += 2));
                if (ISO8601Utils.checkOffset((String)date, (int)offset, (char)':')) {
                    // empty if block
                }
                minutes = ISO8601Utils.parseInt((String)date, (int)(++offset), (int)(offset += 2));
                if (ISO8601Utils.checkOffset((String)date, (int)offset, (char)':')) {
                    ++offset;
                }
                if (date.length() > offset && (c = date.charAt((int)offset)) != 'Z' && c != '+' && c != '-') {
                    v0 = offset;
                    seconds = ISO8601Utils.parseInt((String)date, (int)v0, (int)(offset += 2));
                    if (seconds > 59 && seconds < 63) {
                        seconds = 59;
                    }
                    if (ISO8601Utils.checkOffset((String)date, (int)offset, (char)'.')) {
                        endOffset = ISO8601Utils.indexOfNonDigit((String)date, (int)(++offset + 1));
                        parseEndOffset = Math.min((int)endOffset, (int)(offset + 3));
                        fraction = ISO8601Utils.parseInt((String)date, (int)offset, (int)parseEndOffset);
                        switch (parseEndOffset - offset) {
                            case 2: {
                                milliseconds = fraction * 10;
                                ** break;
                            }
                            case 1: {
                                milliseconds = fraction * 100;
                                ** break;
                            }
                        }
                        milliseconds = fraction;
lbl44: // 3 sources:
                        offset = endOffset;
                    }
                }
            }
            if (date.length() <= offset) {
                throw new IllegalArgumentException((String)"No time zone indicator");
            }
            timezone = null;
            timezoneIndicator = date.charAt((int)offset);
            if (timezoneIndicator == 'Z') {
                timezone = ISO8601Utils.TIMEZONE_UTC;
                ++offset;
            } else {
                if (timezoneIndicator != '+') {
                    if (timezoneIndicator != '-') throw new IndexOutOfBoundsException((String)("Invalid time zone indicator '" + timezoneIndicator + "'"));
                }
                timezoneOffset = (timezoneOffset = date.substring((int)offset)).length() >= 5 ? timezoneOffset : timezoneOffset + "00";
                offset += timezoneOffset.length();
                if ("+0000".equals((Object)timezoneOffset) || "+00:00".equals((Object)timezoneOffset)) {
                    timezone = ISO8601Utils.TIMEZONE_UTC;
                } else {
                    timezoneId = "GMT" + timezoneOffset;
                    timezone = TimeZone.getTimeZone((String)timezoneId);
                    act = timezone.getID();
                    if (!act.equals((Object)timezoneId) && !(cleaned = act.replace((CharSequence)":", (CharSequence)"")).equals((Object)timezoneId)) {
                        throw new IndexOutOfBoundsException((String)("Mismatching time zone indicator: " + timezoneId + " given, resolves to " + timezone.getID()));
                    }
                }
            }
            calendar = new GregorianCalendar((TimeZone)timezone);
            calendar.setLenient((boolean)false);
            calendar.set((int)1, (int)year);
            calendar.set((int)2, (int)(month - 1));
            calendar.set((int)5, (int)day);
            calendar.set((int)11, (int)hour);
            calendar.set((int)12, (int)minutes);
            calendar.set((int)13, (int)seconds);
            calendar.set((int)14, (int)milliseconds);
            pos.setIndex((int)offset);
            return calendar.getTime();
        }
        catch (IndexOutOfBoundsException e) {
            fail = e;
        }
        catch (NumberFormatException e) {
            fail = e;
        }
        catch (IllegalArgumentException e) {
            fail = e;
        }
        input = date == null ? null : '\"' + date + "'";
        msg = fail.getMessage();
        if (msg == null || msg.isEmpty()) {
            msg = "(" + fail.getClass().getName() + ")";
        }
        ex = new ParseException((String)("Failed to parse date [" + input + "]: " + msg), (int)pos.getIndex());
        ex.initCause((Throwable)fail);
        throw ex;
    }

    private static boolean checkOffset(String value, int offset, char expected) {
        if (offset >= value.length()) return false;
        if (value.charAt((int)offset) != expected) return false;
        return true;
    }

    private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
        int digit;
        if (beginIndex < 0) throw new NumberFormatException((String)value);
        if (endIndex > value.length()) throw new NumberFormatException((String)value);
        if (beginIndex > endIndex) {
            throw new NumberFormatException((String)value);
        }
        int i = beginIndex;
        int result = 0;
        if (i < endIndex) {
            if ((digit = Character.digit((char)value.charAt((int)i++), (int)10)) < 0) {
                throw new NumberFormatException((String)("Invalid number: " + value.substring((int)beginIndex, (int)endIndex)));
            }
            result = -digit;
        }
        while (i < endIndex) {
            if ((digit = Character.digit((char)value.charAt((int)i++), (int)10)) < 0) {
                throw new NumberFormatException((String)("Invalid number: " + value.substring((int)beginIndex, (int)endIndex)));
            }
            result *= 10;
            result -= digit;
        }
        return -result;
    }

    private static void padInt(StringBuilder buffer, int value, int length) {
        String strValue = Integer.toString((int)value);
        int i = length - strValue.length();
        do {
            if (i <= 0) {
                buffer.append((String)strValue);
                return;
            }
            buffer.append((char)'0');
            --i;
        } while (true);
    }

    private static int indexOfNonDigit(String string, int offset) {
        int i = offset;
        while (i < string.length()) {
            char c = string.charAt((int)i);
            if (c < '0') return i;
            if (c > '9') {
                return i;
            }
            ++i;
        }
        return string.length();
    }
}

