/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class MysqlDefs {
    static final int COM_BINLOG_DUMP = 18;
    static final int COM_CHANGE_USER = 17;
    static final int COM_CLOSE_STATEMENT = 25;
    static final int COM_CONNECT_OUT = 20;
    static final int COM_END = 29;
    static final int COM_EXECUTE = 23;
    static final int COM_FETCH = 28;
    static final int COM_LONG_DATA = 24;
    static final int COM_PREPARE = 22;
    static final int COM_REGISTER_SLAVE = 21;
    static final int COM_RESET_STMT = 26;
    static final int COM_SET_OPTION = 27;
    static final int COM_TABLE_DUMP = 19;
    static final int CONNECT = 11;
    static final int CREATE_DB = 5;
    static final int DEBUG = 13;
    static final int DELAYED_INSERT = 16;
    static final int DROP_DB = 6;
    static final int FIELD_LIST = 4;
    static final int FIELD_TYPE_BIT = 16;
    public static final int FIELD_TYPE_BLOB = 252;
    static final int FIELD_TYPE_DATE = 10;
    static final int FIELD_TYPE_DATETIME = 12;
    static final int FIELD_TYPE_DECIMAL = 0;
    static final int FIELD_TYPE_DOUBLE = 5;
    static final int FIELD_TYPE_ENUM = 247;
    static final int FIELD_TYPE_FLOAT = 4;
    static final int FIELD_TYPE_GEOMETRY = 255;
    static final int FIELD_TYPE_INT24 = 9;
    static final int FIELD_TYPE_LONG = 3;
    static final int FIELD_TYPE_LONG_BLOB = 251;
    static final int FIELD_TYPE_LONGLONG = 8;
    static final int FIELD_TYPE_MEDIUM_BLOB = 250;
    static final int FIELD_TYPE_NEW_DECIMAL = 246;
    static final int FIELD_TYPE_NEWDATE = 14;
    static final int FIELD_TYPE_NULL = 6;
    static final int FIELD_TYPE_SET = 248;
    static final int FIELD_TYPE_SHORT = 2;
    static final int FIELD_TYPE_STRING = 254;
    static final int FIELD_TYPE_TIME = 11;
    static final int FIELD_TYPE_TIMESTAMP = 7;
    static final int FIELD_TYPE_TINY = 1;
    static final int FIELD_TYPE_TINY_BLOB = 249;
    static final int FIELD_TYPE_VAR_STRING = 253;
    static final int FIELD_TYPE_VARCHAR = 15;
    static final int FIELD_TYPE_YEAR = 13;
    static final int FIELD_TYPE_JSON = 245;
    static final int INIT_DB = 2;
    static final long LENGTH_BLOB = 65535L;
    static final long LENGTH_LONGBLOB = 0xFFFFFFFFL;
    static final long LENGTH_MEDIUMBLOB = 0xFFFFFFL;
    static final long LENGTH_TINYBLOB = 255L;
    static final int MAX_ROWS = 50000000;
    public static final int NO_CHARSET_INFO = -1;
    static final byte OPEN_CURSOR_FLAG = 1;
    static final int PING = 14;
    static final int PROCESS_INFO = 10;
    static final int PROCESS_KILL = 12;
    static final int QUERY = 3;
    static final int QUIT = 1;
    static final int RELOAD = 7;
    static final int SHUTDOWN = 8;
    static final int SLEEP = 0;
    static final int STATISTICS = 9;
    static final int TIME = 15;
    private static Map<String, Integer> mysqlToJdbcTypesMap = new HashMap<String, Integer>();

    static int mysqlToJavaType(int mysqlType) {
        switch (mysqlType) {
            case 0: 
            case 246: {
                return 3;
            }
            case 1: {
                return -6;
            }
            case 2: {
                return 5;
            }
            case 3: {
                return 4;
            }
            case 4: {
                return 7;
            }
            case 5: {
                return 8;
            }
            case 6: {
                return 0;
            }
            case 7: {
                return 93;
            }
            case 8: {
                return -5;
            }
            case 9: {
                return 4;
            }
            case 10: {
                return 91;
            }
            case 11: {
                return 92;
            }
            case 12: {
                return 93;
            }
            case 13: {
                return 91;
            }
            case 14: {
                return 91;
            }
            case 247: {
                return 1;
            }
            case 248: {
                return 1;
            }
            case 249: {
                return -3;
            }
            case 250: {
                return -4;
            }
            case 251: {
                return -4;
            }
            case 252: {
                return -4;
            }
            case 15: 
            case 253: {
                return 12;
            }
            case 245: 
            case 254: {
                return 1;
            }
            case 255: {
                return -2;
            }
            case 16: {
                return -7;
            }
        }
        return 12;
    }

    static int mysqlToJavaType(String mysqlType) {
        if (mysqlType.equalsIgnoreCase((String)"BIT")) {
            return MysqlDefs.mysqlToJavaType((int)16);
        }
        if (mysqlType.equalsIgnoreCase((String)"TINYINT")) {
            return MysqlDefs.mysqlToJavaType((int)1);
        }
        if (mysqlType.equalsIgnoreCase((String)"SMALLINT")) {
            return MysqlDefs.mysqlToJavaType((int)2);
        }
        if (mysqlType.equalsIgnoreCase((String)"MEDIUMINT")) {
            return MysqlDefs.mysqlToJavaType((int)9);
        }
        if (mysqlType.equalsIgnoreCase((String)"INT")) return MysqlDefs.mysqlToJavaType((int)3);
        if (mysqlType.equalsIgnoreCase((String)"INTEGER")) {
            return MysqlDefs.mysqlToJavaType((int)3);
        }
        if (mysqlType.equalsIgnoreCase((String)"BIGINT")) {
            return MysqlDefs.mysqlToJavaType((int)8);
        }
        if (mysqlType.equalsIgnoreCase((String)"INT24")) {
            return MysqlDefs.mysqlToJavaType((int)9);
        }
        if (mysqlType.equalsIgnoreCase((String)"REAL")) {
            return MysqlDefs.mysqlToJavaType((int)5);
        }
        if (mysqlType.equalsIgnoreCase((String)"FLOAT")) {
            return MysqlDefs.mysqlToJavaType((int)4);
        }
        if (mysqlType.equalsIgnoreCase((String)"DECIMAL")) {
            return MysqlDefs.mysqlToJavaType((int)0);
        }
        if (mysqlType.equalsIgnoreCase((String)"NUMERIC")) {
            return MysqlDefs.mysqlToJavaType((int)0);
        }
        if (mysqlType.equalsIgnoreCase((String)"DOUBLE")) {
            return MysqlDefs.mysqlToJavaType((int)5);
        }
        if (mysqlType.equalsIgnoreCase((String)"CHAR")) {
            return MysqlDefs.mysqlToJavaType((int)254);
        }
        if (mysqlType.equalsIgnoreCase((String)"VARCHAR")) {
            return MysqlDefs.mysqlToJavaType((int)253);
        }
        if (mysqlType.equalsIgnoreCase((String)"DATE")) {
            return MysqlDefs.mysqlToJavaType((int)10);
        }
        if (mysqlType.equalsIgnoreCase((String)"TIME")) {
            return MysqlDefs.mysqlToJavaType((int)11);
        }
        if (mysqlType.equalsIgnoreCase((String)"YEAR")) {
            return MysqlDefs.mysqlToJavaType((int)13);
        }
        if (mysqlType.equalsIgnoreCase((String)"TIMESTAMP")) {
            return MysqlDefs.mysqlToJavaType((int)7);
        }
        if (mysqlType.equalsIgnoreCase((String)"DATETIME")) {
            return MysqlDefs.mysqlToJavaType((int)12);
        }
        if (mysqlType.equalsIgnoreCase((String)"TINYBLOB")) {
            return -2;
        }
        if (mysqlType.equalsIgnoreCase((String)"BLOB")) {
            return -4;
        }
        if (mysqlType.equalsIgnoreCase((String)"MEDIUMBLOB")) {
            return -4;
        }
        if (mysqlType.equalsIgnoreCase((String)"LONGBLOB")) {
            return -4;
        }
        if (mysqlType.equalsIgnoreCase((String)"TINYTEXT")) {
            return 12;
        }
        if (mysqlType.equalsIgnoreCase((String)"TEXT")) {
            return -1;
        }
        if (mysqlType.equalsIgnoreCase((String)"MEDIUMTEXT")) {
            return -1;
        }
        if (mysqlType.equalsIgnoreCase((String)"LONGTEXT")) {
            return -1;
        }
        if (mysqlType.equalsIgnoreCase((String)"ENUM")) {
            return MysqlDefs.mysqlToJavaType((int)247);
        }
        if (mysqlType.equalsIgnoreCase((String)"SET")) {
            return MysqlDefs.mysqlToJavaType((int)248);
        }
        if (mysqlType.equalsIgnoreCase((String)"GEOMETRY")) {
            return MysqlDefs.mysqlToJavaType((int)255);
        }
        if (mysqlType.equalsIgnoreCase((String)"BINARY")) {
            return -2;
        }
        if (mysqlType.equalsIgnoreCase((String)"VARBINARY")) {
            return -3;
        }
        if (mysqlType.equalsIgnoreCase((String)"BIT")) {
            return MysqlDefs.mysqlToJavaType((int)16);
        }
        if (!mysqlType.equalsIgnoreCase((String)"JSON")) return 1111;
        return MysqlDefs.mysqlToJavaType((int)245);
    }

    public static String typeToName(int mysqlType) {
        switch (mysqlType) {
            case 0: {
                return "FIELD_TYPE_DECIMAL";
            }
            case 1: {
                return "FIELD_TYPE_TINY";
            }
            case 2: {
                return "FIELD_TYPE_SHORT";
            }
            case 3: {
                return "FIELD_TYPE_LONG";
            }
            case 4: {
                return "FIELD_TYPE_FLOAT";
            }
            case 5: {
                return "FIELD_TYPE_DOUBLE";
            }
            case 6: {
                return "FIELD_TYPE_NULL";
            }
            case 7: {
                return "FIELD_TYPE_TIMESTAMP";
            }
            case 8: {
                return "FIELD_TYPE_LONGLONG";
            }
            case 9: {
                return "FIELD_TYPE_INT24";
            }
            case 16: {
                return "FIELD_TYPE_BIT";
            }
            case 10: {
                return "FIELD_TYPE_DATE";
            }
            case 11: {
                return "FIELD_TYPE_TIME";
            }
            case 12: {
                return "FIELD_TYPE_DATETIME";
            }
            case 13: {
                return "FIELD_TYPE_YEAR";
            }
            case 14: {
                return "FIELD_TYPE_NEWDATE";
            }
            case 247: {
                return "FIELD_TYPE_ENUM";
            }
            case 248: {
                return "FIELD_TYPE_SET";
            }
            case 249: {
                return "FIELD_TYPE_TINY_BLOB";
            }
            case 250: {
                return "FIELD_TYPE_MEDIUM_BLOB";
            }
            case 251: {
                return "FIELD_TYPE_LONG_BLOB";
            }
            case 252: {
                return "FIELD_TYPE_BLOB";
            }
            case 253: {
                return "FIELD_TYPE_VAR_STRING";
            }
            case 254: {
                return "FIELD_TYPE_STRING";
            }
            case 15: {
                return "FIELD_TYPE_VARCHAR";
            }
            case 255: {
                return "FIELD_TYPE_GEOMETRY";
            }
            case 245: {
                return "FIELD_TYPE_JSON";
            }
        }
        return " Unknown MySQL Type # " + mysqlType;
    }

    static final void appendJdbcTypeMappingQuery(StringBuilder buf, String mysqlTypeColumnName) {
        buf.append((String)"CASE ");
        HashMap<String, Integer> typesMap = new HashMap<String, Integer>();
        typesMap.putAll(mysqlToJdbcTypesMap);
        typesMap.put("BINARY", Integer.valueOf((int)-2));
        typesMap.put("VARBINARY", Integer.valueOf((int)-3));
        Iterator<K> mysqlTypes = typesMap.keySet().iterator();
        do {
            if (!mysqlTypes.hasNext()) {
                buf.append((String)" ELSE ");
                buf.append((int)1111);
                buf.append((String)" END ");
                return;
            }
            String mysqlTypeName = (String)mysqlTypes.next();
            buf.append((String)" WHEN UPPER(");
            buf.append((String)mysqlTypeColumnName);
            buf.append((String)")='");
            buf.append((String)mysqlTypeName);
            buf.append((String)"' THEN ");
            buf.append(typesMap.get((Object)mysqlTypeName));
            if (!mysqlTypeName.equalsIgnoreCase((String)"DOUBLE") && !mysqlTypeName.equalsIgnoreCase((String)"FLOAT") && !mysqlTypeName.equalsIgnoreCase((String)"DECIMAL") && !mysqlTypeName.equalsIgnoreCase((String)"NUMERIC")) continue;
            buf.append((String)" WHEN ");
            buf.append((String)mysqlTypeColumnName);
            buf.append((String)"='");
            buf.append((String)mysqlTypeName);
            buf.append((String)" UNSIGNED' THEN ");
            buf.append(typesMap.get((Object)mysqlTypeName));
        } while (true);
    }

    static {
        mysqlToJdbcTypesMap.put((String)"BIT", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)16)));
        mysqlToJdbcTypesMap.put((String)"TINYINT", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)1)));
        mysqlToJdbcTypesMap.put((String)"SMALLINT", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)2)));
        mysqlToJdbcTypesMap.put((String)"MEDIUMINT", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)9)));
        mysqlToJdbcTypesMap.put((String)"INT", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)3)));
        mysqlToJdbcTypesMap.put((String)"INTEGER", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)3)));
        mysqlToJdbcTypesMap.put((String)"BIGINT", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)8)));
        mysqlToJdbcTypesMap.put((String)"INT24", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)9)));
        mysqlToJdbcTypesMap.put((String)"REAL", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)5)));
        mysqlToJdbcTypesMap.put((String)"FLOAT", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)4)));
        mysqlToJdbcTypesMap.put((String)"DECIMAL", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)0)));
        mysqlToJdbcTypesMap.put((String)"NUMERIC", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)0)));
        mysqlToJdbcTypesMap.put((String)"DOUBLE", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)5)));
        mysqlToJdbcTypesMap.put((String)"CHAR", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)254)));
        mysqlToJdbcTypesMap.put((String)"VARCHAR", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)253)));
        mysqlToJdbcTypesMap.put((String)"DATE", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)10)));
        mysqlToJdbcTypesMap.put((String)"TIME", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)11)));
        mysqlToJdbcTypesMap.put((String)"YEAR", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)13)));
        mysqlToJdbcTypesMap.put((String)"TIMESTAMP", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)7)));
        mysqlToJdbcTypesMap.put((String)"DATETIME", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)12)));
        mysqlToJdbcTypesMap.put((String)"TINYBLOB", (Integer)Integer.valueOf((int)-2));
        mysqlToJdbcTypesMap.put((String)"BLOB", (Integer)Integer.valueOf((int)-4));
        mysqlToJdbcTypesMap.put((String)"MEDIUMBLOB", (Integer)Integer.valueOf((int)-4));
        mysqlToJdbcTypesMap.put((String)"LONGBLOB", (Integer)Integer.valueOf((int)-4));
        mysqlToJdbcTypesMap.put((String)"TINYTEXT", (Integer)Integer.valueOf((int)12));
        mysqlToJdbcTypesMap.put((String)"TEXT", (Integer)Integer.valueOf((int)-1));
        mysqlToJdbcTypesMap.put((String)"MEDIUMTEXT", (Integer)Integer.valueOf((int)-1));
        mysqlToJdbcTypesMap.put((String)"LONGTEXT", (Integer)Integer.valueOf((int)-1));
        mysqlToJdbcTypesMap.put((String)"ENUM", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)247)));
        mysqlToJdbcTypesMap.put((String)"SET", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)248)));
        mysqlToJdbcTypesMap.put((String)"GEOMETRY", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)255)));
        mysqlToJdbcTypesMap.put((String)"JSON", (Integer)Integer.valueOf((int)MysqlDefs.mysqlToJavaType((int)245)));
    }
}

