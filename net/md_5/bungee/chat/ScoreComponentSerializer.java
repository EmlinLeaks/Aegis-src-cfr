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
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.chat.BaseComponentSerializer;

public class ScoreComponentSerializer
extends BaseComponentSerializer
implements JsonSerializer<ScoreComponent>,
JsonDeserializer<ScoreComponent> {
    @Override
    public ScoreComponent deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = element.getAsJsonObject();
        JsonObject score = json.get((String)"score").getAsJsonObject();
        if (!score.has((String)"name")) throw new JsonParseException((String)"A score component needs at least a name and an objective");
        if (!score.has((String)"objective")) {
            throw new JsonParseException((String)"A score component needs at least a name and an objective");
        }
        String name = score.get((String)"name").getAsString();
        String objective = score.get((String)"objective").getAsString();
        ScoreComponent component = new ScoreComponent((String)name, (String)objective);
        if (score.has((String)"value") && !score.get((String)"value").getAsString().isEmpty()) {
            component.setValue((String)score.get((String)"value").getAsString());
        }
        this.deserialize((JsonObject)json, (BaseComponent)component, (JsonDeserializationContext)context);
        return component;
    }

    @Override
    public JsonElement serialize(ScoreComponent component, Type type, JsonSerializationContext context) {
        JsonObject root = new JsonObject();
        this.serialize((JsonObject)root, (BaseComponent)component, (JsonSerializationContext)context);
        JsonObject json = new JsonObject();
        json.addProperty((String)"name", (String)component.getName());
        json.addProperty((String)"objective", (String)component.getObjective());
        json.addProperty((String)"value", (String)component.getValue());
        root.add((String)"score", (JsonElement)json);
        return root;
    }
}

