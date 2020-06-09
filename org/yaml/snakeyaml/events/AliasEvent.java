/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.NodeEvent;

public final class AliasEvent
extends NodeEvent {
    public AliasEvent(String anchor, Mark startMark, Mark endMark) {
        super((String)anchor, (Mark)startMark, (Mark)endMark);
        if (anchor != null) return;
        throw new NullPointerException();
    }

    @Override
    public boolean is(Event.ID id) {
        if (Event.ID.Alias != id) return false;
        return true;
    }
}

