/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.representer;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.BaseRepresenter;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.SafeRepresenter;

class SafeRepresenter
extends BaseRepresenter {
    protected Map<Class<? extends Object>, Tag> classTags;
    protected TimeZone timeZone = null;
    protected DumperOptions.NonPrintableStyle nonPrintableStyle;
    public static Pattern MULTILINE_PATTERN = Pattern.compile((String)"\n|\u0085|\u2028|\u2029");

    public SafeRepresenter() {
        this((DumperOptions)new DumperOptions());
    }

    public SafeRepresenter(DumperOptions options) {
        this.nullRepresenter = new RepresentNull((SafeRepresenter)this);
        this.representers.put(String.class, new RepresentString((SafeRepresenter)this));
        this.representers.put(Boolean.class, new RepresentBoolean((SafeRepresenter)this));
        this.representers.put(Character.class, new RepresentString((SafeRepresenter)this));
        this.representers.put(UUID.class, new RepresentUuid((SafeRepresenter)this));
        this.representers.put(byte[].class, new RepresentByteArray((SafeRepresenter)this));
        RepresentPrimitiveArray primitiveArray = new RepresentPrimitiveArray((SafeRepresenter)this);
        this.representers.put(short[].class, primitiveArray);
        this.representers.put(int[].class, primitiveArray);
        this.representers.put(long[].class, primitiveArray);
        this.representers.put(float[].class, primitiveArray);
        this.representers.put(double[].class, primitiveArray);
        this.representers.put(char[].class, primitiveArray);
        this.representers.put(boolean[].class, primitiveArray);
        this.multiRepresenters.put(Number.class, new RepresentNumber((SafeRepresenter)this));
        this.multiRepresenters.put(List.class, new RepresentList((SafeRepresenter)this));
        this.multiRepresenters.put(Map.class, new RepresentMap((SafeRepresenter)this));
        this.multiRepresenters.put(Set.class, new RepresentSet((SafeRepresenter)this));
        this.multiRepresenters.put(Iterator.class, new RepresentIterator((SafeRepresenter)this));
        this.multiRepresenters.put(new Object[0].getClass(), new RepresentArray((SafeRepresenter)this));
        this.multiRepresenters.put(Date.class, new RepresentDate((SafeRepresenter)this));
        this.multiRepresenters.put(Enum.class, new RepresentEnum((SafeRepresenter)this));
        this.multiRepresenters.put(Calendar.class, new RepresentDate((SafeRepresenter)this));
        this.classTags = new HashMap<Class<? extends Object>, Tag>();
        this.nonPrintableStyle = options.getNonPrintableStyle();
    }

    protected Tag getTag(Class<?> clazz, Tag defaultTag) {
        if (!this.classTags.containsKey(clazz)) return defaultTag;
        return this.classTags.get(clazz);
    }

    public Tag addClassTag(Class<? extends Object> clazz, Tag tag) {
        if (tag != null) return this.classTags.put(clazz, (Tag)tag);
        throw new NullPointerException((String)"Tag must be provided.");
    }

    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}

