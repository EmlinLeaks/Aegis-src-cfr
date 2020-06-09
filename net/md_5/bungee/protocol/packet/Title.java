/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.Title;

public class Title
extends DefinedPacket {
    private Action action;
    private String text;
    private int fadeIn;
    private int stay;
    private int fadeOut;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        int index = Title.readVarInt((ByteBuf)buf);
        if (protocolVersion <= 210 && index >= 2) {
            ++index;
        }
        this.action = Action.values()[index];
        switch (1.$SwitchMap$net$md_5$bungee$protocol$packet$Title$Action[this.action.ordinal()]) {
            case 1: 
            case 2: 
            case 3: {
                this.text = Title.readString((ByteBuf)buf);
                return;
            }
            case 4: {
                this.fadeIn = buf.readInt();
                this.stay = buf.readInt();
                this.fadeOut = buf.readInt();
            }
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        int index = this.action.ordinal();
        if (protocolVersion <= 210 && index >= 2) {
            --index;
        }
        Title.writeVarInt((int)index, (ByteBuf)buf);
        switch (1.$SwitchMap$net$md_5$bungee$protocol$packet$Title$Action[this.action.ordinal()]) {
            case 1: 
            case 2: 
            case 3: {
                Title.writeString((String)this.text, (ByteBuf)buf);
                return;
            }
            case 4: {
                buf.writeInt((int)this.fadeIn);
                buf.writeInt((int)this.stay);
                buf.writeInt((int)this.fadeOut);
            }
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle((Title)this);
    }

    public Action getAction() {
        return this.action;
    }

    public String getText() {
        return this.text;
    }

    public int getFadeIn() {
        return this.fadeIn;
    }

    public int getStay() {
        return this.stay;
    }

    public int getFadeOut() {
        return this.fadeOut;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
    }

    public void setStay(int stay) {
        this.stay = stay;
    }

    public void setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
    }

    @Override
    public String toString() {
        return "Title(action=" + (Object)((Object)this.getAction()) + ", text=" + this.getText() + ", fadeIn=" + this.getFadeIn() + ", stay=" + this.getStay() + ", fadeOut=" + this.getFadeOut() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Title)) {
            return false;
        }
        Title other = (Title)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Action this$action = this.getAction();
        Action other$action = other.getAction();
        if (this$action == null ? other$action != null : !((Object)((Object)this$action)).equals((Object)((Object)other$action))) {
            return false;
        }
        String this$text = this.getText();
        String other$text = other.getText();
        if (this$text == null ? other$text != null : !this$text.equals((Object)other$text)) {
            return false;
        }
        if (this.getFadeIn() != other.getFadeIn()) {
            return false;
        }
        if (this.getStay() != other.getStay()) {
            return false;
        }
        if (this.getFadeOut() == other.getFadeOut()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Title;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Action $action = this.getAction();
        result = result * 59 + ($action == null ? 43 : ((Object)((Object)$action)).hashCode());
        String $text = this.getText();
        result = result * 59 + ($text == null ? 43 : $text.hashCode());
        result = result * 59 + this.getFadeIn();
        result = result * 59 + this.getStay();
        return result * 59 + this.getFadeOut();
    }
}

