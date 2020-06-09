/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteOrder;

public class NBTTester {
    public static void main(String[] args) {
        ByteOrder order;
        NBTInputStream input;
        if (args.length < 1) {
            System.err.println((String)"No files provided! Usage: <nbt file> [compressed] [byteorder]");
            System.exit((int)1);
        }
        File argFile = new File((String)args[0]);
        boolean compressed = args.length >= 2 ? Boolean.valueOf((String)args[1]).booleanValue() : true;
        ByteOrder byteOrder = order = args.length >= 3 ? NBTTester.getByteOrder((String)args[2]) : ByteOrder.BIG_ENDIAN;
        if (!argFile.isFile()) {
            System.err.println((String)("File " + argFile + " does not exist!"));
            System.exit((int)1);
        }
        try {
            input = new NBTInputStream((InputStream)new FileInputStream((File)argFile), (boolean)compressed, (ByteOrder)order);
        }
        catch (IOException e) {
            System.err.println((String)("Error opening NBT file: " + e));
            e.printStackTrace();
            System.exit((int)1);
            return;
        }
        try {
            Tag tag = input.readTag();
            System.out.println((String)("NBT data from file: " + argFile.getCanonicalPath()));
            System.out.println((Object)tag);
            return;
        }
        catch (IOException e) {
            System.err.println((String)("Error reading tag from file: " + e));
            e.printStackTrace();
            System.exit((int)1);
        }
    }

    private static ByteOrder getByteOrder(String name) {
        if (name.equalsIgnoreCase((String)"big_endian")) return ByteOrder.BIG_ENDIAN;
        if (name.equalsIgnoreCase((String)"bigendian")) return ByteOrder.BIG_ENDIAN;
        if (name.equalsIgnoreCase((String)"be")) {
            return ByteOrder.BIG_ENDIAN;
        }
        if (name.equalsIgnoreCase((String)"little_endian")) return ByteOrder.LITTLE_ENDIAN;
        if (name.equalsIgnoreCase((String)"littleendian")) return ByteOrder.LITTLE_ENDIAN;
        if (!name.equalsIgnoreCase((String)"le")) throw new IllegalArgumentException((String)("Unknown ByteOrder: " + name));
        return ByteOrder.LITTLE_ENDIAN;
    }
}

