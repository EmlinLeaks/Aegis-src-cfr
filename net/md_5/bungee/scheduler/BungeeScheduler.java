/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.scheduler;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.scheduler.BungeeScheduler;
import net.md_5.bungee.scheduler.BungeeTask;

public class BungeeScheduler
implements TaskScheduler {
    private final Object lock = new Object();
    private final AtomicInteger taskCounter = new AtomicInteger();
    private final TIntObjectMap<BungeeTask> tasks = TCollections.synchronizedMap(new TIntObjectHashMap<V>());
    private final Multimap<Plugin, BungeeTask> tasksByPlugin = Multimaps.synchronizedMultimap(HashMultimap.<K, V>create());
    private final TaskScheduler.Unsafe unsafe = new TaskScheduler.Unsafe((BungeeScheduler)this){
        final /* synthetic */ BungeeScheduler this$0;
        {
            this.this$0 = this$0;
        }

        public ExecutorService getExecutorService(Plugin plugin) {
            return plugin.getExecutorService();
        }
    };

    @Override
    public void cancel(int id) {
        BungeeTask task = this.tasks.get((int)id);
        Preconditions.checkArgument((boolean)(task != null), (String)"No task with id %s", (int)id);
        task.cancel();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void cancel0(BungeeTask task) {
        Object object = this.lock;
        // MONITORENTER : object
        this.tasks.remove((int)task.getId());
        this.tasksByPlugin.values().remove((Object)task);
        // MONITOREXIT : object
        return;
    }

    @Override
    public void cancel(ScheduledTask task) {
        task.cancel();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int cancel(Plugin plugin) {
        HashSet<ScheduledTask> toRemove = new HashSet<ScheduledTask>();
        Iterator<E> iterator = this.lock;
        // MONITORENTER : iterator
        for (ScheduledTask task : this.tasksByPlugin.get((Plugin)plugin)) {
            toRemove.add(task);
        }
        // MONITOREXIT : iterator
        iterator = toRemove.iterator();
        while (iterator.hasNext()) {
            ScheduledTask task = (ScheduledTask)iterator.next();
            this.cancel((ScheduledTask)task);
        }
        return toRemove.size();
    }

    @Override
    public ScheduledTask runAsync(Plugin owner, Runnable task) {
        return this.schedule((Plugin)owner, (Runnable)task, (long)0L, (TimeUnit)TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledTask schedule(Plugin owner, Runnable task, long delay, TimeUnit unit) {
        return this.schedule((Plugin)owner, (Runnable)task, (long)delay, (long)0L, (TimeUnit)unit);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ScheduledTask schedule(Plugin owner, Runnable task, long delay, long period, TimeUnit unit) {
        Preconditions.checkNotNull(owner, (Object)"owner");
        Preconditions.checkNotNull(task, (Object)"task");
        BungeeTask prepared = new BungeeTask((BungeeScheduler)this, (int)this.taskCounter.getAndIncrement(), (Plugin)owner, (Runnable)task, (long)delay, (long)period, (TimeUnit)unit);
        Object object = this.lock;
        // MONITORENTER : object
        this.tasks.put((int)prepared.getId(), (BungeeTask)prepared);
        this.tasksByPlugin.put((Plugin)owner, (BungeeTask)prepared);
        // MONITOREXIT : object
        owner.getExecutorService().execute((Runnable)prepared);
        return prepared;
    }

    @Override
    public TaskScheduler.Unsafe unsafe() {
        return this.unsafe;
    }
}

