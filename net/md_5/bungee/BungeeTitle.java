/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee;

import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.Title;

public class BungeeTitle
implements Title {
    private net.md_5.bungee.protocol.packet.Title title;
    private net.md_5.bungee.protocol.packet.Title subtitle;
    private net.md_5.bungee.protocol.packet.Title times;
    private net.md_5.bungee.protocol.packet.Title clear;
    private net.md_5.bungee.protocol.packet.Title reset;

    private static net.md_5.bungee.protocol.packet.Title createPacket(Title.Action action) {
        net.md_5.bungee.protocol.packet.Title title = new net.md_5.bungee.protocol.packet.Title();
        title.setAction((Title.Action)action);
        if (action != Title.Action.TIMES) return title;
        title.setFadeIn((int)20);
        title.setStay((int)60);
        title.setFadeOut((int)20);
        return title;
    }

    @Override
    public Title title(BaseComponent text) {
        if (this.title == null) {
            this.title = BungeeTitle.createPacket((Title.Action)Title.Action.TITLE);
        }
        this.title.setText((String)ComponentSerializer.toString((BaseComponent)text));
        return this;
    }

    @Override
    public Title title(BaseComponent ... text) {
        if (this.title == null) {
            this.title = BungeeTitle.createPacket((Title.Action)Title.Action.TITLE);
        }
        this.title.setText((String)ComponentSerializer.toString((BaseComponent[])text));
        return this;
    }

    @Override
    public Title subTitle(BaseComponent text) {
        if (this.subtitle == null) {
            this.subtitle = BungeeTitle.createPacket((Title.Action)Title.Action.SUBTITLE);
        }
        this.subtitle.setText((String)ComponentSerializer.toString((BaseComponent)text));
        return this;
    }

    @Override
    public Title subTitle(BaseComponent ... text) {
        if (this.subtitle == null) {
            this.subtitle = BungeeTitle.createPacket((Title.Action)Title.Action.SUBTITLE);
        }
        this.subtitle.setText((String)ComponentSerializer.toString((BaseComponent[])text));
        return this;
    }

    @Override
    public Title fadeIn(int ticks) {
        if (this.times == null) {
            this.times = BungeeTitle.createPacket((Title.Action)Title.Action.TIMES);
        }
        this.times.setFadeIn((int)ticks);
        return this;
    }

    @Override
    public Title stay(int ticks) {
        if (this.times == null) {
            this.times = BungeeTitle.createPacket((Title.Action)Title.Action.TIMES);
        }
        this.times.setStay((int)ticks);
        return this;
    }

    @Override
    public Title fadeOut(int ticks) {
        if (this.times == null) {
            this.times = BungeeTitle.createPacket((Title.Action)Title.Action.TIMES);
        }
        this.times.setFadeOut((int)ticks);
        return this;
    }

    @Override
    public Title clear() {
        if (this.clear == null) {
            this.clear = BungeeTitle.createPacket((Title.Action)Title.Action.CLEAR);
        }
        this.title = null;
        return this;
    }

    @Override
    public Title reset() {
        if (this.reset == null) {
            this.reset = BungeeTitle.createPacket((Title.Action)Title.Action.RESET);
        }
        this.title = null;
        this.subtitle = null;
        this.times = null;
        return this;
    }

    private static void sendPacket(ProxiedPlayer player, DefinedPacket packet) {
        if (packet == null) return;
        player.unsafe().sendPacket((DefinedPacket)packet);
    }

    @Override
    public Title send(ProxiedPlayer player) {
        BungeeTitle.sendPacket((ProxiedPlayer)player, (DefinedPacket)this.clear);
        BungeeTitle.sendPacket((ProxiedPlayer)player, (DefinedPacket)this.reset);
        BungeeTitle.sendPacket((ProxiedPlayer)player, (DefinedPacket)this.times);
        BungeeTitle.sendPacket((ProxiedPlayer)player, (DefinedPacket)this.subtitle);
        BungeeTitle.sendPacket((ProxiedPlayer)player, (DefinedPacket)this.title);
        return this;
    }
}

