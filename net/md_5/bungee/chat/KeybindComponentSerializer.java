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
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.chat.BaseComponentSerializer;

public class KeybindComponentSerializer
extends BaseComponentSerializer
implements JsonSerializer<KeybindComponent>,
JsonDeserializer<KeybindComponent> {
    @Override
    public KeybindComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        KeybindComponent component = new KeybindComponent();
        JsonObject object = json.getAsJsonObject();
        this.deserialize((JsonObject)object, (BaseComponent)component, (JsonDeserializationContext)context);
        component.setKeybind((String)object.get((String)"keybind").getAsString());
        return component;
    }

    @Override
    public JsonElement serialize(KeybindComponent src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        this.serialize((JsonObject)object, (BaseComponent)src, (JsonSerializationContext)context);
        object.addProperty((String)"keybind", (String)src.getKeybind());
        return object;
    }
}

