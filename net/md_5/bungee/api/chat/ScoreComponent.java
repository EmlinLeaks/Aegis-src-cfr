/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.chat;

import net.md_5.bungee.api.chat.BaseComponent;

public final class ScoreComponent
extends BaseComponent {
    private String name;
    private String objective;
    private String value = "";

    public ScoreComponent(String name, String objective) {
        this.setName((String)name);
        this.setObjective((String)objective);
    }

    public ScoreComponent(ScoreComponent original) {
        super((BaseComponent)original);
        this.setName((String)original.getName());
        this.setObjective((String)original.getObjective());
        this.setValue((String)original.getValue());
    }

    @Override
    public ScoreComponent duplicate() {
        return new ScoreComponent((ScoreComponent)this);
    }

    @Override
    protected void toLegacyText(StringBuilder builder) {
        builder.append((String)this.value);
        super.toLegacyText((StringBuilder)builder);
    }

    public String getName() {
        return this.name;
    }

    public String getObjective() {
        return this.objective;
    }

    public String getValue() {
        return this.value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ScoreComponent(name=" + this.getName() + ", objective=" + this.getObjective() + ", value=" + this.getValue() + ")";
    }

    public ScoreComponent(String name, String objective, String value) {
        this.name = name;
        this.objective = objective;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ScoreComponent)) {
            return false;
        }
        ScoreComponent other = (ScoreComponent)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals((Object)o)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals((Object)other$name)) {
            return false;
        }
        String this$objective = this.getObjective();
        String other$objective = other.getObjective();
        if (this$objective == null ? other$objective != null : !this$objective.equals((Object)other$objective)) {
            return false;
        }
        String this$value = this.getValue();
        String other$value = other.getValue();
        if (this$value == null) {
            if (other$value == null) return true;
            return false;
        }
        if (this$value.equals((Object)other$value)) return true;
        return false;
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof ScoreComponent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $objective = this.getObjective();
        result = result * 59 + ($objective == null ? 43 : $objective.hashCode());
        String $value = this.getValue();
        return result * 59 + ($value == null ? 43 : $value.hashCode());
    }
}

