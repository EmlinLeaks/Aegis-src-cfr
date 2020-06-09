/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.util;

import com.mysql.jdbc.NonRegisteringDriver;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class VersionFSHierarchyMaker {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            VersionFSHierarchyMaker.usage();
            System.exit((int)1);
        }
        String jdbcUrl = null;
        String jvmVersion = VersionFSHierarchyMaker.removeWhitespaceChars((String)System.getProperty((String)"java.version"));
        String jvmVendor = VersionFSHierarchyMaker.removeWhitespaceChars((String)System.getProperty((String)"java.vendor"));
        String osName = VersionFSHierarchyMaker.removeWhitespaceChars((String)System.getProperty((String)"os.name"));
        String osArch = VersionFSHierarchyMaker.removeWhitespaceChars((String)System.getProperty((String)"os.arch"));
        String osVersion = VersionFSHierarchyMaker.removeWhitespaceChars((String)System.getProperty((String)"os.version"));
        jdbcUrl = System.getProperty((String)"com.mysql.jdbc.testsuite.url");
        String mysqlVersion = "MySQL" + args[2] + "_";
        try {
            Properties props = new Properties();
            props.setProperty((String)"allowPublicKeyRetrieval", (String)"true");
            Connection conn = new NonRegisteringDriver().connect((String)jdbcUrl, (Properties)props);
            ResultSet rs = conn.createStatement().executeQuery((String)"SELECT VERSION()");
            rs.next();
            mysqlVersion = mysqlVersion + VersionFSHierarchyMaker.removeWhitespaceChars((String)rs.getString((int)1));
        }
        catch (Throwable t) {
            mysqlVersion = mysqlVersion + "no-server-running-on-" + VersionFSHierarchyMaker.removeWhitespaceChars((String)jdbcUrl);
        }
        String jvmSubdirName = jvmVendor + "-" + jvmVersion;
        String osSubdirName = osName + "-" + osArch + "-" + osVersion;
        File baseDir = new File((String)args[0]);
        File mysqlVersionDir = new File((File)baseDir, (String)mysqlVersion);
        File osVersionDir = new File((File)mysqlVersionDir, (String)osSubdirName);
        File jvmVersionDir = new File((File)osVersionDir, (String)jvmSubdirName);
        jvmVersionDir.mkdirs();
        FileOutputStream pathOut = null;
        try {
            String propsOutputPath = args[1];
            pathOut = new FileOutputStream((String)propsOutputPath);
            String baseDirStr = baseDir.getAbsolutePath();
            String jvmVersionDirStr = jvmVersionDir.getAbsolutePath();
            if (jvmVersionDirStr.startsWith((String)baseDirStr)) {
                jvmVersionDirStr = jvmVersionDirStr.substring((int)(baseDirStr.length() + 1));
            }
            pathOut.write((byte[])jvmVersionDirStr.getBytes());
            Object var19_19 = null;
            if (pathOut == null) return;
            pathOut.flush();
            pathOut.close();
            return;
        }
        catch (Throwable throwable) {
            Object var19_20 = null;
            if (pathOut == null) throw throwable;
            pathOut.flush();
            pathOut.close();
            throw throwable;
        }
    }

    public static String removeWhitespaceChars(String input) {
        if (input == null) {
            return input;
        }
        int strLen = input.length();
        StringBuilder output = new StringBuilder((int)strLen);
        int i = 0;
        while (i < strLen) {
            char c = input.charAt((int)i);
            if (!Character.isDigit((char)c) && !Character.isLetter((char)c)) {
                if (Character.isWhitespace((char)c)) {
                    output.append((String)"_");
                } else {
                    output.append((String)".");
                }
            } else {
                output.append((char)c);
            }
            ++i;
        }
        return output.toString();
    }

    private static void usage() {
        System.err.println((String)"Creates a fs hierarchy representing MySQL version, OS version and JVM version.");
        System.err.println((String)"Stores the full path as 'outputDirectory' property in file 'directoryPropPath'");
        System.err.println();
        System.err.println((String)"Usage: java VersionFSHierarchyMaker baseDirectory directoryPropPath jdbcUrlIter");
    }
}

