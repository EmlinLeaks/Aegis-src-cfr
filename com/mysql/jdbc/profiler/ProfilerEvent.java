/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.profiler;

import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.log.LogUtils;
import java.util.Date;

public class ProfilerEvent {
    public static final byte TYPE_USAGE = 0;
    public static final byte TYPE_WARN = 0;
    public static final byte TYPE_OBJECT_CREATION = 1;
    public static final byte TYPE_PREPARE = 2;
    public static final byte TYPE_QUERY = 3;
    public static final byte TYPE_EXECUTE = 4;
    public static final byte TYPE_FETCH = 5;
    public static final byte TYPE_SLOW_QUERY = 6;
    public static final byte NA = -1;
    protected byte eventType;
    protected String hostName;
    protected String catalog;
    protected long connectionId;
    protected int statementId;
    protected int resultSetId;
    protected long eventCreationTime;
    protected long eventDuration;
    protected String durationUnits;
    protected String eventCreationPointDesc;
    protected String message;
    public int hostNameIndex;
    public int catalogIndex;
    public int eventCreationPointIndex;

    public ProfilerEvent(byte eventType, String hostName, String catalog, long connectionId, int statementId, int resultSetId, long eventDuration, String durationUnits, Throwable eventCreationPoint, String message) {
        this((byte)eventType, (String)hostName, (String)catalog, (long)connectionId, (int)statementId, (int)resultSetId, (long)System.currentTimeMillis(), (long)eventDuration, (String)durationUnits, (String)LogUtils.findCallingClassAndMethod((Throwable)eventCreationPoint), (String)message, (int)-1, (int)-1, (int)-1);
    }

    private ProfilerEvent(byte eventType, String hostName, String catalog, long connectionId, int statementId, int resultSetId, long eventCreationTime, long eventDuration, String durationUnits, String eventCreationPointDesc, String message, int hostNameIndex, int catalogIndex, int eventCreationPointIndex) {
        this.eventType = eventType;
        this.hostName = hostName == null ? "" : hostName;
        this.catalog = catalog == null ? "" : catalog;
        this.connectionId = connectionId;
        this.statementId = statementId;
        this.resultSetId = resultSetId;
        this.eventCreationTime = eventCreationTime;
        this.eventDuration = eventDuration;
        this.durationUnits = durationUnits == null ? "" : durationUnits;
        this.eventCreationPointDesc = eventCreationPointDesc == null ? "" : eventCreationPointDesc;
        this.message = message == null ? "" : message;
        this.hostNameIndex = hostNameIndex;
        this.catalogIndex = catalogIndex;
        this.eventCreationPointIndex = eventCreationPointIndex;
    }

    public byte getEventType() {
        return this.eventType;
    }

    public String getHostName() {
        return this.hostName;
    }

    public String getCatalog() {
        return this.catalog;
    }

    public long getConnectionId() {
        return this.connectionId;
    }

    public int getStatementId() {
        return this.statementId;
    }

    public int getResultSetId() {
        return this.resultSetId;
    }

    public long getEventCreationTime() {
        return this.eventCreationTime;
    }

    public long getEventDuration() {
        return this.eventDuration;
    }

    public String getDurationUnits() {
        return this.durationUnits;
    }

    public String getEventCreationPointAsString() {
        return this.eventCreationPointDesc;
    }

    public String getMessage() {
        return this.message;
    }

    /*
     * Unable to fully structure code
     */
    public String toString() {
        buf = new StringBuilder();
        buf.append((String)"[");
        switch (this.getEventType()) {
            case 4: {
                buf.append((String)"EXECUTE");
                ** break;
            }
            case 5: {
                buf.append((String)"FETCH");
                ** break;
            }
            case 1: {
                buf.append((String)"CONSTRUCT");
                ** break;
            }
            case 2: {
                buf.append((String)"PREPARE");
                ** break;
            }
            case 3: {
                buf.append((String)"QUERY");
                ** break;
            }
            case 0: {
                buf.append((String)"USAGE ADVISOR");
                ** break;
            }
            case 6: {
                buf.append((String)"SLOW QUERY");
                ** break;
            }
        }
        buf.append((String)"UNKNOWN");
lbl35: // 8 sources:
        buf.append((String)"] ");
        buf.append((String)this.message);
        buf.append((String)" [Created on: ");
        buf.append((Object)new Date((long)this.eventCreationTime));
        buf.append((String)", duration: ");
        buf.append((long)this.eventDuration);
        buf.append((String)", connection-id: ");
        buf.append((long)this.connectionId);
        buf.append((String)", statement-id: ");
        buf.append((int)this.statementId);
        buf.append((String)", resultset-id: ");
        buf.append((int)this.resultSetId);
        buf.append((String)",");
        buf.append((String)this.eventCreationPointDesc);
        buf.append((String)", hostNameIndex: ");
        buf.append((int)this.hostNameIndex);
        buf.append((String)", catalogIndex: ");
        buf.append((int)this.catalogIndex);
        buf.append((String)", eventCreationPointIndex: ");
        buf.append((int)this.eventCreationPointIndex);
        buf.append((String)"]");
        return buf.toString();
    }

    public static ProfilerEvent unpack(byte[] buf) throws Exception {
        int pos = 0;
        byte eventType = buf[pos++];
        byte[] host = ProfilerEvent.readBytes((byte[])buf, (int)pos);
        byte[] db = ProfilerEvent.readBytes((byte[])buf, (int)(pos += 4 + host.length));
        long connectionId = ProfilerEvent.readLong((byte[])buf, (int)(pos += 4 + db.length));
        int statementId = ProfilerEvent.readInt((byte[])buf, (int)(pos += 8));
        int resultSetId = ProfilerEvent.readInt((byte[])buf, (int)(pos += 4));
        long eventCreationTime = ProfilerEvent.readLong((byte[])buf, (int)(pos += 4));
        long eventDuration = ProfilerEvent.readLong((byte[])buf, (int)(pos += 8));
        byte[] eventDurationUnits = ProfilerEvent.readBytes((byte[])buf, (int)(pos += 8));
        byte[] eventCreationAsBytes = ProfilerEvent.readBytes((byte[])buf, (int)(pos += 4 + eventDurationUnits.length));
        byte[] message = ProfilerEvent.readBytes((byte[])buf, (int)(pos += 4 + eventCreationAsBytes.length));
        int hostNameIndex = ProfilerEvent.readInt((byte[])buf, (int)(pos += 4 + message.length));
        int catalogIndex = ProfilerEvent.readInt((byte[])buf, (int)(pos += 4));
        int eventCreationPointIndex = ProfilerEvent.readInt((byte[])buf, (int)(pos += 4));
        pos += 4;
        return new ProfilerEvent((byte)eventType, (String)StringUtils.toString((byte[])host, (String)"ISO8859_1"), (String)StringUtils.toString((byte[])db, (String)"ISO8859_1"), (long)connectionId, (int)statementId, (int)resultSetId, (long)eventCreationTime, (long)eventDuration, (String)StringUtils.toString((byte[])eventDurationUnits, (String)"ISO8859_1"), (String)StringUtils.toString((byte[])eventCreationAsBytes, (String)"ISO8859_1"), (String)StringUtils.toString((byte[])message, (String)"ISO8859_1"), (int)hostNameIndex, (int)catalogIndex, (int)eventCreationPointIndex);
    }

    public byte[] pack() throws Exception {
        byte[] hostNameAsBytes = StringUtils.getBytes((String)this.hostName, (String)"ISO8859_1");
        byte[] dbAsBytes = StringUtils.getBytes((String)this.catalog, (String)"ISO8859_1");
        byte[] durationUnitsAsBytes = StringUtils.getBytes((String)this.durationUnits, (String)"ISO8859_1");
        byte[] eventCreationAsBytes = StringUtils.getBytes((String)this.eventCreationPointDesc, (String)"ISO8859_1");
        byte[] messageAsBytes = StringUtils.getBytes((String)this.message, (String)"ISO8859_1");
        int len = 1 + (4 + hostNameAsBytes.length) + (4 + dbAsBytes.length) + 8 + 4 + 4 + 8 + 8 + (4 + durationUnitsAsBytes.length) + (4 + eventCreationAsBytes.length) + (4 + messageAsBytes.length) + 4 + 4 + 4;
        byte[] buf = new byte[len];
        int pos = 0;
        buf[pos++] = this.eventType;
        pos = ProfilerEvent.writeBytes((byte[])hostNameAsBytes, (byte[])buf, (int)pos);
        pos = ProfilerEvent.writeBytes((byte[])dbAsBytes, (byte[])buf, (int)pos);
        pos = ProfilerEvent.writeLong((long)this.connectionId, (byte[])buf, (int)pos);
        pos = ProfilerEvent.writeInt((int)this.statementId, (byte[])buf, (int)pos);
        pos = ProfilerEvent.writeInt((int)this.resultSetId, (byte[])buf, (int)pos);
        pos = ProfilerEvent.writeLong((long)this.eventCreationTime, (byte[])buf, (int)pos);
        pos = ProfilerEvent.writeLong((long)this.eventDuration, (byte[])buf, (int)pos);
        pos = ProfilerEvent.writeBytes((byte[])durationUnitsAsBytes, (byte[])buf, (int)pos);
        pos = ProfilerEvent.writeBytes((byte[])eventCreationAsBytes, (byte[])buf, (int)pos);
        pos = ProfilerEvent.writeBytes((byte[])messageAsBytes, (byte[])buf, (int)pos);
        pos = ProfilerEvent.writeInt((int)this.hostNameIndex, (byte[])buf, (int)pos);
        pos = ProfilerEvent.writeInt((int)this.catalogIndex, (byte[])buf, (int)pos);
        pos = ProfilerEvent.writeInt((int)this.eventCreationPointIndex, (byte[])buf, (int)pos);
        return buf;
    }

    private static int writeInt(int i, byte[] buf, int pos) {
        buf[pos++] = (byte)(i & 255);
        buf[pos++] = (byte)(i >>> 8);
        buf[pos++] = (byte)(i >>> 16);
        buf[pos++] = (byte)(i >>> 24);
        return pos;
    }

    private static int writeLong(long l, byte[] buf, int pos) {
        buf[pos++] = (byte)((int)(l & 255L));
        buf[pos++] = (byte)((int)(l >>> 8));
        buf[pos++] = (byte)((int)(l >>> 16));
        buf[pos++] = (byte)((int)(l >>> 24));
        buf[pos++] = (byte)((int)(l >>> 32));
        buf[pos++] = (byte)((int)(l >>> 40));
        buf[pos++] = (byte)((int)(l >>> 48));
        buf[pos++] = (byte)((int)(l >>> 56));
        return pos;
    }

    private static int writeBytes(byte[] msg, byte[] buf, int pos) {
        pos = ProfilerEvent.writeInt((int)msg.length, (byte[])buf, (int)pos);
        System.arraycopy((Object)msg, (int)0, (Object)buf, (int)pos, (int)msg.length);
        return pos + msg.length;
    }

    private static int readInt(byte[] buf, int pos) {
        return buf[pos++] & 255 | (buf[pos++] & 255) << 8 | (buf[pos++] & 255) << 16 | (buf[pos++] & 255) << 24;
    }

    private static long readLong(byte[] buf, int pos) {
        return (long)(buf[pos++] & 255) | (long)(buf[pos++] & 255) << 8 | (long)(buf[pos++] & 255) << 16 | (long)(buf[pos++] & 255) << 24 | (long)(buf[pos++] & 255) << 32 | (long)(buf[pos++] & 255) << 40 | (long)(buf[pos++] & 255) << 48 | (long)(buf[pos++] & 255) << 56;
    }

    private static byte[] readBytes(byte[] buf, int pos) {
        int length = ProfilerEvent.readInt((byte[])buf, (int)pos);
        byte[] msg = new byte[length];
        System.arraycopy((Object)buf, (int)(pos + 4), (Object)msg, (int)0, (int)length);
        return msg;
    }
}

