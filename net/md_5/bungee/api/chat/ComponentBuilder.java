/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.chat;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public final class ComponentBuilder {
    private BaseComponent current;
    private final List<BaseComponent> parts = new ArrayList<BaseComponent>();

    public ComponentBuilder(ComponentBuilder original) {
        this.current = original.current.duplicate();
        Iterator<BaseComponent> iterator = original.parts.iterator();
        while (iterator.hasNext()) {
            BaseComponent baseComponent = iterator.next();
            this.parts.add((BaseComponent)baseComponent.duplicate());
        }
    }

    public ComponentBuilder(String text) {
        this.current = new TextComponent((String)text);
    }

    public ComponentBuilder(BaseComponent component) {
        this.current = component.duplicate();
    }

    public ComponentBuilder append(BaseComponent component) {
        return this.append((BaseComponent)component, (FormatRetention)FormatRetention.ALL);
    }

    public ComponentBuilder append(BaseComponent component, FormatRetention retention) {
        this.parts.add((BaseComponent)this.current);
        BaseComponent previous = this.current;
        this.current = component.duplicate();
        this.current.copyFormatting((BaseComponent)previous, (FormatRetention)retention, (boolean)false);
        return this;
    }

    public ComponentBuilder append(BaseComponent[] components) {
        return this.append((BaseComponent[])components, (FormatRetention)FormatRetention.ALL);
    }

    public ComponentBuilder append(BaseComponent[] components, FormatRetention retention) {
        Preconditions.checkArgument((boolean)(components.length != 0), (Object)"No components to append");
        BaseComponent previous = this.current;
        BaseComponent[] arrbaseComponent = components;
        int n = arrbaseComponent.length;
        int n2 = 0;
        while (n2 < n) {
            BaseComponent component = arrbaseComponent[n2];
            this.parts.add((BaseComponent)this.current);
            this.current = component.duplicate();
            this.current.copyFormatting((BaseComponent)previous, (FormatRetention)retention, (boolean)false);
            ++n2;
        }
        return this;
    }

    public ComponentBuilder append(String text) {
        return this.append((String)text, (FormatRetention)FormatRetention.ALL);
    }

    public ComponentBuilder appendLegacy(String text) {
        return this.append((BaseComponent[])TextComponent.fromLegacyText((String)text));
    }

    public ComponentBuilder append(String text, FormatRetention retention) {
        this.parts.add((BaseComponent)this.current);
        BaseComponent old = this.current;
        this.current = new TextComponent((String)text);
        this.current.copyFormatting((BaseComponent)old, (FormatRetention)retention, (boolean)false);
        return this;
    }

    public ComponentBuilder append(Joiner joiner) {
        return joiner.join((ComponentBuilder)this, (FormatRetention)FormatRetention.ALL);
    }

    public ComponentBuilder append(Joiner joiner, FormatRetention retention) {
        return joiner.join((ComponentBuilder)this, (FormatRetention)retention);
    }

    public ComponentBuilder color(ChatColor color) {
        this.current.setColor((ChatColor)color);
        return this;
    }

    public ComponentBuilder bold(boolean bold) {
        this.current.setBold((Boolean)Boolean.valueOf((boolean)bold));
        return this;
    }

    public ComponentBuilder italic(boolean italic) {
        this.current.setItalic((Boolean)Boolean.valueOf((boolean)italic));
        return this;
    }

    public ComponentBuilder underlined(boolean underlined) {
        this.current.setUnderlined((Boolean)Boolean.valueOf((boolean)underlined));
        return this;
    }

    public ComponentBuilder strikethrough(boolean strikethrough) {
        this.current.setStrikethrough((Boolean)Boolean.valueOf((boolean)strikethrough));
        return this;
    }

    public ComponentBuilder obfuscated(boolean obfuscated) {
        this.current.setObfuscated((Boolean)Boolean.valueOf((boolean)obfuscated));
        return this;
    }

    public ComponentBuilder insertion(String insertion) {
        this.current.setInsertion((String)insertion);
        return this;
    }

    public ComponentBuilder event(ClickEvent clickEvent) {
        this.current.setClickEvent((ClickEvent)clickEvent);
        return this;
    }

    public ComponentBuilder event(HoverEvent hoverEvent) {
        this.current.setHoverEvent((HoverEvent)hoverEvent);
        return this;
    }

    public ComponentBuilder reset() {
        return this.retain((FormatRetention)FormatRetention.NONE);
    }

    public ComponentBuilder retain(FormatRetention retention) {
        this.current.retain((FormatRetention)retention);
        return this;
    }

    public BaseComponent[] create() {
        BaseComponent[] result = this.parts.toArray(new BaseComponent[this.parts.size() + 1]);
        result[this.parts.size()] = this.current;
        return result;
    }
}

