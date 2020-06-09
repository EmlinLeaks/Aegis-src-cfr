/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt;

import com.flowpowered.nbt.ByteArrayTag;
import com.flowpowered.nbt.ByteTag;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.DoubleTag;
import com.flowpowered.nbt.EndTag;
import com.flowpowered.nbt.FloatTag;
import com.flowpowered.nbt.IntArrayTag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.LongTag;
import com.flowpowered.nbt.ShortArrayTag;
import com.flowpowered.nbt.ShortTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;
import java.util.HashMap;
import java.util.Map;

public enum TagType {
    TAG_END(EndTag.class, (String)"TAG_End", (int)0),
    TAG_BYTE(ByteTag.class, (String)"TAG_Byte", (int)1),
    TAG_SHORT(ShortTag.class, (String)"TAG_Short", (int)2),
    TAG_INT(IntTag.class, (String)"TAG_Int", (int)3),
    TAG_LONG(LongTag.class, (String)"TAG_Long", (int)4),
    TAG_FLOAT(FloatTag.class, (String)"TAG_Float", (int)5),
    TAG_DOUBLE(DoubleTag.class, (String)"TAG_Double", (int)6),
    TAG_BYTE_ARRAY(ByteArrayTag.class, (String)"TAG_Byte_Array", (int)7),
    TAG_STRING(StringTag.class, (String)"TAG_String", (int)8),
    TAG_LIST(ListTag.class, (String)"TAG_List", (int)9),
    TAG_COMPOUND(CompoundTag.class, (String)"TAG_Compound", (int)10),
    TAG_INT_ARRAY(IntArrayTag.class, (String)"TAG_Int_Array", (int)11),
    TAG_SHORT_ARRAY(ShortArrayTag.class, (String)"TAG_Short_Array", (int)100);
    
    private static final Map<Class<? extends Tag<?>>, TagType> BY_CLASS;
    private static final Map<String, TagType> BY_NAME;
    private static final TagType[] BY_ID;
    private final Class<? extends Tag<?>> tagClass;
    private final String typeName;
    private final int id;

    private TagType(Class<? extends Tag<?>> tagClass, String typeName, int id) {
        this.tagClass = tagClass;
        this.typeName = typeName;
        this.id = id;
        if (this.id <= BaseData.maxId) return;
        BaseData.maxId = (int)this.id;
    }

    public Class<? extends Tag<?>> getTagClass() {
        return this.tagClass;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public int getId() {
        return this.id;
    }

    public static TagType getByTagClass(Class<? extends Tag<?>> clazz) {
        TagType ret = BY_CLASS.get(clazz);
        if (ret != null) return ret;
        throw new IllegalArgumentException((String)("Tag type " + clazz + " is unknown!"));
    }

    public static TagType getByTypeName(String typeName) {
        TagType ret = BY_NAME.get((Object)typeName);
        if (ret != null) return ret;
        throw new IllegalArgumentException((String)("Tag type " + typeName + " is unknown!"));
    }

    public static TagType getById(int id) {
        if (id < 0) throw new IndexOutOfBoundsException((String)("Tag type id " + id + " is out of bounds!"));
        if (id >= BY_ID.length) throw new IndexOutOfBoundsException((String)("Tag type id " + id + " is out of bounds!"));
        TagType ret = BY_ID[id];
        if (ret != null) return ret;
        throw new IllegalArgumentException((String)("Tag type id " + id + " is unknown!"));
    }

    static {
        BY_CLASS = new HashMap<Class<? extends Tag<?>>, TagType>();
        BY_NAME = new HashMap<String, TagType>();
        BY_ID = new TagType[BaseData.maxId + 1];
        TagType[] arrtagType = TagType.values();
        int n = arrtagType.length;
        int n2 = 0;
        while (n2 < n) {
            TagType type = arrtagType[n2];
            BY_CLASS.put(type.getTagClass(), (TagType)type);
            BY_NAME.put((String)type.getTypeName(), (TagType)type);
            TagType.BY_ID[type.getId()] = type;
            ++n2;
        }
    }
}

