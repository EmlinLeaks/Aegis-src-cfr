/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.scheduler;

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public interface TaskScheduler {
    public void cancel(int var1);

    public void cancel(ScheduledTask var1);

    public int cancel(Plugin var1);

    public ScheduledTask runAsync(Plugin var1, Runnable var2);

    public ScheduledTask schedule(Plugin var1, Runnable var2, long var3, TimeUnit var5);

    public ScheduledTask schedule(Plugin var1, Runnable var2, long var3, long var5, TimeUnit var7);

    public Unsafe unsafe();
}

