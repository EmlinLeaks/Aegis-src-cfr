/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MysqlDataSourceFactory
implements ObjectFactory {
    protected static final String DATA_SOURCE_CLASS_NAME = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
    protected static final String POOL_DATA_SOURCE_CLASS_NAME = "com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource";
    protected static final String XA_DATA_SOURCE_CLASS_NAME = "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource";

    @Override
    public Object getObjectInstance(Object refObj, Name nm, Context ctx, Hashtable<?, ?> env) throws Exception {
        String password;
        String explicitUrlAsString;
        String databaseName;
        String serverName;
        Reference ref = (Reference)refObj;
        String className = ref.getClassName();
        if (className == null) return null;
        if (!className.equals((Object)DATA_SOURCE_CLASS_NAME) && !className.equals((Object)POOL_DATA_SOURCE_CLASS_NAME)) {
            if (!className.equals((Object)XA_DATA_SOURCE_CLASS_NAME)) return null;
        }
        MysqlDataSource dataSource = null;
        try {
            dataSource = (MysqlDataSource)Class.forName((String)className).newInstance();
        }
        catch (Exception ex) {
            throw new RuntimeException((String)("Unable to create DataSource of class '" + className + "', reason: " + ex.toString()));
        }
        int portNumber = 3306;
        String portNumberAsString = this.nullSafeRefAddrStringGet((String)"port", (Reference)ref);
        if (portNumberAsString != null) {
            portNumber = Integer.parseInt((String)portNumberAsString);
        }
        dataSource.setPort((int)portNumber);
        String user = this.nullSafeRefAddrStringGet((String)"user", (Reference)ref);
        if (user != null) {
            dataSource.setUser((String)user);
        }
        if ((password = this.nullSafeRefAddrStringGet((String)"password", (Reference)ref)) != null) {
            dataSource.setPassword((String)password);
        }
        if ((serverName = this.nullSafeRefAddrStringGet((String)"serverName", (Reference)ref)) != null) {
            dataSource.setServerName((String)serverName);
        }
        if ((databaseName = this.nullSafeRefAddrStringGet((String)"databaseName", (Reference)ref)) != null) {
            dataSource.setDatabaseName((String)databaseName);
        }
        if ((explicitUrlAsString = this.nullSafeRefAddrStringGet((String)"explicitUrl", (Reference)ref)) != null && Boolean.valueOf((String)explicitUrlAsString).booleanValue()) {
            dataSource.setUrl((String)this.nullSafeRefAddrStringGet((String)"url", (Reference)ref));
        }
        dataSource.setPropertiesViaRef((Reference)ref);
        return dataSource;
    }

    private String nullSafeRefAddrStringGet(String referenceName, Reference ref) {
        RefAddr refAddr = ref.get((String)referenceName);
        if (refAddr == null) return null;
        String string = (String)refAddr.getContent();
        return string;
    }
}

