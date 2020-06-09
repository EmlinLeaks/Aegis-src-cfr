/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public final class TextComponent
extends BaseComponent {
    private static final Pattern url = Pattern.compile((String)"^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");
    private String text;

    public static BaseComponent[] fromLegacyText(String message) {
        return TextComponent.fromLegacyText((String)message, (ChatColor)ChatColor.WHITE);
    }

    /*
     * Unable to fully structure code
     */
    public static BaseComponent[] fromLegacyText(String message, ChatColor defaultColor) {
        components = new ArrayList<TextComponent>();
        builder = new StringBuilder();
        component = new TextComponent();
        matcher = TextComponent.url.matcher((CharSequence)message);
        for (i = 0; i < message.length(); ++i) {
            c = message.charAt((int)i);
            if (c == '\u00a7') {
                if (++i >= message.length()) break;
                c = message.charAt((int)i);
                if (c >= 'A' && c <= 'Z') {
                    c = (char)(c + 32);
                }
                if ((format = ChatColor.getByChar((char)c)) == null) continue;
                if (builder.length() > 0) {
                    old = component;
                    component = new TextComponent((TextComponent)old);
                    old.setText((String)builder.toString());
                    builder = new StringBuilder();
                    components.add(old);
                }
                switch (1.$SwitchMap$net$md_5$bungee$api$ChatColor[format.ordinal()]) {
                    case 1: {
                        component.setBold((Boolean)Boolean.valueOf((boolean)true));
                        ** break;
                    }
                    case 2: {
                        component.setItalic((Boolean)Boolean.valueOf((boolean)true));
                        ** break;
                    }
                    case 3: {
                        component.setUnderlined((Boolean)Boolean.valueOf((boolean)true));
                        ** break;
                    }
                    case 4: {
                        component.setStrikethrough((Boolean)Boolean.valueOf((boolean)true));
                        ** break;
                    }
                    case 5: {
                        component.setObfuscated((Boolean)Boolean.valueOf((boolean)true));
                        ** break;
                    }
                    case 6: {
                        format = defaultColor;
                    }
                }
                component = new TextComponent();
                component.setColor((ChatColor)format);
                ** break;
lbl41: // 6 sources:
                continue;
            }
            pos = message.indexOf((int)32, (int)i);
            if (pos == -1) {
                pos = message.length();
            }
            if (matcher.region((int)i, (int)pos).find()) {
                if (builder.length() > 0) {
                    old = component;
                    component = new TextComponent((TextComponent)old);
                    old.setText((String)builder.toString());
                    builder = new StringBuilder();
                    components.add(old);
                }
                old = component;
                component = new TextComponent((TextComponent)old);
                urlString = message.substring((int)i, (int)pos);
                component.setText((String)urlString);
                component.setClickEvent((ClickEvent)new ClickEvent((ClickEvent.Action)ClickEvent.Action.OPEN_URL, (String)(urlString.startsWith((String)"http") != false ? urlString : "http://" + urlString)));
                components.add(component);
                i += pos - i - 1;
                component = old;
                continue;
            }
            builder.append((char)c);
        }
        component.setText((String)builder.toString());
        components.add(component);
        return components.toArray(new BaseComponent[components.size()]);
    }

    public TextComponent() {
        this.text = "";
    }

    public TextComponent(TextComponent textComponent) {
        super((BaseComponent)textComponent);
        this.setText((String)textComponent.getText());
    }

    public TextComponent(BaseComponent ... extras) {
        this.setText((String)"");
        this.setExtra(new ArrayList<BaseComponent>(Arrays.asList(extras)));
    }

    @Override
    public BaseComponent duplicate() {
        return new TextComponent((TextComponent)this);
    }

    @Override
    protected void toPlainText(StringBuilder builder) {
        builder.append((String)this.text);
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
        builder.append((String)this.text);
        super.toLegacyText((StringBuilder)builder);
    }

    @Override
    public String toString() {
        return String.format((String)"TextComponent{text=%s, %s}", (Object[])new Object[]{this.text, super.toString()});
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TextComponent(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TextComponent)) {
            return false;
        }
        TextComponent other = (TextComponent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals((Object)o)) {
            return false;
        }
        String this$text = this.getText();
        String other$text = other.getText();
        if (this$text == null) {
            if (other$text == null) return true;
            return false;
        }
        if (this$text.equals((Object)other$text)) return true;
        return false;
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof TextComponent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $text = this.getText();
        return result * 59 + ($text == null ? 43 : $text.hashCode());
    }
}

