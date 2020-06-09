/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.CommunicationsException;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.LoadBalanceExceptionChecker;
import com.mysql.jdbc.StringUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class StandardLoadBalanceExceptionChecker
implements LoadBalanceExceptionChecker {
    private List<String> sqlStateList;
    private List<Class<?>> sqlExClassList;

    @Override
    public boolean shouldExceptionTriggerFailover(SQLException ex) {
        Iterator<Object> i;
        String sqlState = ex.getSQLState();
        if (sqlState != null) {
            if (sqlState.startsWith((String)"08")) {
                return true;
            }
            if (this.sqlStateList != null) {
                i = this.sqlStateList.iterator();
                while (i.hasNext()) {
                    if (!sqlState.startsWith((String)((String)i.next()).toString())) continue;
                    return true;
                }
            }
        }
        if (ex instanceof CommunicationsException) {
            return true;
        }
        if (this.sqlExClassList == null) return false;
        i = this.sqlExClassList.iterator();
        do {
            if (!i.hasNext()) return false;
        } while (!((Class)i.next()).isInstance((Object)ex));
        return true;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(Connection conn, Properties props) throws SQLException {
        this.configureSQLStateList((String)props.getProperty((String)"loadBalanceSQLStateFailover", null));
        this.configureSQLExceptionSubclassList((String)props.getProperty((String)"loadBalanceSQLExceptionSubclassFailover", null));
    }

    private void configureSQLStateList(String sqlStates) {
        if (sqlStates == null) return;
        if ("".equals((Object)sqlStates)) {
            return;
        }
        List<String> states = StringUtils.split((String)sqlStates, (String)",", (boolean)true);
        ArrayList<String> newStates = new ArrayList<String>();
        Iterator<String> i$ = states.iterator();
        do {
            if (!i$.hasNext()) {
                if (newStates.size() <= 0) return;
                this.sqlStateList = newStates;
                return;
            }
            String state = i$.next();
            if (state.length() <= 0) continue;
            newStates.add((String)state);
        } while (true);
    }

    private void configureSQLExceptionSubclassList(String sqlExClasses) {
        if (sqlExClasses == null) return;
        if ("".equals((Object)sqlExClasses)) {
            return;
        }
        List<String> classes = StringUtils.split((String)sqlExClasses, (String)",", (boolean)true);
        ArrayList<Class<?>> newClasses = new ArrayList<Class<?>>();
        Iterator<String> i$ = classes.iterator();
        do {
            if (!i$.hasNext()) {
                if (newClasses.size() <= 0) return;
                this.sqlExClassList = newClasses;
                return;
            }
            String exClass = i$.next();
            try {
                Class<?> c = Class.forName((String)exClass);
                newClasses.add(c);
            }
            catch (Exception e) {
            }
        } while (true);
    }
}

