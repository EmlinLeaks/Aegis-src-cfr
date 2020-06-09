/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.composer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.composer.ComposerException;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.NodeEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.resolver.Resolver;

public class Composer {
    protected final Parser parser;
    private final Resolver resolver;
    private final Map<String, Node> anchors;
    private final Set<Node> recursiveNodes;

    public Composer(Parser parser, Resolver resolver) {
        this.parser = parser;
        this.resolver = resolver;
        this.anchors = new HashMap<String, Node>();
        this.recursiveNodes = new HashSet<Node>();
    }

    public boolean checkNode() {
        if (this.parser.checkEvent((Event.ID)Event.ID.StreamStart)) {
            this.parser.getEvent();
        }
        if (this.parser.checkEvent((Event.ID)Event.ID.StreamEnd)) return false;
        return true;
    }

    public Node getNode() {
        this.parser.getEvent();
        Node node = this.composeNode(null);
        this.parser.getEvent();
        this.anchors.clear();
        this.recursiveNodes.clear();
        return node;
    }

    public Node getSingleNode() {
        this.parser.getEvent();
        Node document = null;
        if (!this.parser.checkEvent((Event.ID)Event.ID.StreamEnd)) {
            document = this.getNode();
        }
        if (!this.parser.checkEvent((Event.ID)Event.ID.StreamEnd)) {
            Event event = this.parser.getEvent();
            throw new ComposerException((String)"expected a single document in the stream", (Mark)document.getStartMark(), (String)"but found another document", (Mark)event.getStartMark());
        }
        this.parser.getEvent();
        return document;
    }

    private Node composeNode(Node parent) {
        Node node;
        if (parent != null) {
            this.recursiveNodes.add((Node)parent);
        }
        if (this.parser.checkEvent((Event.ID)Event.ID.Alias)) {
            AliasEvent event = (AliasEvent)this.parser.getEvent();
            String anchor = event.getAnchor();
            if (!this.anchors.containsKey((Object)anchor)) {
                throw new ComposerException(null, null, (String)("found undefined alias " + anchor), (Mark)event.getStartMark());
            }
            node = this.anchors.get((Object)anchor);
            if (this.recursiveNodes.remove((Object)node)) {
                node.setTwoStepsConstruction((boolean)true);
            }
        } else {
            NodeEvent event = (NodeEvent)this.parser.peekEvent();
            String anchor = event.getAnchor();
            node = this.parser.checkEvent((Event.ID)Event.ID.Scalar) ? this.composeScalarNode((String)anchor) : (this.parser.checkEvent((Event.ID)Event.ID.SequenceStart) ? this.composeSequenceNode((String)anchor) : this.composeMappingNode((String)anchor));
        }
        this.recursiveNodes.remove((Object)parent);
        return node;
    }

    protected Node composeScalarNode(String anchor) {
        Tag nodeTag;
        ScalarEvent ev = (ScalarEvent)this.parser.getEvent();
        String tag = ev.getTag();
        boolean resolved = false;
        if (tag == null || tag.equals((Object)"!")) {
            nodeTag = this.resolver.resolve((NodeId)NodeId.scalar, (String)ev.getValue(), (boolean)ev.getImplicit().canOmitTagInPlainScalar());
            resolved = true;
        } else {
            nodeTag = new Tag((String)tag);
        }
        ScalarNode node = new ScalarNode((Tag)nodeTag, (boolean)resolved, (String)ev.getValue(), (Mark)ev.getStartMark(), (Mark)ev.getEndMark(), (DumperOptions.ScalarStyle)ev.getScalarStyle());
        if (anchor == null) return node;
        node.setAnchor((String)anchor);
        this.anchors.put((String)anchor, (Node)node);
        return node;
    }

    protected Node composeSequenceNode(String anchor) {
        Tag nodeTag;
        SequenceStartEvent startEvent = (SequenceStartEvent)this.parser.getEvent();
        String tag = startEvent.getTag();
        boolean resolved = false;
        if (tag == null || tag.equals((Object)"!")) {
            nodeTag = this.resolver.resolve((NodeId)NodeId.sequence, null, (boolean)startEvent.getImplicit());
            resolved = true;
        } else {
            nodeTag = new Tag((String)tag);
        }
        ArrayList<Node> children = new ArrayList<Node>();
        SequenceNode node = new SequenceNode((Tag)nodeTag, (boolean)resolved, children, (Mark)startEvent.getStartMark(), null, (DumperOptions.FlowStyle)startEvent.getFlowStyle());
        if (anchor != null) {
            node.setAnchor((String)anchor);
            this.anchors.put((String)anchor, (Node)node);
        }
        do {
            if (this.parser.checkEvent((Event.ID)Event.ID.SequenceEnd)) {
                Event endEvent = this.parser.getEvent();
                node.setEndMark((Mark)endEvent.getEndMark());
                return node;
            }
            children.add((Node)this.composeNode((Node)node));
        } while (true);
    }

    protected Node composeMappingNode(String anchor) {
        Tag nodeTag;
        MappingStartEvent startEvent = (MappingStartEvent)this.parser.getEvent();
        String tag = startEvent.getTag();
        boolean resolved = false;
        if (tag == null || tag.equals((Object)"!")) {
            nodeTag = this.resolver.resolve((NodeId)NodeId.mapping, null, (boolean)startEvent.getImplicit());
            resolved = true;
        } else {
            nodeTag = new Tag((String)tag);
        }
        ArrayList<NodeTuple> children = new ArrayList<NodeTuple>();
        MappingNode node = new MappingNode((Tag)nodeTag, (boolean)resolved, children, (Mark)startEvent.getStartMark(), null, (DumperOptions.FlowStyle)startEvent.getFlowStyle());
        if (anchor != null) {
            node.setAnchor((String)anchor);
            this.anchors.put((String)anchor, (Node)node);
        }
        do {
            if (this.parser.checkEvent((Event.ID)Event.ID.MappingEnd)) {
                Event endEvent = this.parser.getEvent();
                node.setEndMark((Mark)endEvent.getEndMark());
                return node;
            }
            this.composeMappingChildren(children, (MappingNode)node);
        } while (true);
    }

    protected void composeMappingChildren(List<NodeTuple> children, MappingNode node) {
        Node itemKey = this.composeKeyNode((MappingNode)node);
        if (itemKey.getTag().equals((Object)Tag.MERGE)) {
            node.setMerged((boolean)true);
        }
        Node itemValue = this.composeValueNode((MappingNode)node);
        children.add((NodeTuple)new NodeTuple((Node)itemKey, (Node)itemValue));
    }

    protected Node composeKeyNode(MappingNode node) {
        return this.composeNode((Node)node);
    }

    protected Node composeValueNode(MappingNode node) {
        return this.composeNode((Node)node);
    }
}

