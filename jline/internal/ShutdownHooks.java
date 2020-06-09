/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.internal;

import java.util.ArrayList;
import java.util.List;
import jline.internal.Configuration;
import jline.internal.Log;
import jline.internal.Preconditions;
import jline.internal.ShutdownHooks;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ShutdownHooks {
    public static final String JLINE_SHUTDOWNHOOK = "jline.shutdownhook";
    private static final boolean enabled = Configuration.getBoolean((String)"jline.shutdownhook", (boolean)true);
    private static final List<Task> tasks = new ArrayList<Task>();
    private static Thread hook;

    public static synchronized <T extends Task> T add(T task) {
        Preconditions.checkNotNull(task);
        if (!enabled) {
            Log.debug((Object[])new Object[]{"Shutdown-hook is disabled; not installing: ", task});
            return (T)task;
        }
        if (hook == null) {
            hook = ShutdownHooks.addHook((Thread)new Thread((String)"JLine Shutdown Hook"){

                public void run() {
                    ShutdownHooks.access$000();
                }
            });
        }
        Log.debug((Object[])new Object[]{"Adding shutdown-hook task: ", task});
        tasks.add(task);
        return (T)task;
    }

    private static synchronized void runTasks() {
        Log.debug((Object[])new Object[]{"Running all shutdown-hook tasks"});
        Task[] arr$ = tasks.toArray(new Task[tasks.size()]);
        int len$ = arr$.length;
        int i$ = 0;
        do {
            if (i$ >= len$) {
                tasks.clear();
                return;
            }
            Task task = arr$[i$];
            Log.debug((Object[])new Object[]{"Running task: ", task});
            try {
                task.run();
            }
            catch (Throwable e) {
                Log.warn((Object[])new Object[]{"Task failed", e});
            }
            ++i$;
        } while (true);
    }

    private static Thread addHook(Thread thread) {
        Log.debug((Object[])new Object[]{"Registering shutdown-hook: ", thread});
        try {
            Runtime.getRuntime().addShutdownHook((Thread)thread);
            return thread;
        }
        catch (AbstractMethodError e) {
            Log.debug((Object[])new Object[]{"Failed to register shutdown-hook", e});
        }
        return thread;
    }

    public static synchronized void remove(Task task) {
        Preconditions.checkNotNull(task);
        if (!enabled) return;
        if (hook == null) {
            return;
        }
        tasks.remove((Object)task);
        if (!tasks.isEmpty()) return;
        ShutdownHooks.removeHook((Thread)hook);
        hook = null;
    }

    private static void removeHook(Thread thread) {
        Log.debug((Object[])new Object[]{"Removing shutdown-hook: ", thread});
        try {
            Runtime.getRuntime().removeShutdownHook((Thread)thread);
            return;
        }
        catch (AbstractMethodError e) {
            Log.debug((Object[])new Object[]{"Failed to remove shutdown-hook", e});
            return;
        }
        catch (IllegalStateException e) {
            // empty catch block
        }
    }

    static /* synthetic */ void access$000() {
        ShutdownHooks.runTasks();
    }
}

