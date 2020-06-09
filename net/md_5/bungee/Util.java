/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee;

import com.google.common.base.Joiner;
import com.google.common.primitives.UnsignedLongs;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public class Util {
    public static final int DEFAULT_PORT = 25565;

    public static InetSocketAddress getAddr(String hostline) {
        URI uri;
        int n;
        try {
            uri = new URI((String)("tcp://" + hostline));
        }
        catch (URISyntaxException ex) {
            throw new IllegalArgumentException((String)("Bad hostline: " + hostline), (Throwable)ex);
        }
        if (uri.getHost() == null) {
            throw new IllegalArgumentException((String)("Invalid host/address: " + hostline));
        }
        if (uri.getPort() == -1) {
            n = 25565;
            return new InetSocketAddress((String)uri.getHost(), (int)n);
        }
        n = uri.getPort();
        return new InetSocketAddress((String)uri.getHost(), (int)n);
    }

    public static String hex(int i) {
        return String.format((String)"0x%02X", (Object[])new Object[]{Integer.valueOf((int)i)});
    }

    public static String exception(Throwable t) {
        String string;
        StackTraceElement[] trace = t.getStackTrace();
        if (trace.length > 0) {
            string = " @ " + t.getStackTrace()[0].getClassName() + ":" + t.getStackTrace()[0].getLineNumber();
            return t.getClass().getSimpleName() + " : " + t.getMessage() + string;
        }
        string = "";
        return t.getClass().getSimpleName() + " : " + t.getMessage() + string;
    }

    public static String csv(Iterable<?> objects) {
        return Util.format(objects, (String)", ");
    }

    public static String format(Iterable<?> objects, String separators) {
        return Joiner.on((String)separators).join(objects);
    }

    public static UUID getUUID(String uuid) {
        return new UUID((long)UnsignedLongs.parseUnsignedLong((String)uuid.substring((int)0, (int)16), (int)16), (long)UnsignedLongs.parseUnsignedLong((String)uuid.substring((int)16), (int)16));
    }
}

