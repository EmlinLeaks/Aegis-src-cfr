/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Extension;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MultiHostConnectionProxy;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.Util;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Util {
    private static Util enclosingInstance;
    private static boolean isJdbc4;
    private static boolean isJdbc42;
    private static int jvmVersion;
    private static int jvmUpdateNumber;
    private static boolean isColdFusion;
    private static final ConcurrentMap<Class<?>, Boolean> isJdbcInterfaceCache;
    private static final String MYSQL_JDBC_PACKAGE_ROOT;
    private static final ConcurrentMap<Class<?>, Class<?>[]> implementedInterfacesCache;

    public static boolean isJdbc4() {
        return isJdbc4;
    }

    public static boolean isJdbc42() {
        return isJdbc42;
    }

    public static int getJVMVersion() {
        return jvmVersion;
    }

    public static boolean jvmMeetsMinimum(int version, int updateNumber) {
        if (Util.getJVMVersion() > version) return true;
        if (Util.getJVMVersion() != version) return false;
        if (Util.getJVMUpdateNumber() < updateNumber) return false;
        return true;
    }

    public static int getJVMUpdateNumber() {
        return jvmUpdateNumber;
    }

    public static boolean isColdFusion() {
        return isColdFusion;
    }

    public static boolean isCommunityEdition(String serverVersion) {
        if (Util.isEnterpriseEdition((String)serverVersion)) return false;
        return true;
    }

    public static boolean isEnterpriseEdition(String serverVersion) {
        if (serverVersion.contains((CharSequence)"enterprise")) return true;
        if (serverVersion.contains((CharSequence)"commercial")) return true;
        if (serverVersion.contains((CharSequence)"advanced")) return true;
        return false;
    }

    public static String newCrypt(String password, String seed, String encoding) {
        byte b;
        double d;
        int i;
        if (password == null) return password;
        if (password.length() == 0) {
            return password;
        }
        long[] pw = Util.newHash((byte[])seed.getBytes());
        long[] msg = Util.hashPre41Password((String)password, (String)encoding);
        long max = 0x3FFFFFFFL;
        long seed1 = (pw[0] ^ msg[0]) % max;
        long seed2 = (pw[1] ^ msg[1]) % max;
        char[] chars = new char[seed.length()];
        for (i = 0; i < seed.length(); ++i) {
            seed1 = (seed1 * 3L + seed2) % max;
            seed2 = (seed1 + seed2 + 33L) % max;
            d = (double)seed1 / (double)max;
            b = (byte)((int)Math.floor((double)(d * 31.0 + 64.0)));
            chars[i] = (char)b;
        }
        seed1 = (seed1 * 3L + seed2) % max;
        seed2 = (seed1 + seed2 + 33L) % max;
        d = (double)seed1 / (double)max;
        b = (byte)((int)Math.floor((double)(d * 31.0)));
        i = 0;
        while (i < seed.length()) {
            char[] arrc = chars;
            int n = i++;
            arrc[n] = (char)(arrc[n] ^ (char)b);
        }
        return new String((char[])chars);
    }

    public static long[] hashPre41Password(String password, String encoding) {
        try {
            return Util.newHash((byte[])password.replaceAll((String)"\\s", (String)"").getBytes((String)encoding));
        }
        catch (UnsupportedEncodingException e) {
            return new long[0];
        }
    }

    public static long[] hashPre41Password(String password) {
        return Util.hashPre41Password((String)password, (String)Charset.defaultCharset().name());
    }

    static long[] newHash(byte[] password) {
        long nr = 1345345333L;
        long add = 7L;
        long nr2 = 305419889L;
        byte[] arr$ = password;
        int len$ = arr$.length;
        int i$ = 0;
        do {
            if (i$ >= len$) {
                long[] result = new long[]{nr & Integer.MAX_VALUE, nr2 & Integer.MAX_VALUE};
                return result;
            }
            byte b = arr$[i$];
            long tmp = (long)(255 & b);
            nr ^= ((nr & 63L) + add) * tmp + (nr << 8);
            nr2 += nr2 << 8 ^ nr;
            add += tmp;
            ++i$;
        } while (true);
    }

    public static String oldCrypt(String password, String seed) {
        long max = 0x1FFFFFFL;
        if (password == null) return password;
        if (password.length() == 0) {
            return password;
        }
        long hp = Util.oldHash((String)seed);
        long hm = Util.oldHash((String)password);
        long nr = hp ^ hm;
        long s1 = nr %= max;
        long s2 = nr / 2L;
        char[] chars = new char[seed.length()];
        int i = 0;
        while (i < seed.length()) {
            s1 = (s1 * 3L + s2) % max;
            s2 = (s1 + s2 + 33L) % max;
            double d = (double)s1 / (double)max;
            byte b = (byte)((int)Math.floor((double)(d * 31.0 + 64.0)));
            chars[i] = (char)b;
            ++i;
        }
        return new String((char[])chars);
    }

    static long oldHash(String password) {
        long nr = 1345345333L;
        long nr2 = 7L;
        int i = 0;
        while (i < password.length()) {
            if (password.charAt((int)i) != ' ' && password.charAt((int)i) != '\t') {
                long tmp = (long)password.charAt((int)i);
                nr ^= ((nr & 63L) + nr2) * tmp + (nr << 8);
                nr2 += tmp;
            }
            ++i;
        }
        return nr & Integer.MAX_VALUE;
    }

    private static RandStructcture randomInit(long seed1, long seed2) {
        RandStructcture randStruct = new RandStructcture((Util)enclosingInstance);
        randStruct.maxValue = 0x3FFFFFFFL;
        randStruct.maxValueDbl = (double)randStruct.maxValue;
        randStruct.seed1 = seed1 % randStruct.maxValue;
        randStruct.seed2 = seed2 % randStruct.maxValue;
        return randStruct;
    }

    public static Object readObject(ResultSet resultSet, int index) throws Exception {
        ObjectInputStream objIn = new ObjectInputStream((InputStream)resultSet.getBinaryStream((int)index));
        Object obj = objIn.readObject();
        objIn.close();
        return obj;
    }

    private static double rnd(RandStructcture randStruct) {
        randStruct.seed1 = (randStruct.seed1 * 3L + randStruct.seed2) % randStruct.maxValue;
        randStruct.seed2 = (randStruct.seed1 + randStruct.seed2 + 33L) % randStruct.maxValue;
        return (double)randStruct.seed1 / randStruct.maxValueDbl;
    }

    public static String scramble(String message, String password) {
        byte[] to = new byte[8];
        String val = "";
        message = message.substring((int)0, (int)8);
        if (password == null) return val;
        if (password.length() <= 0) return val;
        long[] hashPass = Util.hashPre41Password((String)password);
        long[] hashMessage = Util.newHash((byte[])message.getBytes());
        RandStructcture randStruct = Util.randomInit((long)(hashPass[0] ^ hashMessage[0]), (long)(hashPass[1] ^ hashMessage[1]));
        int msgPos = 0;
        int msgLength = message.length();
        int toPos = 0;
        while (msgPos++ < msgLength) {
            to[toPos++] = (byte)((int)(Math.floor((double)(Util.rnd((RandStructcture)randStruct) * 31.0)) + 64.0));
        }
        byte extra = (byte)((int)Math.floor((double)(Util.rnd((RandStructcture)randStruct) * 31.0)));
        int i = 0;
        while (i < to.length) {
            byte[] arrby = to;
            int n = i++;
            arrby[n] = (byte)(arrby[n] ^ extra);
        }
        return StringUtils.toString((byte[])to);
    }

    public static String stackTraceToString(Throwable ex) {
        StringBuilder traceBuf = new StringBuilder();
        traceBuf.append((String)Messages.getString((String)"Util.1"));
        if (ex != null) {
            traceBuf.append((String)ex.getClass().getName());
            String message = ex.getMessage();
            if (message != null) {
                traceBuf.append((String)Messages.getString((String)"Util.2"));
                traceBuf.append((String)message);
            }
            StringWriter out = new StringWriter();
            PrintWriter printOut = new PrintWriter((Writer)out);
            ex.printStackTrace((PrintWriter)printOut);
            traceBuf.append((String)Messages.getString((String)"Util.3"));
            traceBuf.append((String)out.toString());
        }
        traceBuf.append((String)Messages.getString((String)"Util.4"));
        return traceBuf.toString();
    }

    public static Object getInstance(String className, Class<?>[] argTypes, Object[] args, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        try {
            return Util.handleNewInstance(Class.forName((String)className).getConstructor(argTypes), (Object[])args, (ExceptionInterceptor)exceptionInterceptor);
        }
        catch (SecurityException e) {
            throw SQLError.createSQLException((String)"Can't instantiate required class", (String)"S1000", (Throwable)e, (ExceptionInterceptor)exceptionInterceptor);
        }
        catch (NoSuchMethodException e) {
            throw SQLError.createSQLException((String)"Can't instantiate required class", (String)"S1000", (Throwable)e, (ExceptionInterceptor)exceptionInterceptor);
        }
        catch (ClassNotFoundException e) {
            throw SQLError.createSQLException((String)"Can't instantiate required class", (String)"S1000", (Throwable)e, (ExceptionInterceptor)exceptionInterceptor);
        }
    }

    public static final Object handleNewInstance(Constructor<?> ctor, Object[] args, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        try {
            return ctor.newInstance((Object[])args);
        }
        catch (IllegalArgumentException e) {
            throw SQLError.createSQLException((String)"Can't instantiate required class", (String)"S1000", (Throwable)e, (ExceptionInterceptor)exceptionInterceptor);
        }
        catch (InstantiationException e) {
            throw SQLError.createSQLException((String)"Can't instantiate required class", (String)"S1000", (Throwable)e, (ExceptionInterceptor)exceptionInterceptor);
        }
        catch (IllegalAccessException e) {
            throw SQLError.createSQLException((String)"Can't instantiate required class", (String)"S1000", (Throwable)e, (ExceptionInterceptor)exceptionInterceptor);
        }
        catch (InvocationTargetException e) {
            Throwable target = e.getTargetException();
            if (target instanceof SQLException) {
                throw (SQLException)target;
            }
            if (!(target instanceof ExceptionInInitializerError)) throw SQLError.createSQLException((String)target.toString(), (String)"S1000", (Throwable)target, (ExceptionInterceptor)exceptionInterceptor);
            target = ((ExceptionInInitializerError)target).getException();
            throw SQLError.createSQLException((String)target.toString(), (String)"S1000", (Throwable)target, (ExceptionInterceptor)exceptionInterceptor);
        }
    }

    public static boolean interfaceExists(String hostname) {
        try {
            Class<?> networkInterfaceClass = Class.forName((String)"java.net.NetworkInterface");
            if (networkInterfaceClass.getMethod((String)"getByName", (Class[])null).invoke(networkInterfaceClass, (Object[])new Object[]{hostname}) == null) return false;
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }

    public static void resultSetToMap(Map mappedValues, ResultSet rs) throws SQLException {
        while (rs.next()) {
            mappedValues.put(rs.getObject((int)1), rs.getObject((int)2));
        }
    }

    public static void resultSetToMap(Map mappedValues, ResultSet rs, int key, int value) throws SQLException {
        while (rs.next()) {
            mappedValues.put(rs.getObject((int)key), rs.getObject((int)value));
        }
    }

    public static void resultSetToMap(Map mappedValues, ResultSet rs, String key, String value) throws SQLException {
        while (rs.next()) {
            mappedValues.put(rs.getObject((String)key), rs.getObject((String)value));
        }
    }

    public static Map<Object, Object> calculateDifferences(Map<?, ?> map1, Map<?, ?> map2) {
        HashMap<Object, Object> diffMap = new HashMap<Object, Object>();
        Iterator<Map.Entry<?, ?>> i$ = map1.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<?, ?> entry = i$.next();
            ? key = entry.getKey();
            Number value1 = null;
            Number value2 = null;
            if (entry.getValue() instanceof Number) {
                value1 = (Number)entry.getValue();
                value2 = (Number)map2.get(key);
            } else {
                try {
                    value1 = new Double((String)entry.getValue().toString());
                    value2 = new Double((String)map2.get(key).toString());
                }
                catch (NumberFormatException nfe) {
                    continue;
                }
            }
            if (value1.equals((Object)value2)) continue;
            if (value1 instanceof Byte) {
                diffMap.put(key, (Object)Byte.valueOf((byte)((byte)(((Byte)value2).byteValue() - ((Byte)value1).byteValue()))));
                continue;
            }
            if (value1 instanceof Short) {
                diffMap.put(key, (Object)Short.valueOf((short)((short)(((Short)value2).shortValue() - ((Short)value1).shortValue()))));
                continue;
            }
            if (value1 instanceof Integer) {
                diffMap.put(key, (Object)Integer.valueOf((int)(((Integer)value2).intValue() - ((Integer)value1).intValue())));
                continue;
            }
            if (value1 instanceof Long) {
                diffMap.put(key, (Object)Long.valueOf((long)(((Long)value2).longValue() - ((Long)value1).longValue())));
                continue;
            }
            if (value1 instanceof Float) {
                diffMap.put(key, (Object)Float.valueOf((float)(((Float)value2).floatValue() - ((Float)value1).floatValue())));
                continue;
            }
            if (value1 instanceof Double) {
                diffMap.put(key, (Object)Double.valueOf((double)((double)(((Double)value2).shortValue() - ((Double)value1).shortValue()))));
                continue;
            }
            if (value1 instanceof BigDecimal) {
                diffMap.put(key, (Object)((BigDecimal)value2).subtract((BigDecimal)((BigDecimal)value1)));
                continue;
            }
            if (!(value1 instanceof BigInteger)) continue;
            diffMap.put(key, (Object)((BigInteger)value2).subtract((BigInteger)((BigInteger)value1)));
        }
        return diffMap;
    }

    public static List<Extension> loadExtensions(Connection conn, Properties props, String extensionClassNames, String errorMessageKey, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        LinkedList<Extension> extensionList = new LinkedList<Extension>();
        List<String> interceptorsToCreate = StringUtils.split((String)extensionClassNames, (String)",", (boolean)true);
        String className = null;
        try {
            int i = 0;
            int s = interceptorsToCreate.size();
            while (i < s) {
                className = interceptorsToCreate.get((int)i);
                Extension extensionInstance = (Extension)Class.forName((String)className).newInstance();
                extensionInstance.init((Connection)conn, (Properties)props);
                extensionList.add((Extension)extensionInstance);
                ++i;
            }
            return extensionList;
        }
        catch (Throwable t) {
            SQLException sqlEx = SQLError.createSQLException((String)Messages.getString((String)errorMessageKey, (Object[])new Object[]{className}), (ExceptionInterceptor)exceptionInterceptor);
            sqlEx.initCause((Throwable)t);
            throw sqlEx;
        }
    }

    public static boolean isJdbcInterface(Class<?> clazz) {
        if (isJdbcInterfaceCache.containsKey(clazz)) {
            return ((Boolean)isJdbcInterfaceCache.get(clazz)).booleanValue();
        }
        if (clazz.isInterface()) {
            try {
                if (Util.isJdbcPackage((String)Util.getPackageName(clazz))) {
                    isJdbcInterfaceCache.putIfAbsent(clazz, (Boolean)Boolean.valueOf((boolean)true));
                    return true;
                }
            }
            catch (Exception ex) {
                // empty catch block
            }
        }
        for (Class<?> iface : clazz.getInterfaces()) {
            if (!Util.isJdbcInterface(iface)) continue;
            isJdbcInterfaceCache.putIfAbsent(clazz, (Boolean)Boolean.valueOf((boolean)true));
            return true;
        }
        if (clazz.getSuperclass() != null && Util.isJdbcInterface(clazz.getSuperclass())) {
            isJdbcInterfaceCache.putIfAbsent(clazz, (Boolean)Boolean.valueOf((boolean)true));
            return true;
        }
        isJdbcInterfaceCache.putIfAbsent(clazz, (Boolean)Boolean.valueOf((boolean)false));
        return false;
    }

    public static boolean isJdbcPackage(String packageName) {
        if (packageName == null) return false;
        if (packageName.startsWith((String)"java.sql")) return true;
        if (packageName.startsWith((String)"javax.sql")) return true;
        if (!packageName.startsWith((String)MYSQL_JDBC_PACKAGE_ROOT)) return false;
        return true;
    }

    public static Class<?>[] getImplementedInterfaces(Class<?> clazz) {
        Class[] implementedInterfaces = (Class[])implementedInterfacesCache.get(clazz);
        if (implementedInterfaces != null) {
            return implementedInterfaces;
        }
        LinkedHashSet<E> interfaces = new LinkedHashSet<E>();
        Class<?> superClass = clazz;
        do {
            Collections.addAll(interfaces, superClass.getInterfaces());
        } while ((superClass = superClass.getSuperclass()) != null);
        implementedInterfaces = interfaces.toArray(new Class[interfaces.size()]);
        Class[] oldValue = implementedInterfacesCache.putIfAbsent(clazz, implementedInterfaces);
        if (oldValue == null) return implementedInterfaces;
        return oldValue;
    }

    public static long secondsSinceMillis(long timeInMillis) {
        return (System.currentTimeMillis() - timeInMillis) / 1000L;
    }

    public static int truncateAndConvertToInt(long longValue) {
        if (longValue > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (longValue < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        int n = (int)longValue;
        return n;
    }

    public static int[] truncateAndConvertToInt(long[] longArray) {
        int[] intArray = new int[longArray.length];
        int i = 0;
        while (i < longArray.length) {
            intArray[i] = longArray[i] > Integer.MAX_VALUE ? Integer.MAX_VALUE : (longArray[i] < Integer.MIN_VALUE ? Integer.MIN_VALUE : (int)longArray[i]);
            ++i;
        }
        return intArray;
    }

    public static String getPackageName(Class<?> clazz) {
        String fqcn = clazz.getName();
        int classNameStartsAt = fqcn.lastIndexOf((int)46);
        if (classNameStartsAt <= 0) return "";
        return fqcn.substring((int)0, (int)classNameStartsAt);
    }

    static {
        String loadedFrom;
        enclosingInstance = new Util();
        jvmVersion = -1;
        jvmUpdateNumber = -1;
        isColdFusion = false;
        try {
            Class.forName((String)"java.sql.NClob");
            isJdbc4 = true;
        }
        catch (ClassNotFoundException e) {
            isJdbc4 = false;
        }
        try {
            Class.forName((String)"java.sql.JDBCType");
            isJdbc42 = true;
        }
        catch (Throwable t) {
            isJdbc42 = false;
        }
        String jvmVersionString = System.getProperty((String)"java.version");
        int startPos = jvmVersionString.indexOf((int)46);
        int endPos = startPos + 1;
        if (startPos != -1) {
            while (Character.isDigit((char)jvmVersionString.charAt((int)endPos)) && ++endPos < jvmVersionString.length()) {
            }
        }
        jvmVersion = endPos > ++startPos ? Integer.parseInt((String)jvmVersionString.substring((int)startPos, (int)endPos)) : (isJdbc42 ? 8 : (isJdbc4 ? 6 : 5));
        startPos = jvmVersionString.indexOf((String)"_");
        endPos = startPos + 1;
        if (startPos != -1) {
            while (Character.isDigit((char)jvmVersionString.charAt((int)endPos)) && ++endPos < jvmVersionString.length()) {
            }
        }
        if (endPos > ++startPos) {
            jvmUpdateNumber = Integer.parseInt((String)jvmVersionString.substring((int)startPos, (int)endPos));
        }
        isColdFusion = (loadedFrom = Util.stackTraceToString((Throwable)new Throwable())) != null ? loadedFrom.indexOf((String)"coldfusion") != -1 : false;
        isJdbcInterfaceCache = new ConcurrentHashMap<Class<?>, Boolean>();
        String packageName = Util.getPackageName(MultiHostConnectionProxy.class);
        MYSQL_JDBC_PACKAGE_ROOT = packageName.substring((int)0, (int)(packageName.indexOf((String)"jdbc") + 4));
        implementedInterfacesCache = new ConcurrentHashMap<Class<?>, Class<?>[]>();
    }
}

