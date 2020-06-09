/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.profiler;

import com.mysql.jdbc.Extension;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.profiler.ProfilerEvent;

public interface ProfilerEventHandler
extends Extension {
    public void consumeEvent(ProfilerEvent var1);

    public void processEvent(byte var1, MySQLConnection var2, Statement var3, ResultSetInternalMethods var4, long var5, Throwable var7, String var8);
}

