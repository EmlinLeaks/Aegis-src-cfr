/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.connection;

import java.util.Arrays;
import net.md_5.bungee.connection.LoginResult;

public class LoginResult {
    private String id;
    private String name;
    private Property[] properties;

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Property[] getProperties() {
        return this.properties;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProperties(Property[] properties) {
        this.properties = properties;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LoginResult)) {
            return false;
        }
        LoginResult other = (LoginResult)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals((Object)other$id)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals((Object)other$name)) {
            return false;
        }
        if (Arrays.deepEquals((Object[])this.getProperties(), (Object[])other.getProperties())) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof LoginResult;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        return result * 59 + Arrays.deepHashCode((Object[])this.getProperties());
    }

    public String toString() {
        return "LoginResult(id=" + this.getId() + ", name=" + this.getName() + ", properties=" + Arrays.deepToString((Object[])this.getProperties()) + ")";
    }

    public LoginResult(String id, String name, Property[] properties) {
        this.id = id;
        this.name = name;
        this.properties = properties;
    }
}

