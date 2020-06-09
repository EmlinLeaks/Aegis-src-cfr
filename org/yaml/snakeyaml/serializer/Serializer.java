/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.emitter.Emitable;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.serializer.AnchorGenerator;
import org.yaml.snakeyaml.serializer.Serializer;
import org.yaml.snakeyaml.serializer.SerializerException;

public final class Serializer {
    private final Emitable emitter;
    private final Resolver resolver;
    private boolean explicitStart;
    private boolean explicitEnd;
    private DumperOptions.Version useVersion;
    private Map<String, String> useTags;
    private Set<Node> serializedNodes;
    private Map<Node, String> anchors;
    private AnchorGenerator anchorGenerator;
    private Boolean closed;
    private Tag explicitRoot;

    public Serializer(Emitable emitter, Resolver resolver, DumperOptions opts, Tag rootTag) {
        this.emitter = emitter;
        this.resolver = resolver;
        this.explicitStart = opts.isExplicitStart();
        this.explicitEnd = opts.isExplicitEnd();
        if (opts.getVersion() != null) {
            this.useVersion = opts.getVersion();
        }
        this.useTags = opts.getTags();
        this.serializedNodes = new HashSet<Node>();
        this.anchors = new HashMap<Node, String>();
        this.anchorGenerator = opts.getAnchorGenerator();
        this.closed = null;
        this.explicitRoot = rootTag;
    }

    public void open() throws IOException {
        if (this.closed == null) {
            this.emitter.emit((Event)new StreamStartEvent(null, null));
            this.closed = Boolean.FALSE;
            return;
        }
        if (!Boolean.TRUE.equals((Object)this.closed)) throw new SerializerException((String)"serializer is already opened");
        throw new SerializerException((String)"serializer is closed");
    }

    public void close() throws IOException {
        if (this.closed == null) {
            throw new SerializerException((String)"serializer is not opened");
        }
        if (Boolean.TRUE.equals((Object)this.closed)) return;
        this.emitter.emit((Event)new StreamEndEvent(null, null));
        this.closed = Boolean.TRUE;
    }

    public void serialize(Node node) throws IOException {
        if (this.closed == null) {
            throw new SerializerException((String)"serializer is not opened");
        }
        if (this.closed.booleanValue()) {
            throw new SerializerException((String)"serializer is closed");
        }
        this.emitter.emit((Event)new DocumentStartEvent(null, null, (boolean)this.explicitStart, (DumperOptions.Version)this.useVersion, this.useTags));
        this.anchorNode((Node)node);
        if (this.explicitRoot != null) {
            node.setTag((Tag)this.explicitRoot);
        }
        this.serializeNode((Node)node, null);
        this.emitter.emit((Event)new DocumentEndEvent(null, null, (boolean)this.explicitEnd));
        this.serializedNodes.clear();
        this.anchors.clear();
    }

    private void anchorNode(Node node) {
        if (node.getNodeId() == NodeId.anchor) {
            node = ((AnchorNode)node).getRealNode();
        }
        if (this.anchors.containsKey((Object)node)) {
            String anchor = this.anchors.get((Object)node);
            if (null != anchor) return;
            anchor = this.anchorGenerator.nextAnchor((Node)node);
            this.anchors.put((Node)node, (String)anchor);
            return;
        }
        this.anchors.put((Node)node, null);
        switch (node.getNodeId()) {
            case sequence: {
                SequenceNode seqNode = (SequenceNode)node;
                List<Node> list = seqNode.getValue();
                Iterator<Node> iterator = list.iterator();
                while (iterator.hasNext()) {
                    Node item = iterator.next();
                    this.anchorNode((Node)item);
                }
                return;
            }
            case mapping: {
                MappingNode mnode = (MappingNode)node;
                List<NodeTuple> map = mnode.getValue();
                Iterator<NodeTuple> iterator = map.iterator();
                while (iterator.hasNext()) {
                    NodeTuple object = iterator.next();
                    Node key = object.getKeyNode();
                    Node value = object.getValueNode();
                    this.anchorNode((Node)key);
                    this.anchorNode((Node)value);
                }
                return;
            }
        }
    }

    private void serializeNode(Node node, Node parent) throws IOException {
        if (node.getNodeId() == NodeId.anchor) {
            node = ((AnchorNode)node).getRealNode();
        }
        String tAlias = this.anchors.get((Object)node);
        if (this.serializedNodes.contains((Object)node)) {
            this.emitter.emit((Event)new AliasEvent((String)tAlias, null, null));
            return;
        }
        this.serializedNodes.add((Node)node);
        switch (1.$SwitchMap$org$yaml$snakeyaml$nodes$NodeId[node.getNodeId().ordinal()]) {
            case 3: {
                ScalarNode scalarNode = (ScalarNode)node;
                Tag detectedTag = this.resolver.resolve((NodeId)NodeId.scalar, (String)scalarNode.getValue(), (boolean)true);
                Tag defaultTag = this.resolver.resolve((NodeId)NodeId.scalar, (String)scalarNode.getValue(), (boolean)false);
                ImplicitTuple tuple = new ImplicitTuple((boolean)node.getTag().equals((Object)detectedTag), (boolean)node.getTag().equals((Object)defaultTag));
                ScalarEvent event = new ScalarEvent((String)tAlias, (String)node.getTag().getValue(), (ImplicitTuple)tuple, (String)scalarNode.getValue(), null, null, (DumperOptions.ScalarStyle)scalarNode.getScalarStyle());
                this.emitter.emit((Event)event);
                return;
            }
            case 1: {
                SequenceNode seqNode = (SequenceNode)node;
                boolean implicitS = node.getTag().equals((Object)this.resolver.resolve((NodeId)NodeId.sequence, null, (boolean)true));
                this.emitter.emit((Event)new SequenceStartEvent((String)tAlias, (String)node.getTag().getValue(), (boolean)implicitS, null, null, (DumperOptions.FlowStyle)seqNode.getFlowStyle()));
                List<Node> list = seqNode.getValue();
                Iterator<Node> iterator = list.iterator();
                do {
                    if (!iterator.hasNext()) {
                        this.emitter.emit((Event)new SequenceEndEvent(null, null));
                        return;
                    }
                    Node item = iterator.next();
                    this.serializeNode((Node)item, (Node)node);
                } while (true);
            }
        }
        Tag implicitTag = this.resolver.resolve((NodeId)NodeId.mapping, null, (boolean)true);
        boolean implicitM = node.getTag().equals((Object)implicitTag);
        this.emitter.emit((Event)new MappingStartEvent((String)tAlias, (String)node.getTag().getValue(), (boolean)implicitM, null, null, (DumperOptions.FlowStyle)((CollectionNode)node).getFlowStyle()));
        MappingNode mnode = (MappingNode)node;
        List<NodeTuple> map = mnode.getValue();
        for (NodeTuple row : map) {
            Node key = row.getKeyNode();
            Node value = row.getValueNode();
            this.serializeNode((Node)key, (Node)mnode);
            this.serializeNode((Node)value, (Node)mnode);
        }
        this.emitter.emit((Event)new MappingEndEvent(null, null));
    }
}

