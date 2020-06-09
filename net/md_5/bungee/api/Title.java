/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface Title {
    public Title title(BaseComponent var1);

    public Title title(BaseComponent ... var1);

    public Title subTitle(BaseComponent var1);

    public Title subTitle(BaseComponent ... var1);

    public Title fadeIn(int var1);

    public Title stay(int var1);

    public Title fadeOut(int var1);

    public Title clear();

    public Title reset();

    public Title send(ProxiedPlayer var1);
}

