/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.util;

public class PlatformFeatureDetector {
    private Boolean isRunningOnAndroid = null;

    public boolean isRunningOnAndroid() {
        if (this.isRunningOnAndroid != null) return this.isRunningOnAndroid.booleanValue();
        String name = System.getProperty((String)"java.runtime.name");
        this.isRunningOnAndroid = Boolean.valueOf((boolean)(name != null && name.startsWith((String)"Android Runtime")));
        return this.isRunningOnAndroid.booleanValue();
    }
}

