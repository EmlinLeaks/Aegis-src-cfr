/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.util;

import com.mysql.jdbc.StringUtils;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class ServerController {
    public static final String BASEDIR_KEY = "basedir";
    public static final String DATADIR_KEY = "datadir";
    public static final String DEFAULTS_FILE_KEY = "defaults-file";
    public static final String EXECUTABLE_NAME_KEY = "executable";
    public static final String EXECUTABLE_PATH_KEY = "executablePath";
    private Process serverProcess = null;
    private Properties serverProps = null;
    private Properties systemProps = null;

    public ServerController(String baseDir) {
        this.setBaseDir((String)baseDir);
    }

    public ServerController(String basedir, String datadir) {
    }

    public void setBaseDir(String baseDir) {
        this.getServerProps().setProperty((String)BASEDIR_KEY, (String)baseDir);
    }

    public void setDataDir(String dataDir) {
        this.getServerProps().setProperty((String)DATADIR_KEY, (String)dataDir);
    }

    public Process start() throws IOException {
        if (this.serverProcess != null) {
            throw new IllegalArgumentException((String)"Server already started");
        }
        this.serverProcess = Runtime.getRuntime().exec((String)this.getCommandLine());
        return this.serverProcess;
    }

    public void stop(boolean forceIfNecessary) throws IOException {
        if (this.serverProcess == null) return;
        String basedir = this.getServerProps().getProperty((String)BASEDIR_KEY);
        StringBuilder pathBuf = new StringBuilder((String)basedir);
        if (!basedir.endsWith((String)File.separator)) {
            pathBuf.append((String)File.separator);
        }
        pathBuf.append((String)"bin");
        pathBuf.append((String)File.separator);
        pathBuf.append((String)"mysqladmin shutdown");
        System.out.println((String)pathBuf.toString());
        Process mysqladmin = Runtime.getRuntime().exec((String)pathBuf.toString());
        int exitStatus = -1;
        try {
            exitStatus = mysqladmin.waitFor();
        }
        catch (InterruptedException ie) {
            // empty catch block
        }
        if (exitStatus == 0) return;
        if (!forceIfNecessary) return;
        this.forceStop();
    }

    public void forceStop() {
        if (this.serverProcess == null) return;
        this.serverProcess.destroy();
        this.serverProcess = null;
    }

    public synchronized Properties getServerProps() {
        if (this.serverProps != null) return this.serverProps;
        this.serverProps = new Properties();
        return this.serverProps;
    }

    private String getCommandLine() {
        StringBuilder commandLine = new StringBuilder((String)this.getFullExecutablePath());
        commandLine.append((String)this.buildOptionalCommandLine());
        return commandLine.toString();
    }

    private String getFullExecutablePath() {
        StringBuilder pathBuf = new StringBuilder();
        String optionalExecutablePath = this.getServerProps().getProperty((String)EXECUTABLE_PATH_KEY);
        if (optionalExecutablePath == null) {
            String basedir = this.getServerProps().getProperty((String)BASEDIR_KEY);
            pathBuf.append((String)basedir);
            if (!basedir.endsWith((String)File.separator)) {
                pathBuf.append((char)File.separatorChar);
            }
            if (this.runningOnWindows()) {
                pathBuf.append((String)"bin");
            } else {
                pathBuf.append((String)"libexec");
            }
            pathBuf.append((char)File.separatorChar);
        } else {
            pathBuf.append((String)optionalExecutablePath);
            if (!optionalExecutablePath.endsWith((String)File.separator)) {
                pathBuf.append((char)File.separatorChar);
            }
        }
        String executableName = this.getServerProps().getProperty((String)EXECUTABLE_NAME_KEY, (String)"mysqld");
        pathBuf.append((String)executableName);
        return pathBuf.toString();
    }

    private String buildOptionalCommandLine() {
        StringBuilder commandLineBuf = new StringBuilder();
        if (this.serverProps == null) return commandLineBuf.toString();
        Iterator<K> iter = this.serverProps.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String)iter.next();
            String value = this.serverProps.getProperty((String)key);
            if (this.isNonCommandLineArgument((String)key)) continue;
            if (value != null && value.length() > 0) {
                commandLineBuf.append((String)" \"");
                commandLineBuf.append((String)"--");
                commandLineBuf.append((String)key);
                commandLineBuf.append((String)"=");
                commandLineBuf.append((String)value);
                commandLineBuf.append((String)"\"");
                continue;
            }
            commandLineBuf.append((String)" --");
            commandLineBuf.append((String)key);
        }
        return commandLineBuf.toString();
    }

    private boolean isNonCommandLineArgument(String propName) {
        if (propName.equals((Object)EXECUTABLE_NAME_KEY)) return true;
        if (propName.equals((Object)EXECUTABLE_PATH_KEY)) return true;
        return false;
    }

    private synchronized Properties getSystemProperties() {
        if (this.systemProps != null) return this.systemProps;
        this.systemProps = System.getProperties();
        return this.systemProps;
    }

    private boolean runningOnWindows() {
        if (StringUtils.indexOfIgnoreCase((String)this.getSystemProperties().getProperty((String)"os.name"), (String)"WINDOWS") == -1) return false;
        return true;
    }
}

