/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

@Beta
@GwtIncompatible
public final class Resources {
    private Resources() {
    }

    public static ByteSource asByteSource(URL url) {
        return new UrlByteSource((URL)url, null);
    }

    public static CharSource asCharSource(URL url, Charset charset) {
        return Resources.asByteSource((URL)url).asCharSource((Charset)charset);
    }

    public static byte[] toByteArray(URL url) throws IOException {
        return Resources.asByteSource((URL)url).read();
    }

    public static String toString(URL url, Charset charset) throws IOException {
        return Resources.asCharSource((URL)url, (Charset)charset).read();
    }

    @CanIgnoreReturnValue
    public static <T> T readLines(URL url, Charset charset, LineProcessor<T> callback) throws IOException {
        return (T)Resources.asCharSource((URL)url, (Charset)charset).readLines(callback);
    }

    public static List<String> readLines(URL url, Charset charset) throws IOException {
        return Resources.readLines((URL)url, (Charset)charset, new LineProcessor<List<String>>(){
            final List<String> result;
            {
                this.result = com.google.common.collect.Lists.newArrayList();
            }

            public boolean processLine(String line) {
                this.result.add((String)line);
                return true;
            }

            public List<String> getResult() {
                return this.result;
            }
        });
    }

    public static void copy(URL from, OutputStream to) throws IOException {
        Resources.asByteSource((URL)from).copyTo((OutputStream)to);
    }

    @CanIgnoreReturnValue
    public static URL getResource(String resourceName) {
        ClassLoader loader = MoreObjects.firstNonNull(Thread.currentThread().getContextClassLoader(), Resources.class.getClassLoader());
        URL url = loader.getResource((String)resourceName);
        Preconditions.checkArgument((boolean)(url != null), (String)"resource %s not found.", (Object)resourceName);
        return url;
    }

    public static URL getResource(Class<?> contextClass, String resourceName) {
        URL url = contextClass.getResource((String)resourceName);
        Preconditions.checkArgument((boolean)(url != null), (String)"resource %s relative to %s not found.", (Object)resourceName, (Object)contextClass.getName());
        return url;
    }
}

