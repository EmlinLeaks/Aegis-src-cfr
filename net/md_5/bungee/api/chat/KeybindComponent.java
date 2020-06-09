/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.chat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;

public final class KeybindComponent
extends BaseComponent {
    private String keybind;

    public KeybindComponent(KeybindComponent original) {
        super((BaseComponent)original);
        this.setKeybind((String)original.getKeybind());
    }

    public KeybindComponent(String keybind) {
        this.setKeybind((String)keybind);
    }

    @Override
    public BaseComponent duplicate() {
        return new KeybindComponent((KeybindComponent)this);
    }

    @Override
    protected void toPlainText(StringBuilder builder) {
        builder.append((String)this.getKeybind());
        super.toPlainText((StringBuilder)builder);
    }

    @Override
    protected void toLegacyText(StringBuilder builder) {
        builder.append((Object)((Object)this.getColor()));
        if (this.isBold()) {
            builder.append((Object)((Object)ChatColor.BOLD));
        }
        if (this.isItalic()) {
            builder.append((Object)((Object)ChatColor.ITALIC));
        }
        if (this.isUnderlined()) {
            builder.append((Object)((Object)ChatColor.UNDERLINE));
        }
        if (this.isStrikethrough()) {
            builder.append((Object)((Object)ChatColor.STRIKETHROUGH));
        }
        if (this.isObfuscated()) {
            builder.append((Object)((Object)ChatColor.MAGIC));
        }
        builder.append((String)this.getKeybind());
        super.toLegacyText((StringBuilder)builder);
    }

    public String getKeybind() {
        return this.keybind;
    }

    public void setKeybind(String keybind) {
        this.keybind = keybind;
    }

    @Override
    public String toString() {
        return "KeybindComponent(keybind=" + this.getKeybind() + ")";
    }

    public KeybindComponent() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof KeybindComponent)) {
            return false;
        }
        KeybindComponent other = (KeybindComponent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals((Object)o)) {
            return false;
        }
        String this$keybind = this.getKeybind();
        String other$keybind = other.getKeybind();
        if (this$keybind == null) {
            if (other$keybind == null) return true;
            return false;
        }
        if (this$keybind.equals((Object)other$keybind)) return true;
        return false;
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof KeybindComponent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $keybind = this.getKeybind();
        return result * 59 + ($keybind == null ? 43 : $keybind.hashCode());
    }
}

