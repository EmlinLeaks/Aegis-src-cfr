/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public final class ResourcesUtil {
    public static File getFile(Class resourceClass, String fileName) {
        try {
            return new File((String)URLDecoder.decode((String)resourceClass.getResource((String)fileName).getFile(), (String)"UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            return new File((String)resourceClass.getResource((String)fileName).getFile());
        }
    }

    private ResourcesUtil() {
    }
}

