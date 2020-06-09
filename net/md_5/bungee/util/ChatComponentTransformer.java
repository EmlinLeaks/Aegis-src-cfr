/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.score.Objective;
import net.md_5.bungee.api.score.Score;
import net.md_5.bungee.api.score.Scoreboard;

public final class ChatComponentTransformer {
    private static final ChatComponentTransformer INSTANCE = new ChatComponentTransformer();
    private static final Pattern SELECTOR_PATTERN = Pattern.compile((String)"^@([pares])(?:\\[([^ ]*)\\])?$");

    public static ChatComponentTransformer getInstance() {
        return INSTANCE;
    }

    public BaseComponent[] transform(ProxiedPlayer player, BaseComponent ... component) {
        if (component == null || component.length < 1 || component.length == 1 && component[0] == null) {
            return new BaseComponent[]{new TextComponent((String)"")};
        }
        BaseComponent[] arrbaseComponent = component;
        int n = arrbaseComponent.length;
        int n2 = 0;
        while (n2 < n) {
            BaseComponent root = arrbaseComponent[n2];
            if (root.getExtra() != null && !root.getExtra().isEmpty()) {
                ArrayList<BaseComponent> list = Lists.newArrayList(this.transform((ProxiedPlayer)player, (BaseComponent[])root.getExtra().toArray(new BaseComponent[root.getExtra().size()])));
                root.setExtra(list);
            }
            if (root instanceof ScoreComponent) {
                this.transformScoreComponent((ProxiedPlayer)player, (ScoreComponent)((ScoreComponent)root));
            }
            ++n2;
        }
        return component;
    }

    private void transformScoreComponent(ProxiedPlayer player, ScoreComponent component) {
        Preconditions.checkArgument((boolean)(!this.isSelectorPattern((String)component.getName())), (Object)"Cannot transform entity selector patterns");
        if (component.getValue() != null && !component.getValue().isEmpty()) {
            return;
        }
        if (component.getName().equals((Object)"*")) {
            component.setName((String)player.getName());
        }
        if (player.getScoreboard().getObjective((String)component.getObjective()) == null) return;
        Score score = player.getScoreboard().getScore((String)component.getName());
        if (score == null) return;
        component.setValue((String)Integer.toString((int)score.getValue()));
    }

    public boolean isSelectorPattern(String pattern) {
        return SELECTOR_PATTERN.matcher((CharSequence)pattern).matches();
    }

    private ChatComponentTransformer() {
    }
}

