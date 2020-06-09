/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.scheduler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.scheduler.BungeeScheduler;

public class BungeeTask
implements Runnable,
ScheduledTask {
    private final BungeeScheduler sched;
    private final int id;
    private final Plugin owner;
    private final Runnable task;
    private final long delay;
    private final long period;
    private final AtomicBoolean running = new AtomicBoolean((boolean)true);

    public BungeeTask(BungeeScheduler sched, int id, Plugin owner, Runnable task, long delay, long period, TimeUnit unit) {
        this.sched = sched;
        this.id = id;
        this.owner = owner;
        this.task = task;
        this.delay = unit.toMillis((long)delay);
        this.period = unit.toMillis((long)period);
    }

    @Override
    public void cancel() {
        boolean wasRunning = this.running.getAndSet((boolean)false);
        if (!wasRunning) return;
        this.sched.cancel0((BungeeTask)this);
    }

    @Override
    public void run() {
        if (this.delay > 0L) {
            try {
                Thread.sleep((long)this.delay);
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        while (this.running.get()) {
            try {
                this.task.run();
            }
            catch (Throwable t) {
                ProxyServer.getInstance().getLogger().log((Level)Level.SEVERE, (String)String.format((String)"Task %s encountered an exception", (Object[])new Object[]{this}), (Throwable)t);
            }
            if (this.period <= 0L) break;
            try {
                Thread.sleep((long)this.period);
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        this.cancel();
    }

    public BungeeScheduler getSched() {
        return this.sched;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public Plugin getOwner() {
        return this.owner;
    }

    @Override
    public Runnable getTask() {
        return this.task;
    }

    public long getDelay() {
        return this.delay;
    }

    public long getPeriod() {
        return this.period;
    }

    public AtomicBoolean getRunning() {
        return this.running;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BungeeTask)) {
            return false;
        }
        BungeeTask other = (BungeeTask)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        BungeeScheduler this$sched = this.getSched();
        BungeeScheduler other$sched = other.getSched();
        if (this$sched == null ? other$sched != null : !this$sched.equals((Object)other$sched)) {
            return false;
        }
        if (this.getId() != other.getId()) {
            return false;
        }
        Plugin this$owner = this.getOwner();
        Plugin other$owner = other.getOwner();
        if (this$owner == null ? other$owner != null : !this$owner.equals((Object)other$owner)) {
            return false;
        }
        Runnable this$task = this.getTask();
        Runnable other$task = other.getTask();
        if (this$task == null ? other$task != null : !this$task.equals((Object)other$task)) {
            return false;
        }
        if (this.getDelay() != other.getDelay()) {
            return false;
        }
        if (this.getPeriod() != other.getPeriod()) {
            return false;
        }
        AtomicBoolean this$running = this.getRunning();
        AtomicBoolean other$running = other.getRunning();
        if (this$running == null) {
            if (other$running == null) return true;
            return false;
        }
        if (this$running.equals((Object)other$running)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof BungeeTask;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        BungeeScheduler $sched = this.getSched();
        result = result * 59 + ($sched == null ? 43 : $sched.hashCode());
        result = result * 59 + this.getId();
        Plugin $owner = this.getOwner();
        result = result * 59 + ($owner == null ? 43 : $owner.hashCode());
        Runnable $task = this.getTask();
        result = result * 59 + ($task == null ? 43 : $task.hashCode());
        long $delay = this.getDelay();
        result = result * 59 + (int)($delay >>> 32 ^ $delay);
        long $period = this.getPeriod();
        result = result * 59 + (int)($period >>> 32 ^ $period);
        AtomicBoolean $running = this.getRunning();
        return result * 59 + ($running == null ? 43 : $running.hashCode());
    }

    public String toString() {
        return "BungeeTask(sched=" + this.getSched() + ", id=" + this.getId() + ", owner=" + this.getOwner() + ", task=" + this.getTask() + ", delay=" + this.getDelay() + ", period=" + this.getPeriod() + ", running=" + this.getRunning() + ")";
    }
}

