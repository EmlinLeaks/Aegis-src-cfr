/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.chat;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.md_5.bungee.chat.TranslationRegistry;

public final class TranslationRegistry {
    public static final TranslationRegistry INSTANCE = new TranslationRegistry();
    private final List<TranslationProvider> providers = new LinkedList<TranslationProvider>();

    private void addProvider(TranslationProvider provider) {
        this.providers.add((TranslationProvider)provider);
    }

    public String translate(String s) {
        String translation;
        TranslationProvider provider;
        Iterator<TranslationProvider> iterator = this.providers.iterator();
        do {
            if (!iterator.hasNext()) return s;
        } while ((translation = (provider = iterator.next()).translate((String)s)) == null);
        return translation;
    }

    public List<TranslationProvider> getProviders() {
        return this.providers;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TranslationRegistry)) {
            return false;
        }
        TranslationRegistry other = (TranslationRegistry)o;
        List<TranslationProvider> this$providers = this.getProviders();
        List<TranslationProvider> other$providers = other.getProviders();
        if (this$providers == null) {
            if (other$providers == null) return true;
            return false;
        }
        if (((Object)this$providers).equals(other$providers)) return true;
        return false;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List<TranslationProvider> $providers = this.getProviders();
        return result * 59 + ($providers == null ? 43 : ((Object)$providers).hashCode());
    }

    public String toString() {
        return "TranslationRegistry(providers=" + this.getProviders() + ")";
    }

    private TranslationRegistry() {
    }

    static {
        try {
            INSTANCE.addProvider((TranslationProvider)new JsonProvider((String)"/assets/minecraft/lang/en_us.json"));
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            INSTANCE.addProvider((TranslationProvider)new JsonProvider((String)"/mojang-translations/en_us.json"));
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            INSTANCE.addProvider((TranslationProvider)new ResourceBundleProvider((String)"mojang-translations/en_US"));
            return;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

