/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.DateFormatter;
import io.netty.util.AsciiString;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.ObjectUtil;
import java.util.BitSet;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public final class DateFormatter {
    private static final BitSet DELIMITERS;
    private static final String[] DAY_OF_WEEK_TO_SHORT_NAME;
    private static final String[] CALENDAR_MONTH_TO_SHORT_NAME;
    private static final FastThreadLocal<DateFormatter> INSTANCES;
    private final GregorianCalendar cal = new GregorianCalendar((TimeZone)TimeZone.getTimeZone((String)"UTC"));
    private final StringBuilder sb = new StringBuilder((int)29);
    private boolean timeFound;
    private int hours;
    private int minutes;
    private int seconds;
    private boolean dayOfMonthFound;
    private int dayOfMonth;
    private boolean monthFound;
    private int month;
    private boolean yearFound;
    private int year;

    public static Date parseHttpDate(CharSequence txt) {
        return DateFormatter.parseHttpDate((CharSequence)txt, (int)0, (int)txt.length());
    }

    public static Date parseHttpDate(CharSequence txt, int start, int end) {
        int length = end - start;
        if (length == 0) {
            return null;
        }
        if (length < 0) {
            throw new IllegalArgumentException((String)"Can't have end < start");
        }
        if (length <= 64) return DateFormatter.formatter().parse0((CharSequence)ObjectUtil.checkNotNull(txt, (String)"txt"), (int)start, (int)end);
        throw new IllegalArgumentException((String)"Can't parse more than 64 chars,looks like a user error or a malformed header");
    }

    public static String format(Date date) {
        return DateFormatter.formatter().format0((Date)ObjectUtil.checkNotNull(date, (String)"date"));
    }

    public static StringBuilder append(Date date, StringBuilder sb) {
        return DateFormatter.formatter().append0((Date)ObjectUtil.checkNotNull(date, (String)"date"), (StringBuilder)ObjectUtil.checkNotNull(sb, (String)"sb"));
    }

    private static DateFormatter formatter() {
        DateFormatter formatter = INSTANCES.get();
        formatter.reset();
        return formatter;
    }

    private static boolean isDelim(char c) {
        return DELIMITERS.get((int)c);
    }

    private static boolean isDigit(char c) {
        if (c < '0') return false;
        if (c > '9') return false;
        return true;
    }

    private static int getNumericalValue(char c) {
        return c - 48;
    }

    private DateFormatter() {
        this.reset();
    }

    public void reset() {
        this.timeFound = false;
        this.hours = -1;
        this.minutes = -1;
        this.seconds = -1;
        this.dayOfMonthFound = false;
        this.dayOfMonth = -1;
        this.monthFound = false;
        this.month = -1;
        this.yearFound = false;
        this.year = -1;
        this.cal.clear();
        this.sb.setLength((int)0);
    }

    /*
     * Unable to fully structure code
     */
    private boolean tryParseTime(CharSequence txt, int tokenStart, int tokenEnd) {
        len = tokenEnd - tokenStart;
        if (len < 5) return false;
        if (len > 8) {
            return false;
        }
        localHours = -1;
        localMinutes = -1;
        localSeconds = -1;
        currentPartNumber = 0;
        currentPartValue = 0;
        numDigits = 0;
        for (i = tokenStart; i < tokenEnd; ++i) {
            c = txt.charAt((int)i);
            if (DateFormatter.isDigit((char)c)) {
                currentPartValue = currentPartValue * 10 + DateFormatter.getNumericalValue((char)c);
                if (++numDigits <= 2) continue;
                return false;
            }
            if (c != ':') return false;
            if (numDigits == 0) {
                return false;
            }
            switch (currentPartNumber) {
                case 0: {
                    localHours = currentPartValue;
                    ** break;
                }
                case 1: {
                    localMinutes = currentPartValue;
                    ** break;
                }
            }
            return false;
lbl28: // 2 sources:
            currentPartValue = 0;
            ++currentPartNumber;
            numDigits = 0;
        }
        if (numDigits > 0) {
            localSeconds = currentPartValue;
        }
        if (localHours < 0) return false;
        if (localMinutes < 0) return false;
        if (localSeconds < 0) return false;
        this.hours = localHours;
        this.minutes = localMinutes;
        this.seconds = localSeconds;
        return true;
    }

    private boolean tryParseDayOfMonth(CharSequence txt, int tokenStart, int tokenEnd) {
        int len = tokenEnd - tokenStart;
        if (len == 1) {
            char c0 = txt.charAt((int)tokenStart);
            if (!DateFormatter.isDigit((char)c0)) return false;
            this.dayOfMonth = DateFormatter.getNumericalValue((char)c0);
            return true;
        }
        if (len != 2) return false;
        char c0 = txt.charAt((int)tokenStart);
        char c1 = txt.charAt((int)(tokenStart + 1));
        if (!DateFormatter.isDigit((char)c0)) return false;
        if (!DateFormatter.isDigit((char)c1)) return false;
        this.dayOfMonth = DateFormatter.getNumericalValue((char)c0) * 10 + DateFormatter.getNumericalValue((char)c1);
        return true;
    }

    private boolean tryParseMonth(CharSequence txt, int tokenStart, int tokenEnd) {
        int len = tokenEnd - tokenStart;
        if (len != 3) {
            return false;
        }
        char monthChar1 = AsciiString.toLowerCase((char)txt.charAt((int)tokenStart));
        char monthChar2 = AsciiString.toLowerCase((char)txt.charAt((int)(tokenStart + 1)));
        char monthChar3 = AsciiString.toLowerCase((char)txt.charAt((int)(tokenStart + 2)));
        if (monthChar1 == 'j' && monthChar2 == 'a' && monthChar3 == 'n') {
            this.month = 0;
            return true;
        }
        if (monthChar1 == 'f' && monthChar2 == 'e' && monthChar3 == 'b') {
            this.month = 1;
            return true;
        }
        if (monthChar1 == 'm' && monthChar2 == 'a' && monthChar3 == 'r') {
            this.month = 2;
            return true;
        }
        if (monthChar1 == 'a' && monthChar2 == 'p' && monthChar3 == 'r') {
            this.month = 3;
            return true;
        }
        if (monthChar1 == 'm' && monthChar2 == 'a' && monthChar3 == 'y') {
            this.month = 4;
            return true;
        }
        if (monthChar1 == 'j' && monthChar2 == 'u' && monthChar3 == 'n') {
            this.month = 5;
            return true;
        }
        if (monthChar1 == 'j' && monthChar2 == 'u' && monthChar3 == 'l') {
            this.month = 6;
            return true;
        }
        if (monthChar1 == 'a' && monthChar2 == 'u' && monthChar3 == 'g') {
            this.month = 7;
            return true;
        }
        if (monthChar1 == 's' && monthChar2 == 'e' && monthChar3 == 'p') {
            this.month = 8;
            return true;
        }
        if (monthChar1 == 'o' && monthChar2 == 'c' && monthChar3 == 't') {
            this.month = 9;
            return true;
        }
        if (monthChar1 == 'n' && monthChar2 == 'o' && monthChar3 == 'v') {
            this.month = 10;
            return true;
        }
        if (monthChar1 != 'd') return false;
        if (monthChar2 != 'e') return false;
        if (monthChar3 != 'c') return false;
        this.month = 11;
        return true;
    }

    private boolean tryParseYear(CharSequence txt, int tokenStart, int tokenEnd) {
        int len = tokenEnd - tokenStart;
        if (len == 2) {
            char c0 = txt.charAt((int)tokenStart);
            char c1 = txt.charAt((int)(tokenStart + 1));
            if (!DateFormatter.isDigit((char)c0)) return false;
            if (!DateFormatter.isDigit((char)c1)) return false;
            this.year = DateFormatter.getNumericalValue((char)c0) * 10 + DateFormatter.getNumericalValue((char)c1);
            return true;
        }
        if (len != 4) return false;
        char c0 = txt.charAt((int)tokenStart);
        char c1 = txt.charAt((int)(tokenStart + 1));
        char c2 = txt.charAt((int)(tokenStart + 2));
        char c3 = txt.charAt((int)(tokenStart + 3));
        if (!DateFormatter.isDigit((char)c0)) return false;
        if (!DateFormatter.isDigit((char)c1)) return false;
        if (!DateFormatter.isDigit((char)c2)) return false;
        if (!DateFormatter.isDigit((char)c3)) return false;
        this.year = DateFormatter.getNumericalValue((char)c0) * 1000 + DateFormatter.getNumericalValue((char)c1) * 100 + DateFormatter.getNumericalValue((char)c2) * 10 + DateFormatter.getNumericalValue((char)c3);
        return true;
    }

    private boolean parseToken(CharSequence txt, int tokenStart, int tokenEnd) {
        if (!this.timeFound) {
            this.timeFound = this.tryParseTime((CharSequence)txt, (int)tokenStart, (int)tokenEnd);
            if (this.timeFound) {
                if (!this.dayOfMonthFound) return false;
                if (!this.monthFound) return false;
                if (!this.yearFound) return false;
                return true;
            }
        }
        if (!this.dayOfMonthFound) {
            this.dayOfMonthFound = this.tryParseDayOfMonth((CharSequence)txt, (int)tokenStart, (int)tokenEnd);
            if (this.dayOfMonthFound) {
                if (!this.timeFound) return false;
                if (!this.monthFound) return false;
                if (!this.yearFound) return false;
                return true;
            }
        }
        if (!this.monthFound) {
            this.monthFound = this.tryParseMonth((CharSequence)txt, (int)tokenStart, (int)tokenEnd);
            if (this.monthFound) {
                if (!this.timeFound) return false;
                if (!this.dayOfMonthFound) return false;
                if (!this.yearFound) return false;
                return true;
            }
        }
        if (!this.yearFound) {
            this.yearFound = this.tryParseYear((CharSequence)txt, (int)tokenStart, (int)tokenEnd);
        }
        if (!this.timeFound) return false;
        if (!this.dayOfMonthFound) return false;
        if (!this.monthFound) return false;
        if (!this.yearFound) return false;
        return true;
    }

    private Date parse0(CharSequence txt, int start, int end) {
        boolean allPartsFound = this.parse1((CharSequence)txt, (int)start, (int)end);
        if (!allPartsFound) return null;
        if (!this.normalizeAndValidate()) return null;
        Date date = this.computeDate();
        return date;
    }

    private boolean parse1(CharSequence txt, int start, int end) {
        int tokenStart = -1;
        for (int i = start; i < end; ++i) {
            char c = txt.charAt((int)i);
            if (DateFormatter.isDelim((char)c)) {
                if (tokenStart == -1) continue;
                if (this.parseToken((CharSequence)txt, (int)tokenStart, (int)i)) {
                    return true;
                }
                tokenStart = -1;
                continue;
            }
            if (tokenStart != -1) continue;
            tokenStart = i;
        }
        if (tokenStart == -1) return false;
        if (!this.parseToken((CharSequence)txt, (int)tokenStart, (int)txt.length())) return false;
        return true;
    }

    private boolean normalizeAndValidate() {
        if (this.dayOfMonth < 1) return false;
        if (this.dayOfMonth > 31) return false;
        if (this.hours > 23) return false;
        if (this.minutes > 59) return false;
        if (this.seconds > 59) {
            return false;
        }
        if (this.year >= 70 && this.year <= 99) {
            this.year += 1900;
            return true;
        }
        if (this.year >= 0 && this.year < 70) {
            this.year += 2000;
            return true;
        }
        if (this.year >= 1601) return true;
        return false;
    }

    private Date computeDate() {
        this.cal.set((int)5, (int)this.dayOfMonth);
        this.cal.set((int)2, (int)this.month);
        this.cal.set((int)1, (int)this.year);
        this.cal.set((int)11, (int)this.hours);
        this.cal.set((int)12, (int)this.minutes);
        this.cal.set((int)13, (int)this.seconds);
        return this.cal.getTime();
    }

    private String format0(Date date) {
        this.append0((Date)date, (StringBuilder)this.sb);
        return this.sb.toString();
    }

    private StringBuilder append0(Date date, StringBuilder sb) {
        this.cal.setTime((Date)date);
        sb.append((String)DAY_OF_WEEK_TO_SHORT_NAME[this.cal.get((int)7) - 1]).append((String)", ");
        sb.append((int)this.cal.get((int)5)).append((char)' ');
        sb.append((String)CALENDAR_MONTH_TO_SHORT_NAME[this.cal.get((int)2)]).append((char)' ');
        sb.append((int)this.cal.get((int)1)).append((char)' ');
        DateFormatter.appendZeroLeftPadded((int)this.cal.get((int)11), (StringBuilder)sb).append((char)':');
        DateFormatter.appendZeroLeftPadded((int)this.cal.get((int)12), (StringBuilder)sb).append((char)':');
        return DateFormatter.appendZeroLeftPadded((int)this.cal.get((int)13), (StringBuilder)sb).append((String)" GMT");
    }

    private static StringBuilder appendZeroLeftPadded(int value, StringBuilder sb) {
        if (value >= 10) return sb.append((int)value);
        sb.append((char)'0');
        return sb.append((int)value);
    }

    static {
        int c;
        DELIMITERS = new BitSet();
        DELIMITERS.set((int)9);
        for (c = 32; c <= 47; c = (int)((char)(c + 1))) {
            DELIMITERS.set((int)c);
        }
        for (c = 59; c <= 64; c = (int)((char)(c + 1))) {
            DELIMITERS.set((int)c);
        }
        for (c = 91; c <= 96; c = (int)((char)(c + 1))) {
            DELIMITERS.set((int)c);
        }
        c = 123;
        do {
            if (c > 126) {
                DAY_OF_WEEK_TO_SHORT_NAME = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
                CALENDAR_MONTH_TO_SHORT_NAME = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                INSTANCES = new FastThreadLocal<DateFormatter>(){

                    protected DateFormatter initialValue() {
                        return new DateFormatter();
                    }
                };
                return;
            }
            DELIMITERS.set((int)c);
            c = (int)((char)(c + 1));
        } while (true);
    }
}

