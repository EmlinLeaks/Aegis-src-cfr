/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package net.md_5.bungee.api.score;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;

public class Team {
    @NonNull
    private final String name;
    private String displayName;
    private String prefix;
    private String suffix;
    private byte friendlyFire;
    private String nameTagVisibility;
    private String collisionRule;
    private int color;
    private Set<String> players = new HashSet<String>();

    public Collection<String> getPlayers() {
        return Collections.unmodifiableSet(this.players);
    }

    public void addPlayer(String name) {
        this.players.add((String)name);
    }

    public void removePlayer(String name) {
        this.players.remove((Object)name);
    }

    public Team(@NonNull String name) {
        if (name == null) {
            throw new NullPointerException((String)"name is marked non-null but is null");
        }
        this.name = name;
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public byte getFriendlyFire() {
        return this.friendlyFire;
    }

    public String getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    public String getCollisionRule() {
        return this.collisionRule;
    }

    public int getColor() {
        return this.color;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setFriendlyFire(byte friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public void setNameTagVisibility(String nameTagVisibility) {
        this.nameTagVisibility = nameTagVisibility;
    }

    public void setCollisionRule(String collisionRule) {
        this.collisionRule = collisionRule;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setPlayers(Set<String> players) {
        this.players = players;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Team)) {
            return false;
        }
        Team other = (Team)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals((Object)other$name)) {
            return false;
        }
        String this$displayName = this.getDisplayName();
        String other$displayName = other.getDisplayName();
        if (this$displayName == null ? other$displayName != null : !this$displayName.equals((Object)other$displayName)) {
            return false;
        }
        String this$prefix = this.getPrefix();
        String other$prefix = other.getPrefix();
        if (this$prefix == null ? other$prefix != null : !this$prefix.equals((Object)other$prefix)) {
            return false;
        }
        String this$suffix = this.getSuffix();
        String other$suffix = other.getSuffix();
        if (this$suffix == null ? other$suffix != null : !this$suffix.equals((Object)other$suffix)) {
            return false;
        }
        if (this.getFriendlyFire() != other.getFriendlyFire()) {
            return false;
        }
        String this$nameTagVisibility = this.getNameTagVisibility();
        String other$nameTagVisibility = other.getNameTagVisibility();
        if (this$nameTagVisibility == null ? other$nameTagVisibility != null : !this$nameTagVisibility.equals((Object)other$nameTagVisibility)) {
            return false;
        }
        String this$collisionRule = this.getCollisionRule();
        String other$collisionRule = other.getCollisionRule();
        if (this$collisionRule == null ? other$collisionRule != null : !this$collisionRule.equals((Object)other$collisionRule)) {
            return false;
        }
        if (this.getColor() != other.getColor()) {
            return false;
        }
        Collection<String> this$players = this.getPlayers();
        Collection<String> other$players = other.getPlayers();
        if (this$players == null) {
            if (other$players == null) return true;
            return false;
        }
        if (((Object)this$players).equals(other$players)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Team;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $displayName = this.getDisplayName();
        result = result * 59 + ($displayName == null ? 43 : $displayName.hashCode());
        String $prefix = this.getPrefix();
        result = result * 59 + ($prefix == null ? 43 : $prefix.hashCode());
        String $suffix = this.getSuffix();
        result = result * 59 + ($suffix == null ? 43 : $suffix.hashCode());
        result = result * 59 + this.getFriendlyFire();
        String $nameTagVisibility = this.getNameTagVisibility();
        result = result * 59 + ($nameTagVisibility == null ? 43 : $nameTagVisibility.hashCode());
        String $collisionRule = this.getCollisionRule();
        result = result * 59 + ($collisionRule == null ? 43 : $collisionRule.hashCode());
        result = result * 59 + this.getColor();
        Collection<String> $players = this.getPlayers();
        return result * 59 + ($players == null ? 43 : ((Object)$players).hashCode());
    }

    public String toString() {
        return "Team(name=" + this.getName() + ", displayName=" + this.getDisplayName() + ", prefix=" + this.getPrefix() + ", suffix=" + this.getSuffix() + ", friendlyFire=" + this.getFriendlyFire() + ", nameTagVisibility=" + this.getNameTagVisibility() + ", collisionRule=" + this.getCollisionRule() + ", color=" + this.getColor() + ", players=" + this.getPlayers() + ")";
    }
}

