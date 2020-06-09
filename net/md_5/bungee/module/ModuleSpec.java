/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.module;

import java.io.File;
import net.md_5.bungee.module.ModuleSource;

public class ModuleSpec {
    private final String name;
    private final File file;
    private final ModuleSource provider;

    public ModuleSpec(String name, File file, ModuleSource provider) {
        this.name = name;
        this.file = file;
        this.provider = provider;
    }

    public String getName() {
        return this.name;
    }

    public File getFile() {
        return this.file;
    }

    public ModuleSource getProvider() {
        return this.provider;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ModuleSpec)) {
            return false;
        }
        ModuleSpec other = (ModuleSpec)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals((Object)other$name)) {
            return false;
        }
        File this$file = this.getFile();
        File other$file = other.getFile();
        if (this$file == null ? other$file != null : !((Object)this$file).equals((Object)other$file)) {
            return false;
        }
        ModuleSource this$provider = this.getProvider();
        ModuleSource other$provider = other.getProvider();
        if (this$provider == null) {
            if (other$provider == null) return true;
            return false;
        }
        if (this$provider.equals((Object)other$provider)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ModuleSpec;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        File $file = this.getFile();
        result = result * 59 + ($file == null ? 43 : ((Object)$file).hashCode());
        ModuleSource $provider = this.getProvider();
        return result * 59 + ($provider == null ? 43 : $provider.hashCode());
    }

    public String toString() {
        return "ModuleSpec(name=" + this.getName() + ", file=" + this.getFile() + ", provider=" + this.getProvider() + ")";
    }
}

