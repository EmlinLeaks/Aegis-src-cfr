/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt.holder;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.holder.FieldValue;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class FieldHolder {
    private final List<FieldValue<?>> fields = new ArrayList<FieldValue<?>>();

    protected FieldHolder(FieldValue<?> ... fields) {
        this.addFields(fields);
    }

    protected void addFields(FieldValue<?> ... fields) {
        Collections.addAll(this.fields, fields);
    }

    public CompoundMap save() {
        CompoundMap map = new CompoundMap();
        Iterator<FieldValue<?>> iterator = this.fields.iterator();
        while (iterator.hasNext()) {
            FieldValue<?> field = iterator.next();
            field.save((CompoundMap)map);
        }
        return map;
    }

    public void load(CompoundTag tag) {
        Iterator<FieldValue<?>> iterator = this.fields.iterator();
        while (iterator.hasNext()) {
            FieldValue<?> field = iterator.next();
            field.load((CompoundTag)tag);
        }
    }

    public void save(File file, boolean compressed) throws IOException {
        this.save((OutputStream)new FileOutputStream((File)file), (boolean)compressed);
    }

    public void save(OutputStream stream, boolean compressed) throws IOException {
        NBTOutputStream os = new NBTOutputStream((OutputStream)stream, (boolean)compressed);
        os.writeTag(new CompoundTag((String)"", (CompoundMap)this.save()));
    }

    public void load(File file, boolean compressed) throws IOException {
        this.load((InputStream)new FileInputStream((File)file), (boolean)compressed);
    }

    public void load(InputStream stream, boolean compressed) throws IOException {
        NBTInputStream is = new NBTInputStream((InputStream)stream, (boolean)compressed);
        Tag tag = is.readTag();
        if (!(tag instanceof CompoundTag)) {
            throw new IllegalArgumentException((String)("Expected CompoundTag, got " + tag.getClass()));
        }
        CompoundTag compound = (CompoundTag)tag;
        this.load((CompoundTag)compound);
    }
}

