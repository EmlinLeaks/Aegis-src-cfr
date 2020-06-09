/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;

public final class StreamEndEvent
extends Event {
    public StreamEndEvent(Mark startMark, Mark endMark) {
        super((Mark)startMark, (Mark)endMark);
    }

    @Override
    public boolean is(Event.ID id) {
        if (Event.ID.StreamEnd != id) return false;
        return true;
    }
}

