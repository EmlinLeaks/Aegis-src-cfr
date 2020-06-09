/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.config;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

public class YamlConfiguration
extends ConfigurationProvider {
    private final ThreadLocal<Yaml> yaml = new ThreadLocal<Yaml>((YamlConfiguration)this){
        final /* synthetic */ YamlConfiguration this$0;
        {
            this.this$0 = this$0;
        }

        protected Yaml initialValue() {
            org.yaml.snakeyaml.representer.Representer representer = new org.yaml.snakeyaml.representer.Representer(this){
                final /* synthetic */ 1 this$1;
                {
                    this.this$1 = this$1;
                    this.representers.put(Configuration.class, new org.yaml.snakeyaml.representer.Represent(this){
                        final /* synthetic */ net.md_5.bungee.config.YamlConfiguration$1$1 this$2;
                        {
                            this.this$2 = this$2;
                        }

                        public org.yaml.snakeyaml.nodes.Node representData(Object data) {
                            return this.this$2.represent(((Configuration)data).self);
                        }
                    });
                }
            };
            org.yaml.snakeyaml.DumperOptions options = new org.yaml.snakeyaml.DumperOptions();
            options.setDefaultFlowStyle((org.yaml.snakeyaml.DumperOptions$FlowStyle)org.yaml.snakeyaml.DumperOptions$FlowStyle.BLOCK);
            return new Yaml((org.yaml.snakeyaml.constructor.BaseConstructor)new org.yaml.snakeyaml.constructor.Constructor(), (org.yaml.snakeyaml.representer.Representer)representer, (org.yaml.snakeyaml.DumperOptions)options);
        }
    };

    @Override
    public void save(Configuration config, File file) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter((OutputStream)new FileOutputStream((File)file), (Charset)Charsets.UTF_8);
        Throwable throwable = null;
        try {
            this.save((Configuration)config, (Writer)writer);
            return;
        }
        catch (Throwable throwable2) {
            throwable = throwable2;
            throw throwable2;
        }
        finally {
            YamlConfiguration.$closeResource((Throwable)throwable, (AutoCloseable)writer);
        }
    }

    @Override
    public void save(Configuration config, Writer writer) {
        this.yaml.get().dump(config.self, (Writer)writer);
    }

    @Override
    public Configuration load(File file) throws IOException {
        return this.load((File)file, null);
    }

    @Override
    public Configuration load(File file, Configuration defaults) throws IOException {
        FileInputStream is = new FileInputStream((File)file);
        Throwable throwable = null;
        try {
            Configuration configuration = this.load((InputStream)is, (Configuration)defaults);
            return configuration;
        }
        catch (Throwable throwable2) {
            throwable = throwable2;
            throw throwable2;
        }
        finally {
            YamlConfiguration.$closeResource((Throwable)throwable, (AutoCloseable)is);
        }
    }

    @Override
    public Configuration load(Reader reader) {
        return this.load((Reader)reader, null);
    }

    @Override
    public Configuration load(Reader reader, Configuration defaults) {
        LinkedHashMap<K, V> map = (LinkedHashMap<K, V>)this.yaml.get().loadAs((Reader)reader, LinkedHashMap.class);
        if (map != null) return new Configuration(map, (Configuration)defaults);
        map = new LinkedHashMap<K, V>();
        return new Configuration(map, (Configuration)defaults);
    }

    @Override
    public Configuration load(InputStream is) {
        return this.load((InputStream)is, null);
    }

    @Override
    public Configuration load(InputStream is, Configuration defaults) {
        LinkedHashMap<K, V> map = (LinkedHashMap<K, V>)this.yaml.get().loadAs((InputStream)is, LinkedHashMap.class);
        if (map != null) return new Configuration(map, (Configuration)defaults);
        map = new LinkedHashMap<K, V>();
        return new Configuration(map, (Configuration)defaults);
    }

    @Override
    public Configuration load(String string) {
        return this.load((String)string, null);
    }

    @Override
    public Configuration load(String string, Configuration defaults) {
        LinkedHashMap<K, V> map = (LinkedHashMap<K, V>)this.yaml.get().loadAs((String)string, LinkedHashMap.class);
        if (map != null) return new Configuration(map, (Configuration)defaults);
        map = new LinkedHashMap<K, V>();
        return new Configuration(map, (Configuration)defaults);
    }

    YamlConfiguration() {
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 == null) {
            x1.close();
            return;
        }
        try {
            x1.close();
            return;
        }
        catch (Throwable throwable) {
            x0.addSuppressed((Throwable)throwable);
            return;
        }
    }
}

