/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.chat;

import java.util.Arrays;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;

public final class HoverEvent {
    private final Action action;
    private final BaseComponent[] value;

    public Action getAction() {
        return this.action;
    }

    public BaseComponent[] getValue() {
        return this.value;
    }

    public String toString() {
        return "HoverEvent(action=" + (Object)((Object)this.getAction()) + ", value=" + Arrays.deepToString((Object[])this.getValue()) + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof HoverEvent)) {
            return false;
        }
        HoverEvent other = (HoverEvent)o;
        Action this$action = this.getAction();
        Action other$action = other.getAction();
        if (this$action == null ? other$action != null : !((Object)((Object)this$action)).equals((Object)((Object)other$action))) {
            return false;
        }
        if (Arrays.deepEquals((Object[])this.getValue(), (Object[])other.getValue())) return true;
        return false;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Action $action = this.getAction();
        result = result * 59 + ($action == null ? 43 : ((Object)((Object)$action)).hashCode());
        return result * 59 + Arrays.deepHashCode((Object[])this.getValue());
    }

    public HoverEvent(Action action, BaseComponent[] value) {
        this.action = action;
        this.value = value;
    }
}

