/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.emitter.Emitable;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.serializer.Serializer;

public class Yaml {
    protected final Resolver resolver;
    private String name;
    protected BaseConstructor constructor;
    protected Representer representer;
    protected DumperOptions dumperOptions;
    protected LoaderOptions loadingConfig;

    public Yaml() {
        this((BaseConstructor)new Constructor(), (Representer)new Representer(), (DumperOptions)new DumperOptions(), (LoaderOptions)new LoaderOptions(), (Resolver)new Resolver());
    }

    public Yaml(DumperOptions dumperOptions) {
        this((BaseConstructor)new Constructor(), (Representer)new Representer((DumperOptions)dumperOptions), (DumperOptions)dumperOptions);
    }

    public Yaml(LoaderOptions loadingConfig) {
        this((BaseConstructor)new Constructor(), (Representer)new Representer(), (DumperOptions)new DumperOptions(), (LoaderOptions)loadingConfig);
    }

    public Yaml(Representer representer) {
        this((BaseConstructor)new Constructor(), (Representer)representer);
    }

    public Yaml(BaseConstructor constructor) {
        this((BaseConstructor)constructor, (Representer)new Representer());
    }

    public Yaml(BaseConstructor constructor, Representer representer) {
        this((BaseConstructor)constructor, (Representer)representer, (DumperOptions)Yaml.initDumperOptions((Representer)representer));
    }

    private static DumperOptions initDumperOptions(Representer representer) {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle((DumperOptions.FlowStyle)representer.getDefaultFlowStyle());
        dumperOptions.setDefaultScalarStyle((DumperOptions.ScalarStyle)representer.getDefaultScalarStyle());
        dumperOptions.setAllowReadOnlyProperties((boolean)representer.getPropertyUtils().isAllowReadOnlyProperties());
        dumperOptions.setTimeZone((TimeZone)representer.getTimeZone());
        return dumperOptions;
    }

    public Yaml(Representer representer, DumperOptions dumperOptions) {
        this((BaseConstructor)new Constructor(), (Representer)representer, (DumperOptions)dumperOptions, (LoaderOptions)new LoaderOptions(), (Resolver)new Resolver());
    }

    public Yaml(BaseConstructor constructor, Representer representer, DumperOptions dumperOptions) {
        this((BaseConstructor)constructor, (Representer)representer, (DumperOptions)dumperOptions, (LoaderOptions)new LoaderOptions(), (Resolver)new Resolver());
    }

    public Yaml(BaseConstructor constructor, Representer representer, DumperOptions dumperOptions, LoaderOptions loadingConfig) {
        this((BaseConstructor)constructor, (Representer)representer, (DumperOptions)dumperOptions, (LoaderOptions)loadingConfig, (Resolver)new Resolver());
    }

    public Yaml(BaseConstructor constructor, Representer representer, DumperOptions dumperOptions, Resolver resolver) {
        this((BaseConstructor)constructor, (Representer)representer, (DumperOptions)dumperOptions, (LoaderOptions)new LoaderOptions(), (Resolver)resolver);
    }

    public Yaml(BaseConstructor constructor, Representer representer, DumperOptions dumperOptions, LoaderOptions loadingConfig, Resolver resolver) {
        if (!constructor.isExplicitPropertyUtils()) {
            constructor.setPropertyUtils((PropertyUtils)representer.getPropertyUtils());
        } else if (!representer.isExplicitPropertyUtils()) {
            representer.setPropertyUtils((PropertyUtils)constructor.getPropertyUtils());
        }
        this.constructor = constructor;
        this.constructor.setAllowDuplicateKeys((boolean)loadingConfig.isAllowDuplicateKeys());
        this.constructor.setWrappedToRootException((boolean)loadingConfig.isWrappedToRootException());
        if (dumperOptions.getIndent() <= dumperOptions.getIndicatorIndent()) {
            throw new YAMLException((String)"Indicator indent must be smaller then indent.");
        }
        representer.setDefaultFlowStyle((DumperOptions.FlowStyle)dumperOptions.getDefaultFlowStyle());
        representer.setDefaultScalarStyle((DumperOptions.ScalarStyle)dumperOptions.getDefaultScalarStyle());
        representer.getPropertyUtils().setAllowReadOnlyProperties((boolean)dumperOptions.isAllowReadOnlyProperties());
        representer.setTimeZone((TimeZone)dumperOptions.getTimeZone());
        this.representer = representer;
        this.dumperOptions = dumperOptions;
        this.loadingConfig = loadingConfig;
        this.resolver = resolver;
        this.name = "Yaml:" + System.identityHashCode((Object)this);
    }

    public String dump(Object data) {
        ArrayList<Object> list = new ArrayList<Object>((int)1);
        list.add(data);
        return this.dumpAll(list.iterator());
    }

    public Node represent(Object data) {
        return this.representer.represent((Object)data);
    }

    public String dumpAll(Iterator<? extends Object> data) {
        StringWriter buffer = new StringWriter();
        this.dumpAll(data, (Writer)buffer, null);
        return buffer.toString();
    }

    public void dump(Object data, Writer output) {
        ArrayList<Object> list = new ArrayList<Object>((int)1);
        list.add(data);
        this.dumpAll(list.iterator(), (Writer)output, null);
    }

    public void dumpAll(Iterator<? extends Object> data, Writer output) {
        this.dumpAll(data, (Writer)output, null);
    }

    private void dumpAll(Iterator<? extends Object> data, Writer output, Tag rootTag) {
        Serializer serializer = new Serializer((Emitable)new Emitter((Writer)output, (DumperOptions)this.dumperOptions), (Resolver)this.resolver, (DumperOptions)this.dumperOptions, (Tag)rootTag);
        try {
            serializer.open();
            do {
                if (!data.hasNext()) {
                    serializer.close();
                    return;
                }
                Node node = this.representer.represent((Object)data.next());
                serializer.serialize((Node)node);
            } while (true);
        }
        catch (IOException e) {
            throw new YAMLException((Throwable)e);
        }
    }

    public String dumpAs(Object data, Tag rootTag, DumperOptions.FlowStyle flowStyle) {
        DumperOptions.FlowStyle oldStyle = this.representer.getDefaultFlowStyle();
        if (flowStyle != null) {
            this.representer.setDefaultFlowStyle((DumperOptions.FlowStyle)flowStyle);
        }
        ArrayList<Object> list = new ArrayList<Object>((int)1);
        list.add(data);
        StringWriter buffer = new StringWriter();
        this.dumpAll(list.iterator(), (Writer)buffer, (Tag)rootTag);
        this.representer.setDefaultFlowStyle((DumperOptions.FlowStyle)oldStyle);
        return buffer.toString();
    }

    public String dumpAsMap(Object data) {
        return this.dumpAs((Object)data, (Tag)Tag.MAP, (DumperOptions.FlowStyle)DumperOptions.FlowStyle.BLOCK);
    }

    public List<Event> serialize(Node data) {
        SilentEmitter emitter = new SilentEmitter(null);
        Serializer serializer = new Serializer((Emitable)emitter, (Resolver)this.resolver, (DumperOptions)this.dumperOptions, null);
        try {
            serializer.open();
            serializer.serialize((Node)data);
            serializer.close();
            return emitter.getEvents();
        }
        catch (IOException e) {
            throw new YAMLException((Throwable)e);
        }
    }

    public <T> T load(String yaml) {
        return (T)this.loadFromReader((StreamReader)new StreamReader((String)yaml), Object.class);
    }

    public <T> T load(InputStream io) {
        return (T)this.loadFromReader((StreamReader)new StreamReader((Reader)new UnicodeReader((InputStream)io)), Object.class);
    }

    public <T> T load(Reader io) {
        return (T)this.loadFromReader((StreamReader)new StreamReader((Reader)io), Object.class);
    }

    public <T> T loadAs(Reader io, Class<T> type) {
        return (T)this.loadFromReader((StreamReader)new StreamReader((Reader)io), type);
    }

    public <T> T loadAs(String yaml, Class<T> type) {
        return (T)this.loadFromReader((StreamReader)new StreamReader((String)yaml), type);
    }

    public <T> T loadAs(InputStream input, Class<T> type) {
        return (T)this.loadFromReader((StreamReader)new StreamReader((Reader)new UnicodeReader((InputStream)input)), type);
    }

    private Object loadFromReader(StreamReader sreader, Class<?> type) {
        Composer composer = new Composer((Parser)new ParserImpl((StreamReader)sreader), (Resolver)this.resolver);
        this.constructor.setComposer((Composer)composer);
        return this.constructor.getSingleData(type);
    }

    public Iterable<Object> loadAll(Reader yaml) {
        Composer composer = new Composer((Parser)new ParserImpl((StreamReader)new StreamReader((Reader)yaml)), (Resolver)this.resolver);
        this.constructor.setComposer((Composer)composer);
        Iterator<Object> result = new Iterator<Object>((Yaml)this){
            final /* synthetic */ Yaml this$0;
            {
                this.this$0 = this$0;
            }

            public boolean hasNext() {
                return this.this$0.constructor.checkData();
            }

            public Object next() {
                return this.this$0.constructor.getData();
            }

            public void remove() {
                throw new java.lang.UnsupportedOperationException();
            }
        };
        return new YamlIterable((Iterator<Object>)result);
    }

    public Iterable<Object> loadAll(String yaml) {
        return this.loadAll((Reader)new StringReader((String)yaml));
    }

    public Iterable<Object> loadAll(InputStream yaml) {
        return this.loadAll((Reader)new UnicodeReader((InputStream)yaml));
    }

    public Node compose(Reader yaml) {
        Composer composer = new Composer((Parser)new ParserImpl((StreamReader)new StreamReader((Reader)yaml)), (Resolver)this.resolver);
        return composer.getSingleNode();
    }

    public Iterable<Node> composeAll(Reader yaml) {
        Composer composer = new Composer((Parser)new ParserImpl((StreamReader)new StreamReader((Reader)yaml)), (Resolver)this.resolver);
        Iterator<Node> result = new Iterator<Node>((Yaml)this, (Composer)composer){
            final /* synthetic */ Composer val$composer;
            final /* synthetic */ Yaml this$0;
            {
                this.this$0 = this$0;
                this.val$composer = composer;
            }

            public boolean hasNext() {
                return this.val$composer.checkNode();
            }

            public Node next() {
                return this.val$composer.getNode();
            }

            public void remove() {
                throw new java.lang.UnsupportedOperationException();
            }
        };
        return new NodeIterable((Iterator<Node>)result);
    }

    public void addImplicitResolver(Tag tag, Pattern regexp, String first) {
        this.resolver.addImplicitResolver((Tag)tag, (Pattern)regexp, (String)first);
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Iterable<Event> parse(Reader yaml) {
        ParserImpl parser = new ParserImpl((StreamReader)new StreamReader((Reader)yaml));
        Iterator<Event> result = new Iterator<Event>((Yaml)this, (Parser)parser){
            final /* synthetic */ Parser val$parser;
            final /* synthetic */ Yaml this$0;
            {
                this.this$0 = this$0;
                this.val$parser = parser;
            }

            public boolean hasNext() {
                if (this.val$parser.peekEvent() == null) return false;
                return true;
            }

            public Event next() {
                return this.val$parser.getEvent();
            }

            public void remove() {
                throw new java.lang.UnsupportedOperationException();
            }
        };
        return new EventIterable((Iterator<Event>)result);
    }

    public void setBeanAccess(BeanAccess beanAccess) {
        this.constructor.getPropertyUtils().setBeanAccess((BeanAccess)beanAccess);
        this.representer.getPropertyUtils().setBeanAccess((BeanAccess)beanAccess);
    }

    public void addTypeDescription(TypeDescription td) {
        this.constructor.addTypeDescription((TypeDescription)td);
        this.representer.addTypeDescription((TypeDescription)td);
    }
}

