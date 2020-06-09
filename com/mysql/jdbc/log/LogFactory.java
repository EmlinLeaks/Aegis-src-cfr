/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.log;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.log.Log;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class LogFactory {
    public static Log getLogger(String className, String instanceName, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        if (className == null) {
            throw SQLError.createSQLException((String)"Logger class can not be NULL", (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
        if (instanceName == null) {
            throw SQLError.createSQLException((String)"Logger instance name can not be NULL", (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
        }
        try {
            Class<?> loggerClass = null;
            try {
                loggerClass = Class.forName((String)className);
            }
            catch (ClassNotFoundException nfe) {
                loggerClass = Class.forName((String)(Util.getPackageName(Log.class) + "." + className));
            }
            Constructor<?> constructor = loggerClass.getConstructor(String.class);
            return (Log)constructor.newInstance((Object[])new Object[]{instanceName});
        }
        catch (ClassNotFoundException cnfe) {
            SQLException sqlEx = SQLError.createSQLException((String)("Unable to load class for logger '" + className + "'"), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
            sqlEx.initCause((Throwable)cnfe);
            throw sqlEx;
        }
        catch (NoSuchMethodException nsme) {
            SQLException sqlEx = SQLError.createSQLException((String)"Logger class does not have a single-arg constructor that takes an instance name", (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
            sqlEx.initCause((Throwable)nsme);
            throw sqlEx;
        }
        catch (InstantiationException inse) {
            SQLException sqlEx = SQLError.createSQLException((String)("Unable to instantiate logger class '" + className + "', exception in constructor?"), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
            sqlEx.initCause((Throwable)inse);
            throw sqlEx;
        }
        catch (InvocationTargetException ite) {
            SQLException sqlEx = SQLError.createSQLException((String)("Unable to instantiate logger class '" + className + "', exception in constructor?"), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
            sqlEx.initCause((Throwable)ite);
            throw sqlEx;
        }
        catch (IllegalAccessException iae) {
            SQLException sqlEx = SQLError.createSQLException((String)("Unable to instantiate logger class '" + className + "', constructor not public"), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
            sqlEx.initCause((Throwable)iae);
            throw sqlEx;
        }
        catch (ClassCastException cce) {
            SQLException sqlEx = SQLError.createSQLException((String)("Logger class '" + className + "' does not implement the '" + Log.class.getName() + "' interface"), (String)"S1009", (ExceptionInterceptor)exceptionInterceptor);
            sqlEx.initCause((Throwable)cce);
            throw sqlEx;
        }
    }
}

