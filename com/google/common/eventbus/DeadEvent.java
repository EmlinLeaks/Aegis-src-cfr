/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.eventbus;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

@Beta
public class DeadEvent {
    private final Object source;
    private final Object event;

    public DeadEvent(Object source, Object event) {
        this.source = Preconditions.checkNotNull(source);
        this.event = Preconditions.checkNotNull(event);
    }

    public Object getSource() {
        return this.source;
    }

    public Object getEvent() {
        return this.event;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add((String)"source", (Object)this.source).add((String)"event", (Object)this.event).toString();
    }
}

