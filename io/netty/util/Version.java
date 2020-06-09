/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.internal.PlatformDependent;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

public final class Version {
    private static final String PROP_VERSION = ".version";
    private static final String PROP_BUILD_DATE = ".buildDate";
    private static final String PROP_COMMIT_DATE = ".commitDate";
    private static final String PROP_SHORT_COMMIT_HASH = ".shortCommitHash";
    private static final String PROP_LONG_COMMIT_HASH = ".longCommitHash";
    private static final String PROP_REPO_STATUS = ".repoStatus";
    private final String artifactId;
    private final String artifactVersion;
    private final long buildTimeMillis;
    private final long commitTimeMillis;
    private final String shortCommitHash;
    private final String longCommitHash;
    private final String repositoryStatus;

    public static Map<String, Version> identify() {
        return Version.identify(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Map<String, Version> identify(ClassLoader classLoader) {
        Object o2;
        if (classLoader == null) {
            classLoader = PlatformDependent.getContextClassLoader();
        }
        Properties props = new Properties();
        try {
            Enumeration<URL> resources = classLoader.getResources((String)"META-INF/io.netty.versions.properties");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                InputStream in = url.openStream();
                try {
                    props.load((InputStream)in);
                }
                finally {
                    try {
                        in.close();
                    }
                    catch (Exception exception) {}
                }
            }
        }
        catch (Exception resources) {
            // empty catch block
        }
        HashSet<String> artifactIds = new HashSet<String>();
        for (Object o2 : props.keySet()) {
            String artifactId;
            String k = (String)o2;
            int dotIndex = k.indexOf((int)46);
            if (dotIndex <= 0 || !props.containsKey((Object)((artifactId = k.substring((int)0, (int)dotIndex)) + PROP_VERSION)) || !props.containsKey((Object)(artifactId + PROP_BUILD_DATE)) || !props.containsKey((Object)(artifactId + PROP_COMMIT_DATE)) || !props.containsKey((Object)(artifactId + PROP_SHORT_COMMIT_HASH)) || !props.containsKey((Object)(artifactId + PROP_LONG_COMMIT_HASH)) || !props.containsKey((Object)(artifactId + PROP_REPO_STATUS))) continue;
            artifactIds.add(artifactId);
        }
        TreeMap<String, Version> versions = new TreeMap<String, Version>();
        o2 = artifactIds.iterator();
        while (o2.hasNext()) {
            String artifactId = (String)o2.next();
            versions.put((String)artifactId, (Version)new Version((String)artifactId, (String)props.getProperty((String)(artifactId + PROP_VERSION)), (long)Version.parseIso8601((String)props.getProperty((String)(artifactId + PROP_BUILD_DATE))), (long)Version.parseIso8601((String)props.getProperty((String)(artifactId + PROP_COMMIT_DATE))), (String)props.getProperty((String)(artifactId + PROP_SHORT_COMMIT_HASH)), (String)props.getProperty((String)(artifactId + PROP_LONG_COMMIT_HASH)), (String)props.getProperty((String)(artifactId + PROP_REPO_STATUS))));
        }
        return versions;
    }

    private static long parseIso8601(String value) {
        try {
            return new SimpleDateFormat((String)"yyyy-MM-dd HH:mm:ss Z").parse((String)value).getTime();
        }
        catch (ParseException ignored) {
            return 0L;
        }
    }

    public static void main(String[] args) {
        Iterator<Version> iterator = Version.identify().values().iterator();
        while (iterator.hasNext()) {
            Version v = iterator.next();
            System.err.println((Object)v);
        }
    }

    private Version(String artifactId, String artifactVersion, long buildTimeMillis, long commitTimeMillis, String shortCommitHash, String longCommitHash, String repositoryStatus) {
        this.artifactId = artifactId;
        this.artifactVersion = artifactVersion;
        this.buildTimeMillis = buildTimeMillis;
        this.commitTimeMillis = commitTimeMillis;
        this.shortCommitHash = shortCommitHash;
        this.longCommitHash = longCommitHash;
        this.repositoryStatus = repositoryStatus;
    }

    public String artifactId() {
        return this.artifactId;
    }

    public String artifactVersion() {
        return this.artifactVersion;
    }

    public long buildTimeMillis() {
        return this.buildTimeMillis;
    }

    public long commitTimeMillis() {
        return this.commitTimeMillis;
    }

    public String shortCommitHash() {
        return this.shortCommitHash;
    }

    public String longCommitHash() {
        return this.longCommitHash;
    }

    public String repositoryStatus() {
        return this.repositoryStatus;
    }

    public String toString() {
        String string;
        if ("clean".equals((Object)this.repositoryStatus)) {
            string = "";
            return this.artifactId + '-' + this.artifactVersion + '.' + this.shortCommitHash + string;
        }
        string = " (repository: " + this.repositoryStatus + ')';
        return this.artifactId + '-' + this.artifactVersion + '.' + this.shortCommitHash + string;
    }
}

