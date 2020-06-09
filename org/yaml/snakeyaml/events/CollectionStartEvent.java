/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.NodeEvent;

public abstract class CollectionStartEvent
extends NodeEvent {
    private final String tag;
    private final boolean implicit;
    private final DumperOptions.FlowStyle flowStyle;

    public CollectionStartEvent(String anchor, String tag, boolean implicit, Mark startMark, Mark endMark, DumperOptions.FlowStyle flowStyle) {
        super((String)anchor, (Mark)startMark, (Mark)endMark);
        this.tag = tag;
        this.implicit = implicit;
        if (flowStyle == null) {
            throw new NullPointerException((String)"Flow style must be provided.");
        }
        this.flowStyle = flowStyle;
    }

    @Deprecated
    public CollectionStartEvent(String anchor, String tag, boolean implicit, Mark startMark, Mark endMark, Boolean flowStyle) {
        this((String)anchor, (String)tag, (boolean)implicit, (Mark)startMark, (Mark)endMark, (DumperOptions.FlowStyle)DumperOptions.FlowStyle.fromBoolean((Boolean)flowStyle));
    }

    public String getTag() {
        return this.tag;
    }

    public boolean getImplicit() {
        return this.implicit;
    }

    public DumperOptions.FlowStyle getFlowStyle() {
        return this.flowStyle;
    }

    @Override
    protected String getArguments() {
        return super.getArguments() + ", tag=" + this.tag + ", implicit=" + this.implicit;
    }

    public boolean isFlow() {
        if (DumperOptions.FlowStyle.FLOW != this.flowStyle) return false;
        return true;
    }
}

