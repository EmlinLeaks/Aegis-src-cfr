/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelId;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.MacAddressUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public final class DefaultChannelId
implements ChannelId {
    private static final long serialVersionUID = 3884076183504074063L;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultChannelId.class);
    private static final byte[] MACHINE_ID;
    private static final int PROCESS_ID_LEN = 4;
    private static final int PROCESS_ID;
    private static final int SEQUENCE_LEN = 4;
    private static final int TIMESTAMP_LEN = 8;
    private static final int RANDOM_LEN = 4;
    private static final AtomicInteger nextSequence;
    private final byte[] data = new byte[MACHINE_ID.length + 4 + 4 + 8 + 4];
    private final int hashCode;
    private transient String shortValue;
    private transient String longValue;

    public static DefaultChannelId newInstance() {
        return new DefaultChannelId();
    }

    private static int defaultProcessId() {
        String value;
        int pid;
        ClassLoader loader = null;
        try {
            loader = PlatformDependent.getClassLoader(DefaultChannelId.class);
            Class<?> mgmtFactoryType = Class.forName((String)"java.lang.management.ManagementFactory", (boolean)true, (ClassLoader)loader);
            Class<?> runtimeMxBeanType = Class.forName((String)"java.lang.management.RuntimeMXBean", (boolean)true, (ClassLoader)loader);
            Method getRuntimeMXBean = mgmtFactoryType.getMethod((String)"getRuntimeMXBean", EmptyArrays.EMPTY_CLASSES);
            Object bean = getRuntimeMXBean.invoke(null, (Object[])EmptyArrays.EMPTY_OBJECTS);
            Method getName = runtimeMxBeanType.getMethod((String)"getName", EmptyArrays.EMPTY_CLASSES);
            value = (String)getName.invoke((Object)bean, (Object[])EmptyArrays.EMPTY_OBJECTS);
        }
        catch (Throwable t) {
            logger.debug((String)"Could not invoke ManagementFactory.getRuntimeMXBean().getName(); Android?", (Throwable)t);
            try {
                Class<?> processType = Class.forName((String)"android.os.Process", (boolean)true, (ClassLoader)loader);
                Method myPid = processType.getMethod((String)"myPid", EmptyArrays.EMPTY_CLASSES);
                value = myPid.invoke(null, (Object[])EmptyArrays.EMPTY_OBJECTS).toString();
            }
            catch (Throwable t2) {
                logger.debug((String)"Could not invoke Process.myPid(); not Android?", (Throwable)t2);
                value = "";
            }
        }
        int atIndex = value.indexOf((int)64);
        if (atIndex >= 0) {
            value = value.substring((int)0, (int)atIndex);
        }
        try {
            pid = Integer.parseInt((String)value);
        }
        catch (NumberFormatException e) {
            pid = -1;
        }
        if (pid >= 0) return pid;
        pid = PlatformDependent.threadLocalRandom().nextInt();
        logger.warn((String)"Failed to find the current process ID from '{}'; using a random value: {}", (Object)value, (Object)Integer.valueOf((int)pid));
        return pid;
    }

    private DefaultChannelId() {
        int i = 0;
        System.arraycopy((Object)MACHINE_ID, (int)0, (Object)this.data, (int)i, (int)MACHINE_ID.length);
        i += MACHINE_ID.length;
        i = this.writeInt((int)i, (int)PROCESS_ID);
        i = this.writeInt((int)i, (int)nextSequence.getAndIncrement());
        i = this.writeLong((int)i, (long)(Long.reverse((long)System.nanoTime()) ^ System.currentTimeMillis()));
        int random = PlatformDependent.threadLocalRandom().nextInt();
        i = this.writeInt((int)i, (int)random);
        assert (i == this.data.length);
        this.hashCode = Arrays.hashCode((byte[])this.data);
    }

    private int writeInt(int i, int value) {
        this.data[i++] = (byte)(value >>> 24);
        this.data[i++] = (byte)(value >>> 16);
        this.data[i++] = (byte)(value >>> 8);
        this.data[i++] = (byte)value;
        return i;
    }

    private int writeLong(int i, long value) {
        this.data[i++] = (byte)((int)(value >>> 56));
        this.data[i++] = (byte)((int)(value >>> 48));
        this.data[i++] = (byte)((int)(value >>> 40));
        this.data[i++] = (byte)((int)(value >>> 32));
        this.data[i++] = (byte)((int)(value >>> 24));
        this.data[i++] = (byte)((int)(value >>> 16));
        this.data[i++] = (byte)((int)(value >>> 8));
        this.data[i++] = (byte)((int)value);
        return i;
    }

    @Override
    public String asShortText() {
        String shortValue = this.shortValue;
        if (shortValue != null) return shortValue;
        this.shortValue = shortValue = ByteBufUtil.hexDump((byte[])this.data, (int)(this.data.length - 4), (int)4);
        return shortValue;
    }

    @Override
    public String asLongText() {
        String longValue = this.longValue;
        if (longValue != null) return longValue;
        this.longValue = longValue = this.newLongValue();
        return longValue;
    }

    private String newLongValue() {
        StringBuilder buf = new StringBuilder((int)(2 * this.data.length + 5));
        int i = 0;
        i = this.appendHexDumpField((StringBuilder)buf, (int)i, (int)MACHINE_ID.length);
        i = this.appendHexDumpField((StringBuilder)buf, (int)i, (int)4);
        i = this.appendHexDumpField((StringBuilder)buf, (int)i, (int)4);
        i = this.appendHexDumpField((StringBuilder)buf, (int)i, (int)8);
        i = this.appendHexDumpField((StringBuilder)buf, (int)i, (int)4);
        if ($assertionsDisabled) return buf.substring((int)0, (int)(buf.length() - 1));
        if (i == this.data.length) return buf.substring((int)0, (int)(buf.length() - 1));
        throw new AssertionError();
    }

    private int appendHexDumpField(StringBuilder buf, int i, int length) {
        buf.append((String)ByteBufUtil.hexDump((byte[])this.data, (int)i, (int)length));
        buf.append((char)'-');
        return i += length;
    }

    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public int compareTo(ChannelId o) {
        if (this == o) {
            return 0;
        }
        if (!(o instanceof DefaultChannelId)) return this.asLongText().compareTo((String)o.asLongText());
        byte[] otherData = ((DefaultChannelId)o).data;
        int len1 = this.data.length;
        int len2 = otherData.length;
        int len = Math.min((int)len1, (int)len2);
        int k = 0;
        while (k < len) {
            byte x = this.data[k];
            byte y = otherData[k];
            if (x != y) {
                return (x & 255) - (y & 255);
            }
            ++k;
        }
        return len1 - len2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DefaultChannelId)) {
            return false;
        }
        DefaultChannelId other = (DefaultChannelId)obj;
        if (this.hashCode != other.hashCode) return false;
        if (!Arrays.equals((byte[])this.data, (byte[])other.data)) return false;
        return true;
    }

    public String toString() {
        return this.asShortText();
    }

    static {
        nextSequence = new AtomicInteger();
        int processId = -1;
        String customProcessId = SystemPropertyUtil.get((String)"io.netty.processId");
        if (customProcessId != null) {
            try {
                processId = Integer.parseInt((String)customProcessId);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            if (processId < 0) {
                processId = -1;
                logger.warn((String)"-Dio.netty.processId: {} (malformed)", (Object)customProcessId);
            } else if (logger.isDebugEnabled()) {
                logger.debug((String)"-Dio.netty.processId: {} (user-set)", (Object)Integer.valueOf((int)processId));
            }
        }
        if (processId < 0) {
            processId = DefaultChannelId.defaultProcessId();
            if (logger.isDebugEnabled()) {
                logger.debug((String)"-Dio.netty.processId: {} (auto-detected)", (Object)Integer.valueOf((int)processId));
            }
        }
        PROCESS_ID = processId;
        byte[] machineId = null;
        String customMachineId = SystemPropertyUtil.get((String)"io.netty.machineId");
        if (customMachineId != null) {
            try {
                machineId = MacAddressUtil.parseMAC((String)customMachineId);
            }
            catch (Exception e) {
                logger.warn((String)"-Dio.netty.machineId: {} (malformed)", (Object)customMachineId, (Object)e);
            }
            if (machineId != null) {
                logger.debug((String)"-Dio.netty.machineId: {} (user-set)", (Object)customMachineId);
            }
        }
        if (machineId == null) {
            machineId = MacAddressUtil.defaultMachineId();
            if (logger.isDebugEnabled()) {
                logger.debug((String)"-Dio.netty.machineId: {} (auto-detected)", (Object)MacAddressUtil.formatAddress((byte[])machineId));
            }
        }
        MACHINE_ID = machineId;
    }
}

