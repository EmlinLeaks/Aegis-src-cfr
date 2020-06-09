/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.chat.BaseComponentSerializer;

public class SelectorComponentSerializer
extends BaseComponentSerializer
implements JsonSerializer<SelectorComponent>,
JsonDeserializer<SelectorComponent> {
    @Override
    public SelectorComponent deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();
        SelectorComponent component = new SelectorComponent((String)object.get((String)"selector").getAsString());
        this.deserialize((JsonObject)object, (BaseComponent)component, (JsonDeserializationContext)context);
        return component;
    }

    @Override
    public JsonElement serialize(SelectorComponent component, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        this.serialize((JsonObject)object, (BaseComponent)component, (JsonSerializationContext)context);
        object.addProperty((String)"selector", (String)component.getSelector());
        return object;
    }
}

