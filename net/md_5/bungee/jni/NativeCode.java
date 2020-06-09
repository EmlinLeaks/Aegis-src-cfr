/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.jni;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import net.md_5.bungee.jni.cipher.BungeeCipher;

public final class NativeCode<T> {
    private final String name;
    private final Class<? extends T> javaImpl;
    private final Class<? extends T> nativeImpl;
    private boolean loaded;

    public NativeCode(String name, Class<? extends T> javaImpl, Class<? extends T> nativeImpl) {
        this.name = name;
        this.javaImpl = javaImpl;
        this.nativeImpl = nativeImpl;
    }

    public T newInstance() {
        try {
            T t;
            if (this.loaded) {
                t = this.nativeImpl.newInstance();
                return (T)((T)t);
            }
            t = this.javaImpl.newInstance();
            return (T)t;
        }
        catch (IllegalAccessException | InstantiationException ex) {
            throw new RuntimeException((String)"Error getting instance", (Throwable)ex);
        }
    }

    public boolean load() {
        if (this.loaded) return this.loaded;
        if (!NativeCode.isSupported()) return this.loaded;
        String fullName = "bungeecord-" + this.name;
        try {
            System.loadLibrary((String)fullName);
            this.loaded = true;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (this.loaded) return this.loaded;
        try {
            InputStream soFile = BungeeCipher.class.getClassLoader().getResourceAsStream((String)(this.name + ".so"));
            Throwable throwable = null;
            try {
                File temp = File.createTempFile((String)fullName, (String)".so");
                temp.deleteOnExit();
                FileOutputStream outputStream = new FileOutputStream((File)temp);
                Throwable throwable2 = null;
                try {
                    ByteStreams.copy((InputStream)soFile, (OutputStream)outputStream);
                }
                catch (Throwable throwable3) {
                    throwable2 = throwable3;
                    throw throwable3;
                }
                finally {
                    NativeCode.$closeResource((Throwable)throwable2, (AutoCloseable)outputStream);
                }
                System.load((String)temp.getPath());
                this.loaded = true;
                return this.loaded;
            }
            catch (Throwable temp) {
                throwable = temp;
                throw temp;
            }
            finally {
                if (soFile != null) {
                    NativeCode.$closeResource((Throwable)throwable, (AutoCloseable)soFile);
                }
            }
        }
        catch (IOException soFile) {
            return this.loaded;
        }
        catch (UnsatisfiedLinkError ex) {
            System.out.println((String)("Could not load native library: " + ex.getMessage()));
        }
        return this.loaded;
    }

    public static boolean isSupported() {
        if (!"Linux".equals((Object)System.getProperty((String)"os.name"))) return false;
        if (!"amd64".equals((Object)System.getProperty((String)"os.arch"))) return false;
        return true;
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 == null) {
            x1.close();
            return;
        }
        try {
            x1.close();
            return;
        }
        catch (Throwable throwable) {
            x0.addSuppressed((Throwable)throwable);
            return;
        }
    }
}

