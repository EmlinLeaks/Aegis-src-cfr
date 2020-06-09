/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.chat;

import net.md_5.bungee.api.chat.ClickEvent;

public final class ClickEvent {
    private final Action action;
    private final String value;

    public Action getAction() {
        return this.action;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return "ClickEvent(action=" + (Object)((Object)this.getAction()) + ", value=" + this.getValue() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ClickEvent)) {
            return false;
        }
        ClickEvent other = (ClickEvent)o;
        Action this$action = this.getAction();
        Action other$action = other.getAction();
        if (this$action == null ? other$action != null : !((Object)((Object)this$action)).equals((Object)((Object)other$action))) {
            return false;
        }
        String this$value = this.getValue();
        String other$value = other.getValue();
        if (this$value == null) {
            if (other$value == null) return true;
            return false;
        }
        if (this$value.equals((Object)other$value)) return true;
        return false;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Action $action = this.getAction();
        result = result * 59 + ($action == null ? 43 : ((Object)((Object)$action)).hashCode());
        String $value = this.getValue();
        return result * 59 + ($value == null ? 43 : $value.hashCode());
    }

    public ClickEvent(Action action, String value) {
        this.action = action;
        this.value = value;
    }
}

