/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.SQLError;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

public class TimeUtil {
    static final TimeZone GMT_TIMEZONE;
    private static final TimeZone DEFAULT_TIMEZONE;
    private static final String TIME_ZONE_MAPPINGS_RESOURCE = "/com/mysql/jdbc/TimeZoneMapping.properties";
    private static Properties timeZoneMappings;
    protected static final Method systemNanoTimeMethod;

    public static boolean nanoTimeAvailable() {
        if (systemNanoTimeMethod == null) return false;
        return true;
    }

    public static final TimeZone getDefaultTimeZone(boolean useCache) {
        Object object;
        if (useCache) {
            object = DEFAULT_TIMEZONE.clone();
            return (TimeZone)object;
        }
        object = TimeZone.getDefault().clone();
        return (TimeZone)object;
    }

    public static long getCurrentTimeNanosOrMillis() {
        if (systemNanoTimeMethod == null) return System.currentTimeMillis();
        try {
            return ((Long)systemNanoTimeMethod.invoke(null, (Object[])((Object[])null))).longValue();
        }
        catch (IllegalArgumentException e) {
            return System.currentTimeMillis();
        }
        catch (IllegalAccessException e) {
            return System.currentTimeMillis();
        }
        catch (InvocationTargetException e) {
            // empty catch block
        }
        return System.currentTimeMillis();
    }

    public static Time changeTimezone(MySQLConnection conn, Calendar sessionCalendar, Calendar targetCalendar, Time t, TimeZone fromTz, TimeZone toTz, boolean rollForward) {
        if (conn == null) return t;
        if (conn.getUseTimezone() && !conn.getNoTimezoneConversionForTimeType()) {
            Calendar fromCal = Calendar.getInstance((TimeZone)fromTz);
            fromCal.setTime((java.util.Date)t);
            int fromOffset = fromCal.get((int)15) + fromCal.get((int)16);
            Calendar toCal = Calendar.getInstance((TimeZone)toTz);
            toCal.setTime((java.util.Date)t);
            int toOffset = toCal.get((int)15) + toCal.get((int)16);
            int offsetDiff = fromOffset - toOffset;
            long toTime = toCal.getTime().getTime();
            toTime = rollForward ? (toTime += (long)offsetDiff) : (toTime -= (long)offsetDiff);
            return new Time((long)toTime);
        }
        if (!conn.getUseJDBCCompliantTimezoneShift()) return t;
        if (targetCalendar == null) return t;
        return new Time((long)TimeUtil.jdbcCompliantZoneShift((Calendar)sessionCalendar, (Calendar)targetCalendar, (java.util.Date)t));
    }

    public static Timestamp changeTimezone(MySQLConnection conn, Calendar sessionCalendar, Calendar targetCalendar, Timestamp tstamp, TimeZone fromTz, TimeZone toTz, boolean rollForward) {
        if (conn == null) return tstamp;
        if (!conn.getUseTimezone()) {
            if (!conn.getUseJDBCCompliantTimezoneShift()) return tstamp;
            if (targetCalendar == null) return tstamp;
            Timestamp adjustedTimestamp = new Timestamp((long)TimeUtil.jdbcCompliantZoneShift((Calendar)sessionCalendar, (Calendar)targetCalendar, (java.util.Date)tstamp));
            adjustedTimestamp.setNanos((int)tstamp.getNanos());
            return adjustedTimestamp;
        }
        Calendar fromCal = Calendar.getInstance((TimeZone)fromTz);
        fromCal.setTime((java.util.Date)tstamp);
        int fromOffset = fromCal.get((int)15) + fromCal.get((int)16);
        Calendar toCal = Calendar.getInstance((TimeZone)toTz);
        toCal.setTime((java.util.Date)tstamp);
        int toOffset = toCal.get((int)15) + toCal.get((int)16);
        int offsetDiff = fromOffset - toOffset;
        long toTime = toCal.getTime().getTime();
        toTime = rollForward ? (toTime += (long)offsetDiff) : (toTime -= (long)offsetDiff);
        return new Timestamp((long)toTime);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled unnecessary exception pruning
     */
    private static long jdbcCompliantZoneShift(Calendar sessionCalendar, Calendar targetCalendar, java.util.Date dt) {
        if (sessionCalendar == null) {
            sessionCalendar = new GregorianCalendar();
        }
        Calendar calendar = sessionCalendar;
        // MONITORENTER : calendar
        java.util.Date origCalDate = targetCalendar.getTime();
        java.util.Date origSessionDate = sessionCalendar.getTime();
        try {
            sessionCalendar.setTime((java.util.Date)dt);
            targetCalendar.set((int)1, (int)sessionCalendar.get((int)1));
            targetCalendar.set((int)2, (int)sessionCalendar.get((int)2));
            targetCalendar.set((int)5, (int)sessionCalendar.get((int)5));
            targetCalendar.set((int)11, (int)sessionCalendar.get((int)11));
            targetCalendar.set((int)12, (int)sessionCalendar.get((int)12));
            targetCalendar.set((int)13, (int)sessionCalendar.get((int)13));
            targetCalendar.set((int)14, (int)sessionCalendar.get((int)14));
            long l = targetCalendar.getTime().getTime();
            Object var9_7 = null;
            sessionCalendar.setTime((java.util.Date)origSessionDate);
            targetCalendar.setTime((java.util.Date)origCalDate);
            return l;
        }
        catch (Throwable throwable) {
            Object var9_8 = null;
            sessionCalendar.setTime((java.util.Date)origSessionDate);
            targetCalendar.setTime((java.util.Date)origCalDate);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled unnecessary exception pruning
     */
    static final Date fastDateCreate(boolean useGmtConversion, Calendar gmtCalIfNeeded, Calendar cal, int year, int month, int day) {
        Calendar dateCal = cal;
        if (useGmtConversion) {
            if (gmtCalIfNeeded == null) {
                gmtCalIfNeeded = Calendar.getInstance((TimeZone)TimeZone.getTimeZone((String)"GMT"));
            }
            dateCal = gmtCalIfNeeded;
        }
        Calendar calendar = dateCal;
        // MONITORENTER : calendar
        java.util.Date origCalDate = dateCal.getTime();
        try {
            dateCal.clear();
            dateCal.set((int)14, (int)0);
            dateCal.set((int)year, (int)(month - 1), (int)day, (int)0, (int)0, (int)0);
            long dateAsMillis = dateCal.getTimeInMillis();
            Date date = new Date((long)dateAsMillis);
            Object var13_11 = null;
            dateCal.setTime((java.util.Date)origCalDate);
            return date;
        }
        catch (Throwable throwable) {
            Object var13_12 = null;
            dateCal.setTime((java.util.Date)origCalDate);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled unnecessary exception pruning
     */
    static final Date fastDateCreate(int year, int month, int day, Calendar targetCalendar) {
        Calendar dateCal;
        Calendar calendar = dateCal = targetCalendar == null ? new GregorianCalendar() : targetCalendar;
        // MONITORENTER : calendar
        java.util.Date origCalDate = dateCal.getTime();
        try {
            dateCal.clear();
            dateCal.set((int)year, (int)(month - 1), (int)day, (int)0, (int)0, (int)0);
            dateCal.set((int)14, (int)0);
            long dateAsMillis = dateCal.getTimeInMillis();
            Date date = new Date((long)dateAsMillis);
            Object var11_9 = null;
            dateCal.setTime((java.util.Date)origCalDate);
            return date;
        }
        catch (Throwable throwable) {
            Object var11_10 = null;
            dateCal.setTime((java.util.Date)origCalDate);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled unnecessary exception pruning
     */
    static final Time fastTimeCreate(Calendar cal, int hour, int minute, int second, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        if (hour < 0) throw SQLError.createSQLException((String)("Illegal hour value '" + hour + "' for java.sql.Time type in value '" + TimeUtil.timeFormattedString((int)hour, (int)minute, (int)second) + "."), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        if (hour > 24) {
            throw SQLError.createSQLException((String)("Illegal hour value '" + hour + "' for java.sql.Time type in value '" + TimeUtil.timeFormattedString((int)hour, (int)minute, (int)second) + "."), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
        if (minute < 0) throw SQLError.createSQLException((String)("Illegal minute value '" + minute + "' for java.sql.Time type in value '" + TimeUtil.timeFormattedString((int)hour, (int)minute, (int)second) + "."), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        if (minute > 59) {
            throw SQLError.createSQLException((String)("Illegal minute value '" + minute + "' for java.sql.Time type in value '" + TimeUtil.timeFormattedString((int)hour, (int)minute, (int)second) + "."), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
        if (second < 0) throw SQLError.createSQLException((String)("Illegal minute value '" + second + "' for java.sql.Time type in value '" + TimeUtil.timeFormattedString((int)hour, (int)minute, (int)second) + "."), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        if (second > 59) {
            throw SQLError.createSQLException((String)("Illegal minute value '" + second + "' for java.sql.Time type in value '" + TimeUtil.timeFormattedString((int)hour, (int)minute, (int)second) + "."), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
        Calendar calendar = cal;
        // MONITORENTER : calendar
        java.util.Date origCalDate = cal.getTime();
        try {
            cal.clear();
            cal.set((int)1970, (int)0, (int)1, (int)hour, (int)minute, (int)second);
            long timeAsMillis = cal.getTimeInMillis();
            Time time = new Time((long)timeAsMillis);
            Object var11_9 = null;
            cal.setTime((java.util.Date)origCalDate);
            return time;
        }
        catch (Throwable throwable) {
            Object var11_10 = null;
            cal.setTime((java.util.Date)origCalDate);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled unnecessary exception pruning
     */
    static final Time fastTimeCreate(int hour, int minute, int second, Calendar targetCalendar, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        Calendar cal;
        if (hour < 0) throw SQLError.createSQLException((String)("Illegal hour value '" + hour + "' for java.sql.Time type in value '" + TimeUtil.timeFormattedString((int)hour, (int)minute, (int)second) + "."), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        if (hour > 23) {
            throw SQLError.createSQLException((String)("Illegal hour value '" + hour + "' for java.sql.Time type in value '" + TimeUtil.timeFormattedString((int)hour, (int)minute, (int)second) + "."), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
        if (minute < 0) throw SQLError.createSQLException((String)("Illegal minute value '" + minute + "' for java.sql.Time type in value '" + TimeUtil.timeFormattedString((int)hour, (int)minute, (int)second) + "."), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        if (minute > 59) {
            throw SQLError.createSQLException((String)("Illegal minute value '" + minute + "' for java.sql.Time type in value '" + TimeUtil.timeFormattedString((int)hour, (int)minute, (int)second) + "."), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
        if (second < 0) throw SQLError.createSQLException((String)("Illegal minute value '" + second + "' for java.sql.Time type in value '" + TimeUtil.timeFormattedString((int)hour, (int)minute, (int)second) + "."), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        if (second > 59) {
            throw SQLError.createSQLException((String)("Illegal minute value '" + second + "' for java.sql.Time type in value '" + TimeUtil.timeFormattedString((int)hour, (int)minute, (int)second) + "."), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
        Calendar calendar = cal = targetCalendar == null ? new GregorianCalendar() : targetCalendar;
        // MONITORENTER : calendar
        java.util.Date origCalDate = cal.getTime();
        try {
            cal.clear();
            cal.set((int)1970, (int)0, (int)1, (int)hour, (int)minute, (int)second);
            long timeAsMillis = cal.getTimeInMillis();
            Time time = new Time((long)timeAsMillis);
            Object var12_10 = null;
            cal.setTime((java.util.Date)origCalDate);
            return time;
        }
        catch (Throwable throwable) {
            Object var12_11 = null;
            cal.setTime((java.util.Date)origCalDate);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled unnecessary exception pruning
     */
    static final Timestamp fastTimestampCreate(boolean useGmtConversion, Calendar gmtCalIfNeeded, Calendar cal, int year, int month, int day, int hour, int minute, int seconds, int secondsPart) {
        Calendar calendar = cal;
        // MONITORENTER : calendar
        java.util.Date origCalDate = cal.getTime();
        try {
            cal.clear();
            cal.set((int)year, (int)(month - 1), (int)day, (int)hour, (int)minute, (int)seconds);
            int offsetDiff = 0;
            if (useGmtConversion) {
                int fromOffset = cal.get((int)15) + cal.get((int)16);
                if (gmtCalIfNeeded == null) {
                    gmtCalIfNeeded = Calendar.getInstance((TimeZone)TimeZone.getTimeZone((String)"GMT"));
                }
                gmtCalIfNeeded.clear();
                gmtCalIfNeeded.setTimeInMillis((long)cal.getTimeInMillis());
                int toOffset = gmtCalIfNeeded.get((int)15) + gmtCalIfNeeded.get((int)16);
                offsetDiff = fromOffset - toOffset;
            }
            if (secondsPart != 0) {
                cal.set((int)14, (int)(secondsPart / 1000000));
            }
            long tsAsMillis = cal.getTimeInMillis();
            Timestamp ts = new Timestamp((long)(tsAsMillis + (long)offsetDiff));
            ts.setNanos((int)secondsPart);
            Timestamp timestamp = ts;
            Object var18_18 = null;
            cal.setTime((java.util.Date)origCalDate);
            return timestamp;
        }
        catch (Throwable throwable) {
            Object var18_19 = null;
            cal.setTime((java.util.Date)origCalDate);
            throw throwable;
        }
    }

    static final Timestamp fastTimestampCreate(TimeZone tz, int year, int month, int day, int hour, int minute, int seconds, int secondsPart) {
        GregorianCalendar cal = tz == null ? new GregorianCalendar() : new GregorianCalendar((TimeZone)tz);
        cal.clear();
        cal.set((int)year, (int)(month - 1), (int)day, (int)hour, (int)minute, (int)seconds);
        long tsAsMillis = cal.getTimeInMillis();
        Timestamp ts = new Timestamp((long)tsAsMillis);
        ts.setNanos((int)secondsPart);
        return ts;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getCanonicalTimezone(String timezoneStr, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        if (timezoneStr == null) {
            return null;
        }
        if ((timezoneStr = timezoneStr.trim()).length() > 2 && (timezoneStr.charAt((int)0) == '+' || timezoneStr.charAt((int)0) == '-') && Character.isDigit((char)timezoneStr.charAt((int)1))) {
            return "GMT" + timezoneStr;
        }
        Class<TimeUtil> class_ = TimeUtil.class;
        // MONITORENTER : com.mysql.jdbc.TimeUtil.class
        if (timeZoneMappings == null) {
            TimeUtil.loadTimeZoneMappings((ExceptionInterceptor)exceptionInterceptor);
        }
        // MONITOREXIT : class_
        String canonicalTz = timeZoneMappings.getProperty((String)timezoneStr);
        if (canonicalTz == null) throw SQLError.createSQLException((String)Messages.getString((String)"TimeUtil.UnrecognizedTimezoneId", (Object[])new Object[]{timezoneStr}), (String)"01S00", (ExceptionInterceptor)exceptionInterceptor);
        return canonicalTz;
    }

    private static String timeFormattedString(int hours, int minutes, int seconds) {
        StringBuilder buf = new StringBuilder((int)8);
        if (hours < 10) {
            buf.append((String)"0");
        }
        buf.append((int)hours);
        buf.append((String)":");
        if (minutes < 10) {
            buf.append((String)"0");
        }
        buf.append((int)minutes);
        buf.append((String)":");
        if (seconds < 10) {
            buf.append((String)"0");
        }
        buf.append((int)seconds);
        return buf.toString();
    }

    public static Timestamp adjustTimestampNanosPrecision(Timestamp ts, int fsp, boolean serverRoundFracSecs) throws SQLException {
        if (fsp < 0) throw SQLError.createSQLException((String)"fsp value must be in 0 to 6 range.", (String)"S1009", null);
        if (fsp > 6) {
            throw SQLError.createSQLException((String)"fsp value must be in 0 to 6 range.", (String)"S1009", null);
        }
        Timestamp res = (Timestamp)ts.clone();
        int nanos = res.getNanos();
        double tail = Math.pow((double)10.0, (double)((double)(9 - fsp)));
        if (serverRoundFracSecs) {
            if ((nanos = (int)Math.round((double)((double)nanos / tail)) * (int)tail) > 999999999) {
                nanos %= 1000000000;
                res.setTime((long)(res.getTime() + 1000L));
            }
        } else {
            nanos = (int)((double)nanos / tail) * (int)tail;
        }
        res.setNanos((int)nanos);
        return res;
    }

    public static String formatNanos(int nanos, boolean serverSupportsFracSecs, int fsp) throws SQLException {
        if (nanos < 0) throw SQLError.createSQLException((String)("nanos value must be in 0 to 999999999 range but was " + nanos), (String)"S1009", null);
        if (nanos > 999999999) {
            throw SQLError.createSQLException((String)("nanos value must be in 0 to 999999999 range but was " + nanos), (String)"S1009", null);
        }
        if (fsp < 0) throw SQLError.createSQLException((String)("fsp value must be in 0 to 6 range but was " + fsp), (String)"S1009", null);
        if (fsp > 6) {
            throw SQLError.createSQLException((String)("fsp value must be in 0 to 6 range but was " + fsp), (String)"S1009", null);
        }
        if (!serverSupportsFracSecs) return "0";
        if (fsp == 0) return "0";
        if (nanos == 0) {
            return "0";
        }
        if ((nanos = (int)((double)nanos / Math.pow((double)10.0, (double)((double)(9 - fsp))))) == 0) {
            return "0";
        }
        String nanosString = Integer.toString((int)nanos);
        String zeroPadding = "000000000";
        nanosString = "000000000".substring((int)0, (int)(fsp - nanosString.length())) + nanosString;
        int pos = fsp - 1;
        while (nanosString.charAt((int)pos) == '0') {
            --pos;
        }
        return nanosString.substring((int)0, (int)(pos + 1));
    }

    private static void loadTimeZoneMappings(ExceptionInterceptor exceptionInterceptor) throws SQLException {
        timeZoneMappings = new Properties();
        try {
            timeZoneMappings.load((InputStream)TimeUtil.class.getResourceAsStream((String)TIME_ZONE_MAPPINGS_RESOURCE));
        }
        catch (IOException e) {
            throw SQLError.createSQLException((String)Messages.getString((String)"TimeUtil.LoadTimeZoneMappingError"), (String)"01S00", (ExceptionInterceptor)exceptionInterceptor);
        }
        String[] arr$ = TimeZone.getAvailableIDs();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            String tz = arr$[i$];
            if (!timeZoneMappings.containsKey((Object)tz)) {
                timeZoneMappings.put(tz, tz);
            }
            ++i$;
        }
    }

    public static Timestamp truncateFractionalSeconds(Timestamp timestamp) {
        Timestamp truncatedTimestamp = new Timestamp((long)timestamp.getTime());
        truncatedTimestamp.setNanos((int)0);
        return truncatedTimestamp;
    }

    public static SimpleDateFormat getSimpleDateFormat(SimpleDateFormat cachedSimpleDateFormat, String pattern, Calendar cal, TimeZone tz) {
        SimpleDateFormat sdf;
        SimpleDateFormat simpleDateFormat = sdf = cachedSimpleDateFormat != null ? cachedSimpleDateFormat : new SimpleDateFormat((String)pattern, (Locale)Locale.US);
        if (cal != null) {
            sdf.setCalendar((Calendar)((Calendar)cal.clone()));
        }
        if (tz == null) return sdf;
        sdf.setTimeZone((TimeZone)tz);
        return sdf;
    }

    public static Calendar setProlepticIfNeeded(Calendar origCalendar, Calendar refCalendar) {
        if (origCalendar == null) return origCalendar;
        if (refCalendar == null) return origCalendar;
        if (!(origCalendar instanceof GregorianCalendar)) return origCalendar;
        if (!(refCalendar instanceof GregorianCalendar)) return origCalendar;
        if (((GregorianCalendar)refCalendar).getGregorianChange().getTime() != Long.MIN_VALUE) return origCalendar;
        origCalendar = (GregorianCalendar)origCalendar.clone();
        ((GregorianCalendar)origCalendar).setGregorianChange((java.util.Date)new Date((long)Long.MIN_VALUE));
        origCalendar.clear();
        return origCalendar;
    }

    static {
        Method aMethod;
        GMT_TIMEZONE = TimeZone.getTimeZone((String)"GMT");
        DEFAULT_TIMEZONE = TimeZone.getDefault();
        timeZoneMappings = null;
        try {
            aMethod = System.class.getMethod((String)"nanoTime", (Class[])null);
        }
        catch (SecurityException e) {
            aMethod = null;
        }
        catch (NoSuchMethodException e) {
            aMethod = null;
        }
        systemNanoTimeMethod = aMethod;
    }
}

