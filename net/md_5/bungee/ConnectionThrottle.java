/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.ConnectionThrottle;

public class ConnectionThrottle {
    private final LoadingCache<InetAddress, Integer> throttle;
    private final int throttleLimit;

    public ConnectionThrottle(int throttleTime, int throttleLimit) {
        this((Ticker)Ticker.systemTicker(), (int)throttleTime, (int)throttleLimit);
    }

    @VisibleForTesting
    ConnectionThrottle(Ticker ticker, int throttleTime, int throttleLimit) {
        this.throttle = CacheBuilder.newBuilder().ticker((Ticker)ticker).concurrencyLevel((int)Runtime.getRuntime().availableProcessors()).initialCapacity((int)100).expireAfterWrite((long)((long)throttleTime), (TimeUnit)TimeUnit.MILLISECONDS).build(new CacheLoader<InetAddress, Integer>((ConnectionThrottle)this){
            final /* synthetic */ ConnectionThrottle this$0;
            {
                this.this$0 = this$0;
            }

            public Integer load(InetAddress key) throws java.lang.Exception {
                return Integer.valueOf((int)0);
            }
        });
        this.throttleLimit = throttleLimit;
    }

    public void unthrottle(InetAddress address) {
        int throttleCount = this.throttle.getUnchecked((InetAddress)address).intValue() - 1;
        this.throttle.put((InetAddress)address, (Integer)Integer.valueOf((int)throttleCount));
    }

    public boolean throttle(InetAddress address) {
        int throttleCount = this.throttle.getUnchecked((InetAddress)address).intValue() + 1;
        this.throttle.put((InetAddress)address, (Integer)Integer.valueOf((int)throttleCount));
        if (throttleCount <= this.throttleLimit) return false;
        return true;
    }
}

