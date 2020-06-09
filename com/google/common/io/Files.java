/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.TreeTraverser;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.Closer;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Beta
@GwtIncompatible
public final class Files {
    private static final int TEMP_DIR_ATTEMPTS = 10000;
    private static final TreeTraverser<File> FILE_TREE_TRAVERSER = new TreeTraverser<File>(){

        public Iterable<File> children(File file) {
            if (!file.isDirectory()) return java.util.Collections.emptyList();
            File[] files = file.listFiles();
            if (files == null) return java.util.Collections.emptyList();
            return java.util.Collections.unmodifiableList(java.util.Arrays.asList(files));
        }

        public String toString() {
            return "Files.fileTreeTraverser()";
        }
    };

    private Files() {
    }

    public static BufferedReader newReader(File file, Charset charset) throws FileNotFoundException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(charset);
        return new BufferedReader((Reader)new InputStreamReader((InputStream)new FileInputStream((File)file), (Charset)charset));
    }

    public static BufferedWriter newWriter(File file, Charset charset) throws FileNotFoundException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(charset);
        return new BufferedWriter((Writer)new OutputStreamWriter((OutputStream)new FileOutputStream((File)file), (Charset)charset));
    }

    public static ByteSource asByteSource(File file) {
        return new FileByteSource((File)file, null);
    }

    static byte[] readFile(InputStream in, long expectedSize) throws IOException {
        byte[] arrby;
        if (expectedSize > Integer.MAX_VALUE) {
            throw new OutOfMemoryError((String)("file is too large to fit in a byte array: " + expectedSize + " bytes"));
        }
        if (expectedSize == 0L) {
            arrby = ByteStreams.toByteArray((InputStream)in);
            return arrby;
        }
        arrby = ByteStreams.toByteArray((InputStream)in, (int)((int)expectedSize));
        return arrby;
    }

    public static ByteSink asByteSink(File file, FileWriteMode ... modes) {
        return new FileByteSink((File)file, (FileWriteMode[])modes, null);
    }

    public static CharSource asCharSource(File file, Charset charset) {
        return Files.asByteSource((File)file).asCharSource((Charset)charset);
    }

    public static CharSink asCharSink(File file, Charset charset, FileWriteMode ... modes) {
        return Files.asByteSink((File)file, (FileWriteMode[])modes).asCharSink((Charset)charset);
    }

    private static FileWriteMode[] modes(boolean append) {
        FileWriteMode[] arrfileWriteMode;
        if (append) {
            FileWriteMode[] arrfileWriteMode2 = new FileWriteMode[1];
            arrfileWriteMode = arrfileWriteMode2;
            arrfileWriteMode2[0] = FileWriteMode.APPEND;
            return arrfileWriteMode;
        }
        arrfileWriteMode = new FileWriteMode[]{};
        return arrfileWriteMode;
    }

    public static byte[] toByteArray(File file) throws IOException {
        return Files.asByteSource((File)file).read();
    }

    public static String toString(File file, Charset charset) throws IOException {
        return Files.asCharSource((File)file, (Charset)charset).read();
    }

    public static void write(byte[] from, File to) throws IOException {
        Files.asByteSink((File)to, (FileWriteMode[])new FileWriteMode[0]).write((byte[])from);
    }

    public static void copy(File from, OutputStream to) throws IOException {
        Files.asByteSource((File)from).copyTo((OutputStream)to);
    }

    public static void copy(File from, File to) throws IOException {
        Preconditions.checkArgument((boolean)(!from.equals((Object)to)), (String)"Source %s and destination %s must be different", (Object)from, (Object)to);
        Files.asByteSource((File)from).copyTo((ByteSink)Files.asByteSink((File)to, (FileWriteMode[])new FileWriteMode[0]));
    }

    public static void write(CharSequence from, File to, Charset charset) throws IOException {
        Files.asCharSink((File)to, (Charset)charset, (FileWriteMode[])new FileWriteMode[0]).write((CharSequence)from);
    }

    public static void append(CharSequence from, File to, Charset charset) throws IOException {
        Files.write((CharSequence)from, (File)to, (Charset)charset, (boolean)true);
    }

    private static void write(CharSequence from, File to, Charset charset, boolean append) throws IOException {
        Files.asCharSink((File)to, (Charset)charset, (FileWriteMode[])Files.modes((boolean)append)).write((CharSequence)from);
    }

    public static void copy(File from, Charset charset, Appendable to) throws IOException {
        Files.asCharSource((File)from, (Charset)charset).copyTo((Appendable)to);
    }

    public static boolean equal(File file1, File file2) throws IOException {
        Preconditions.checkNotNull(file1);
        Preconditions.checkNotNull(file2);
        if (file1 == file2) return true;
        if (file1.equals((Object)file2)) {
            return true;
        }
        long len1 = file1.length();
        long len2 = file2.length();
        if (len1 == 0L) return Files.asByteSource((File)file1).contentEquals((ByteSource)Files.asByteSource((File)file2));
        if (len2 == 0L) return Files.asByteSource((File)file1).contentEquals((ByteSource)Files.asByteSource((File)file2));
        if (len1 == len2) return Files.asByteSource((File)file1).contentEquals((ByteSource)Files.asByteSource((File)file2));
        return false;
    }

    public static File createTempDir() {
        File baseDir = new File((String)System.getProperty((String)"java.io.tmpdir"));
        String baseName = System.currentTimeMillis() + "-";
        int counter = 0;
        while (counter < 10000) {
            File tempDir = new File((File)baseDir, (String)(baseName + counter));
            if (tempDir.mkdir()) {
                return tempDir;
            }
            ++counter;
        }
        throw new IllegalStateException((String)("Failed to create directory within 10000 attempts (tried " + baseName + "0 to " + baseName + 9999 + ')'));
    }

    public static void touch(File file) throws IOException {
        Preconditions.checkNotNull(file);
        if (file.createNewFile()) return;
        if (file.setLastModified((long)System.currentTimeMillis())) return;
        throw new IOException((String)("Unable to update modification time of " + file));
    }

    public static void createParentDirs(File file) throws IOException {
        Preconditions.checkNotNull(file);
        File parent = file.getCanonicalFile().getParentFile();
        if (parent == null) {
            return;
        }
        parent.mkdirs();
        if (parent.isDirectory()) return;
        throw new IOException((String)("Unable to create parent directories of " + file));
    }

    public static void move(File from, File to) throws IOException {
        Preconditions.checkNotNull(from);
        Preconditions.checkNotNull(to);
        Preconditions.checkArgument((boolean)(!from.equals((Object)to)), (String)"Source %s and destination %s must be different", (Object)from, (Object)to);
        if (from.renameTo((File)to)) return;
        Files.copy((File)from, (File)to);
        if (from.delete()) return;
        if (to.delete()) throw new IOException((String)("Unable to delete " + from));
        throw new IOException((String)("Unable to delete " + to));
    }

    public static String readFirstLine(File file, Charset charset) throws IOException {
        return Files.asCharSource((File)file, (Charset)charset).readFirstLine();
    }

    public static List<String> readLines(File file, Charset charset) throws IOException {
        return Files.readLines((File)file, (Charset)charset, new LineProcessor<List<String>>(){
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

    @CanIgnoreReturnValue
    public static <T> T readLines(File file, Charset charset, LineProcessor<T> callback) throws IOException {
        return (T)Files.asCharSource((File)file, (Charset)charset).readLines(callback);
    }

    @CanIgnoreReturnValue
    public static <T> T readBytes(File file, ByteProcessor<T> processor) throws IOException {
        return (T)Files.asByteSource((File)file).read(processor);
    }

    public static HashCode hash(File file, HashFunction hashFunction) throws IOException {
        return Files.asByteSource((File)file).hash((HashFunction)hashFunction);
    }

    public static MappedByteBuffer map(File file) throws IOException {
        Preconditions.checkNotNull(file);
        return Files.map((File)file, (FileChannel.MapMode)FileChannel.MapMode.READ_ONLY);
    }

    public static MappedByteBuffer map(File file, FileChannel.MapMode mode) throws IOException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(mode);
        if (file.exists()) return Files.map((File)file, (FileChannel.MapMode)mode, (long)file.length());
        throw new FileNotFoundException((String)file.toString());
    }

    public static MappedByteBuffer map(File file, FileChannel.MapMode mode, long size) throws FileNotFoundException, IOException {
        Preconditions.checkNotNull(file);
        Preconditions.checkNotNull(mode);
        Closer closer = Closer.create();
        try {
            RandomAccessFile raf = closer.register(new RandomAccessFile((File)file, (String)(mode == FileChannel.MapMode.READ_ONLY ? "r" : "rw")));
            MappedByteBuffer mappedByteBuffer = Files.map((RandomAccessFile)raf, (FileChannel.MapMode)mode, (long)size);
            return mappedByteBuffer;
        }
        catch (Throwable e) {
            throw closer.rethrow((Throwable)e);
        }
        finally {
            closer.close();
        }
    }

    private static MappedByteBuffer map(RandomAccessFile raf, FileChannel.MapMode mode, long size) throws IOException {
        Closer closer = Closer.create();
        try {
            FileChannel channel = closer.register(raf.getChannel());
            MappedByteBuffer mappedByteBuffer = channel.map((FileChannel.MapMode)mode, (long)0L, (long)size);
            return mappedByteBuffer;
        }
        catch (Throwable e) {
            throw closer.rethrow((Throwable)e);
        }
        finally {
            closer.close();
        }
    }

    public static String simplifyPath(String pathname) {
        Preconditions.checkNotNull(pathname);
        if (pathname.length() == 0) {
            return ".";
        }
        Iterable<String> components = Splitter.on((char)'/').omitEmptyStrings().split((CharSequence)pathname);
        ArrayList<String> path = new ArrayList<String>();
        for (String component : components) {
            if (component.equals((Object)".")) continue;
            if (component.equals((Object)"..")) {
                if (path.size() > 0 && !((String)path.get((int)(path.size() - 1))).equals((Object)"..")) {
                    path.remove((int)(path.size() - 1));
                    continue;
                }
                path.add("..");
                continue;
            }
            path.add(component);
        }
        String result = Joiner.on((char)'/').join(path);
        if (pathname.charAt((int)0) == '/') {
            result = "/" + result;
        }
        while (result.startsWith((String)"/../")) {
            result = result.substring((int)3);
        }
        if (result.equals((Object)"/..")) {
            return "/";
        }
        if (!"".equals((Object)result)) return result;
        return ".";
    }

    public static String getFileExtension(String fullName) {
        Preconditions.checkNotNull(fullName);
        String fileName = new File((String)fullName).getName();
        int dotIndex = fileName.lastIndexOf((int)46);
        if (dotIndex == -1) {
            return "";
        }
        String string = fileName.substring((int)(dotIndex + 1));
        return string;
    }

    public static String getNameWithoutExtension(String file) {
        String string;
        Preconditions.checkNotNull(file);
        String fileName = new File((String)file).getName();
        int dotIndex = fileName.lastIndexOf((int)46);
        if (dotIndex == -1) {
            string = fileName;
            return string;
        }
        string = fileName.substring((int)0, (int)dotIndex);
        return string;
    }

    public static TreeTraverser<File> fileTreeTraverser() {
        return FILE_TREE_TRAVERSER;
    }

    public static Predicate<File> isDirectory() {
        return FilePredicate.IS_DIRECTORY;
    }

    public static Predicate<File> isFile() {
        return FilePredicate.IS_FILE;
    }
}

