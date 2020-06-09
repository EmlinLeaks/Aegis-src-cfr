/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.GuardedBy
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.Monitor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.concurrent.GuardedBy;

@Beta
@GwtIncompatible
public final class Monitor {
    private final boolean fair;
    private final ReentrantLock lock;
    @GuardedBy(value="lock")
    private Guard activeGuards = null;

    public Monitor() {
        this((boolean)false);
    }

    public Monitor(boolean fair) {
        this.fair = fair;
        this.lock = new ReentrantLock((boolean)fair);
    }

    public void enter() {
        this.lock.lock();
    }

    public void enterInterruptibly() throws InterruptedException {
        this.lock.lockInterruptibly();
    }

    /*
     * Exception decompiling
     */
    public boolean enter(long time, TimeUnit unit) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 3[CATCHBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    public boolean enterInterruptibly(long time, TimeUnit unit) throws InterruptedException {
        return this.lock.tryLock((long)time, (TimeUnit)unit);
    }

    public boolean tryEnter() {
        return this.lock.tryLock();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void enterWhen(Guard guard) throws InterruptedException {
        if (guard.monitor != this) {
            throw new IllegalMonitorStateException();
        }
        ReentrantLock lock = this.lock;
        boolean signalBeforeWaiting = lock.isHeldByCurrentThread();
        lock.lockInterruptibly();
        boolean satisfied = false;
        try {
            if (!guard.isSatisfied()) {
                this.await((Guard)guard, (boolean)signalBeforeWaiting);
            }
            satisfied = true;
            return;
        }
        finally {
            if (!satisfied) {
                this.leave();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void enterWhenUninterruptibly(Guard guard) {
        if (guard.monitor != this) {
            throw new IllegalMonitorStateException();
        }
        ReentrantLock lock = this.lock;
        boolean signalBeforeWaiting = lock.isHeldByCurrentThread();
        lock.lock();
        boolean satisfied = false;
        try {
            if (!guard.isSatisfied()) {
                this.awaitUninterruptibly((Guard)guard, (boolean)signalBeforeWaiting);
            }
            satisfied = true;
            return;
        }
        finally {
            if (!satisfied) {
                this.leave();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean enterWhen(Guard guard, long time, TimeUnit unit) throws InterruptedException {
        long startTime;
        ReentrantLock lock;
        boolean reentrant;
        long timeoutNanos;
        block19 : {
            block18 : {
                timeoutNanos = Monitor.toSafeNanos((long)time, (TimeUnit)unit);
                if (guard.monitor != this) {
                    throw new IllegalMonitorStateException();
                }
                lock = this.lock;
                reentrant = lock.isHeldByCurrentThread();
                startTime = 0L;
                if (this.fair) break block18;
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                if (lock.tryLock()) break block19;
            }
            startTime = Monitor.initNanoTime((long)timeoutNanos);
            if (!lock.tryLock((long)time, (TimeUnit)unit)) {
                return false;
            }
        }
        boolean satisfied = false;
        boolean threw = true;
        try {
            satisfied = guard.isSatisfied() || this.awaitNanos((Guard)guard, (long)(startTime == 0L ? timeoutNanos : Monitor.remainingNanos((long)startTime, (long)timeoutNanos)), (boolean)reentrant);
            threw = false;
            boolean bl = satisfied;
            return bl;
        }
        finally {
            if (!satisfied) {
                try {
                    if (threw && !reentrant) {
                        this.signalNextWaiter();
                    }
                }
                finally {
                    lock.unlock();
                }
            }
        }
    }

    public boolean enterWhenUninterruptibly(Guard guard, long time, TimeUnit unit) {
        boolean satisfied;
        long timeoutNanos = Monitor.toSafeNanos((long)time, (TimeUnit)unit);
        if (guard.monitor != this) {
            throw new IllegalMonitorStateException();
        }
        ReentrantLock lock = this.lock;
        long startTime = 0L;
        boolean signalBeforeWaiting = lock.isHeldByCurrentThread();
        boolean interrupted = Thread.interrupted();
        try {
            if (this.fair || !lock.tryLock()) {
                startTime = Monitor.initNanoTime((long)timeoutNanos);
                long remainingNanos = timeoutNanos;
                do {
                    try {
                        if (!lock.tryLock((long)remainingNanos, (TimeUnit)TimeUnit.NANOSECONDS)) {
                            boolean bl = false;
                            return bl;
                        }
                    }
                    catch (InterruptedException interrupt) {
                        interrupted = true;
                        remainingNanos = Monitor.remainingNanos((long)startTime, (long)timeoutNanos);
                        continue;
                    }
                    break;
                } while (true);
            }
            satisfied = false;
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
        do {
            try {
                if (guard.isSatisfied()) {
                    satisfied = true;
                } else {
                    long remainingNanos;
                    if (startTime == 0L) {
                        startTime = Monitor.initNanoTime((long)timeoutNanos);
                        remainingNanos = timeoutNanos;
                    } else {
                        remainingNanos = Monitor.remainingNanos((long)startTime, (long)timeoutNanos);
                    }
                    satisfied = this.awaitNanos((Guard)guard, (long)remainingNanos, (boolean)signalBeforeWaiting);
                }
                boolean remainingNanos = satisfied;
                return remainingNanos;
            }
            catch (InterruptedException interrupt) {
                try {
                    interrupted = true;
                    signalBeforeWaiting = false;
                    continue;
                }
                catch (Throwable throwable) {
                    throw throwable;
                }
                finally {
                    if (!satisfied) {
                        lock.unlock();
                    }
                }
            }
            break;
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean enterIf(Guard guard) {
        if (guard.monitor != this) {
            throw new IllegalMonitorStateException();
        }
        ReentrantLock lock = this.lock;
        lock.lock();
        boolean satisfied = false;
        try {
            boolean bl = satisfied = guard.isSatisfied();
            return bl;
        }
        finally {
            if (!satisfied) {
                lock.unlock();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean enterIfInterruptibly(Guard guard) throws InterruptedException {
        if (guard.monitor != this) {
            throw new IllegalMonitorStateException();
        }
        ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        boolean satisfied = false;
        try {
            boolean bl = satisfied = guard.isSatisfied();
            return bl;
        }
        finally {
            if (!satisfied) {
                lock.unlock();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean enterIf(Guard guard, long time, TimeUnit unit) {
        if (guard.monitor != this) {
            throw new IllegalMonitorStateException();
        }
        if (!this.enter((long)time, (TimeUnit)unit)) {
            return false;
        }
        boolean satisfied = false;
        try {
            boolean bl = satisfied = guard.isSatisfied();
            return bl;
        }
        finally {
            if (!satisfied) {
                this.lock.unlock();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean enterIfInterruptibly(Guard guard, long time, TimeUnit unit) throws InterruptedException {
        if (guard.monitor != this) {
            throw new IllegalMonitorStateException();
        }
        ReentrantLock lock = this.lock;
        if (!lock.tryLock((long)time, (TimeUnit)unit)) {
            return false;
        }
        boolean satisfied = false;
        try {
            boolean bl = satisfied = guard.isSatisfied();
            return bl;
        }
        finally {
            if (!satisfied) {
                lock.unlock();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean tryEnterIf(Guard guard) {
        if (guard.monitor != this) {
            throw new IllegalMonitorStateException();
        }
        ReentrantLock lock = this.lock;
        if (!lock.tryLock()) {
            return false;
        }
        boolean satisfied = false;
        try {
            boolean bl = satisfied = guard.isSatisfied();
            return bl;
        }
        finally {
            if (!satisfied) {
                lock.unlock();
            }
        }
    }

    public void waitFor(Guard guard) throws InterruptedException {
        if (!(guard.monitor == this & this.lock.isHeldByCurrentThread())) {
            throw new IllegalMonitorStateException();
        }
        if (guard.isSatisfied()) return;
        this.await((Guard)guard, (boolean)true);
    }

    public void waitForUninterruptibly(Guard guard) {
        if (!(guard.monitor == this & this.lock.isHeldByCurrentThread())) {
            throw new IllegalMonitorStateException();
        }
        if (guard.isSatisfied()) return;
        this.awaitUninterruptibly((Guard)guard, (boolean)true);
    }

    public boolean waitFor(Guard guard, long time, TimeUnit unit) throws InterruptedException {
        long timeoutNanos = Monitor.toSafeNanos((long)time, (TimeUnit)unit);
        if (!(guard.monitor == this & this.lock.isHeldByCurrentThread())) {
            throw new IllegalMonitorStateException();
        }
        if (guard.isSatisfied()) {
            return true;
        }
        if (!Thread.interrupted()) return this.awaitNanos((Guard)guard, (long)timeoutNanos, (boolean)true);
        throw new InterruptedException();
    }

    /*
     * Exception decompiling
     */
    public boolean waitForUninterruptibly(Guard guard, long time, TimeUnit unit) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 4[CATCHBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void leave() {
        ReentrantLock lock = this.lock;
        try {
            if (lock.getHoldCount() != 1) return;
            this.signalNextWaiter();
            return;
        }
        finally {
            lock.unlock();
        }
    }

    public boolean isFair() {
        return this.fair;
    }

    public boolean isOccupied() {
        return this.lock.isLocked();
    }

    public boolean isOccupiedByCurrentThread() {
        return this.lock.isHeldByCurrentThread();
    }

    public int getOccupiedDepth() {
        return this.lock.getHoldCount();
    }

    public int getQueueLength() {
        return this.lock.getQueueLength();
    }

    public boolean hasQueuedThreads() {
        return this.lock.hasQueuedThreads();
    }

    public boolean hasQueuedThread(Thread thread) {
        return this.lock.hasQueuedThread((Thread)thread);
    }

    public boolean hasWaiters(Guard guard) {
        if (this.getWaitQueueLength((Guard)guard) <= 0) return false;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getWaitQueueLength(Guard guard) {
        if (guard.monitor != this) {
            throw new IllegalMonitorStateException();
        }
        this.lock.lock();
        try {
            int n = guard.waiterCount;
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }

    private static long toSafeNanos(long time, TimeUnit unit) {
        long timeoutNanos = unit.toNanos((long)time);
        if (timeoutNanos <= 0L) {
            return 0L;
        }
        if (timeoutNanos > 6917529027641081853L) {
            return 6917529027641081853L;
        }
        long l = timeoutNanos;
        return l;
    }

    private static long initNanoTime(long timeoutNanos) {
        if (timeoutNanos <= 0L) {
            return 0L;
        }
        long startTime = System.nanoTime();
        if (startTime == 0L) {
            return 1L;
        }
        long l = startTime;
        return l;
    }

    private static long remainingNanos(long startTime, long timeoutNanos) {
        if (timeoutNanos <= 0L) {
            return 0L;
        }
        long l = timeoutNanos - (System.nanoTime() - startTime);
        return l;
    }

    @GuardedBy(value="lock")
    private void signalNextWaiter() {
        Guard guard = this.activeGuards;
        while (guard != null) {
            if (this.isSatisfied((Guard)guard)) {
                guard.condition.signal();
                return;
            }
            guard = guard.next;
        }
    }

    @GuardedBy(value="lock")
    private boolean isSatisfied(Guard guard) {
        try {
            return guard.isSatisfied();
        }
        catch (Throwable throwable) {
            this.signalAllWaiters();
            throw Throwables.propagate((Throwable)throwable);
        }
    }

    @GuardedBy(value="lock")
    private void signalAllWaiters() {
        Guard guard = this.activeGuards;
        while (guard != null) {
            guard.condition.signalAll();
            guard = guard.next;
        }
    }

    @GuardedBy(value="lock")
    private void beginWaitingFor(Guard guard) {
        int waiters;
        if ((waiters = guard.waiterCount++) != 0) return;
        guard.next = this.activeGuards;
        this.activeGuards = guard;
    }

    @GuardedBy(value="lock")
    private void endWaitingFor(Guard guard) {
        int waiters;
        if ((waiters = --guard.waiterCount) != 0) return;
        Guard p = this.activeGuards;
        Guard pred = null;
        do {
            if (p == guard) {
                if (pred == null) {
                    this.activeGuards = p.next;
                    break;
                }
                pred.next = p.next;
                break;
            }
            pred = p;
            p = p.next;
        } while (true);
        p.next = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @GuardedBy(value="lock")
    private void await(Guard guard, boolean signalBeforeWaiting) throws InterruptedException {
        if (signalBeforeWaiting) {
            this.signalNextWaiter();
        }
        this.beginWaitingFor((Guard)guard);
        try {
            do {
                guard.condition.await();
            } while (!guard.isSatisfied());
            return;
        }
        finally {
            this.endWaitingFor((Guard)guard);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @GuardedBy(value="lock")
    private void awaitUninterruptibly(Guard guard, boolean signalBeforeWaiting) {
        if (signalBeforeWaiting) {
            this.signalNextWaiter();
        }
        this.beginWaitingFor((Guard)guard);
        try {
            do {
                guard.condition.awaitUninterruptibly();
            } while (!guard.isSatisfied());
            return;
        }
        finally {
            this.endWaitingFor((Guard)guard);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @GuardedBy(value="lock")
    private boolean awaitNanos(Guard guard, long nanos, boolean signalBeforeWaiting) throws InterruptedException {
        boolean firstTime = true;
        try {
            do {
                if (nanos <= 0L) {
                    boolean bl = false;
                    return bl;
                }
                if (firstTime) {
                    if (signalBeforeWaiting) {
                        this.signalNextWaiter();
                    }
                    this.beginWaitingFor((Guard)guard);
                    firstTime = false;
                }
                nanos = guard.condition.awaitNanos((long)nanos);
            } while (!guard.isSatisfied());
            boolean bl = true;
            return bl;
        }
        finally {
            if (!firstTime) {
                this.endWaitingFor((Guard)guard);
            }
        }
    }

    static /* synthetic */ ReentrantLock access$000(Monitor x0) {
        return x0.lock;
    }
}

