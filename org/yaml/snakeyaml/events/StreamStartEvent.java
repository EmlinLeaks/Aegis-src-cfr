/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;

public final class StreamStartEvent
extends Event {
    public StreamStartEvent(Mark startMark, Mark endMark) {
        super((Mark)startMark, (Mark)endMark);
    }

    @Override
    public boolean is(Event.ID id) {
        if (Event.ID.StreamStart != id) return false;
        return true;
    }
}

