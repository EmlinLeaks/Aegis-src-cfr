/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.chat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class BaseComponent {
    BaseComponent parent;
    private ChatColor color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underlined;
    private Boolean strikethrough;
    private Boolean obfuscated;
    private String insertion;
    private List<BaseComponent> extra;
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;

    BaseComponent(BaseComponent old) {
        this.copyFormatting((BaseComponent)old, (ComponentBuilder.FormatRetention)ComponentBuilder.FormatRetention.ALL, (boolean)true);
        if (old.getExtra() == null) return;
        Iterator<BaseComponent> iterator = old.getExtra().iterator();
        while (iterator.hasNext()) {
            BaseComponent extra = iterator.next();
            this.addExtra((BaseComponent)extra.duplicate());
        }
    }

    public void copyFormatting(BaseComponent component) {
        this.copyFormatting((BaseComponent)component, (ComponentBuilder.FormatRetention)ComponentBuilder.FormatRetention.ALL, (boolean)true);
    }

    public void copyFormatting(BaseComponent component, boolean replace) {
        this.copyFormatting((BaseComponent)component, (ComponentBuilder.FormatRetention)ComponentBuilder.FormatRetention.ALL, (boolean)replace);
    }

    public void copyFormatting(BaseComponent component, ComponentBuilder.FormatRetention retention, boolean replace) {
        if (retention == ComponentBuilder.FormatRetention.EVENTS || retention == ComponentBuilder.FormatRetention.ALL) {
            if (replace || this.clickEvent == null) {
                this.setClickEvent((ClickEvent)component.getClickEvent());
            }
            if (replace || this.hoverEvent == null) {
                this.setHoverEvent((HoverEvent)component.getHoverEvent());
            }
        }
        if (retention != ComponentBuilder.FormatRetention.FORMATTING) {
            if (retention != ComponentBuilder.FormatRetention.ALL) return;
        }
        if (replace || this.color == null) {
            this.setColor((ChatColor)component.getColorRaw());
        }
        if (replace || this.bold == null) {
            this.setBold((Boolean)component.isBoldRaw());
        }
        if (replace || this.italic == null) {
            this.setItalic((Boolean)component.isItalicRaw());
        }
        if (replace || this.underlined == null) {
            this.setUnderlined((Boolean)component.isUnderlinedRaw());
        }
        if (replace || this.strikethrough == null) {
            this.setStrikethrough((Boolean)component.isStrikethroughRaw());
        }
        if (replace || this.obfuscated == null) {
            this.setObfuscated((Boolean)component.isObfuscatedRaw());
        }
        if (!replace) {
            if (this.insertion != null) return;
        }
        this.setInsertion((String)component.getInsertion());
    }

    public void retain(ComponentBuilder.FormatRetention retention) {
        if (retention == ComponentBuilder.FormatRetention.FORMATTING || retention == ComponentBuilder.FormatRetention.NONE) {
            this.setClickEvent(null);
            this.setHoverEvent(null);
        }
        if (retention != ComponentBuilder.FormatRetention.EVENTS) {
            if (retention != ComponentBuilder.FormatRetention.NONE) return;
        }
        this.setColor(null);
        this.setBold(null);
        this.setItalic(null);
        this.setUnderlined(null);
        this.setStrikethrough(null);
        this.setObfuscated(null);
        this.setInsertion(null);
    }

    public abstract BaseComponent duplicate();

    @Deprecated
    public BaseComponent duplicateWithoutFormatting() {
        BaseComponent component = this.duplicate();
        component.retain((ComponentBuilder.FormatRetention)ComponentBuilder.FormatRetention.NONE);
        return component;
    }

    public static String toLegacyText(BaseComponent ... components) {
        StringBuilder builder = new StringBuilder();
        BaseComponent[] arrbaseComponent = components;
        int n = arrbaseComponent.length;
        int n2 = 0;
        while (n2 < n) {
            BaseComponent msg = arrbaseComponent[n2];
            builder.append((String)msg.toLegacyText());
            ++n2;
        }
        return builder.toString();
    }

    public static String toPlainText(BaseComponent ... components) {
        StringBuilder builder = new StringBuilder();
        BaseComponent[] arrbaseComponent = components;
        int n = arrbaseComponent.length;
        int n2 = 0;
        while (n2 < n) {
            BaseComponent msg = arrbaseComponent[n2];
            builder.append((String)msg.toPlainText());
            ++n2;
        }
        return builder.toString();
    }

    public ChatColor getColor() {
        if (this.color != null) return this.color;
        if (this.parent != null) return this.parent.getColor();
        return ChatColor.WHITE;
    }

    public ChatColor getColorRaw() {
        return this.color;
    }

    public boolean isBold() {
        if (this.bold != null) return this.bold.booleanValue();
        if (this.parent == null) return false;
        if (!this.parent.isBold()) return false;
        return true;
    }

    public Boolean isBoldRaw() {
        return this.bold;
    }

    public boolean isItalic() {
        if (this.italic != null) return this.italic.booleanValue();
        if (this.parent == null) return false;
        if (!this.parent.isItalic()) return false;
        return true;
    }

    public Boolean isItalicRaw() {
        return this.italic;
    }

    public boolean isUnderlined() {
        if (this.underlined != null) return this.underlined.booleanValue();
        if (this.parent == null) return false;
        if (!this.parent.isUnderlined()) return false;
        return true;
    }

    public Boolean isUnderlinedRaw() {
        return this.underlined;
    }

    public boolean isStrikethrough() {
        if (this.strikethrough != null) return this.strikethrough.booleanValue();
        if (this.parent == null) return false;
        if (!this.parent.isStrikethrough()) return false;
        return true;
    }

    public Boolean isStrikethroughRaw() {
        return this.strikethrough;
    }

    public boolean isObfuscated() {
        if (this.obfuscated != null) return this.obfuscated.booleanValue();
        if (this.parent == null) return false;
        if (!this.parent.isObfuscated()) return false;
        return true;
    }

    public Boolean isObfuscatedRaw() {
        return this.obfuscated;
    }

    public void setExtra(List<BaseComponent> components) {
        Iterator<BaseComponent> iterator = components.iterator();
        do {
            if (!iterator.hasNext()) {
                this.extra = components;
                return;
            }
            BaseComponent component = iterator.next();
            component.parent = this;
        } while (true);
    }

    public void addExtra(String text) {
        this.addExtra((BaseComponent)new TextComponent((String)text));
    }

    public void addExtra(BaseComponent component) {
        if (this.extra == null) {
            this.extra = new ArrayList<BaseComponent>();
        }
        component.parent = this;
        this.extra.add((BaseComponent)component);
    }

    public boolean hasFormatting() {
        if (this.color != null) return true;
        if (this.bold != null) return true;
        if (this.italic != null) return true;
        if (this.underlined != null) return true;
        if (this.strikethrough != null) return true;
        if (this.obfuscated != null) return true;
        if (this.insertion != null) return true;
        if (this.hoverEvent != null) return true;
        if (this.clickEvent != null) return true;
        return false;
    }

    public String toPlainText() {
        StringBuilder builder = new StringBuilder();
        this.toPlainText((StringBuilder)builder);
        return builder.toString();
    }

    void toPlainText(StringBuilder builder) {
        if (this.extra == null) return;
        Iterator<BaseComponent> iterator = this.extra.iterator();
        while (iterator.hasNext()) {
            BaseComponent e = iterator.next();
            e.toPlainText((StringBuilder)builder);
        }
    }

    public String toLegacyText() {
        StringBuilder builder = new StringBuilder();
        this.toLegacyText((StringBuilder)builder);
        return builder.toString();
    }

    void toLegacyText(StringBuilder builder) {
        if (this.extra == null) return;
        Iterator<BaseComponent> iterator = this.extra.iterator();
        while (iterator.hasNext()) {
            BaseComponent e = iterator.next();
            e.toLegacyText((StringBuilder)builder);
        }
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public void setBold(Boolean bold) {
        this.bold = bold;
    }

    public void setItalic(Boolean italic) {
        this.italic = italic;
    }

    public void setUnderlined(Boolean underlined) {
        this.underlined = underlined;
    }

    public void setStrikethrough(Boolean strikethrough) {
        this.strikethrough = strikethrough;
    }

    public void setObfuscated(Boolean obfuscated) {
        this.obfuscated = obfuscated;
    }

    public void setInsertion(String insertion) {
        this.insertion = insertion;
    }

    public void setClickEvent(ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
    }

    public void setHoverEvent(HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
    }

    public String toString() {
        return "BaseComponent(color=" + (Object)((Object)this.getColor()) + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", strikethrough=" + this.strikethrough + ", obfuscated=" + this.obfuscated + ", insertion=" + this.getInsertion() + ", extra=" + this.getExtra() + ", clickEvent=" + this.getClickEvent() + ", hoverEvent=" + this.getHoverEvent() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BaseComponent)) {
            return false;
        }
        BaseComponent other = (BaseComponent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        BaseComponent this$parent = this.parent;
        BaseComponent other$parent = other.parent;
        if (this$parent == null ? other$parent != null : !((Object)this$parent).equals((Object)other$parent)) {
            return false;
        }
        ChatColor this$color = this.getColor();
        ChatColor other$color = other.getColor();
        if (this$color == null ? other$color != null : !((Object)((Object)this$color)).equals((Object)((Object)other$color))) {
            return false;
        }
        Boolean this$bold = this.bold;
        Boolean other$bold = other.bold;
        if (this$bold == null ? other$bold != null : !((Object)this$bold).equals((Object)other$bold)) {
            return false;
        }
        Boolean this$italic = this.italic;
        Boolean other$italic = other.italic;
        if (this$italic == null ? other$italic != null : !((Object)this$italic).equals((Object)other$italic)) {
            return false;
        }
        Boolean this$underlined = this.underlined;
        Boolean other$underlined = other.underlined;
        if (this$underlined == null ? other$underlined != null : !((Object)this$underlined).equals((Object)other$underlined)) {
            return false;
        }
        Boolean this$strikethrough = this.strikethrough;
        Boolean other$strikethrough = other.strikethrough;
        if (this$strikethrough == null ? other$strikethrough != null : !((Object)this$strikethrough).equals((Object)other$strikethrough)) {
            return false;
        }
        Boolean this$obfuscated = this.obfuscated;
        Boolean other$obfuscated = other.obfuscated;
        if (this$obfuscated == null ? other$obfuscated != null : !((Object)this$obfuscated).equals((Object)other$obfuscated)) {
            return false;
        }
        String this$insertion = this.getInsertion();
        String other$insertion = other.getInsertion();
        if (this$insertion == null ? other$insertion != null : !this$insertion.equals((Object)other$insertion)) {
            return false;
        }
        List<BaseComponent> this$extra = this.getExtra();
        List<BaseComponent> other$extra = other.getExtra();
        if (this$extra == null ? other$extra != null : !((Object)this$extra).equals(other$extra)) {
            return false;
        }
        ClickEvent this$clickEvent = this.getClickEvent();
        ClickEvent other$clickEvent = other.getClickEvent();
        if (this$clickEvent == null ? other$clickEvent != null : !((Object)this$clickEvent).equals((Object)other$clickEvent)) {
            return false;
        }
        HoverEvent this$hoverEvent = this.getHoverEvent();
        HoverEvent other$hoverEvent = other.getHoverEvent();
        if (this$hoverEvent == null) {
            if (other$hoverEvent == null) return true;
            return false;
        }
        if (((Object)this$hoverEvent).equals((Object)other$hoverEvent)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof BaseComponent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        BaseComponent $parent = this.parent;
        result = result * 59 + ($parent == null ? 43 : ((Object)$parent).hashCode());
        ChatColor $color = this.getColor();
        result = result * 59 + ($color == null ? 43 : ((Object)((Object)$color)).hashCode());
        Boolean $bold = this.bold;
        result = result * 59 + ($bold == null ? 43 : ((Object)$bold).hashCode());
        Boolean $italic = this.italic;
        result = result * 59 + ($italic == null ? 43 : ((Object)$italic).hashCode());
        Boolean $underlined = this.underlined;
        result = result * 59 + ($underlined == null ? 43 : ((Object)$underlined).hashCode());
        Boolean $strikethrough = this.strikethrough;
        result = result * 59 + ($strikethrough == null ? 43 : ((Object)$strikethrough).hashCode());
        Boolean $obfuscated = this.obfuscated;
        result = result * 59 + ($obfuscated == null ? 43 : ((Object)$obfuscated).hashCode());
        String $insertion = this.getInsertion();
        result = result * 59 + ($insertion == null ? 43 : $insertion.hashCode());
        List<BaseComponent> $extra = this.getExtra();
        result = result * 59 + ($extra == null ? 43 : ((Object)$extra).hashCode());
        ClickEvent $clickEvent = this.getClickEvent();
        result = result * 59 + ($clickEvent == null ? 43 : ((Object)$clickEvent).hashCode());
        HoverEvent $hoverEvent = this.getHoverEvent();
        return result * 59 + ($hoverEvent == null ? 43 : ((Object)$hoverEvent).hashCode());
    }

    public BaseComponent() {
    }

    public String getInsertion() {
        return this.insertion;
    }

    public List<BaseComponent> getExtra() {
        return this.extra;
    }

    public ClickEvent getClickEvent() {
        return this.clickEvent;
    }

    public HoverEvent getHoverEvent() {
        return this.hoverEvent;
    }
}

