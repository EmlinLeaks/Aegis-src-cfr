/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.chat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.TranslationRegistry;

public final class TranslatableComponent
extends BaseComponent {
    private final Pattern format = Pattern.compile((String)"%(?:(\\d+)\\$)?([A-Za-z%]|$)");
    private String translate;
    private List<BaseComponent> with;

    public TranslatableComponent(TranslatableComponent original) {
        super((BaseComponent)original);
        this.setTranslate((String)original.getTranslate());
        if (original.getWith() == null) return;
        ArrayList<BaseComponent> temp = new ArrayList<BaseComponent>();
        Iterator<BaseComponent> iterator = original.getWith().iterator();
        do {
            if (!iterator.hasNext()) {
                this.setWith(temp);
                return;
            }
            BaseComponent baseComponent = iterator.next();
            temp.add((BaseComponent)baseComponent.duplicate());
        } while (true);
    }

    public TranslatableComponent(String translate, Object ... with) {
        this.setTranslate((String)translate);
        if (with == null) return;
        if (with.length == 0) return;
        ArrayList<BaseComponent> temp = new ArrayList<BaseComponent>();
        Object[] arrobject = with;
        int n = arrobject.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                this.setWith(temp);
                return;
            }
            Object w = arrobject[n2];
            if (w instanceof BaseComponent) {
                temp.add((BaseComponent)((BaseComponent)w));
            } else {
                temp.add((BaseComponent)new TextComponent((String)String.valueOf((Object)w)));
            }
            ++n2;
        } while (true);
    }

    @Override
    public BaseComponent duplicate() {
        return new TranslatableComponent((TranslatableComponent)this);
    }

    public void setWith(List<BaseComponent> components) {
        Iterator<BaseComponent> iterator = components.iterator();
        do {
            if (!iterator.hasNext()) {
                this.with = components;
                return;
            }
            BaseComponent component = iterator.next();
            component.parent = this;
        } while (true);
    }

    public void addWith(String text) {
        this.addWith((BaseComponent)new TextComponent((String)text));
    }

    public void addWith(BaseComponent component) {
        if (this.with == null) {
            this.with = new ArrayList<BaseComponent>();
        }
        component.parent = this;
        this.with.add((BaseComponent)component);
    }

    @Override
    protected void toPlainText(StringBuilder builder) {
        String trans = TranslationRegistry.INSTANCE.translate((String)this.translate);
        Matcher matcher = this.format.matcher((CharSequence)trans);
        int position = 0;
        int i = 0;
        while (matcher.find((int)position)) {
            int pos = matcher.start();
            if (pos != position) {
                builder.append((String)trans.substring((int)position, (int)pos));
            }
            position = matcher.end();
            String formatCode = matcher.group((int)2);
            switch (formatCode.charAt((int)0)) {
                case 'd': 
                case 's': {
                    String withIndex = matcher.group((int)1);
                    this.with.get((int)(withIndex != null ? Integer.parseInt((String)withIndex) - 1 : i++)).toPlainText((StringBuilder)builder);
                    break;
                }
                case '%': {
                    builder.append((char)'%');
                }
            }
        }
        if (trans.length() != position) {
            builder.append((String)trans.substring((int)position, (int)trans.length()));
        }
        super.toPlainText((StringBuilder)builder);
    }

    @Override
    protected void toLegacyText(StringBuilder builder) {
        String trans = TranslationRegistry.INSTANCE.translate((String)this.translate);
        Matcher matcher = this.format.matcher((CharSequence)trans);
        int position = 0;
        int i = 0;
        while (matcher.find((int)position)) {
            int pos = matcher.start();
            if (pos != position) {
                this.addFormat((StringBuilder)builder);
                builder.append((String)trans.substring((int)position, (int)pos));
            }
            position = matcher.end();
            String formatCode = matcher.group((int)2);
            switch (formatCode.charAt((int)0)) {
                case 'd': 
                case 's': {
                    String withIndex = matcher.group((int)1);
                    this.with.get((int)(withIndex != null ? Integer.parseInt((String)withIndex) - 1 : i++)).toLegacyText((StringBuilder)builder);
                    break;
                }
                case '%': {
                    this.addFormat((StringBuilder)builder);
                    builder.append((char)'%');
                }
            }
        }
        if (trans.length() != position) {
            this.addFormat((StringBuilder)builder);
            builder.append((String)trans.substring((int)position, (int)trans.length()));
        }
        super.toLegacyText((StringBuilder)builder);
    }

    private void addFormat(StringBuilder builder) {
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
        if (!this.isObfuscated()) return;
        builder.append((Object)((Object)ChatColor.MAGIC));
    }

    public Pattern getFormat() {
        return this.format;
    }

    public String getTranslate() {
        return this.translate;
    }

    public List<BaseComponent> getWith() {
        return this.with;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    @Override
    public String toString() {
        return "TranslatableComponent(format=" + this.getFormat() + ", translate=" + this.getTranslate() + ", with=" + this.getWith() + ")";
    }

    public TranslatableComponent() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TranslatableComponent)) {
            return false;
        }
        TranslatableComponent other = (TranslatableComponent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals((Object)o)) {
            return false;
        }
        Pattern this$format = this.getFormat();
        Pattern other$format = other.getFormat();
        if (this$format == null ? other$format != null : !this$format.equals((Object)other$format)) {
            return false;
        }
        String this$translate = this.getTranslate();
        String other$translate = other.getTranslate();
        if (this$translate == null ? other$translate != null : !this$translate.equals((Object)other$translate)) {
            return false;
        }
        List<BaseComponent> this$with = this.getWith();
        List<BaseComponent> other$with = other.getWith();
        if (this$with == null) {
            if (other$with == null) return true;
            return false;
        }
        if (((Object)this$with).equals(other$with)) return true;
        return false;
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof TranslatableComponent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        Pattern $format = this.getFormat();
        result = result * 59 + ($format == null ? 43 : $format.hashCode());
        String $translate = this.getTranslate();
        result = result * 59 + ($translate == null ? 43 : $translate.hashCode());
        List<BaseComponent> $with = this.getWith();
        return result * 59 + ($with == null ? 43 : ((Object)$with).hashCode());
    }
}

