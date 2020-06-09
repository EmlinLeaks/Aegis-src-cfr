/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.UUID;
import net.md_5.bungee.api.ServerPing;

public class PlayerInfoSerializer
implements JsonSerializer<ServerPing.PlayerInfo>,
JsonDeserializer<ServerPing.PlayerInfo> {
    @Override
    public ServerPing.PlayerInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject js = json.getAsJsonObject();
        ServerPing.PlayerInfo info = new ServerPing.PlayerInfo((String)js.get((String)"name").getAsString(), (UUID)((UUID)null));
        String id = js.get((String)"id").getAsString();
        if (!id.contains((CharSequence)"-")) {
            info.setId((String)id);
            return info;
        }
        info.setUniqueId((UUID)UUID.fromString((String)id));
        return info;
    }

    @Override
    public JsonElement serialize(ServerPing.PlayerInfo src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject out = new JsonObject();
        out.addProperty((String)"name", (String)src.getName());
        out.addProperty((String)"id", (String)src.getUniqueId().toString());
        return out;
    }
}

