/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.NativeLibraryUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;

public final class NativeLibraryLoader {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NativeLibraryLoader.class);
    private static final String NATIVE_RESOURCE_HOME = "META-INF/native/";
    private static final File WORKDIR;
    private static final boolean DELETE_NATIVE_LIB_AFTER_LOADING;
    private static final boolean TRY_TO_PATCH_SHADED_ID;
    private static final byte[] UNIQUE_ID_BYTES;

    public static void loadFirstAvailable(ClassLoader loader, String ... names) {
        ArrayList<Throwable> suppressed = new ArrayList<Throwable>();
        String[] arrstring = names;
        int n = arrstring.length;
        int n2 = 0;
        do {
            if (n2 >= n) {
                IllegalArgumentException iae = new IllegalArgumentException((String)("Failed to load any of the given libraries: " + Arrays.toString((Object[])names)));
                ThrowableUtil.addSuppressedAndClear((Throwable)iae, suppressed);
                throw iae;
            }
            String name = arrstring[n2];
            try {
                NativeLibraryLoader.load((String)name, (ClassLoader)loader);
                return;
            }
            catch (Throwable t) {
                suppressed.add(t);
                logger.debug((String)"Unable to load the library '{}', trying next name...", (Object)name, (Object)t);
                ++n2;
                continue;
            }
            break;
        } while (true);
    }

    private static String calculatePackagePrefix() {
        String expected;
        String maybeShaded = NativeLibraryLoader.class.getName();
        if (maybeShaded.endsWith((String)(expected = "io!netty!util!internal!NativeLibraryLoader".replace((char)'!', (char)'.')))) return maybeShaded.substring((int)0, (int)(maybeShaded.length() - expected.length()));
        throw new UnsatisfiedLinkError((String)String.format((String)"Could not find prefix added to %s to get %s. When shading, only adding a package prefix is supported", (Object[])new Object[]{expected, maybeShaded}));
    }

    /*
     * Loose catch block
     * Enabled unnecessary exception pruning
     */
    public static void load(String originalName, ClassLoader loader) {
        String packagePrefix = NativeLibraryLoader.calculatePackagePrefix().replace((char)'.', (char)'_');
        String name = packagePrefix + originalName;
        ArrayList<Throwable> suppressed = new ArrayList<Throwable>();
        try {
            NativeLibraryLoader.loadLibrary((ClassLoader)loader, (String)name, (boolean)false);
            return;
        }
        catch (Throwable ex) {
            suppressed.add(ex);
            logger.debug((String)"{} cannot be loaded from java.library.path, now trying export to -Dio.netty.native.workdir: {}", (Object[])new Object[]{name, WORKDIR, ex});
            String libname = System.mapLibraryName((String)name);
            String path = NATIVE_RESOURCE_HOME + libname;
            InputStream in = null;
            FileOutputStream out = null;
            File tmpFile = null;
            URL url = loader == null ? ClassLoader.getSystemResource((String)path) : loader.getResource((String)path);
            try {
                if (url == null) {
                    if (!PlatformDependent.isOsx()) {
                        FileNotFoundException fnf = new FileNotFoundException((String)path);
                        ThrowableUtil.addSuppressedAndClear((Throwable)fnf, suppressed);
                        throw fnf;
                    }
                    String fileName = path.endsWith((String)".jnilib") ? "META-INF/native/lib" + name + ".dynlib" : "META-INF/native/lib" + name + ".jnilib";
                    url = loader == null ? ClassLoader.getSystemResource((String)fileName) : loader.getResource((String)fileName);
                    if (url == null) {
                        FileNotFoundException fnf = new FileNotFoundException((String)fileName);
                        ThrowableUtil.addSuppressedAndClear((Throwable)fnf, suppressed);
                        throw fnf;
                    }
                }
                int index = libname.lastIndexOf((int)46);
                String prefix = libname.substring((int)0, (int)index);
                String suffix = libname.substring((int)index);
                tmpFile = File.createTempFile((String)prefix, (String)suffix, (File)WORKDIR);
                in = url.openStream();
                out = new FileOutputStream((File)tmpFile);
                if (NativeLibraryLoader.shouldShadedLibraryIdBePatched((String)packagePrefix)) {
                    NativeLibraryLoader.patchShadedLibraryId((InputStream)in, (OutputStream)out, (String)originalName, (String)name);
                } else {
                    int length;
                    byte[] buffer = new byte[8192];
                    while ((length = in.read((byte[])buffer)) > 0) {
                        ((OutputStream)out).write((byte[])buffer, (int)0, (int)length);
                    }
                }
                out.flush();
                NativeLibraryLoader.closeQuietly((Closeable)out);
                out = null;
                NativeLibraryLoader.loadLibrary((ClassLoader)loader, (String)tmpFile.getPath(), (boolean)true);
            }
            catch (UnsatisfiedLinkError e) {
                try {
                    try {
                        if (tmpFile != null && tmpFile.isFile() && tmpFile.canRead() && !NoexecVolumeDetector.canExecuteExecutable((File)((File)tmpFile))) {
                            logger.info((String)"{} exists but cannot be executed even when execute permissions set; check volume for \"noexec\" flag; use -D{}=[path] to set native working directory separately.", (Object)tmpFile.getPath(), (Object)"io.netty.native.workdir");
                        }
                    }
                    catch (Throwable t) {
                        suppressed.add(t);
                        logger.debug((String)"Error checking if {} is on a file store mounted with noexec", (Object)tmpFile, (Object)t);
                    }
                    ThrowableUtil.addSuppressedAndClear((Throwable)e, suppressed);
                    throw e;
                    catch (Exception e2) {
                        UnsatisfiedLinkError ule = new UnsatisfiedLinkError((String)("could not load a native library: " + name));
                        ule.initCause((Throwable)e2);
                        ThrowableUtil.addSuppressedAndClear((Throwable)ule, suppressed);
                        throw ule;
                    }
                }
                catch (Throwable throwable) {
                    NativeLibraryLoader.closeQuietly(in);
                    NativeLibraryLoader.closeQuietly(out);
                    if (tmpFile == null) throw throwable;
                    if (DELETE_NATIVE_LIB_AFTER_LOADING) {
                        if (tmpFile.delete()) throw throwable;
                    }
                    tmpFile.deleteOnExit();
                    throw throwable;
                }
            }
            NativeLibraryLoader.closeQuietly((Closeable)in);
            NativeLibraryLoader.closeQuietly((Closeable)out);
            if (tmpFile == null) return;
            if (DELETE_NATIVE_LIB_AFTER_LOADING) {
                if (tmpFile.delete()) return;
            }
            tmpFile.deleteOnExit();
            return;
        }
    }

    static boolean patchShadedLibraryId(InputStream in, OutputStream out, String originalName, String name) throws IOException {
        boolean patched;
        int length;
        byte[] buffer = new byte[8192];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((int)in.available());
        while ((length = in.read((byte[])buffer)) > 0) {
            byteArrayOutputStream.write((byte[])buffer, (int)0, (int)length);
        }
        byteArrayOutputStream.flush();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        if (!NativeLibraryLoader.patchShadedLibraryId((byte[])bytes, (String)originalName, (String)name)) {
            String os = PlatformDependent.normalizedOs();
            String arch = PlatformDependent.normalizedArch();
            String osArch = "_" + os + "_" + arch;
            patched = originalName.endsWith((String)osArch) ? NativeLibraryLoader.patchShadedLibraryId((byte[])bytes, (String)originalName.substring((int)0, (int)(originalName.length() - osArch.length())), (String)name) : false;
        } else {
            patched = true;
        }
        out.write((byte[])bytes, (int)0, (int)bytes.length);
        return patched;
    }

    private static boolean shouldShadedLibraryIdBePatched(String packagePrefix) {
        if (!TRY_TO_PATCH_SHADED_ID) return false;
        if (!PlatformDependent.isOsx()) return false;
        if (packagePrefix.isEmpty()) return false;
        return true;
    }

    private static boolean patchShadedLibraryId(byte[] bytes, String originalName, String name) {
        int i;
        byte[] nameBytes = originalName.getBytes((Charset)CharsetUtil.UTF_8);
        int idIdx = -1;
        block0 : for (i = 0; i < bytes.length && bytes.length - i >= nameBytes.length; ++i) {
            int idx = i;
            int j = 0;
            while (j < nameBytes.length && bytes[idx++] == nameBytes[j++]) {
                if (j != nameBytes.length) continue;
                idIdx = i;
                break block0;
            }
        }
        if (idIdx == -1) {
            logger.debug((String)"Was not able to find the ID of the shaded native library {}, can't adjust it.", (Object)name);
            return false;
        }
        i = 0;
        do {
            if (i >= nameBytes.length) {
                if (!logger.isDebugEnabled()) return true;
                logger.debug((String)"Found the ID of the shaded native library {}. Replacing ID part {} with {}", (Object[])new Object[]{name, originalName, new String((byte[])bytes, (int)idIdx, (int)nameBytes.length, (Charset)CharsetUtil.UTF_8)});
                return true;
            }
            bytes[idIdx + i] = UNIQUE_ID_BYTES[PlatformDependent.threadLocalRandom().nextInt((int)UNIQUE_ID_BYTES.length)];
            ++i;
        } while (true);
    }

    /*
     * Loose catch block
     * Enabled unnecessary exception pruning
     */
    private static void loadLibrary(ClassLoader loader, String name, boolean absolute) {
        Throwable suppressed = null;
        try {
            Class<?> newHelper = NativeLibraryLoader.tryToLoadClass((ClassLoader)loader, NativeLibraryUtil.class);
            NativeLibraryLoader.loadLibraryByHelper(newHelper, (String)name, (boolean)absolute);
            logger.debug((String)"Successfully loaded the library {}", (Object)name);
            return;
        }
        catch (UnsatisfiedLinkError e) {
            try {
                block5 : {
                    suppressed = e;
                    logger.debug((String)"Unable to load the library '{}', trying other loading mechanism.", (Object)name, (Object)e);
                    break block5;
                    catch (Exception e2) {
                        suppressed = e2;
                        logger.debug((String)"Unable to load the library '{}', trying other loading mechanism.", (Object)name, (Object)e2);
                    }
                }
                NativeLibraryUtil.loadLibrary((String)name, (boolean)absolute);
                logger.debug((String)"Successfully loaded the library {}", (Object)name);
                return;
            }
            catch (UnsatisfiedLinkError ule) {
                if (suppressed == null) throw ule;
                ThrowableUtil.addSuppressed((Throwable)ule, (Throwable)suppressed);
                throw ule;
            }
        }
    }

    private static void loadLibraryByHelper(Class<?> helper, String name, boolean absolute) throws UnsatisfiedLinkError {
        Object ret = AccessController.doPrivileged(new PrivilegedAction<Object>(helper, (String)name, (boolean)absolute){
            final /* synthetic */ Class val$helper;
            final /* synthetic */ String val$name;
            final /* synthetic */ boolean val$absolute;
            {
                this.val$helper = class_;
                this.val$name = string;
                this.val$absolute = bl;
            }

            public Object run() {
                try {
                    java.lang.reflect.Method method = this.val$helper.getMethod((String)"loadLibrary", String.class, Boolean.TYPE);
                    method.setAccessible((boolean)true);
                    return method.invoke(null, (Object[])new Object[]{this.val$name, Boolean.valueOf((boolean)this.val$absolute)});
                }
                catch (Exception e) {
                    return e;
                }
            }
        });
        if (!(ret instanceof Throwable)) return;
        Throwable t = (Throwable)ret;
        assert (!(t instanceof UnsatisfiedLinkError)) : t + " should be a wrapper throwable";
        Throwable cause = t.getCause();
        if (cause instanceof UnsatisfiedLinkError) {
            throw (UnsatisfiedLinkError)cause;
        }
        UnsatisfiedLinkError ule = new UnsatisfiedLinkError((String)t.getMessage());
        ule.initCause((Throwable)t);
        throw ule;
    }

    private static Class<?> tryToLoadClass(ClassLoader loader, Class<?> helper) throws ClassNotFoundException {
        try {
            return Class.forName((String)helper.getName(), (boolean)false, (ClassLoader)loader);
        }
        catch (ClassNotFoundException e1) {
            if (loader == null) {
                throw e1;
            }
            try {
                byte[] classBinary = NativeLibraryLoader.classToByteArray(helper);
                return (Class)AccessController.doPrivileged(new PrivilegedAction<Class<?>>((ClassLoader)loader, helper, (byte[])classBinary){
                    final /* synthetic */ ClassLoader val$loader;
                    final /* synthetic */ Class val$helper;
                    final /* synthetic */ byte[] val$classBinary;
                    {
                        this.val$loader = classLoader;
                        this.val$helper = class_;
                        this.val$classBinary = arrby;
                    }

                    public Class<?> run() {
                        try {
                            java.lang.reflect.Method defineClass = ClassLoader.class.getDeclaredMethod((String)"defineClass", String.class, byte[].class, java.lang.Integer.TYPE, java.lang.Integer.TYPE);
                            defineClass.setAccessible((boolean)true);
                            return (Class)defineClass.invoke((Object)this.val$loader, (Object[])new Object[]{this.val$helper.getName(), this.val$classBinary, java.lang.Integer.valueOf((int)0), java.lang.Integer.valueOf((int)this.val$classBinary.length)});
                        }
                        catch (Exception e) {
                            throw new java.lang.IllegalStateException((String)"Define class failed!", (Throwable)e);
                        }
                    }
                });
            }
            catch (ClassNotFoundException e2) {
                ThrowableUtil.addSuppressed((Throwable)e2, (Throwable)e1);
                throw e2;
            }
            catch (RuntimeException e2) {
                ThrowableUtil.addSuppressed((Throwable)e2, (Throwable)e1);
                throw e2;
            }
            catch (Error e2) {
                ThrowableUtil.addSuppressed((Throwable)e2, (Throwable)e1);
                throw e2;
            }
        }
    }

    private static byte[] classToByteArray(Class<?> clazz) throws ClassNotFoundException {
        URL classUrl;
        String fileName = clazz.getName();
        int lastDot = fileName.lastIndexOf((int)46);
        if (lastDot > 0) {
            fileName = fileName.substring((int)(lastDot + 1));
        }
        if ((classUrl = clazz.getResource((String)(fileName + ".class"))) == null) {
            throw new ClassNotFoundException((String)clazz.getName());
        }
        byte[] buf = new byte[1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream((int)4096);
        InputStream in = null;
        try {
            int r22;
            in = classUrl.openStream();
            while ((r22 = in.read((byte[])buf)) != -1) {
                out.write((byte[])buf, (int)0, (int)r22);
            }
            byte[] r22 = out.toByteArray();
            return r22;
        }
        catch (IOException ex) {
            throw new ClassNotFoundException((String)clazz.getName(), (Throwable)ex);
        }
        finally {
            NativeLibraryLoader.closeQuietly((Closeable)in);
            NativeLibraryLoader.closeQuietly((Closeable)out);
        }
    }

    private static void closeQuietly(Closeable c) {
        if (c == null) return;
        try {
            c.close();
            return;
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private NativeLibraryLoader() {
    }

    static {
        UNIQUE_ID_BYTES = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes((Charset)CharsetUtil.US_ASCII);
        String workdir = SystemPropertyUtil.get((String)"io.netty.native.workdir");
        if (workdir != null) {
            File f = new File((String)workdir);
            f.mkdirs();
            try {
                f = f.getAbsoluteFile();
            }
            catch (Exception exception) {
                // empty catch block
            }
            WORKDIR = f;
            logger.debug((String)("-Dio.netty.native.workdir: " + WORKDIR));
        } else {
            WORKDIR = PlatformDependent.tmpdir();
            logger.debug((String)("-Dio.netty.native.workdir: " + WORKDIR + " (io.netty.tmpdir)"));
        }
        DELETE_NATIVE_LIB_AFTER_LOADING = SystemPropertyUtil.getBoolean((String)"io.netty.native.deleteLibAfterLoading", (boolean)true);
        logger.debug((String)"-Dio.netty.native.deleteLibAfterLoading: {}", (Object)Boolean.valueOf((boolean)DELETE_NATIVE_LIB_AFTER_LOADING));
        TRY_TO_PATCH_SHADED_ID = SystemPropertyUtil.getBoolean((String)"io.netty.native.tryPatchShadedId", (boolean)true);
        logger.debug((String)"-Dio.netty.native.tryPatchShadedId: {}", (Object)Boolean.valueOf((boolean)TRY_TO_PATCH_SHADED_ID));
    }
}

