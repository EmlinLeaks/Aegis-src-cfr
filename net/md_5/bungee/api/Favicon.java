/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package net.md_5.bungee.api;

import com.google.common.io.BaseEncoding;
import com.google.gson.TypeAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import lombok.NonNull;
import net.md_5.bungee.api.Favicon;

public class Favicon {
    private static final TypeAdapter<Favicon> FAVICON_TYPE_ADAPTER = new TypeAdapter<Favicon>(){

        public void write(com.google.gson.stream.JsonWriter out, Favicon value) throws IOException {
            com.google.gson.internal.bind.TypeAdapters.STRING.write((com.google.gson.stream.JsonWriter)out, (String)(value == null ? null : value.getEncoded()));
        }

        public Favicon read(com.google.gson.stream.JsonReader in) throws IOException {
            String enc = com.google.gson.internal.bind.TypeAdapters.STRING.read((com.google.gson.stream.JsonReader)in);
            if (enc == null) {
                return null;
            }
            Favicon favicon = Favicon.create((String)enc);
            return favicon;
        }
    };
    @NonNull
    private final String encoded;

    public static TypeAdapter<Favicon> getFaviconTypeAdapter() {
        return FAVICON_TYPE_ADAPTER;
    }

    public static Favicon create(BufferedImage image) {
        byte[] imageBytes;
        if (image.getWidth() != 64) throw new IllegalArgumentException((String)"Server icon must be exactly 64x64 pixels");
        if (image.getHeight() != 64) {
            throw new IllegalArgumentException((String)"Server icon must be exactly 64x64 pixels");
        }
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write((RenderedImage)image, (String)"PNG", (OutputStream)stream);
            imageBytes = stream.toByteArray();
        }
        catch (IOException e) {
            throw new AssertionError((Object)e);
        }
        String encoded = "data:image/png;base64," + BaseEncoding.base64().encode((byte[])imageBytes);
        if (encoded.length() <= 32767) return new Favicon((String)encoded);
        throw new IllegalArgumentException((String)"Favicon file too large for server to process");
    }

    @Deprecated
    public static Favicon create(String encodedString) {
        return new Favicon((String)encodedString);
    }

    private Favicon(@NonNull String encoded) {
        if (encoded == null) {
            throw new NullPointerException((String)"encoded is marked non-null but is null");
        }
        this.encoded = encoded;
    }

    @NonNull
    public String getEncoded() {
        return this.encoded;
    }
}

