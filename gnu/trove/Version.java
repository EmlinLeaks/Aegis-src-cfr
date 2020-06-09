/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove;

import java.io.PrintStream;

public class Version {
    public static void main(String[] args) {
        System.out.println((String)Version.getVersion());
    }

    public static String getVersion() {
        String version = Version.class.getPackage().getImplementationVersion();
        if (version == null) return "Sorry no Implementation-Version manifest attribute available";
        return "trove4j version " + version;
    }
}

