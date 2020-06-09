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
import com.flowpowered.nbt.stream.EndianSwitchableInputStream;
import com.flowpowered.nbt.stream.NBTInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public final class NBTInputStream
implements Closeable {
    private final EndianSwitchableInputStream is;

    public NBTInputStream(InputStream is) throws IOException {
        this((InputStream)is, (boolean)true, (ByteOrder)ByteOrder.BIG_ENDIAN);
    }

    public NBTInputStream(InputStream is, boolean compressed) throws IOException {
        this((InputStream)is, (boolean)compressed, (ByteOrder)ByteOrder.BIG_ENDIAN);
    }

    public NBTInputStream(InputStream is, boolean compressed, ByteOrder endianness) throws IOException {
        this.is = new EndianSwitchableInputStream((InputStream)(compressed ? new GZIPInputStream((InputStream)is) : is), (ByteOrder)endianness);
    }

    public Tag readTag() throws IOException {
        return this.readTag((int)0);
    }

    private Tag readTag(int depth) throws IOException {
        String name;
        int typeId = this.is.readByte() & 255;
        TagType type = TagType.getById((int)typeId);
        if (type != TagType.TAG_END) {
            int nameLength = this.is.readShort() & 65535;
            byte[] nameBytes = new byte[nameLength];
            this.is.readFully((byte[])nameBytes);
            name = new String((byte[])nameBytes, (String)NBTConstants.CHARSET.name());
            return this.readTagPayload((TagType)type, (String)name, (int)depth);
        }
        name = "";
        return this.readTagPayload((TagType)type, (String)name, (int)depth);
    }

    private Tag readTagPayload(TagType type, String name, int depth) throws IOException {
        switch (1.$SwitchMap$com$flowpowered$nbt$TagType[type.ordinal()]) {
            case 1: {
                if (depth != 0) return new EndTag();
                throw new IOException((String)"TAG_End found without a TAG_Compound/TAG_List tag preceding it.");
            }
            case 2: {
                return new ByteTag((String)name, (byte)this.is.readByte());
            }
            case 3: {
                return new ShortTag((String)name, (short)this.is.readShort());
            }
            case 4: {
                return new IntTag((String)name, (int)this.is.readInt());
            }
            case 5: {
                return new LongTag((String)name, (long)this.is.readLong());
            }
            case 6: {
                return new FloatTag((String)name, (float)this.is.readFloat());
            }
            case 7: {
                return new DoubleTag((String)name, (double)this.is.readDouble());
            }
            case 8: {
                int length = this.is.readInt();
                byte[] bytes = new byte[length];
                this.is.readFully((byte[])bytes);
                return new ByteArrayTag((String)name, (byte[])bytes);
            }
            case 9: {
                short length = this.is.readShort();
                byte[] bytes = new byte[length];
                this.is.readFully((byte[])bytes);
                return new StringTag((String)name, (String)new String((byte[])bytes, (String)NBTConstants.CHARSET.name()));
            }
            case 10: {
                TagType childType = TagType.getById((int)this.is.readByte());
                int length = this.is.readInt();
                Class<Tag<?>> clazz = childType.getTagClass();
                ArrayList<Tag> tagList = new ArrayList<Tag>((int)length);
                int i = 0;
                while (i < length) {
                    Tag tag = this.readTagPayload((TagType)childType, (String)"", (int)(depth + 1));
                    if (tag instanceof EndTag) {
                        throw new IOException((String)"TAG_End not permitted in a list.");
                    }
                    if (!clazz.isInstance((Object)tag)) {
                        throw new IOException((String)"Mixed tag types within a list.");
                    }
                    tagList.add(tag);
                    ++i;
                }
                return new ListTag<Tag<?>>((String)name, clazz, tagList);
            }
            case 11: {
                CompoundMap compoundTagList = new CompoundMap();
                do {
                    Tag tag;
                    if ((tag = this.readTag((int)(depth + 1))) instanceof EndTag) {
                        return new CompoundTag((String)name, (CompoundMap)compoundTagList);
                    }
                    compoundTagList.put(tag);
                } while (true);
            }
            case 12: {
                int length = this.is.readInt();
                int[] ints = new int[length];
                int i = 0;
                while (i < length) {
                    ints[i] = this.is.readInt();
                    ++i;
                }
                return new IntArrayTag((String)name, (int[])ints);
            }
            case 13: {
                int length = this.is.readInt();
                short[] shorts = new short[length];
                int i = 0;
                while (i < length) {
                    shorts[i] = this.is.readShort();
                    ++i;
                }
                return new ShortArrayTag((String)name, (short[])shorts);
            }
        }
        throw new IOException((String)("Invalid tag type: " + (Object)((Object)type) + "."));
    }

    @Override
    public void close() throws IOException {
        this.is.close();
    }

    public ByteOrder getByteOrder() {
        return this.is.getEndianness();
    }
}

