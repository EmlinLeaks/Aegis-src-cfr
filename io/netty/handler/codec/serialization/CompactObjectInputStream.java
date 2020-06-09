/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.serialization;

import io.netty.handler.codec.serialization.ClassResolver;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;

class CompactObjectInputStream
extends ObjectInputStream {
    private final ClassResolver classResolver;

    CompactObjectInputStream(InputStream in, ClassResolver classResolver) throws IOException {
        super((InputStream)in);
        this.classResolver = classResolver;
    }

    @Override
    protected void readStreamHeader() throws IOException {
        int version = this.readByte() & 255;
        if (version == 5) return;
        throw new StreamCorruptedException((String)("Unsupported version: " + version));
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        int type = this.read();
        if (type < 0) {
            throw new EOFException();
        }
        switch (type) {
            case 0: {
                return super.readClassDescriptor();
            }
            case 1: {
                String className = this.readUTF();
                Class<?> clazz = this.classResolver.resolve((String)className);
                return ObjectStreamClass.lookupAny(clazz);
            }
        }
        throw new StreamCorruptedException((String)("Unexpected class descriptor type: " + type));
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        try {
            return this.classResolver.resolve((String)desc.getName());
        }
        catch (ClassNotFoundException ignored) {
            return super.resolveClass((ObjectStreamClass)desc);
        }
    }
}

