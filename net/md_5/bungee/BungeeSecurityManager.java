/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.security.AccessControlException;
import java.security.Permission;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.GroupedThreadFactory;

public class BungeeSecurityManager
extends SecurityManager {
    private static final boolean ENFORCE = false;
    private final Set<String> seen = new HashSet<String>();

    private void checkRestricted(String text) {
        int i = 2;
        Class[] context = this.getClassContext();
        if (i >= context.length) return;
        ClassLoader loader = context[i].getClassLoader();
        if (loader == ClassLoader.getSystemClassLoader()) return;
        if (loader == null) {
            return;
        }
        AccessControlException ex = new AccessControlException((String)("Plugin violation: " + text));
        StringWriter stack = new StringWriter();
        ex.printStackTrace((PrintWriter)new PrintWriter((Writer)stack));
        if (!this.seen.add((String)stack.toString())) return;
        ProxyServer.getInstance().getLogger().log((Level)Level.WARNING, (String)("Plugin performed restricted action, please inform them to use proper API methods: " + text), (Throwable)ex);
    }

    @Override
    public void checkExit(int status) {
        this.checkRestricted((String)"Exit: Cannot close VM");
    }

    @Override
    public void checkAccess(ThreadGroup g) {
        if (g instanceof GroupedThreadFactory.BungeeGroup) return;
        this.checkRestricted((String)"Illegal thread group access");
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
        this.checkPermission((Permission)perm);
    }

    @Override
    public void checkPermission(Permission perm) {
        String string = perm.getName();
        int n = -1;
        switch (string.hashCode()) {
            case -1282477173: {
                if (!string.equals((Object)"setSecurityManager")) break;
                n = 0;
            }
        }
        switch (n) {
            case 0: {
                throw new AccessControlException((String)"Restricted Action", (Permission)perm);
            }
        }
    }
}

