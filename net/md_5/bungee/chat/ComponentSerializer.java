/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.lang.reflect.Type;
import java.util.Set;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.KeybindComponentSerializer;
import net.md_5.bungee.chat.ScoreComponentSerializer;
import net.md_5.bungee.chat.SelectorComponentSerializer;
import net.md_5.bungee.chat.TextComponentSerializer;
import net.md_5.bungee.chat.TranslatableComponentSerializer;

public class ComponentSerializer
implements JsonDeserializer<BaseComponent> {
    private static final JsonParser JSON_PARSER = new JsonParser();
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(BaseComponent.class, (Object)new ComponentSerializer()).registerTypeAdapter(TextComponent.class, (Object)new TextComponentSerializer()).registerTypeAdapter(TranslatableComponent.class, (Object)new TranslatableComponentSerializer()).registerTypeAdapter(KeybindComponent.class, (Object)new KeybindComponentSerializer()).registerTypeAdapter(ScoreComponent.class, (Object)new ScoreComponentSerializer()).registerTypeAdapter(SelectorComponent.class, (Object)new SelectorComponentSerializer()).create();
    public static final ThreadLocal<Set<BaseComponent>> serializedComponents = new ThreadLocal<T>();

    public static BaseComponent[] parse(String json) {
        JsonElement jsonElement = JSON_PARSER.parse((String)json);
        if (!jsonElement.isJsonArray()) return new BaseComponent[]{gson.fromJson((JsonElement)jsonElement, BaseComponent.class)};
        return gson.fromJson((JsonElement)jsonElement, BaseComponent[].class);
    }

    public static String toString(BaseComponent component) {
        return gson.toJson((Object)component);
    }

    public static String toString(BaseComponent ... components) {
        if (components.length != 1) return gson.toJson((Object)new TextComponent((BaseComponent[])components));
        return gson.toJson((Object)components[0]);
    }

    @Override
    public BaseComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return new TextComponent((String)json.getAsString());
        }
        JsonObject object = json.getAsJsonObject();
        if (object.has((String)"translate")) {
            return (BaseComponent)context.deserialize((JsonElement)json, TranslatableComponent.class);
        }
        if (object.has((String)"keybind")) {
            return (BaseComponent)context.deserialize((JsonElement)json, KeybindComponent.class);
        }
        if (object.has((String)"score")) {
            return (BaseComponent)context.deserialize((JsonElement)json, ScoreComponent.class);
        }
        if (!object.has((String)"selector")) return (BaseComponent)context.deserialize((JsonElement)json, TextComponent.class);
        return (BaseComponent)context.deserialize((JsonElement)json, SelectorComponent.class);
    }
}

