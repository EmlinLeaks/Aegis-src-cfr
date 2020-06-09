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
import java.util.Arrays;
import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.BaseComponentSerializer;

public class TranslatableComponentSerializer
extends BaseComponentSerializer
implements JsonSerializer<TranslatableComponent>,
JsonDeserializer<TranslatableComponent> {
    @Override
    public TranslatableComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        TranslatableComponent component = new TranslatableComponent();
        JsonObject object = json.getAsJsonObject();
        this.deserialize((JsonObject)object, (BaseComponent)component, (JsonDeserializationContext)context);
        component.setTranslate((String)object.get((String)"translate").getAsString());
        if (!object.has((String)"with")) return component;
        component.setWith(Arrays.asList((BaseComponent[])context.deserialize((JsonElement)object.get((String)"with"), BaseComponent[].class)));
        return component;
    }

    @Override
    public JsonElement serialize(TranslatableComponent src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        this.serialize((JsonObject)object, (BaseComponent)src, (JsonSerializationContext)context);
        object.addProperty((String)"translate", (String)src.getTranslate());
        if (src.getWith() == null) return object;
        object.add((String)"with", (JsonElement)context.serialize(src.getWith()));
        return object;
    }
}

