/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import io.netty.buffer.ByteBuf;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.Commands;

public class Commands
extends DefinedPacket {
    private static final int FLAG_TYPE = 3;
    private static final int FLAG_EXECUTABLE = 4;
    private static final int FLAG_REDIRECT = 8;
    private static final int FLAG_SUGGESTIONS = 16;
    private static final int NODE_ROOT = 0;
    private static final int NODE_LITERAL = 1;
    private static final int NODE_ARGUMENT = 2;
    private RootCommandNode root;

    @Override
    public void read(ByteBuf buf) {
        boolean mustCycle;
        int nodeCount = Commands.readVarInt((ByteBuf)buf);
        NetworkNode[] nodes = new NetworkNode[nodeCount];
        ArrayDeque<NetworkNode> nodeQueue = new ArrayDeque<NetworkNode>((int)nodes.length);
        for (int i = 0; i < nodeCount; ++i) {
            NetworkNode node;
            ArgumentBuilder argumentBuilder;
            byte flags = buf.readByte();
            int[] children = Commands.readVarIntArray((ByteBuf)buf);
            int redirectNode = (flags & 8) != 0 ? Commands.readVarInt((ByteBuf)buf) : 0;
            switch (flags & 3) {
                case 0: {
                    argumentBuilder = null;
                    break;
                }
                case 1: {
                    argumentBuilder = LiteralArgumentBuilder.literal((String)Commands.readString((ByteBuf)buf));
                    break;
                }
                case 2: {
                    String name = Commands.readString((ByteBuf)buf);
                    String parser = Commands.readString((ByteBuf)buf);
                    argumentBuilder = RequiredArgumentBuilder.argument((String)name, ArgumentRegistry.read((String)((String)parser), (ByteBuf)((ByteBuf)buf)));
                    if ((flags & 16) == 0) break;
                    String suggster = Commands.readString((ByteBuf)buf);
                    ((RequiredArgumentBuilder)argumentBuilder).suggests(SuggestionRegistry.getProvider((String)((String)suggster)));
                    break;
                }
                default: {
                    throw new IllegalArgumentException((String)("Unhandled node type " + flags));
                }
            }
            nodes[i] = node = new NetworkNode(argumentBuilder, (byte)flags, (int)redirectNode, (int[])children);
            nodeQueue.add(node);
        }
        do {
            if (nodeQueue.isEmpty()) {
                int rootIndex = Commands.readVarInt((ByteBuf)buf);
                this.root = (RootCommandNode)((NetworkNode)nodes[rootIndex]).command;
                return;
            }
            mustCycle = false;
            Iterator<E> iter = nodeQueue.iterator();
            while (iter.hasNext()) {
                NetworkNode node = (NetworkNode)iter.next();
                if (!((NetworkNode)node).buildSelf((NetworkNode[])((NetworkNode[])nodes))) continue;
                iter.remove();
                mustCycle = true;
            }
        } while (mustCycle);
        throw new IllegalStateException((String)"Did not finish building root node");
    }

    @Override
    public void write(ByteBuf buf) {
        LinkedHashMap<CommandNode, Integer> indexMap = new LinkedHashMap<CommandNode, Integer>();
        ArrayDeque<CommandNode<S>> nodeQueue = new ArrayDeque<CommandNode<S>>();
        nodeQueue.add(this.root);
        while (!nodeQueue.isEmpty()) {
            CommandNode command = (CommandNode)nodeQueue.pollFirst();
            if (indexMap.containsKey((Object)command)) continue;
            int currentIndex = indexMap.size();
            indexMap.put(command, Integer.valueOf((int)currentIndex));
            nodeQueue.addAll(command.getChildren());
            if (command.getRedirect() == null) continue;
            nodeQueue.add(command.getRedirect());
        }
        Commands.writeVarInt((int)indexMap.size(), (ByteBuf)buf);
        int currentIndex = 0;
        for (Map.Entry<K, V> entry : indexMap.entrySet()) {
            Preconditions.checkState((boolean)(((Integer)entry.getValue()).intValue() == currentIndex++), (Object)"Iteration out of order!");
            CommandNode node = (CommandNode)entry.getKey();
            int flags = 0;
            if (node.getRedirect() != null) {
                flags = (int)((byte)(flags | 8));
            }
            if (node.getCommand() != null) {
                flags = (int)((byte)(flags | 4));
            }
            if (node instanceof RootCommandNode) {
                flags = (int)((byte)(flags | 0));
            } else if (node instanceof LiteralCommandNode) {
                flags = (int)((byte)(flags | 1));
            } else {
                if (!(node instanceof ArgumentCommandNode)) throw new IllegalArgumentException((String)("Unhandled node type " + node));
                flags = (int)((byte)(flags | 2));
                if (((ArgumentCommandNode)node).getCustomSuggestions() != null) {
                    flags = (int)((byte)(flags | 16));
                }
            }
            buf.writeByte((int)flags);
            Commands.writeVarInt((int)node.getChildren().size(), (ByteBuf)buf);
            for (CommandNode<S> child : node.getChildren()) {
                Commands.writeVarInt((int)((Integer)indexMap.get(child)).intValue(), (ByteBuf)buf);
            }
            if (node.getRedirect() != null) {
                Commands.writeVarInt((int)((Integer)indexMap.get(node.getRedirect())).intValue(), (ByteBuf)buf);
            }
            if (node instanceof LiteralCommandNode) {
                Commands.writeString((String)((LiteralCommandNode)node).getLiteral(), (ByteBuf)buf);
                continue;
            }
            if (!(node instanceof ArgumentCommandNode)) continue;
            ArgumentCommandNode argumentNode = (ArgumentCommandNode)node;
            Commands.writeString((String)argumentNode.getName(), (ByteBuf)buf);
            ArgumentRegistry.write(argumentNode.getType(), (ByteBuf)((ByteBuf)buf));
            if (argumentNode.getCustomSuggestions() == null) continue;
            Commands.writeString((String)SuggestionRegistry.getKey((SuggestionProvider<SuggestionRegistry.DummyProvider>)argumentNode.getCustomSuggestions()), (ByteBuf)buf);
        }
        int rootIndex = ((Integer)indexMap.get((Object)this.root)).intValue();
        Preconditions.checkState((boolean)(rootIndex == 0), (Object)"How did root not land up at index 0?!?");
        Commands.writeVarInt((int)rootIndex, (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((Commands)this);
    }

    private static byte binaryFlag(boolean first, boolean second) {
        byte ret = 0;
        if (first) {
            ret = (byte)(ret | true ? 1 : 0);
        }
        if (!second) return ret;
        return (byte)(ret | 2);
    }

    public RootCommandNode getRoot() {
        return this.root;
    }

    public void setRoot(RootCommandNode root) {
        this.root = root;
    }

    @Override
    public String toString() {
        return "Commands(root=" + this.getRoot() + ")";
    }

    public Commands() {
    }

    public Commands(RootCommandNode root) {
        this.root = root;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Commands)) {
            return false;
        }
        Commands other = (Commands)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        RootCommandNode this$root = this.getRoot();
        RootCommandNode other$root = other.getRoot();
        if (this$root == null) {
            if (other$root == null) return true;
            return false;
        }
        if (((Object)this$root).equals((Object)other$root)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Commands;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        RootCommandNode $root = this.getRoot();
        return result * 59 + ($root == null ? 43 : ((Object)$root).hashCode());
    }

    static /* synthetic */ byte access$700(boolean x0, boolean x1) {
        return Commands.binaryFlag((boolean)x0, (boolean)x1);
    }
}

