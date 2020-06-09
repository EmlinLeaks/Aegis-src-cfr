/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.score;

public class Score {
    private final String itemName;
    private final String scoreName;
    private final int value;

    public Score(String itemName, String scoreName, int value) {
        this.itemName = itemName;
        this.scoreName = scoreName;
        this.value = value;
    }

    public String getItemName() {
        return this.itemName;
    }

    public String getScoreName() {
        return this.scoreName;
    }

    public int getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Score)) {
            return false;
        }
        Score other = (Score)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$itemName = this.getItemName();
        String other$itemName = other.getItemName();
        if (this$itemName == null ? other$itemName != null : !this$itemName.equals((Object)other$itemName)) {
            return false;
        }
        String this$scoreName = this.getScoreName();
        String other$scoreName = other.getScoreName();
        if (this$scoreName == null ? other$scoreName != null : !this$scoreName.equals((Object)other$scoreName)) {
            return false;
        }
        if (this.getValue() == other.getValue()) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Score;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $itemName = this.getItemName();
        result = result * 59 + ($itemName == null ? 43 : $itemName.hashCode());
        String $scoreName = this.getScoreName();
        result = result * 59 + ($scoreName == null ? 43 : $scoreName.hashCode());
        return result * 59 + this.getValue();
    }

    public String toString() {
        return "Score(itemName=" + this.getItemName() + ", scoreName=" + this.getScoreName() + ", value=" + this.getValue() + ")";
    }
}

