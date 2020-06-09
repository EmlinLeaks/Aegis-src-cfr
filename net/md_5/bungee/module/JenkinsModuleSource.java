/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.module;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.module.ModuleSource;
import net.md_5.bungee.module.ModuleSpec;
import net.md_5.bungee.module.ModuleVersion;

public class JenkinsModuleSource
implements ModuleSource {
    @Override
    public void retrieve(ModuleSpec module, ModuleVersion version) {
        System.out.println((String)("Attempting to Jenkins download module " + module.getName() + " v" + version.getBuild()));
        try {
            URL website = new URL((String)("https://ci.md-5.net/job/BungeeCord/" + version.getBuild() + "/artifact/module/" + module.getName().replace((char)'_', (char)'-') + "/target/" + module.getName() + ".jar"));
            URLConnection con = website.openConnection();
            con.setConnectTimeout((int)15000);
            con.setReadTimeout((int)15000);
            Files.write((byte[])ByteStreams.toByteArray((InputStream)con.getInputStream()), (File)module.getFile());
            System.out.println((String)"Download complete");
            return;
        }
        catch (IOException ex) {
            System.out.println((String)("Failed to download: " + Util.exception((Throwable)ex)));
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof JenkinsModuleSource)) {
            return false;
        }
        JenkinsModuleSource other = (JenkinsModuleSource)o;
        if (other.canEqual((Object)this)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof JenkinsModuleSource;
    }

    public int hashCode() {
        return 1;
    }

    public String toString() {
        return "JenkinsModuleSource()";
    }
}

