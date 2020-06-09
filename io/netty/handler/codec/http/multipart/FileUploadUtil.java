/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.http.multipart.FileUpload;

final class FileUploadUtil {
    private FileUploadUtil() {
    }

    static int hashCode(FileUpload upload) {
        return upload.getName().hashCode();
    }

    static boolean equals(FileUpload upload1, FileUpload upload2) {
        return upload1.getName().equalsIgnoreCase((String)upload2.getName());
    }

    static int compareTo(FileUpload upload1, FileUpload upload2) {
        return upload1.getName().compareToIgnoreCase((String)upload2.getName());
    }
}

