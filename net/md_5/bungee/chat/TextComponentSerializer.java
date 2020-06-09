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
import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.BaseComponentSerializer;

public class TextComponentSerializer
extends BaseComponentSerializer
implements JsonSerializer<TextComponent>,
JsonDeserializer<TextComponent> {
    @Override
    public TextComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        TextComponent component = new TextComponent();
        JsonObject object = json.getAsJsonObject();
        this.deserialize((JsonObject)object, (BaseComponent)component, (JsonDeserializationContext)context);
        component.setText((String)object.get((String)"text").getAsString());
        return component;
    }

    @Override
    public JsonElement serialize(TextComponent src, Type typeOfSrc, JsonSerializationContext context) {
        List<BaseComponent> extra = src.getExtra();
        JsonObject object = new JsonObject();
        if (src.hasFormatting() || extra != null && !extra.isEmpty()) {
            this.serialize((JsonObject)object, (BaseComponent)src, (JsonSerializationContext)context);
        }
        object.addProperty((String)"text", (String)src.getText());
        return object;
    }
}

