/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import io.netty.buffer.ByteBuf;
import java.util.LinkedList;
import java.util.List;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class TabCompleteResponse
extends DefinedPacket {
    private int transactionId;
    private Suggestions suggestions;
    private List<String> commands;

    public TabCompleteResponse(int transactionId, Suggestions suggestions) {
        this.transactionId = transactionId;
        this.suggestions = suggestions;
    }

    public TabCompleteResponse(List<String> commands) {
        this.commands = commands;
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        if (protocolVersion >= 393) {
            this.transactionId = TabCompleteResponse.readVarInt((ByteBuf)buf);
            int start = TabCompleteResponse.readVarInt((ByteBuf)buf);
            int length = TabCompleteResponse.readVarInt((ByteBuf)buf);
            StringRange range = StringRange.between((int)start, (int)(start + length));
            int cnt = TabCompleteResponse.readVarInt((ByteBuf)buf);
            LinkedList<Suggestion> matches = new LinkedList<Suggestion>();
            for (int i = 0; i < cnt; ++i) {
                String match = TabCompleteResponse.readString((ByteBuf)buf);
                String tooltip = buf.readBoolean() ? TabCompleteResponse.readString((ByteBuf)buf) : null;
                matches.add((Suggestion)new Suggestion((StringRange)range, (String)match, (Message)new LiteralMessage((String)tooltip)));
            }
            this.suggestions = new Suggestions((StringRange)range, matches);
        }
        if (protocolVersion >= 393) return;
        this.commands = TabCompleteResponse.readStringArray((ByteBuf)buf);
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        if (protocolVersion >= 393) {
            TabCompleteResponse.writeVarInt((int)this.transactionId, (ByteBuf)buf);
            TabCompleteResponse.writeVarInt((int)this.suggestions.getRange().getStart(), (ByteBuf)buf);
            TabCompleteResponse.writeVarInt((int)this.suggestions.getRange().getLength(), (ByteBuf)buf);
            TabCompleteResponse.writeVarInt((int)this.suggestions.getList().size(), (ByteBuf)buf);
            for (Suggestion suggestion : this.suggestions.getList()) {
                TabCompleteResponse.writeString((String)suggestion.getText(), (ByteBuf)buf);
                buf.writeBoolean((boolean)(suggestion.getTooltip() != null && suggestion.getTooltip().getString() != null));
                if (suggestion.getTooltip() == null || suggestion.getTooltip().getString() == null) continue;
                TabCompleteResponse.writeString((String)suggestion.getTooltip().getString(), (ByteBuf)buf);
            }
        }
        if (protocolVersion >= 393) return;
        TabCompleteResponse.writeStringArray(this.commands, (ByteBuf)buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((TabCompleteResponse)this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public Suggestions getSuggestions() {
        return this.suggestions;
    }

    public List<String> getCommands() {
        return this.commands;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public void setSuggestions(Suggestions suggestions) {
        this.suggestions = suggestions;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    @Override
    public String toString() {
        return "TabCompleteResponse(transactionId=" + this.getTransactionId() + ", suggestions=" + this.getSuggestions() + ", commands=" + this.getCommands() + ")";
    }

    public TabCompleteResponse() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TabCompleteResponse)) {
            return false;
        }
        TabCompleteResponse other = (TabCompleteResponse)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getTransactionId() != other.getTransactionId()) {
            return false;
        }
        Suggestions this$suggestions = this.getSuggestions();
        Suggestions other$suggestions = other.getSuggestions();
        if (this$suggestions == null ? other$suggestions != null : !((Object)this$suggestions).equals((Object)other$suggestions)) {
            return false;
        }
        List<String> this$commands = this.getCommands();
        List<String> other$commands = other.getCommands();
        if (this$commands == null) {
            if (other$commands == null) return true;
            return false;
        }
        if (((Object)this$commands).equals(other$commands)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof TabCompleteResponse;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getTransactionId();
        Suggestions $suggestions = this.getSuggestions();
        result = result * 59 + ($suggestions == null ? 43 : ((Object)$suggestions).hashCode());
        List<String> $commands = this.getCommands();
        return result * 59 + ($commands == null ? 43 : ((Object)$commands).hashCode());
    }
}

