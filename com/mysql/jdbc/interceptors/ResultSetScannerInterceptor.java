/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.interceptors;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementInterceptor;
import com.mysql.jdbc.interceptors.ResultSetScannerInterceptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Pattern;

public class ResultSetScannerInterceptor
implements StatementInterceptor {
    protected Pattern regexP;

    @Override
    public void init(Connection conn, Properties props) throws SQLException {
        String regexFromUser = props.getProperty((String)"resultSetScannerRegex");
        if (regexFromUser == null) throw new SQLException((String)"resultSetScannerRegex must be configured, and must be > 0 characters");
        if (regexFromUser.length() == 0) {
            throw new SQLException((String)"resultSetScannerRegex must be configured, and must be > 0 characters");
        }
        try {
            this.regexP = Pattern.compile((String)regexFromUser);
            return;
        }
        catch (Throwable t) {
            SQLException sqlEx = new SQLException((String)"Can't use configured regex due to underlying exception.");
            sqlEx.initCause((Throwable)t);
            throw sqlEx;
        }
    }

    @Override
    public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection) throws SQLException {
        ResultSetInternalMethods finalResultSet = originalResultSet;
        return (ResultSetInternalMethods)Proxy.newProxyInstance((ClassLoader)originalResultSet.getClass().getClassLoader(), new Class[]{ResultSetInternalMethods.class}, (InvocationHandler)new InvocationHandler((ResultSetScannerInterceptor)this, (ResultSetInternalMethods)finalResultSet){
            final /* synthetic */ ResultSetInternalMethods val$finalResultSet;
            final /* synthetic */ ResultSetScannerInterceptor this$0;
            {
                this.this$0 = resultSetScannerInterceptor;
                this.val$finalResultSet = resultSetInternalMethods;
            }

            public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable {
                java.util.regex.Matcher matcher;
                if ("equals".equals((Object)method.getName())) {
                    return java.lang.Boolean.valueOf((boolean)args[0].equals((Object)this));
                }
                Object invocationResult = method.invoke((Object)this.val$finalResultSet, (Object[])args);
                String methodName = method.getName();
                if (!(invocationResult != null && invocationResult instanceof String || "getString".equals((Object)methodName) || "getObject".equals((Object)methodName))) {
                    if (!"getObjectStoredProc".equals((Object)methodName)) return invocationResult;
                }
                if (!(matcher = this.this$0.regexP.matcher((java.lang.CharSequence)invocationResult.toString())).matches()) return invocationResult;
                throw new SQLException((String)"value disallowed by filter");
            }
        });
    }

    @Override
    public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection) throws SQLException {
        return null;
    }

    @Override
    public boolean executeTopLevelOnly() {
        return false;
    }

    @Override
    public void destroy() {
    }
}

