/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.events.NodeEvent;

public final class ScalarEvent
extends NodeEvent {
    private final String tag;
    private final DumperOptions.ScalarStyle style;
    private final String value;
    private final ImplicitTuple implicit;

    public ScalarEvent(String anchor, String tag, ImplicitTuple implicit, String value, Mark startMark, Mark endMark, DumperOptions.ScalarStyle style) {
        super((String)anchor, (Mark)startMark, (Mark)endMark);
        this.tag = tag;
        this.implicit = implicit;
        if (value == null) {
            throw new NullPointerException((String)"Value must be provided.");
        }
        this.value = value;
        if (style == null) {
            throw new NullPointerException((String)"Style must be provided.");
        }
        this.style = style;
    }

    @Deprecated
    public ScalarEvent(String anchor, String tag, ImplicitTuple implicit, String value, Mark startMark, Mark endMark, Character style) {
        this((String)anchor, (String)tag, (ImplicitTuple)implicit, (String)value, (Mark)startMark, (Mark)endMark, (DumperOptions.ScalarStyle)DumperOptions.ScalarStyle.createStyle((Character)style));
    }

    public String getTag() {
        return this.tag;
    }

    public DumperOptions.ScalarStyle getScalarStyle() {
        return this.style;
    }

    @Deprecated
    public Character getStyle() {
        return this.style.getChar();
    }

    public String getValue() {
        return this.value;
    }

    public ImplicitTuple getImplicit() {
        return this.implicit;
    }

    @Override
    protected String getArguments() {
        return super.getArguments() + ", tag=" + this.tag + ", " + this.implicit + ", value=" + this.value;
    }

    @Override
    public boolean is(Event.ID id) {
        if (Event.ID.Scalar != id) return false;
        return true;
    }

    public boolean isPlain() {
        if (this.style != DumperOptions.ScalarStyle.PLAIN) return false;
        return true;
    }
}

