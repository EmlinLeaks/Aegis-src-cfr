/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.timeout;

import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class IdleStateEvent {
    public static final IdleStateEvent FIRST_READER_IDLE_STATE_EVENT = new DefaultIdleStateEvent((IdleState)IdleState.READER_IDLE, (boolean)true);
    public static final IdleStateEvent READER_IDLE_STATE_EVENT = new DefaultIdleStateEvent((IdleState)IdleState.READER_IDLE, (boolean)false);
    public static final IdleStateEvent FIRST_WRITER_IDLE_STATE_EVENT = new DefaultIdleStateEvent((IdleState)IdleState.WRITER_IDLE, (boolean)true);
    public static final IdleStateEvent WRITER_IDLE_STATE_EVENT = new DefaultIdleStateEvent((IdleState)IdleState.WRITER_IDLE, (boolean)false);
    public static final IdleStateEvent FIRST_ALL_IDLE_STATE_EVENT = new DefaultIdleStateEvent((IdleState)IdleState.ALL_IDLE, (boolean)true);
    public static final IdleStateEvent ALL_IDLE_STATE_EVENT = new DefaultIdleStateEvent((IdleState)IdleState.ALL_IDLE, (boolean)false);
    private final IdleState state;
    private final boolean first;

    protected IdleStateEvent(IdleState state, boolean first) {
        this.state = ObjectUtil.checkNotNull(state, (String)"state");
        this.first = first;
    }

    public IdleState state() {
        return this.state;
    }

    public boolean isFirst() {
        return this.first;
    }

    public String toString() {
        String string;
        if (this.first) {
            string = ", first";
            return StringUtil.simpleClassName((Object)this) + '(' + (Object)((Object)this.state) + string + ')';
        }
        string = "";
        return StringUtil.simpleClassName((Object)this) + '(' + (Object)((Object)this.state) + string + ')';
    }
}

