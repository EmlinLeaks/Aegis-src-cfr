/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt.stream;

import com.flowpowered.nbt.ByteArrayTag;
import com.flowpowered.nbt.ByteTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.DoubleTag;
import com.flowpowered.nbt.EndTag;
import com.flowpowered.nbt.FloatTag;
import com.flowpowered.nbt.IntArrayTag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.LongTag;
import com.flowpowered.nbt.NBTConstants;
import com.flowpowered.nbt.ShortArrayTag;
import com.flowpowered.nbt.ShortTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;
import com.flowpowered.nbt.stream.EndianSwitchableOutputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;

public final class NBTOutputStream
implements Closeable {
    private final EndianSwitchableOutputStream os;

    public NBTOutputStream(OutputStream os) throws IOException {
        this((OutputStream)os, (boolean)true, (ByteOrder)ByteOrder.BIG_ENDIAN);
    }

    public NBTOutputStream(OutputStream os, boolean compressed) throws IOException {
        this((OutputStream)os, (boolean)compressed, (ByteOrder)ByteOrder.BIG_ENDIAN);
    }

    public NBTOutputStream(OutputStream os, boolean compressed, ByteOrder endianness) throws IOException {
        this.os = new EndianSwitchableOutputStream((OutputStream)(compressed ? new GZIPOutputStream((OutputStream)os) : os), (ByteOrder)endianness);
    }

    public void writeTag(Tag<?> tag) throws IOException {
        String name = tag.getName();
        byte[] nameBytes = name.getBytes((String)NBTConstants.CHARSET.name());
        this.os.writeByte((int)tag.getType().getId());
        this.os.writeShort((int)nameBytes.length);
        this.os.write((byte[])nameBytes);
        if (tag.getType() == TagType.TAG_END) {
            throw new IOException((String)"Named TAG_End not permitted.");
        }
        this.writeTagPayload(tag);
    }

    private void writeTagPayload(Tag<?> tag) throws IOException {
        switch (1.$SwitchMap$com$flowpowered$nbt$TagType[tag.getType().ordinal()]) {
            case 1: {
                this.writeEndTagPayload((EndTag)((EndTag)tag));
                return;
            }
            case 2: {
                this.writeByteTagPayload((ByteTag)((ByteTag)tag));
                return;
            }
            case 3: {
                this.writeShortTagPayload((ShortTag)((ShortTag)tag));
                return;
            }
            case 4: {
                this.writeIntTagPayload((IntTag)((IntTag)tag));
                return;
            }
            case 5: {
                this.writeLongTagPayload((LongTag)((LongTag)tag));
                return;
            }
            case 6: {
                this.writeFloatTagPayload((FloatTag)((FloatTag)tag));
                return;
            }
            case 7: {
                this.writeDoubleTagPayload((DoubleTag)((DoubleTag)tag));
                return;
            }
            case 8: {
                this.writeByteArrayTagPayload((ByteArrayTag)((ByteArrayTag)tag));
                return;
            }
            case 9: {
                this.writeStringTagPayload((StringTag)((StringTag)tag));
                return;
            }
            case 10: {
                this.writeListTagPayload((ListTag)tag);
                return;
            }
            case 11: {
                this.writeCompoundTagPayload((CompoundTag)((CompoundTag)tag));
                return;
            }
            case 12: {
                this.writeIntArrayTagPayload((IntArrayTag)((IntArrayTag)tag));
                return;
            }
            case 13: {
                this.writeShortArrayTagPayload((ShortArrayTag)((ShortArrayTag)tag));
                return;
            }
        }
        throw new IOException((String)("Invalid tag type: " + (Object)((Object)tag.getType()) + "."));
    }

    private void writeByteTagPayload(ByteTag tag) throws IOException {
        this.os.writeByte((int)tag.getValue().byteValue());
    }

    private void writeByteArrayTagPayload(ByteArrayTag tag) throws IOException {
        byte[] bytes = tag.getValue();
        this.os.writeInt((int)bytes.length);
        this.os.write((byte[])bytes);
    }

    private void writeCompoundTagPayload(CompoundTag tag) throws IOException {
        Iterator<Tag<?>> iterator = tag.getValue().values().iterator();
        do {
            if (!iterator.hasNext()) {
                this.os.writeByte((int)TagType.TAG_END.getId());
                return;
            }
            Tag<?> childTag = iterator.next();
            this.writeTag(childTag);
        } while (true);
    }

    private void writeListTagPayload(ListTag<?> tag) throws IOException {
        Class<?> clazz = tag.getElementType();
        Object tags = tag.getValue();
        int size = tags.size();
        this.os.writeByte((int)TagType.getByTagClass(clazz).getId());
        this.os.writeInt((int)size);
        Iterator<E> iterator = tags.iterator();
        while (iterator.hasNext()) {
            Tag tag1 = (Tag)iterator.next();
            this.writeTagPayload(tag1);
        }
    }

    private void writeStringTagPayload(StringTag tag) throws IOException {
        byte[] bytes = tag.getValue().getBytes((String)NBTConstants.CHARSET.name());
        this.os.writeShort((int)bytes.length);
        this.os.write((byte[])bytes);
    }

    private void writeDoubleTagPayload(DoubleTag tag) throws IOException {
        this.os.writeDouble((double)tag.getValue().doubleValue());
    }

    private void writeFloatTagPayload(FloatTag tag) throws IOException {
        this.os.writeFloat((float)tag.getValue().floatValue());
    }

    private void writeLongTagPayload(LongTag tag) throws IOException {
        this.os.writeLong((long)tag.getValue().longValue());
    }

    private void writeIntTagPayload(IntTag tag) throws IOException {
        this.os.writeInt((int)tag.getValue().intValue());
    }

    private void writeShortTagPayload(ShortTag tag) throws IOException {
        this.os.writeShort((int)tag.getValue().shortValue());
    }

    private void writeIntArrayTagPayload(IntArrayTag tag) throws IOException {
        int[] ints = tag.getValue();
        this.os.writeInt((int)ints.length);
        int i = 0;
        while (i < ints.length) {
            this.os.writeInt((int)ints[i]);
            ++i;
        }
    }

    private void writeShortArrayTagPayload(ShortArrayTag tag) throws IOException {
        short[] shorts = tag.getValue();
        this.os.writeInt((int)shorts.length);
        int i = 0;
        while (i < shorts.length) {
            this.os.writeShort((int)shorts[i]);
            ++i;
        }
    }

    private void writeEndTagPayload(EndTag tag) {
    }

    @Override
    public void close() throws IOException {
        this.os.close();
    }

    public ByteOrder getEndianness() {
        return this.os.getEndianness();
    }

    public void flush() throws IOException {
        this.os.flush();
    }
}

