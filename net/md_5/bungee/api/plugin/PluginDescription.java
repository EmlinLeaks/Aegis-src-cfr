/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api.plugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class PluginDescription {
    private String name;
    private String main;
    private String version;
    private String author;
    private Set<String> depends = new HashSet<String>();
    private Set<String> softDepends = new HashSet<String>();
    private File file = null;
    private String description = null;

    public String getName() {
        return this.name;
    }

    public String getMain() {
        return this.main;
    }

    public String getVersion() {
        return this.version;
    }

    public String getAuthor() {
        return this.author;
    }

    public Set<String> getDepends() {
        return this.depends;
    }

    public Set<String> getSoftDepends() {
        return this.softDepends;
    }

    public File getFile() {
        return this.file;
    }

    public String getDescription() {
        return this.description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDepends(Set<String> depends) {
        this.depends = depends;
    }

    public void setSoftDepends(Set<String> softDepends) {
        this.softDepends = softDepends;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PluginDescription)) {
            return false;
        }
        PluginDescription other = (PluginDescription)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals((Object)other$name)) {
            return false;
        }
        String this$main = this.getMain();
        String other$main = other.getMain();
        if (this$main == null ? other$main != null : !this$main.equals((Object)other$main)) {
            return false;
        }
        String this$version = this.getVersion();
        String other$version = other.getVersion();
        if (this$version == null ? other$version != null : !this$version.equals((Object)other$version)) {
            return false;
        }
        String this$author = this.getAuthor();
        String other$author = other.getAuthor();
        if (this$author == null ? other$author != null : !this$author.equals((Object)other$author)) {
            return false;
        }
        Set<String> this$depends = this.getDepends();
        Set<String> other$depends = other.getDepends();
        if (this$depends == null ? other$depends != null : !((Object)this$depends).equals(other$depends)) {
            return false;
        }
        Set<String> this$softDepends = this.getSoftDepends();
        Set<String> other$softDepends = other.getSoftDepends();
        if (this$softDepends == null ? other$softDepends != null : !((Object)this$softDepends).equals(other$softDepends)) {
            return false;
        }
        File this$file = this.getFile();
        File other$file = other.getFile();
        if (this$file == null ? other$file != null : !((Object)this$file).equals((Object)other$file)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null) {
            if (other$description == null) return true;
            return false;
        }
        if (this$description.equals((Object)other$description)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof PluginDescription;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $main = this.getMain();
        result = result * 59 + ($main == null ? 43 : $main.hashCode());
        String $version = this.getVersion();
        result = result * 59 + ($version == null ? 43 : $version.hashCode());
        String $author = this.getAuthor();
        result = result * 59 + ($author == null ? 43 : $author.hashCode());
        Set<String> $depends = this.getDepends();
        result = result * 59 + ($depends == null ? 43 : ((Object)$depends).hashCode());
        Set<String> $softDepends = this.getSoftDepends();
        result = result * 59 + ($softDepends == null ? 43 : ((Object)$softDepends).hashCode());
        File $file = this.getFile();
        result = result * 59 + ($file == null ? 43 : ((Object)$file).hashCode());
        String $description = this.getDescription();
        return result * 59 + ($description == null ? 43 : $description.hashCode());
    }

    public String toString() {
        return "PluginDescription(name=" + this.getName() + ", main=" + this.getMain() + ", version=" + this.getVersion() + ", author=" + this.getAuthor() + ", depends=" + this.getDepends() + ", softDepends=" + this.getSoftDepends() + ", file=" + this.getFile() + ", description=" + this.getDescription() + ")";
    }

    public PluginDescription() {
    }

    public PluginDescription(String name, String main, String version, String author, Set<String> depends, Set<String> softDepends, File file, String description) {
        this.name = name;
        this.main = main;
        this.version = version;
        this.author = author;
        this.depends = depends;
        this.softDepends = softDepends;
        this.file = file;
        this.description = description;
    }
}

