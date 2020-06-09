/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.chat;

import com.google.common.base.Preconditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.chat.ComponentSerializer;

public class BaseComponentSerializer {
    protected void deserialize(JsonObject object, BaseComponent component, JsonDeserializationContext context) {
        JsonObject event;
        if (object.has((String)"color")) {
            component.setColor((ChatColor)ChatColor.valueOf((String)object.get((String)"color").getAsString().toUpperCase((Locale)Locale.ROOT)));
        }
        if (object.has((String)"bold")) {
            component.setBold((Boolean)Boolean.valueOf((boolean)object.get((String)"bold").getAsBoolean()));
        }
        if (object.has((String)"italic")) {
            component.setItalic((Boolean)Boolean.valueOf((boolean)object.get((String)"italic").getAsBoolean()));
        }
        if (object.has((String)"underlined")) {
            component.setUnderlined((Boolean)Boolean.valueOf((boolean)object.get((String)"underlined").getAsBoolean()));
        }
        if (object.has((String)"strikethrough")) {
            component.setStrikethrough((Boolean)Boolean.valueOf((boolean)object.get((String)"strikethrough").getAsBoolean()));
        }
        if (object.has((String)"obfuscated")) {
            component.setObfuscated((Boolean)Boolean.valueOf((boolean)object.get((String)"obfuscated").getAsBoolean()));
        }
        if (object.has((String)"insertion")) {
            component.setInsertion((String)object.get((String)"insertion").getAsString());
        }
        if (object.has((String)"extra")) {
            component.setExtra(Arrays.asList((BaseComponent[])context.deserialize((JsonElement)object.get((String)"extra"), BaseComponent[].class)));
        }
        if (object.has((String)"clickEvent")) {
            event = object.getAsJsonObject((String)"clickEvent");
            component.setClickEvent((ClickEvent)new ClickEvent((ClickEvent.Action)ClickEvent.Action.valueOf((String)event.get((String)"action").getAsString().toUpperCase((Locale)Locale.ROOT)), (String)event.get((String)"value").getAsString()));
        }
        if (!object.has((String)"hoverEvent")) return;
        event = object.getAsJsonObject((String)"hoverEvent");
        BaseComponent[] res = event.get((String)"value").isJsonArray() ? (BaseComponent[])context.deserialize((JsonElement)event.get((String)"value"), BaseComponent[].class) : new BaseComponent[]{(BaseComponent)context.deserialize((JsonElement)event.get((String)"value"), BaseComponent.class)};
        component.setHoverEvent((HoverEvent)new HoverEvent((HoverEvent.Action)HoverEvent.Action.valueOf((String)event.get((String)"action").getAsString().toUpperCase((Locale)Locale.ROOT)), (BaseComponent[])res));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void serialize(JsonObject object, BaseComponent component, JsonSerializationContext context) {
        boolean first = false;
        if (ComponentSerializer.serializedComponents.get() == null) {
            first = true;
            ComponentSerializer.serializedComponents.set(Collections.newSetFromMap(new IdentityHashMap<K, V>()));
        }
        try {
            Preconditions.checkArgument((boolean)(!ComponentSerializer.serializedComponents.get().contains((Object)component)), (Object)"Component loop");
            ComponentSerializer.serializedComponents.get().add((BaseComponent)component);
            if (component.getColorRaw() != null) {
                object.addProperty((String)"color", (String)component.getColorRaw().getName());
            }
            if (component.isBoldRaw() != null) {
                object.addProperty((String)"bold", (Boolean)component.isBoldRaw());
            }
            if (component.isItalicRaw() != null) {
                object.addProperty((String)"italic", (Boolean)component.isItalicRaw());
            }
            if (component.isUnderlinedRaw() != null) {
                object.addProperty((String)"underlined", (Boolean)component.isUnderlinedRaw());
            }
            if (component.isStrikethroughRaw() != null) {
                object.addProperty((String)"strikethrough", (Boolean)component.isStrikethroughRaw());
            }
            if (component.isObfuscatedRaw() != null) {
                object.addProperty((String)"obfuscated", (Boolean)component.isObfuscatedRaw());
            }
            if (component.getInsertion() != null) {
                object.addProperty((String)"insertion", (String)component.getInsertion());
            }
            if (component.getExtra() != null) {
                object.add((String)"extra", (JsonElement)context.serialize(component.getExtra()));
            }
            if (component.getClickEvent() != null) {
                JsonObject clickEvent = new JsonObject();
                clickEvent.addProperty((String)"action", (String)component.getClickEvent().getAction().toString().toLowerCase((Locale)Locale.ROOT));
                clickEvent.addProperty((String)"value", (String)component.getClickEvent().getValue());
                object.add((String)"clickEvent", (JsonElement)clickEvent);
            }
            if (component.getHoverEvent() == null) return;
            JsonObject hoverEvent = new JsonObject();
            hoverEvent.addProperty((String)"action", (String)component.getHoverEvent().getAction().toString().toLowerCase((Locale)Locale.ROOT));
            hoverEvent.add((String)"value", (JsonElement)context.serialize((Object)component.getHoverEvent().getValue()));
            object.add((String)"hoverEvent", (JsonElement)hoverEvent);
            return;
        }
        finally {
            ComponentSerializer.serializedComponents.get().remove((Object)component);
            if (first) {
                ComponentSerializer.serializedComponents.set(null);
            }
        }
    }
}

