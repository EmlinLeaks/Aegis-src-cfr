/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.score;

public class Objective {
    private final String name;
    private String value;
    private String type;

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public String getType() {
        return this.type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Objective)) {
            return false;
        }
        Objective other = (Objective)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals((Object)other$name)) {
            return false;
        }
        String this$value = this.getValue();
        String other$value = other.getValue();
        if (this$value == null ? other$value != null : !this$value.equals((Object)other$value)) {
            return false;
        }
        String this$type = this.getType();
        String other$type = other.getType();
        if (this$type == null) {
            if (other$type == null) return true;
            return false;
        }
        if (this$type.equals((Object)other$type)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Objective;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $value = this.getValue();
        result = result * 59 + ($value == null ? 43 : $value.hashCode());
        String $type = this.getType();
        return result * 59 + ($type == null ? 43 : $type.hashCode());
    }

    public String toString() {
        return "Objective(name=" + this.getName() + ", value=" + this.getValue() + ", type=" + this.getType() + ")";
    }

    public Objective(String name, String value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }
}

