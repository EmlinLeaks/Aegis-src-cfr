/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.CollectionEndEvent;
import org.yaml.snakeyaml.events.Event;

public final class MappingEndEvent
extends CollectionEndEvent {
    public MappingEndEvent(Mark startMark, Mark endMark) {
        super((Mark)startMark, (Mark)endMark);
    }

    @Override
    public boolean is(Event.ID id) {
        if (Event.ID.MappingEnd != id) return false;
        return true;
    }
}

