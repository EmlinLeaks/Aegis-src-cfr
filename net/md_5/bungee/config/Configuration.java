/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Configuration {
    private static final char SEPARATOR = '.';
    final Map<String, Object> self = new LinkedHashMap<String, Object>();
    private final Configuration defaults;

    public Configuration() {
        this(null);
    }

    public Configuration(Configuration defaults) {
        this(new LinkedHashMap<K, V>(), (Configuration)defaults);
    }

    Configuration(Map<?, ?> map, Configuration defaults) {
        this.defaults = defaults;
        Iterator<Map.Entry<?, ?>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            String key;
            Map.Entry<?, ?> entry = iterator.next();
            String string = key = entry.getKey() == null ? "null" : entry.getKey().toString();
            if (entry.getValue() instanceof Map) {
                this.self.put((String)key, (Object)new Configuration((Map)entry.getValue(), defaults == null ? null : defaults.getSection((String)key)));
                continue;
            }
            this.self.put((String)key, entry.getValue());
        }
    }

    private Configuration getSectionFor(String path) {
        int index = path.indexOf((int)46);
        if (index == -1) {
            return this;
        }
        String root = path.substring((int)0, (int)index);
        Object section = this.self.get((Object)root);
        if (section != null) return (Configuration)section;
        section = new Configuration(this.defaults == null ? null : this.defaults.getSection((String)root));
        this.self.put((String)root, (Object)section);
        return (Configuration)section;
    }

    private String getChild(String path) {
        String string;
        int index = path.indexOf((int)46);
        if (index == -1) {
            string = path;
            return string;
        }
        string = path.substring((int)(index + 1));
        return string;
    }

    public <T> T get(String path, T def) {
        Object object;
        Configuration section = this.getSectionFor((String)path);
        Object val = section == this ? this.self.get((Object)path) : section.get((String)this.getChild((String)path), def);
        if (val == null && def instanceof Configuration) {
            this.self.put((String)path, def);
        }
        if (val != null) {
            object = val;
            return (T)((T)object);
        }
        object = def;
        return (T)object;
    }

    public boolean contains(String path) {
        if (this.get((String)path, null) == null) return false;
        return true;
    }

    public Object get(String path) {
        return this.get((String)path, this.getDefault((String)path));
    }

    public Object getDefault(String path) {
        if (this.defaults == null) {
            return null;
        }
        Object object = this.defaults.get((String)path);
        return object;
    }

    public void set(String path, Object value) {
        Configuration section;
        if (value instanceof Map) {
            value = new Configuration((Map)value, this.defaults == null ? null : this.defaults.getSection((String)path));
        }
        if ((section = this.getSectionFor((String)path)) != this) {
            section.set((String)this.getChild((String)path), (Object)value);
            return;
        }
        if (value == null) {
            this.self.remove((Object)path);
            return;
        }
        this.self.put((String)path, (Object)value);
    }

    public Configuration getSection(String path) {
        Configuration configuration;
        Object object;
        Object def = this.getDefault((String)path);
        if (def instanceof Configuration) {
            object = def;
            return (Configuration)this.get((String)path, object);
        }
        object = new Configuration(configuration);
        if (this.defaults == null) {
            return (Configuration)this.get((String)path, object);
        }
        configuration = this.defaults.getSection((String)path);
        return (Configuration)this.get((String)path, object);
    }

    public Collection<String> getKeys() {
        return new LinkedHashSet<String>(this.self.keySet());
    }

    public byte getByte(String path) {
        byte by;
        Object def = this.getDefault((String)path);
        if (def instanceof Number) {
            by = ((Number)def).byteValue();
            return this.getByte((String)path, (byte)by);
        }
        by = 0;
        return this.getByte((String)path, (byte)by);
    }

    public byte getByte(String path, byte def) {
        byte by;
        Byte val = this.get((String)path, Byte.valueOf((byte)def));
        if (val instanceof Number) {
            by = ((Number)val).byteValue();
            return by;
        }
        by = def;
        return by;
    }

    public List<Byte> getByteList(String path) {
        List<?> list = this.getList((String)path);
        ArrayList<Byte> result = new ArrayList<Byte>();
        Iterator<?> iterator = list.iterator();
        while (iterator.hasNext()) {
            ? object = iterator.next();
            if (!(object instanceof Number)) continue;
            result.add((Byte)Byte.valueOf((byte)((Number)object).byteValue()));
        }
        return result;
    }

    public short getShort(String path) {
        short s;
        Object def = this.getDefault((String)path);
        if (def instanceof Number) {
            s = ((Number)def).shortValue();
            return this.getShort((String)path, (short)s);
        }
        s = 0;
        return this.getShort((String)path, (short)s);
    }

    public short getShort(String path, short def) {
        short s;
        Short val = this.get((String)path, Short.valueOf((short)def));
        if (val instanceof Number) {
            s = ((Number)val).shortValue();
            return s;
        }
        s = def;
        return s;
    }

    public List<Short> getShortList(String path) {
        List<?> list = this.getList((String)path);
        ArrayList<Short> result = new ArrayList<Short>();
        Iterator<?> iterator = list.iterator();
        while (iterator.hasNext()) {
            ? object = iterator.next();
            if (!(object instanceof Number)) continue;
            result.add((Short)Short.valueOf((short)((Number)object).shortValue()));
        }
        return result;
    }

    public int getInt(String path) {
        int n;
        Object def = this.getDefault((String)path);
        if (def instanceof Number) {
            n = ((Number)def).intValue();
            return this.getInt((String)path, (int)n);
        }
        n = 0;
        return this.getInt((String)path, (int)n);
    }

    public int getInt(String path, int def) {
        int n;
        Integer val = this.get((String)path, Integer.valueOf((int)def));
        if (val instanceof Number) {
            n = ((Number)val).intValue();
            return n;
        }
        n = def;
        return n;
    }

    public List<Integer> getIntList(String path) {
        List<?> list = this.getList((String)path);
        ArrayList<Integer> result = new ArrayList<Integer>();
        Iterator<?> iterator = list.iterator();
        while (iterator.hasNext()) {
            ? object = iterator.next();
            if (!(object instanceof Number)) continue;
            result.add((Integer)Integer.valueOf((int)((Number)object).intValue()));
        }
        return result;
    }

    public long getLong(String path) {
        long l;
        Object def = this.getDefault((String)path);
        if (def instanceof Number) {
            l = ((Number)def).longValue();
            return this.getLong((String)path, (long)l);
        }
        l = 0L;
        return this.getLong((String)path, (long)l);
    }

    public long getLong(String path, long def) {
        long l;
        Long val = this.get((String)path, Long.valueOf((long)def));
        if (val instanceof Number) {
            l = ((Number)val).longValue();
            return l;
        }
        l = def;
        return l;
    }

    public List<Long> getLongList(String path) {
        List<?> list = this.getList((String)path);
        ArrayList<Long> result = new ArrayList<Long>();
        Iterator<?> iterator = list.iterator();
        while (iterator.hasNext()) {
            ? object = iterator.next();
            if (!(object instanceof Number)) continue;
            result.add((Long)Long.valueOf((long)((Number)object).longValue()));
        }
        return result;
    }

    public float getFloat(String path) {
        float f;
        Object def = this.getDefault((String)path);
        if (def instanceof Number) {
            f = ((Number)def).floatValue();
            return this.getFloat((String)path, (float)f);
        }
        f = 0.0f;
        return this.getFloat((String)path, (float)f);
    }

    public float getFloat(String path, float def) {
        float f;
        Float val = this.get((String)path, Float.valueOf((float)def));
        if (val instanceof Number) {
            f = ((Number)val).floatValue();
            return f;
        }
        f = def;
        return f;
    }

    public List<Float> getFloatList(String path) {
        List<?> list = this.getList((String)path);
        ArrayList<Float> result = new ArrayList<Float>();
        Iterator<?> iterator = list.iterator();
        while (iterator.hasNext()) {
            ? object = iterator.next();
            if (!(object instanceof Number)) continue;
            result.add((Float)Float.valueOf((float)((Number)object).floatValue()));
        }
        return result;
    }

    public double getDouble(String path) {
        double d;
        Object def = this.getDefault((String)path);
        if (def instanceof Number) {
            d = ((Number)def).doubleValue();
            return this.getDouble((String)path, (double)d);
        }
        d = 0.0;
        return this.getDouble((String)path, (double)d);
    }

    public double getDouble(String path, double def) {
        double d;
        Double val = this.get((String)path, Double.valueOf((double)def));
        if (val instanceof Number) {
            d = ((Number)val).doubleValue();
            return d;
        }
        d = def;
        return d;
    }

    public List<Double> getDoubleList(String path) {
        List<?> list = this.getList((String)path);
        ArrayList<Double> result = new ArrayList<Double>();
        Iterator<?> iterator = list.iterator();
        while (iterator.hasNext()) {
            ? object = iterator.next();
            if (!(object instanceof Number)) continue;
            result.add((Double)Double.valueOf((double)((Number)object).doubleValue()));
        }
        return result;
    }

    public boolean getBoolean(String path) {
        boolean bl;
        Object def = this.getDefault((String)path);
        if (def instanceof Boolean) {
            bl = ((Boolean)def).booleanValue();
            return this.getBoolean((String)path, (boolean)bl);
        }
        bl = false;
        return this.getBoolean((String)path, (boolean)bl);
    }

    public boolean getBoolean(String path, boolean def) {
        boolean bl;
        Boolean val = this.get((String)path, Boolean.valueOf((boolean)def));
        if (val instanceof Boolean) {
            bl = val.booleanValue();
            return bl;
        }
        bl = def;
        return bl;
    }

    public List<Boolean> getBooleanList(String path) {
        List<?> list = this.getList((String)path);
        ArrayList<Boolean> result = new ArrayList<Boolean>();
        Iterator<?> iterator = list.iterator();
        while (iterator.hasNext()) {
            ? object = iterator.next();
            if (!(object instanceof Boolean)) continue;
            result.add((Boolean)((Boolean)object));
        }
        return result;
    }

    public char getChar(String path) {
        char c;
        Object def = this.getDefault((String)path);
        if (def instanceof Character) {
            c = ((Character)def).charValue();
            return this.getChar((String)path, (char)c);
        }
        c = '\u0000';
        return this.getChar((String)path, (char)c);
    }

    public char getChar(String path, char def) {
        char c;
        Character val = this.get((String)path, Character.valueOf((char)def));
        if (val instanceof Character) {
            c = val.charValue();
            return c;
        }
        c = def;
        return c;
    }

    public List<Character> getCharList(String path) {
        List<?> list = this.getList((String)path);
        ArrayList<Character> result = new ArrayList<Character>();
        Iterator<?> iterator = list.iterator();
        while (iterator.hasNext()) {
            ? object = iterator.next();
            if (!(object instanceof Character)) continue;
            result.add((Character)((Character)object));
        }
        return result;
    }

    public String getString(String path) {
        String string;
        Object def = this.getDefault((String)path);
        if (def instanceof String) {
            string = (String)def;
            return this.getString((String)path, (String)string);
        }
        string = "";
        return this.getString((String)path, (String)string);
    }

    public String getString(String path, String def) {
        String string;
        String val = this.get((String)path, def);
        if (val instanceof String) {
            string = val;
            return string;
        }
        string = def;
        return string;
    }

    public List<String> getStringList(String path) {
        List<?> list = this.getList((String)path);
        ArrayList<String> result = new ArrayList<String>();
        Iterator<?> iterator = list.iterator();
        while (iterator.hasNext()) {
            ? object = iterator.next();
            if (!(object instanceof String)) continue;
            result.add((String)((String)object));
        }
        return result;
    }

    public List<?> getList(String path) {
        List list;
        Object def = this.getDefault((String)path);
        if (def instanceof List) {
            list = (List)def;
            return this.getList((String)path, list);
        }
        list = Collections.EMPTY_LIST;
        return this.getList((String)path, list);
    }

    public List<?> getList(String path, List<?> def) {
        List<?> list;
        List<?> val = this.get((String)path, def);
        if (val instanceof List) {
            list = val;
            return list;
        }
        list = def;
        return list;
    }
}

