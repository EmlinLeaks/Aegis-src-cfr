/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdySession;
import io.netty.util.internal.PlatformDependent;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

final class SpdySession {
    private final AtomicInteger activeLocalStreams = new AtomicInteger();
    private final AtomicInteger activeRemoteStreams = new AtomicInteger();
    private final Map<Integer, StreamState> activeStreams = PlatformDependent.newConcurrentHashMap();
    private final StreamComparator streamComparator = new StreamComparator((SpdySession)this);
    private final AtomicInteger sendWindowSize;
    private final AtomicInteger receiveWindowSize;

    SpdySession(int sendWindowSize, int receiveWindowSize) {
        this.sendWindowSize = new AtomicInteger((int)sendWindowSize);
        this.receiveWindowSize = new AtomicInteger((int)receiveWindowSize);
    }

    int numActiveStreams(boolean remote) {
        if (!remote) return this.activeLocalStreams.get();
        return this.activeRemoteStreams.get();
    }

    boolean noActiveStreams() {
        return this.activeStreams.isEmpty();
    }

    boolean isActiveStream(int streamId) {
        return this.activeStreams.containsKey((Object)Integer.valueOf((int)streamId));
    }

    Map<Integer, StreamState> activeStreams() {
        TreeMap<Integer, StreamState> streams = new TreeMap<Integer, StreamState>(this.streamComparator);
        streams.putAll(this.activeStreams);
        return streams;
    }

    void acceptStream(int streamId, byte priority, boolean remoteSideClosed, boolean localSideClosed, int sendWindowSize, int receiveWindowSize, boolean remote) {
        StreamState state;
        if (remoteSideClosed) {
            if (localSideClosed) return;
        }
        if ((state = this.activeStreams.put((Integer)Integer.valueOf((int)streamId), (StreamState)new StreamState((byte)priority, (boolean)remoteSideClosed, (boolean)localSideClosed, (int)sendWindowSize, (int)receiveWindowSize))) != null) return;
        if (remote) {
            this.activeRemoteStreams.incrementAndGet();
            return;
        }
        this.activeLocalStreams.incrementAndGet();
    }

    private StreamState removeActiveStream(int streamId, boolean remote) {
        StreamState state = this.activeStreams.remove((Object)Integer.valueOf((int)streamId));
        if (state == null) return state;
        if (remote) {
            this.activeRemoteStreams.decrementAndGet();
            return state;
        }
        this.activeLocalStreams.decrementAndGet();
        return state;
    }

    void removeStream(int streamId, Throwable cause, boolean remote) {
        StreamState state = this.removeActiveStream((int)streamId, (boolean)remote);
        if (state == null) return;
        state.clearPendingWrites((Throwable)cause);
    }

    boolean isRemoteSideClosed(int streamId) {
        StreamState state = this.activeStreams.get((Object)Integer.valueOf((int)streamId));
        if (state == null) return true;
        if (state.isRemoteSideClosed()) return true;
        return false;
    }

    void closeRemoteSide(int streamId, boolean remote) {
        StreamState state = this.activeStreams.get((Object)Integer.valueOf((int)streamId));
        if (state == null) return;
        state.closeRemoteSide();
        if (!state.isLocalSideClosed()) return;
        this.removeActiveStream((int)streamId, (boolean)remote);
    }

    boolean isLocalSideClosed(int streamId) {
        StreamState state = this.activeStreams.get((Object)Integer.valueOf((int)streamId));
        if (state == null) return true;
        if (state.isLocalSideClosed()) return true;
        return false;
    }

    void closeLocalSide(int streamId, boolean remote) {
        StreamState state = this.activeStreams.get((Object)Integer.valueOf((int)streamId));
        if (state == null) return;
        state.closeLocalSide();
        if (!state.isRemoteSideClosed()) return;
        this.removeActiveStream((int)streamId, (boolean)remote);
    }

    boolean hasReceivedReply(int streamId) {
        StreamState state = this.activeStreams.get((Object)Integer.valueOf((int)streamId));
        if (state == null) return false;
        if (!state.hasReceivedReply()) return false;
        return true;
    }

    void receivedReply(int streamId) {
        StreamState state = this.activeStreams.get((Object)Integer.valueOf((int)streamId));
        if (state == null) return;
        state.receivedReply();
    }

    int getSendWindowSize(int streamId) {
        if (streamId == 0) {
            return this.sendWindowSize.get();
        }
        StreamState state = this.activeStreams.get((Object)Integer.valueOf((int)streamId));
        if (state == null) return -1;
        int n = state.getSendWindowSize();
        return n;
    }

    int updateSendWindowSize(int streamId, int deltaWindowSize) {
        if (streamId == 0) {
            return this.sendWindowSize.addAndGet((int)deltaWindowSize);
        }
        StreamState state = this.activeStreams.get((Object)Integer.valueOf((int)streamId));
        if (state == null) return -1;
        int n = state.updateSendWindowSize((int)deltaWindowSize);
        return n;
    }

    int updateReceiveWindowSize(int streamId, int deltaWindowSize) {
        if (streamId == 0) {
            return this.receiveWindowSize.addAndGet((int)deltaWindowSize);
        }
        StreamState state = this.activeStreams.get((Object)Integer.valueOf((int)streamId));
        if (state == null) {
            return -1;
        }
        if (deltaWindowSize <= 0) return state.updateReceiveWindowSize((int)deltaWindowSize);
        state.setReceiveWindowSizeLowerBound((int)0);
        return state.updateReceiveWindowSize((int)deltaWindowSize);
    }

    int getReceiveWindowSizeLowerBound(int streamId) {
        if (streamId == 0) {
            return 0;
        }
        StreamState state = this.activeStreams.get((Object)Integer.valueOf((int)streamId));
        if (state == null) return 0;
        int n = state.getReceiveWindowSizeLowerBound();
        return n;
    }

    void updateAllSendWindowSizes(int deltaWindowSize) {
        Iterator<StreamState> iterator = this.activeStreams.values().iterator();
        while (iterator.hasNext()) {
            StreamState state = iterator.next();
            state.updateSendWindowSize((int)deltaWindowSize);
        }
    }

    void updateAllReceiveWindowSizes(int deltaWindowSize) {
        Iterator<StreamState> iterator = this.activeStreams.values().iterator();
        while (iterator.hasNext()) {
            StreamState state = iterator.next();
            state.updateReceiveWindowSize((int)deltaWindowSize);
            if (deltaWindowSize >= 0) continue;
            state.setReceiveWindowSizeLowerBound((int)deltaWindowSize);
        }
    }

    boolean putPendingWrite(int streamId, PendingWrite pendingWrite) {
        StreamState state = this.activeStreams.get((Object)Integer.valueOf((int)streamId));
        if (state == null) return false;
        if (!state.putPendingWrite((PendingWrite)pendingWrite)) return false;
        return true;
    }

    PendingWrite getPendingWrite(int streamId) {
        if (streamId == 0) {
            StreamState state;
            PendingWrite pendingWrite;
            Map.Entry<Integer, StreamState> e;
            Iterator<Map.Entry<Integer, StreamState>> iterator = this.activeStreams().entrySet().iterator();
            do {
                if (!iterator.hasNext()) return null;
            } while ((state = (e = iterator.next()).getValue()).getSendWindowSize() <= 0 || (pendingWrite = state.getPendingWrite()) == null);
            return pendingWrite;
        }
        StreamState state = this.activeStreams.get((Object)Integer.valueOf((int)streamId));
        if (state == null) return null;
        PendingWrite pendingWrite = state.getPendingWrite();
        return pendingWrite;
    }

    PendingWrite removePendingWrite(int streamId) {
        StreamState state = this.activeStreams.get((Object)Integer.valueOf((int)streamId));
        if (state == null) return null;
        PendingWrite pendingWrite = state.removePendingWrite();
        return pendingWrite;
    }

    static /* synthetic */ Map access$000(SpdySession x0) {
        return x0.activeStreams;
    }
}

