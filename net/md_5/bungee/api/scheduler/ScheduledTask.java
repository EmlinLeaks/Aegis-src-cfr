/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.scheduler;

import net.md_5.bungee.api.plugin.Plugin;

public interface ScheduledTask {
    public int getId();

    public Plugin getOwner();

    public Runnable getTask();

    public void cancel();
}

