/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.chat;

import net.md_5.bungee.api.chat.BaseComponent;

public final class SelectorComponent
extends BaseComponent {
    private String selector;

    public SelectorComponent(SelectorComponent original) {
        super((BaseComponent)original);
        this.setSelector((String)original.getSelector());
    }

    @Override
    public SelectorComponent duplicate() {
        return new SelectorComponent((SelectorComponent)this);
    }

    @Override
    protected void toLegacyText(StringBuilder builder) {
        builder.append((String)this.selector);
        super.toLegacyText((StringBuilder)builder);
    }

    public String getSelector() {
        return this.selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    @Override
    public String toString() {
        return "SelectorComponent(selector=" + this.getSelector() + ")";
    }

    public SelectorComponent(String selector) {
        this.selector = selector;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SelectorComponent)) {
            return false;
        }
        SelectorComponent other = (SelectorComponent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals((Object)o)) {
            return false;
        }
        String this$selector = this.getSelector();
        String other$selector = other.getSelector();
        if (this$selector == null) {
            if (other$selector == null) return true;
            return false;
        }
        if (this$selector.equals((Object)other$selector)) return true;
        return false;
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof SelectorComponent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $selector = this.getSelector();
        return result * 59 + ($selector == null ? 43 : $selector.hashCode());
    }
}

