/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt.itemmap;

import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.Tag;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StringMapReader {
    public static List<Tag<?>> readFile(File f) {
        ArrayList<Tag<?>> list = new ArrayList<Tag<?>>();
        try {
            FileInputStream fis = new FileInputStream((File)f);
            DataInputStream dis = new DataInputStream((InputStream)fis);
            boolean eof = false;
            while (!eof) {
                int value;
                try {
                    value = dis.readInt();
                }
                catch (EOFException e) {
                    eof = true;
                    continue;
                }
                String key = dis.readUTF();
                list.add(new IntTag((String)key, (int)value));
            }
            return list;
        }
        catch (IOException ioe) {
            return null;
        }
    }
}

