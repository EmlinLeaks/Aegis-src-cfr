/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.MaxMessagesRecvByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.util.internal.ObjectUtil;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultChannelConfig
implements ChannelConfig {
    private static final MessageSizeEstimator DEFAULT_MSG_SIZE_ESTIMATOR = DefaultMessageSizeEstimator.DEFAULT;
    private static final int DEFAULT_CONNECT_TIMEOUT = 30000;
    private static final AtomicIntegerFieldUpdater<DefaultChannelConfig> AUTOREAD_UPDATER = AtomicIntegerFieldUpdater.newUpdater(DefaultChannelConfig.class, (String)"autoRead");
    private static final AtomicReferenceFieldUpdater<DefaultChannelConfig, WriteBufferWaterMark> WATERMARK_UPDATER = AtomicReferenceFieldUpdater.newUpdater(DefaultChannelConfig.class, WriteBufferWaterMark.class, (String)"writeBufferWaterMark");
    protected final Channel channel;
    private volatile ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
    private volatile RecvByteBufAllocator rcvBufAllocator;
    private volatile MessageSizeEstimator msgSizeEstimator = DEFAULT_MSG_SIZE_ESTIMATOR;
    private volatile int connectTimeoutMillis = 30000;
    private volatile int writeSpinCount = 16;
    private volatile int autoRead = 1;
    private volatile boolean autoClose = true;
    private volatile WriteBufferWaterMark writeBufferWaterMark = WriteBufferWaterMark.DEFAULT;
    private volatile boolean pinEventExecutor = true;

    public DefaultChannelConfig(Channel channel) {
        this((Channel)channel, (RecvByteBufAllocator)new AdaptiveRecvByteBufAllocator());
    }

    protected DefaultChannelConfig(Channel channel, RecvByteBufAllocator allocator) {
        this.setRecvByteBufAllocator((RecvByteBufAllocator)allocator, (ChannelMetadata)channel.metadata());
        this.channel = channel;
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(null, ChannelOption.CONNECT_TIMEOUT_MILLIS, ChannelOption.MAX_MESSAGES_PER_READ, ChannelOption.WRITE_SPIN_COUNT, ChannelOption.ALLOCATOR, ChannelOption.AUTO_READ, ChannelOption.AUTO_CLOSE, ChannelOption.RCVBUF_ALLOCATOR, ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, ChannelOption.WRITE_BUFFER_WATER_MARK, ChannelOption.MESSAGE_SIZE_ESTIMATOR, ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP);
    }

    protected Map<ChannelOption<?>, Object> getOptions(Map<ChannelOption<?>, Object> result, ChannelOption<?> ... options) {
        if (result == null) {
            result = new IdentityHashMap<ChannelOption<?>, Object>();
        }
        ChannelOption<?>[] arrchannelOption = options;
        int n = arrchannelOption.length;
        int n2 = 0;
        while (n2 < n) {
            ChannelOption<?> o = arrchannelOption[n2];
            result.put(o, this.getOption(o));
            ++n2;
        }
        return result;
    }

    @Override
    public boolean setOptions(Map<ChannelOption<?>, ?> options) {
        if (options == null) {
            throw new NullPointerException((String)"options");
        }
        boolean setAllOptions = true;
        Iterator<Map.Entry<ChannelOption<?>, ?>> iterator = options.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ChannelOption<?>, ?> e = iterator.next();
            if (this.setOption(e.getKey(), e.getValue())) continue;
            setAllOptions = false;
        }
        return setAllOptions;
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == null) {
            throw new NullPointerException((String)"option");
        }
        if (option == ChannelOption.CONNECT_TIMEOUT_MILLIS) {
            return (T)Integer.valueOf((int)this.getConnectTimeoutMillis());
        }
        if (option == ChannelOption.MAX_MESSAGES_PER_READ) {
            return (T)Integer.valueOf((int)this.getMaxMessagesPerRead());
        }
        if (option == ChannelOption.WRITE_SPIN_COUNT) {
            return (T)Integer.valueOf((int)this.getWriteSpinCount());
        }
        if (option == ChannelOption.ALLOCATOR) {
            return (T)this.getAllocator();
        }
        if (option == ChannelOption.RCVBUF_ALLOCATOR) {
            return (T)this.getRecvByteBufAllocator();
        }
        if (option == ChannelOption.AUTO_READ) {
            return (T)Boolean.valueOf((boolean)this.isAutoRead());
        }
        if (option == ChannelOption.AUTO_CLOSE) {
            return (T)Boolean.valueOf((boolean)this.isAutoClose());
        }
        if (option == ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK) {
            return (T)Integer.valueOf((int)this.getWriteBufferHighWaterMark());
        }
        if (option == ChannelOption.WRITE_BUFFER_LOW_WATER_MARK) {
            return (T)Integer.valueOf((int)this.getWriteBufferLowWaterMark());
        }
        if (option == ChannelOption.WRITE_BUFFER_WATER_MARK) {
            return (T)this.getWriteBufferWaterMark();
        }
        if (option == ChannelOption.MESSAGE_SIZE_ESTIMATOR) {
            return (T)this.getMessageSizeEstimator();
        }
        if (option != ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP) return (T)null;
        return (T)Boolean.valueOf((boolean)this.getPinEventExecutorPerGroup());
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option == ChannelOption.CONNECT_TIMEOUT_MILLIS) {
            this.setConnectTimeoutMillis((int)((Integer)value).intValue());
            return true;
        }
        if (option == ChannelOption.MAX_MESSAGES_PER_READ) {
            this.setMaxMessagesPerRead((int)((Integer)value).intValue());
            return true;
        }
        if (option == ChannelOption.WRITE_SPIN_COUNT) {
            this.setWriteSpinCount((int)((Integer)value).intValue());
            return true;
        }
        if (option == ChannelOption.ALLOCATOR) {
            this.setAllocator((ByteBufAllocator)((ByteBufAllocator)value));
            return true;
        }
        if (option == ChannelOption.RCVBUF_ALLOCATOR) {
            this.setRecvByteBufAllocator((RecvByteBufAllocator)((RecvByteBufAllocator)value));
            return true;
        }
        if (option == ChannelOption.AUTO_READ) {
            this.setAutoRead((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option == ChannelOption.AUTO_CLOSE) {
            this.setAutoClose((boolean)((Boolean)value).booleanValue());
            return true;
        }
        if (option == ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK) {
            this.setWriteBufferHighWaterMark((int)((Integer)value).intValue());
            return true;
        }
        if (option == ChannelOption.WRITE_BUFFER_LOW_WATER_MARK) {
            this.setWriteBufferLowWaterMark((int)((Integer)value).intValue());
            return true;
        }
        if (option == ChannelOption.WRITE_BUFFER_WATER_MARK) {
            this.setWriteBufferWaterMark((WriteBufferWaterMark)((WriteBufferWaterMark)value));
            return true;
        }
        if (option == ChannelOption.MESSAGE_SIZE_ESTIMATOR) {
            this.setMessageSizeEstimator((MessageSizeEstimator)((MessageSizeEstimator)value));
            return true;
        }
        if (option != ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP) return false;
        this.setPinEventExecutorPerGroup((boolean)((Boolean)value).booleanValue());
        return true;
    }

    protected <T> void validate(ChannelOption<T> option, T value) {
        if (option == null) {
            throw new NullPointerException((String)"option");
        }
        option.validate(value);
    }

    @Override
    public int getConnectTimeoutMillis() {
        return this.connectTimeoutMillis;
    }

    @Override
    public ChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        ObjectUtil.checkPositiveOrZero((int)connectTimeoutMillis, (String)"connectTimeoutMillis");
        this.connectTimeoutMillis = connectTimeoutMillis;
        return this;
    }

    @Deprecated
    @Override
    public int getMaxMessagesPerRead() {
        try {
            MaxMessagesRecvByteBufAllocator allocator = (MaxMessagesRecvByteBufAllocator)this.getRecvByteBufAllocator();
            return allocator.maxMessagesPerRead();
        }
        catch (ClassCastException e) {
            throw new IllegalStateException((String)"getRecvByteBufAllocator() must return an object of type MaxMessagesRecvByteBufAllocator", (Throwable)e);
        }
    }

    @Deprecated
    @Override
    public ChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        try {
            MaxMessagesRecvByteBufAllocator allocator = (MaxMessagesRecvByteBufAllocator)this.getRecvByteBufAllocator();
            allocator.maxMessagesPerRead((int)maxMessagesPerRead);
            return this;
        }
        catch (ClassCastException e) {
            throw new IllegalStateException((String)"getRecvByteBufAllocator() must return an object of type MaxMessagesRecvByteBufAllocator", (Throwable)e);
        }
    }

    @Override
    public int getWriteSpinCount() {
        return this.writeSpinCount;
    }

    @Override
    public ChannelConfig setWriteSpinCount(int writeSpinCount) {
        ObjectUtil.checkPositive((int)writeSpinCount, (String)"writeSpinCount");
        if (writeSpinCount == Integer.MAX_VALUE) {
            --writeSpinCount;
        }
        this.writeSpinCount = writeSpinCount;
        return this;
    }

    @Override
    public ByteBufAllocator getAllocator() {
        return this.allocator;
    }

    @Override
    public ChannelConfig setAllocator(ByteBufAllocator allocator) {
        if (allocator == null) {
            throw new NullPointerException((String)"allocator");
        }
        this.allocator = allocator;
        return this;
    }

    @Override
    public <T extends RecvByteBufAllocator> T getRecvByteBufAllocator() {
        return (T)this.rcvBufAllocator;
    }

    @Override
    public ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        this.rcvBufAllocator = ObjectUtil.checkNotNull(allocator, (String)"allocator");
        return this;
    }

    private void setRecvByteBufAllocator(RecvByteBufAllocator allocator, ChannelMetadata metadata) {
        if (allocator instanceof MaxMessagesRecvByteBufAllocator) {
            ((MaxMessagesRecvByteBufAllocator)allocator).maxMessagesPerRead((int)metadata.defaultMaxMessagesPerRead());
        } else if (allocator == null) {
            throw new NullPointerException((String)"allocator");
        }
        this.setRecvByteBufAllocator((RecvByteBufAllocator)allocator);
    }

    @Override
    public boolean isAutoRead() {
        if (this.autoRead != 1) return false;
        return true;
    }

    @Override
    public ChannelConfig setAutoRead(boolean autoRead) {
        boolean oldAutoRead;
        boolean bl = oldAutoRead = AUTOREAD_UPDATER.getAndSet((DefaultChannelConfig)this, (int)(autoRead ? 1 : 0)) == 1;
        if (autoRead && !oldAutoRead) {
            this.channel.read();
            return this;
        }
        if (autoRead) return this;
        if (!oldAutoRead) return this;
        this.autoReadCleared();
        return this;
    }

    protected void autoReadCleared() {
    }

    @Override
    public boolean isAutoClose() {
        return this.autoClose;
    }

    @Override
    public ChannelConfig setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
        return this;
    }

    @Override
    public int getWriteBufferHighWaterMark() {
        return this.writeBufferWaterMark.high();
    }

    @Override
    public ChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        WriteBufferWaterMark waterMark;
        ObjectUtil.checkPositiveOrZero((int)writeBufferHighWaterMark, (String)"writeBufferHighWaterMark");
        do {
            if (writeBufferHighWaterMark >= (waterMark = this.writeBufferWaterMark).low()) continue;
            throw new IllegalArgumentException((String)("writeBufferHighWaterMark cannot be less than writeBufferLowWaterMark (" + waterMark.low() + "): " + writeBufferHighWaterMark));
        } while (!WATERMARK_UPDATER.compareAndSet((DefaultChannelConfig)this, (WriteBufferWaterMark)waterMark, (WriteBufferWaterMark)new WriteBufferWaterMark((int)waterMark.low(), (int)writeBufferHighWaterMark, (boolean)false)));
        return this;
    }

    @Override
    public int getWriteBufferLowWaterMark() {
        return this.writeBufferWaterMark.low();
    }

    @Override
    public ChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        WriteBufferWaterMark waterMark;
        ObjectUtil.checkPositiveOrZero((int)writeBufferLowWaterMark, (String)"writeBufferLowWaterMark");
        do {
            if (writeBufferLowWaterMark <= (waterMark = this.writeBufferWaterMark).high()) continue;
            throw new IllegalArgumentException((String)("writeBufferLowWaterMark cannot be greater than writeBufferHighWaterMark (" + waterMark.high() + "): " + writeBufferLowWaterMark));
        } while (!WATERMARK_UPDATER.compareAndSet((DefaultChannelConfig)this, (WriteBufferWaterMark)waterMark, (WriteBufferWaterMark)new WriteBufferWaterMark((int)writeBufferLowWaterMark, (int)waterMark.high(), (boolean)false)));
        return this;
    }

    @Override
    public ChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        this.writeBufferWaterMark = ObjectUtil.checkNotNull(writeBufferWaterMark, (String)"writeBufferWaterMark");
        return this;
    }

    @Override
    public WriteBufferWaterMark getWriteBufferWaterMark() {
        return this.writeBufferWaterMark;
    }

    @Override
    public MessageSizeEstimator getMessageSizeEstimator() {
        return this.msgSizeEstimator;
    }

    @Override
    public ChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        if (estimator == null) {
            throw new NullPointerException((String)"estimator");
        }
        this.msgSizeEstimator = estimator;
        return this;
    }

    private ChannelConfig setPinEventExecutorPerGroup(boolean pinEventExecutor) {
        this.pinEventExecutor = pinEventExecutor;
        return this;
    }

    private boolean getPinEventExecutorPerGroup() {
        return this.pinEventExecutor;
    }
}

