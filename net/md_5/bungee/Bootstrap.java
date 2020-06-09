/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee;

import java.io.PrintStream;
import net.md_5.bungee.BungeeCordLauncher;

public class Bootstrap {
    public static void main(String[] args) throws Exception {
        if ((double)Float.parseFloat((String)System.getProperty((String)"java.class.version")) < 52.0) {
            System.err.println((String)"*** ERROR *** BungeeCord requires Java 8 or above to function! Please download and install it!");
            System.out.println((String)"You can check your Java version with the command: java -version");
            return;
        }
        BungeeCordLauncher.main((String[])args);
    }
}

