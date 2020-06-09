/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.module;

public class ModuleVersion {
    private final String build;
    private final String git;

    public static ModuleVersion parse(String version) {
        int lastColon = version.lastIndexOf((int)58);
        int secondLastColon = version.lastIndexOf((int)58, (int)(lastColon - 1));
        if (lastColon == -1) return null;
        if (secondLastColon == -1) {
            return null;
        }
        String buildNumber = version.substring((int)(lastColon + 1), (int)version.length());
        String gitCommit = version.substring((int)(secondLastColon + 1), (int)lastColon).replaceAll((String)"\"", (String)"");
        if ("unknown".equals((Object)buildNumber)) return null;
        if (!"unknown".equals((Object)gitCommit)) return new ModuleVersion((String)buildNumber, (String)gitCommit);
        return null;
    }

    public String getBuild() {
        return this.build;
    }

    public String getGit() {
        return this.git;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ModuleVersion)) {
            return false;
        }
        ModuleVersion other = (ModuleVersion)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$build = this.getBuild();
        String other$build = other.getBuild();
        if (this$build == null ? other$build != null : !this$build.equals((Object)other$build)) {
            return false;
        }
        String this$git = this.getGit();
        String other$git = other.getGit();
        if (this$git == null) {
            if (other$git == null) return true;
            return false;
        }
        if (this$git.equals((Object)other$git)) return true;
        return false;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ModuleVersion;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $build = this.getBuild();
        result = result * 59 + ($build == null ? 43 : $build.hashCode());
        String $git = this.getGit();
        return result * 59 + ($git == null ? 43 : $git.hashCode());
    }

    public String toString() {
        return "ModuleVersion(build=" + this.getBuild() + ", git=" + this.getGit() + ")";
    }

    private ModuleVersion(String build, String git) {
        this.build = build;
        this.git = git;
    }
}

