/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt.holder;

import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.holder.Field;
import com.flowpowered.nbt.holder.FieldUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListField<T>
implements Field<List<T>> {
    private final Field<T> backingField;

    public ListField(Field<T> field) {
        this.backingField = field;
    }

    @Override
    public List<T> getValue(Tag<?> tag) throws IllegalArgumentException {
        ListTag listTag = FieldUtils.checkTagCast(tag, ListTag.class);
        ArrayList<T> result = new ArrayList<T>();
        Iterator<E> iterator = listTag.getValue().iterator();
        while (iterator.hasNext()) {
            Tag element = (Tag)iterator.next();
            result.add(this.backingField.getValue(element));
        }
        return result;
    }

    @Override
    public Tag<?> getValue(String name, List<T> value) {
        ArrayList<Tag<?>> tags = new ArrayList<Tag<?>>();
        Class tagClazz = Tag.class;
        Iterator<T> iterator = value.iterator();
        while (iterator.hasNext()) {
            T element = iterator.next();
            Tag<?> tag = this.backingField.getValue((String)"", element);
            tagClazz = tag.getClass();
            tags.add(tag);
        }
        return new ListTag<Tag>((String)name, tagClazz, tags);
    }
}

