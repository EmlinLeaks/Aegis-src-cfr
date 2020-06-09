/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple.internal;

class Row {
    final String option;
    final String description;

    Row(String option, String description) {
        this.option = option;
        this.description = description;
    }

    public boolean equals(Object that) {
        if (that == this) {
            return true;
        }
        if (that == null) return false;
        if (!this.getClass().equals(that.getClass())) {
            return false;
        }
        Row other = (Row)that;
        if (!this.option.equals((Object)other.option)) return false;
        if (!this.description.equals((Object)other.description)) return false;
        return true;
    }

    public int hashCode() {
        return this.option.hashCode() ^ this.description.hashCode();
    }
}

