/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.nodes;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.Tag;

public class ScalarNode
extends Node {
    private DumperOptions.ScalarStyle style;
    private String value;

    public ScalarNode(Tag tag, String value, Mark startMark, Mark endMark, DumperOptions.ScalarStyle style) {
        this((Tag)tag, (boolean)true, (String)value, (Mark)startMark, (Mark)endMark, (DumperOptions.ScalarStyle)style);
    }

    public ScalarNode(Tag tag, boolean resolved, String value, Mark startMark, Mark endMark, DumperOptions.ScalarStyle style) {
        super((Tag)tag, (Mark)startMark, (Mark)endMark);
        if (value == null) {
            throw new NullPointerException((String)"value in a Node is required.");
        }
        this.value = value;
        if (style == null) {
            throw new NullPointerException((String)"Scalar style must be provided.");
        }
        this.style = style;
        this.resolved = resolved;
    }

    @Deprecated
    public ScalarNode(Tag tag, String value, Mark startMark, Mark endMark, Character style) {
        this((Tag)tag, (String)value, (Mark)startMark, (Mark)endMark, (DumperOptions.ScalarStyle)DumperOptions.ScalarStyle.createStyle((Character)style));
    }

    @Deprecated
    public ScalarNode(Tag tag, boolean resolved, String value, Mark startMark, Mark endMark, Character style) {
        this((Tag)tag, (boolean)resolved, (String)value, (Mark)startMark, (Mark)endMark, (DumperOptions.ScalarStyle)DumperOptions.ScalarStyle.createStyle((Character)style));
    }

    @Deprecated
    public Character getStyle() {
        return this.style.getChar();
    }

    public DumperOptions.ScalarStyle getScalarStyle() {
        return this.style;
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.scalar;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return "<" + this.getClass().getName() + " (tag=" + this.getTag() + ", value=" + this.getValue() + ")>";
    }

    public boolean isPlain() {
        if (this.style != DumperOptions.ScalarStyle.PLAIN) return false;
        return true;
    }
}

