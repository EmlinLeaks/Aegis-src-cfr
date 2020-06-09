/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api;

import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ServerPing {
    private Protocol version;
    private Players players;
    private BaseComponent description;
    private Favicon favicon;
    private final ModInfo modinfo = new ModInfo();

    @Deprecated
    public ServerPing(Protocol version, Players players, String description, String favicon) {
        this((Protocol)version, (Players)players, (BaseComponent)new TextComponent((BaseComponent[])TextComponent.fromLegacyText((String)description)), (Favicon)(favicon == null ? null : Favicon.create((String)favicon)));
    }

    @Deprecated
    public ServerPing(Protocol version, Players players, String description, Favicon favicon) {
        this((Protocol)version, (Players)players, (BaseComponent)new TextComponent((BaseComponent[])TextComponent.fromLegacyText((String)description)), (Favicon)favicon);
    }

    @Deprecated
    public String getFavicon() {
        if (this.getFaviconObject() == null) {
            return null;
        }
        String string = this.getFaviconObject().getEncoded();
        return string;
    }

    public Favicon getFaviconObject() {
        return this.favicon;
    }

    @Deprecated
    public void setFavicon(String favicon) {
        this.setFavicon((Favicon)(favicon == null ? null : Favicon.create((String)favicon)));
    }

    public void setFavicon(Favicon favicon) {
        this.favicon = favicon;
    }

    @Deprecated
    public void setDescription(String description) {
        this.description = new TextComponent((BaseComponent[])TextComponent.fromLegacyText((String)description));
    }

    @Deprecated
    public String getDescription() {
        return BaseComponent.toLegacyText((BaseComponent[])new BaseComponent[]{this.description});
    }

    public void setDescriptionComponent(BaseComponent description) {
        this.description = description;
    }

    public BaseComponent getDescriptionComponent() {
        return this.description;
    }

    public Protocol getVersion() {
        return this.version;
    }

    public Players getPlayers() {
        return this.players;
    }

    public ModInfo getModinfo() {
        return this.modinfo;
    }

    public void setVersion(Protocol version) {
        this.version = version;
    }

    public void setPlayers(Players players) {
        this.players = players;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ServerPing)) {
            return false;
        }
        ServerPing other = (ServerPing)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Protocol this$version = this.getVersion();
        Protocol other$version = other.getVersion();
        if (this$version == null ? other$version != null : !((Object)this$version).equals((Object)other$version)) {
            return false;
        }
        Players this$players = this.getPlayers();
        Players other$players = other.getPlayers();
        if (this$players == null ? other$players != null : !((Object)this$players).equals((Object)other$players)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals((Object)other$description)) {
            return false;
        }
        String this$favicon = this.getFavicon();
        String other$favicon = other.getFavicon();
        if (this$favicon == null ? other$favicon != null : !this$favicon.equals((Object)other$favicon)) {
            return false;
        }
        ModInfo this$modinfo = this.getModinfo();
        ModInfo other$modinfo = other.getModinfo();
        if (this$modinfo == null) {
            if (other$modinfo == null) return true;
            return false;
        }
        if (((Object)this$modinfo).equals((Object)other$modinfo)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ServerPing;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Protocol $version = this.getVersion();
        result = result * 59 + ($version == null ? 43 : ((Object)$version).hashCode());
        Players $players = this.getPlayers();
        result = result * 59 + ($players == null ? 43 : ((Object)$players).hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        String $favicon = this.getFavicon();
        result = result * 59 + ($favicon == null ? 43 : $favicon.hashCode());
        ModInfo $modinfo = this.getModinfo();
        return result * 59 + ($modinfo == null ? 43 : ((Object)$modinfo).hashCode());
    }

    public String toString() {
        return "ServerPing(version=" + this.getVersion() + ", players=" + this.getPlayers() + ", description=" + this.getDescription() + ", modinfo=" + this.getModinfo() + ")";
    }

    public ServerPing() {
    }

    public ServerPing(Protocol version, Players players, BaseComponent description, Favicon favicon) {
        this.version = version;
        this.players = players;
        this.description = description;
        this.favicon = favicon;
    }
}

