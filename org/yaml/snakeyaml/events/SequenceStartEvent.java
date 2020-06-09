/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.CollectionStartEvent;
import org.yaml.snakeyaml.events.Event;

public final class SequenceStartEvent
extends CollectionStartEvent {
    public SequenceStartEvent(String anchor, String tag, boolean implicit, Mark startMark, Mark endMark, DumperOptions.FlowStyle flowStyle) {
        super((String)anchor, (String)tag, (boolean)implicit, (Mark)startMark, (Mark)endMark, (DumperOptions.FlowStyle)flowStyle);
    }

    @Deprecated
    public SequenceStartEvent(String anchor, String tag, boolean implicit, Mark startMark, Mark endMark, Boolean flowStyle) {
        this((String)anchor, (String)tag, (boolean)implicit, (Mark)startMark, (Mark)endMark, (DumperOptions.FlowStyle)DumperOptions.FlowStyle.fromBoolean((Boolean)flowStyle));
    }

    @Override
    public boolean is(Event.ID id) {
        if (Event.ID.SequenceStart != id) return false;
        return true;
    }
}

