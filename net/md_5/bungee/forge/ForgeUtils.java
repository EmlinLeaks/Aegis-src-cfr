/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.forge;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.forge.ForgeConstants;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class ForgeUtils {
    public static Set<String> readRegisteredChannels(PluginMessage pluginMessage) {
        String channels = new String((byte[])pluginMessage.getData(), (Charset)Charsets.UTF_8);
        String[] split = channels.split((String)"\u0000");
        return ImmutableSet.copyOf(split);
    }

    public static Map<String, String> readModList(PluginMessage pluginMessage) {
        HashMap<String, String> modTags = new HashMap<String, String>();
        ByteBuf payload = Unpooled.wrappedBuffer((byte[])pluginMessage.getData());
        byte discriminator = payload.readByte();
        if (discriminator != 2) return modTags;
        ByteBuf buffer = payload.slice();
        int modCount = DefinedPacket.readVarInt((ByteBuf)buffer, (int)2);
        int i = 0;
        while (i < modCount) {
            modTags.put((String)DefinedPacket.readString((ByteBuf)buffer), (String)DefinedPacket.readString((ByteBuf)buffer));
            ++i;
        }
        return modTags;
    }

    public static int getFmlBuildNumber(Map<String, String> modList) {
        if (!modList.containsKey((Object)"FML")) return 0;
        String fmlVersion = modList.get((Object)"FML");
        if (fmlVersion.equals((Object)"7.10.99.99")) {
            Matcher matcher = ForgeConstants.FML_HANDSHAKE_VERSION_REGEX.matcher((CharSequence)((CharSequence)modList.get((Object)"Forge")));
            if (!matcher.find()) return 0;
            return Integer.parseInt((String)matcher.group((int)4));
        }
        Matcher matcher = ForgeConstants.FML_HANDSHAKE_VERSION_REGEX.matcher((CharSequence)fmlVersion);
        if (!matcher.find()) return 0;
        return Integer.parseInt((String)matcher.group((int)4));
    }
}

