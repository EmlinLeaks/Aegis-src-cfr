/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteSource;
import com.google.common.io.FileBackedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Beta
@GwtIncompatible
public final class FileBackedOutputStream
extends OutputStream {
    private final int fileThreshold;
    private final boolean resetOnFinalize;
    private final ByteSource source;
    private OutputStream out;
    private MemoryOutput memory;
    private File file;

    @VisibleForTesting
    synchronized File getFile() {
        return this.file;
    }

    public FileBackedOutputStream(int fileThreshold) {
        this((int)fileThreshold, (boolean)false);
    }

    public FileBackedOutputStream(int fileThreshold, boolean resetOnFinalize) {
        this.fileThreshold = fileThreshold;
        this.resetOnFinalize = resetOnFinalize;
        this.memory = new MemoryOutput(null);
        this.out = this.memory;
        if (resetOnFinalize) {
            this.source = new ByteSource((FileBackedOutputStream)this){
                final /* synthetic */ FileBackedOutputStream this$0;
                {
                    this.this$0 = fileBackedOutputStream;
                }

                public InputStream openStream() throws IOException {
                    return FileBackedOutputStream.access$100((FileBackedOutputStream)this.this$0);
                }

                protected void finalize() {
                    try {
                        this.this$0.reset();
                        return;
                    }
                    catch (java.lang.Throwable t) {
                        t.printStackTrace((java.io.PrintStream)java.lang.System.err);
                    }
                }
            };
            return;
        }
        this.source = new ByteSource((FileBackedOutputStream)this){
            final /* synthetic */ FileBackedOutputStream this$0;
            {
                this.this$0 = fileBackedOutputStream;
            }

            public InputStream openStream() throws IOException {
                return FileBackedOutputStream.access$100((FileBackedOutputStream)this.this$0);
            }
        };
    }

    public ByteSource asByteSource() {
        return this.source;
    }

    private synchronized InputStream openInputStream() throws IOException {
        if (this.file == null) return new ByteArrayInputStream((byte[])this.memory.getBuffer(), (int)0, (int)this.memory.getCount());
        return new FileInputStream((File)this.file);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void reset() throws IOException {
        try {
            this.close();
            return;
        }
        finally {
            if (this.memory == null) {
                this.memory = new MemoryOutput(null);
            } else {
                this.memory.reset();
            }
            this.out = this.memory;
            if (this.file != null) {
                File deleteMe = this.file;
                this.file = null;
                if (!deleteMe.delete()) {
                    throw new IOException((String)("Could not delete: " + deleteMe));
                }
            }
        }
    }

    @Override
    public synchronized void write(int b) throws IOException {
        this.update((int)1);
        this.out.write((int)b);
    }

    @Override
    public synchronized void write(byte[] b) throws IOException {
        this.write((byte[])b, (int)0, (int)b.length);
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        this.update((int)len);
        this.out.write((byte[])b, (int)off, (int)len);
    }

    @Override
    public synchronized void close() throws IOException {
        this.out.close();
    }

    @Override
    public synchronized void flush() throws IOException {
        this.out.flush();
    }

    private void update(int len) throws IOException {
        if (this.file != null) return;
        if (this.memory.getCount() + len <= this.fileThreshold) return;
        File temp = File.createTempFile((String)"FileBackedOutputStream", null);
        if (this.resetOnFinalize) {
            temp.deleteOnExit();
        }
        FileOutputStream transfer = new FileOutputStream((File)temp);
        transfer.write((byte[])this.memory.getBuffer(), (int)0, (int)this.memory.getCount());
        transfer.flush();
        this.out = transfer;
        this.file = temp;
        this.memory = null;
    }

    static /* synthetic */ InputStream access$100(FileBackedOutputStream x0) throws IOException {
        return x0.openInputStream();
    }
}

