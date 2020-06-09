/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.ConnectionPropertiesImpl;
import com.mysql.jdbc.ConnectionPropertiesTransform;
import com.mysql.jdbc.FailoverConnectionProxy;
import com.mysql.jdbc.LoadBalancedConnectionProxy;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.ReplicationConnectionProxy;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.Util;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

public class NonRegisteringDriver
implements Driver {
    private static final String ALLOWED_QUOTES = "\"'";
    private static final String REPLICATION_URL_PREFIX = "jdbc:mysql:replication://";
    private static final String URL_PREFIX = "jdbc:mysql://";
    private static final String MXJ_URL_PREFIX = "jdbc:mysql:mxj://";
    public static final String LOADBALANCE_URL_PREFIX = "jdbc:mysql:loadbalance://";
    public static final String PLATFORM = NonRegisteringDriver.getPlatform();
    public static final String OS = NonRegisteringDriver.getOSName();
    public static final String LICENSE = "GPL";
    public static final String RUNTIME_VENDOR = System.getProperty((String)"java.vendor");
    public static final String RUNTIME_VERSION = System.getProperty((String)"java.version");
    public static final String VERSION = "5.1.48";
    public static final String NAME = "MySQL Connector Java";
    public static final String DBNAME_PROPERTY_KEY = "DBNAME";
    public static final boolean DEBUG = false;
    public static final int HOST_NAME_INDEX = 0;
    public static final String HOST_PROPERTY_KEY = "HOST";
    public static final String NUM_HOSTS_PROPERTY_KEY = "NUM_HOSTS";
    public static final String PASSWORD_PROPERTY_KEY = "password";
    public static final int PORT_NUMBER_INDEX = 1;
    public static final String PORT_PROPERTY_KEY = "PORT";
    public static final String PROPERTIES_TRANSFORM_KEY = "propertiesTransform";
    public static final boolean TRACE = false;
    public static final String USE_CONFIG_PROPERTY_KEY = "useConfigs";
    public static final String USER_PROPERTY_KEY = "user";
    public static final String PROTOCOL_PROPERTY_KEY = "PROTOCOL";
    public static final String PATH_PROPERTY_KEY = "PATH";

    public static String getOSName() {
        return System.getProperty((String)"os.name");
    }

    public static String getPlatform() {
        return System.getProperty((String)"os.arch");
    }

    static int getMajorVersionInternal() {
        return NonRegisteringDriver.safeIntParse((String)"5");
    }

    static int getMinorVersionInternal() {
        return NonRegisteringDriver.safeIntParse((String)"1");
    }

    protected static String[] parseHostPortPair(String hostPortPair) throws SQLException {
        String[] splitValues = new String[2];
        if (StringUtils.startsWithIgnoreCaseAndWs((String)hostPortPair, (String)"address=")) {
            splitValues[0] = hostPortPair.trim();
            splitValues[1] = null;
            return splitValues;
        }
        int portIndex = hostPortPair.indexOf((String)":");
        String hostname = null;
        if (portIndex != -1) {
            if (portIndex + 1 >= hostPortPair.length()) throw SQLError.createSQLException((String)Messages.getString((String)"NonRegisteringDriver.37"), (String)"01S00", null);
            String portAsString = hostPortPair.substring((int)(portIndex + 1));
            splitValues[0] = hostname = hostPortPair.substring((int)0, (int)portIndex);
            splitValues[1] = portAsString;
            return splitValues;
        }
        splitValues[0] = hostPortPair;
        splitValues[1] = null;
        return splitValues;
    }

    private static int safeIntParse(String intAsString) {
        try {
            return Integer.parseInt((String)intAsString);
        }
        catch (NumberFormatException nfe) {
            return 0;
        }
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if (url == null) {
            throw SQLError.createSQLException((String)Messages.getString((String)"NonRegisteringDriver.1"), (String)"08001", null);
        }
        if (this.parseURL((String)url, null) == null) return false;
        return true;
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (url == null) {
            throw SQLError.createSQLException((String)Messages.getString((String)"NonRegisteringDriver.1"), (String)"08001", null);
        }
        if (StringUtils.startsWithIgnoreCase((String)url, (String)LOADBALANCE_URL_PREFIX)) {
            return this.connectLoadBalanced((String)url, (Properties)info);
        }
        if (StringUtils.startsWithIgnoreCase((String)url, (String)REPLICATION_URL_PREFIX)) {
            return this.connectReplicationConnection((String)url, (Properties)info);
        }
        Properties props = null;
        props = this.parseURL((String)url, (Properties)info);
        if (props == null) {
            return null;
        }
        if (!"1".equals((Object)props.getProperty((String)NUM_HOSTS_PROPERTY_KEY))) {
            return this.connectFailover((String)url, (Properties)info);
        }
        try {
            return ConnectionImpl.getInstance((String)this.host((Properties)props), (int)this.port((Properties)props), (Properties)props, (String)this.database((Properties)props), (String)url);
        }
        catch (SQLException sqlEx) {
            throw sqlEx;
        }
        catch (Exception ex) {
            SQLException sqlEx = SQLError.createSQLException((String)(Messages.getString((String)"NonRegisteringDriver.17") + ex.toString() + Messages.getString((String)"NonRegisteringDriver.18")), (String)"08001", null);
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
    }

    private Connection connectLoadBalanced(String url, Properties info) throws SQLException {
        Properties parsedProps = this.parseURL((String)url, (Properties)info);
        if (parsedProps == null) {
            return null;
        }
        parsedProps.remove((Object)"roundRobinLoadBalance");
        int numHosts = Integer.parseInt((String)parsedProps.getProperty((String)NUM_HOSTS_PROPERTY_KEY));
        ArrayList<String> hostList = new ArrayList<String>();
        int i = 0;
        while (i < numHosts) {
            int index = i + 1;
            hostList.add((String)(parsedProps.getProperty((String)("HOST." + index)) + ":" + parsedProps.getProperty((String)("PORT." + index))));
            ++i;
        }
        return LoadBalancedConnectionProxy.createProxyInstance(hostList, (Properties)parsedProps);
    }

    private Connection connectFailover(String url, Properties info) throws SQLException {
        Properties parsedProps = this.parseURL((String)url, (Properties)info);
        if (parsedProps == null) {
            return null;
        }
        parsedProps.remove((Object)"roundRobinLoadBalance");
        int numHosts = Integer.parseInt((String)parsedProps.getProperty((String)NUM_HOSTS_PROPERTY_KEY));
        ArrayList<String> hostList = new ArrayList<String>();
        int i = 0;
        while (i < numHosts) {
            int index = i + 1;
            hostList.add((String)(parsedProps.getProperty((String)("HOST." + index)) + ":" + parsedProps.getProperty((String)("PORT." + index))));
            ++i;
        }
        return FailoverConnectionProxy.createProxyInstance(hostList, (Properties)parsedProps);
    }

    protected Connection connectReplicationConnection(String url, Properties info) throws SQLException {
        Properties parsedProps = this.parseURL((String)url, (Properties)info);
        if (parsedProps == null) {
            return null;
        }
        Properties masterProps = (Properties)parsedProps.clone();
        Properties slavesProps = (Properties)parsedProps.clone();
        slavesProps.setProperty((String)"com.mysql.jdbc.ReplicationConnection.isSlave", (String)"true");
        int numHosts = Integer.parseInt((String)parsedProps.getProperty((String)NUM_HOSTS_PROPERTY_KEY));
        if (numHosts < 2) {
            throw SQLError.createSQLException((String)"Must specify at least one slave host to connect to for master/slave replication load-balancing functionality", (String)"01S00", null);
        }
        ArrayList<String> slaveHostList = new ArrayList<String>();
        ArrayList<String> masterHostList = new ArrayList<String>();
        String firstHost = masterProps.getProperty((String)"HOST.1") + ":" + masterProps.getProperty((String)"PORT.1");
        boolean usesExplicitServerType = NonRegisteringDriver.isHostPropertiesList((String)firstHost);
        int i = 0;
        do {
            if (i >= numHosts) {
                slavesProps.remove((Object)NUM_HOSTS_PROPERTY_KEY);
                masterProps.remove((Object)NUM_HOSTS_PROPERTY_KEY);
                masterProps.remove((Object)HOST_PROPERTY_KEY);
                masterProps.remove((Object)PORT_PROPERTY_KEY);
                slavesProps.remove((Object)HOST_PROPERTY_KEY);
                slavesProps.remove((Object)PORT_PROPERTY_KEY);
                return ReplicationConnectionProxy.createProxyInstance(masterHostList, (Properties)masterProps, slaveHostList, (Properties)slavesProps);
            }
            int index = i + 1;
            masterProps.remove((Object)("HOST." + index));
            masterProps.remove((Object)("PORT." + index));
            slavesProps.remove((Object)("HOST." + index));
            slavesProps.remove((Object)("PORT." + index));
            String host = parsedProps.getProperty((String)("HOST." + index));
            String port = parsedProps.getProperty((String)("PORT." + index));
            if (usesExplicitServerType) {
                if (this.isHostMaster((String)host)) {
                    masterHostList.add((String)host);
                } else {
                    slaveHostList.add((String)host);
                }
            } else if (i == 0) {
                masterHostList.add((String)(host + ":" + port));
            } else {
                slaveHostList.add((String)(host + ":" + port));
            }
            ++i;
        } while (true);
    }

    private boolean isHostMaster(String host) {
        if (!NonRegisteringDriver.isHostPropertiesList((String)host)) return false;
        Properties hostSpecificProps = NonRegisteringDriver.expandHostKeyValues((String)host);
        if (!hostSpecificProps.containsKey((Object)"type")) return false;
        if (!"master".equalsIgnoreCase((String)hostSpecificProps.get((Object)"type").toString())) return false;
        return true;
    }

    public String database(Properties props) {
        return props.getProperty((String)DBNAME_PROPERTY_KEY);
    }

    @Override
    public int getMajorVersion() {
        return NonRegisteringDriver.getMajorVersionInternal();
    }

    @Override
    public int getMinorVersion() {
        return NonRegisteringDriver.getMinorVersionInternal();
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        if (info == null) {
            info = new Properties();
        }
        if (url != null && url.startsWith((String)URL_PREFIX)) {
            info = this.parseURL((String)url, (Properties)info);
        }
        DriverPropertyInfo hostProp = new DriverPropertyInfo((String)HOST_PROPERTY_KEY, (String)info.getProperty((String)HOST_PROPERTY_KEY));
        hostProp.required = true;
        hostProp.description = Messages.getString((String)"NonRegisteringDriver.3");
        DriverPropertyInfo portProp = new DriverPropertyInfo((String)PORT_PROPERTY_KEY, (String)info.getProperty((String)PORT_PROPERTY_KEY, (String)"3306"));
        portProp.required = false;
        portProp.description = Messages.getString((String)"NonRegisteringDriver.7");
        DriverPropertyInfo dbProp = new DriverPropertyInfo((String)DBNAME_PROPERTY_KEY, (String)info.getProperty((String)DBNAME_PROPERTY_KEY));
        dbProp.required = false;
        dbProp.description = "Database name";
        DriverPropertyInfo userProp = new DriverPropertyInfo((String)USER_PROPERTY_KEY, (String)info.getProperty((String)USER_PROPERTY_KEY));
        userProp.required = true;
        userProp.description = Messages.getString((String)"NonRegisteringDriver.13");
        DriverPropertyInfo passwordProp = new DriverPropertyInfo((String)PASSWORD_PROPERTY_KEY, (String)info.getProperty((String)PASSWORD_PROPERTY_KEY));
        passwordProp.required = true;
        passwordProp.description = Messages.getString((String)"NonRegisteringDriver.16");
        DriverPropertyInfo[] dpi = ConnectionPropertiesImpl.exposeAsDriverPropertyInfo((Properties)info, (int)5);
        dpi[0] = hostProp;
        dpi[1] = portProp;
        dpi[2] = dbProp;
        dpi[3] = userProp;
        dpi[4] = passwordProp;
        return dpi;
    }

    public String host(Properties props) {
        return props.getProperty((String)HOST_PROPERTY_KEY, (String)"localhost");
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    public Properties parseURL(String url, Properties defaults) throws SQLException {
        Properties urlProps;
        int index;
        Properties properties = urlProps = defaults != null ? new Properties((Properties)defaults) : new Properties();
        if (url == null) {
            return null;
        }
        if (!(StringUtils.startsWithIgnoreCase((String)url, (String)URL_PREFIX) || StringUtils.startsWithIgnoreCase((String)url, (String)MXJ_URL_PREFIX) || StringUtils.startsWithIgnoreCase((String)url, (String)LOADBALANCE_URL_PREFIX) || StringUtils.startsWithIgnoreCase((String)url, (String)REPLICATION_URL_PREFIX))) {
            return null;
        }
        int beginningOfSlashes = url.indexOf((String)"//");
        if (StringUtils.startsWithIgnoreCase((String)url, (String)MXJ_URL_PREFIX)) {
            urlProps.setProperty((String)"socketFactory", (String)"com.mysql.management.driverlaunched.ServerLauncherSocketFactory");
        }
        if ((index = url.indexOf((String)"?")) != -1) {
            String paramString = url.substring((int)(index + 1), (int)url.length());
            url = url.substring((int)0, (int)index);
            StringTokenizer queryParams = new StringTokenizer((String)paramString, (String)"&");
            while (queryParams.hasMoreTokens()) {
                String parameterValuePair = queryParams.nextToken();
                int indexOfEquals = StringUtils.indexOfIgnoreCase((int)0, (String)parameterValuePair, (String)"=");
                String parameter = null;
                String value = null;
                if (indexOfEquals != -1) {
                    parameter = parameterValuePair.substring((int)0, (int)indexOfEquals);
                    if (indexOfEquals + 1 < parameterValuePair.length()) {
                        value = parameterValuePair.substring((int)(indexOfEquals + 1));
                    }
                }
                if (value == null || value.length() <= 0 || parameter == null || parameter.length() <= 0) continue;
                try {
                    urlProps.setProperty((String)parameter, (String)URLDecoder.decode((String)value, (String)"UTF-8"));
                }
                catch (UnsupportedEncodingException badEncoding) {
                    urlProps.setProperty((String)parameter, (String)URLDecoder.decode((String)value));
                }
                catch (NoSuchMethodError nsme) {
                    urlProps.setProperty((String)parameter, (String)URLDecoder.decode((String)value));
                }
            }
        }
        url = url.substring((int)(beginningOfSlashes + 2));
        String hostStuff = null;
        int slashIndex = StringUtils.indexOfIgnoreCase((int)0, (String)url, (String)"/", (String)ALLOWED_QUOTES, (String)ALLOWED_QUOTES, StringUtils.SEARCH_MODE__ALL);
        if (slashIndex != -1) {
            hostStuff = url.substring((int)0, (int)slashIndex);
            if (slashIndex + 1 < url.length()) {
                urlProps.put(DBNAME_PROPERTY_KEY, url.substring((int)(slashIndex + 1), (int)url.length()));
            }
        } else {
            hostStuff = url;
        }
        int numHosts = 0;
        if (hostStuff == null || hostStuff.trim().length() <= 0) {
            numHosts = 1;
            urlProps.setProperty((String)"HOST.1", (String)"localhost");
            urlProps.setProperty((String)"PORT.1", (String)"3306");
        } else {
            List<String> hosts = StringUtils.split((String)hostStuff, (String)",", (String)ALLOWED_QUOTES, (String)ALLOWED_QUOTES, (boolean)false);
            for (String hostAndPort : hosts) {
                ++numHosts;
                String[] hostPortPair = NonRegisteringDriver.parseHostPortPair((String)hostAndPort);
                if (hostPortPair[0] != null && hostPortPair[0].trim().length() > 0) {
                    urlProps.setProperty((String)("HOST." + numHosts), (String)hostPortPair[0]);
                } else {
                    urlProps.setProperty((String)("HOST." + numHosts), (String)"localhost");
                }
                if (hostPortPair[1] != null) {
                    urlProps.setProperty((String)("PORT." + numHosts), (String)hostPortPair[1]);
                    continue;
                }
                urlProps.setProperty((String)("PORT." + numHosts), (String)"3306");
            }
        }
        urlProps.setProperty((String)NUM_HOSTS_PROPERTY_KEY, (String)String.valueOf((int)numHosts));
        urlProps.setProperty((String)HOST_PROPERTY_KEY, (String)urlProps.getProperty((String)"HOST.1"));
        urlProps.setProperty((String)PORT_PROPERTY_KEY, (String)urlProps.getProperty((String)"PORT.1"));
        String propertiesTransformClassName = urlProps.getProperty((String)PROPERTIES_TRANSFORM_KEY);
        if (propertiesTransformClassName != null) {
            try {
                ConnectionPropertiesTransform propTransformer = (ConnectionPropertiesTransform)Class.forName((String)propertiesTransformClassName).newInstance();
                urlProps = propTransformer.transformProperties((Properties)urlProps);
            }
            catch (InstantiationException e) {
                throw SQLError.createSQLException((String)("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString()), (String)"01S00", null);
            }
            catch (IllegalAccessException e) {
                throw SQLError.createSQLException((String)("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString()), (String)"01S00", null);
            }
            catch (ClassNotFoundException e) {
                throw SQLError.createSQLException((String)("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString()), (String)"01S00", null);
            }
        }
        if (Util.isColdFusion() && urlProps.getProperty((String)"autoConfigureForColdFusion", (String)"true").equalsIgnoreCase((String)"true")) {
            String configs = urlProps.getProperty((String)USE_CONFIG_PROPERTY_KEY);
            StringBuilder newConfigs = new StringBuilder();
            if (configs != null) {
                newConfigs.append((String)configs);
                newConfigs.append((String)",");
            }
            newConfigs.append((String)"coldFusion");
            urlProps.setProperty((String)USE_CONFIG_PROPERTY_KEY, (String)newConfigs.toString());
        }
        String configNames = null;
        if (defaults != null) {
            configNames = defaults.getProperty((String)USE_CONFIG_PROPERTY_KEY);
        }
        if (configNames == null) {
            configNames = urlProps.getProperty((String)USE_CONFIG_PROPERTY_KEY);
        }
        if (configNames != null) {
            List<String> splitNames = StringUtils.split((String)configNames, (String)",", (boolean)true);
            Properties configProps = new Properties();
            for (String configName : splitNames) {
                try {
                    InputStream configAsStream = this.getClass().getResourceAsStream((String)("configs/" + configName + ".properties"));
                    if (configAsStream == null) {
                        throw SQLError.createSQLException((String)("Can't find configuration template named '" + configName + "'"), (String)"01S00", null);
                    }
                    configProps.load((InputStream)configAsStream);
                }
                catch (IOException ioEx) {
                    SQLException sqlEx = SQLError.createSQLException((String)("Unable to load configuration template '" + configName + "' due to underlying IOException: " + ioEx), (String)"01S00", null);
                    sqlEx.initCause((Throwable)ioEx);
                    throw sqlEx;
                }
            }
            Iterator<K> propsIter = urlProps.keySet().iterator();
            while (propsIter.hasNext()) {
                String key = propsIter.next().toString();
                String property = urlProps.getProperty((String)key);
                configProps.setProperty((String)key, (String)property);
            }
            urlProps = configProps;
        }
        if (defaults == null) return urlProps;
        Iterator<K> propsIter = defaults.keySet().iterator();
        while (propsIter.hasNext()) {
            String key = propsIter.next().toString();
            if (key.equals((Object)NUM_HOSTS_PROPERTY_KEY)) continue;
            String property = defaults.getProperty((String)key);
            urlProps.setProperty((String)key, (String)property);
        }
        return urlProps;
    }

    public int port(Properties props) {
        return Integer.parseInt((String)props.getProperty((String)PORT_PROPERTY_KEY, (String)"3306"));
    }

    public String property(String name, Properties props) {
        return props.getProperty((String)name);
    }

    public static Properties expandHostKeyValues(String host) {
        Properties hostProps = new Properties();
        if (!NonRegisteringDriver.isHostPropertiesList((String)host)) return hostProps;
        host = host.substring((int)("address=".length() + 1));
        List<String> hostPropsList = StringUtils.split((String)host, (String)")", (String)"'\"", (String)"'\"", (boolean)true);
        Iterator<String> i$ = hostPropsList.iterator();
        while (i$.hasNext()) {
            String value;
            String propDef = i$.next();
            if (propDef.startsWith((String)"(")) {
                propDef = propDef.substring((int)1);
            }
            List<String> kvp = StringUtils.split((String)propDef, (String)"=", (String)"'\"", (String)"'\"", (boolean)true);
            String key = kvp.get((int)0);
            String string = value = kvp.size() > 1 ? kvp.get((int)1) : null;
            if (value != null && (value.startsWith((String)"\"") && value.endsWith((String)"\"") || value.startsWith((String)"'") && value.endsWith((String)"'"))) {
                value = value.substring((int)1, (int)(value.length() - 1));
            }
            if (value == null) continue;
            if (HOST_PROPERTY_KEY.equalsIgnoreCase((String)key) || DBNAME_PROPERTY_KEY.equalsIgnoreCase((String)key) || PORT_PROPERTY_KEY.equalsIgnoreCase((String)key) || PROTOCOL_PROPERTY_KEY.equalsIgnoreCase((String)key) || PATH_PROPERTY_KEY.equalsIgnoreCase((String)key)) {
                key = key.toUpperCase((Locale)Locale.ENGLISH);
            } else if (USER_PROPERTY_KEY.equalsIgnoreCase((String)key) || PASSWORD_PROPERTY_KEY.equalsIgnoreCase((String)key)) {
                key = key.toLowerCase((Locale)Locale.ENGLISH);
            }
            hostProps.setProperty((String)key, (String)value);
        }
        return hostProps;
    }

    public static boolean isHostPropertiesList(String host) {
        if (host == null) return false;
        if (!StringUtils.startsWithIgnoreCase((String)host, (String)"address=")) return false;
        return true;
    }

    static {
        try {
            Class.forName((String)AbandonedConnectionCleanupThread.class.getName());
            return;
        }
        catch (ClassNotFoundException e) {
            // empty catch block
        }
    }
}

