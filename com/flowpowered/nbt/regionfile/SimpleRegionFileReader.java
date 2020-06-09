/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt.regionfile;

import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.InflaterInputStream;

public class SimpleRegionFileReader {
    private static int EXPECTED_VERSION = 1;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List<Tag<?>> readFile(File f) {
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile((File)f, (String)"r");
        }
        catch (FileNotFoundException e) {
            return null;
        }
        try {
            int i;
            int version = raf.readInt();
            if (version != EXPECTED_VERSION) {
                List<Tag<?>> list = null;
                return list;
            }
            int segmentSize = raf.readInt();
            int segmentMask = (1 << segmentSize) - 1;
            int entries = raf.readInt();
            ArrayList<Tag<?>> list = new ArrayList<Tag<?>>((int)entries);
            int[] blockSegmentStart = new int[entries];
            int[] blockActualLength = new int[entries];
            for (i = 0; i < entries; ++i) {
                blockSegmentStart[i] = raf.readInt();
                blockActualLength[i] = raf.readInt();
            }
            for (i = 0; i < entries; ++i) {
                if (blockActualLength[i] == 0) {
                    list.add(null);
                    continue;
                }
                byte[] data = new byte[blockActualLength[i]];
                raf.seek((long)((long)(blockSegmentStart[i] << segmentSize)));
                raf.readFully((byte[])data);
                ByteArrayInputStream in = new ByteArrayInputStream((byte[])data);
                InflaterInputStream iis = new InflaterInputStream((InputStream)in);
                NBTInputStream ns = new NBTInputStream((InputStream)iis, (boolean)false);
                try {
                    Tag t = ns.readTag();
                    list.add(t);
                }
                catch (IOException ioe) {
                    list.add(null);
                }
                try {
                    ns.close();
                    continue;
                }
                catch (IOException ioe) {
                    // empty catch block
                }
            }
            ArrayList<Tag<?>> i2 = list;
            return i2;
        }
        catch (IOException ioe) {
            List<Tag<?>> segmentSize = null;
            return segmentSize;
        }
        finally {
            try {
                raf.close();
            }
            catch (IOException ioe) {}
        }
    }
}

