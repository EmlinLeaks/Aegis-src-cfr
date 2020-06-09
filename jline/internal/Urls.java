/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.internal;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class Urls {
    public static URL create(String input) {
        if (input == null) {
            return null;
        }
        try {
            return new URL((String)input);
        }
        catch (MalformedURLException e) {
            return Urls.create((File)new File((String)input));
        }
    }

    public static URL create(File file) {
        try {
            if (file == null) return null;
            URL uRL = file.toURI().toURL();
            return uRL;
        }
        catch (MalformedURLException e) {
            throw new IllegalStateException((Throwable)e);
        }
    }
}

