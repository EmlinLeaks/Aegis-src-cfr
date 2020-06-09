/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.console.history;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Iterator;
import jline.console.history.History;
import jline.console.history.MemoryHistory;
import jline.console.history.PersistentHistory;
import jline.internal.Log;
import jline.internal.Preconditions;

public class FileHistory
extends MemoryHistory
implements PersistentHistory,
Flushable {
    private final File file;

    public FileHistory(File file) throws IOException {
        this.file = Preconditions.checkNotNull(file);
        this.load((File)file);
    }

    public File getFile() {
        return this.file;
    }

    public void load(File file) throws IOException {
        Preconditions.checkNotNull(file);
        if (!file.exists()) return;
        Log.trace((Object[])new Object[]{"Loading history from: ", file});
        this.load((Reader)new FileReader((File)file));
    }

    public void load(InputStream input) throws IOException {
        Preconditions.checkNotNull(input);
        this.load((Reader)new InputStreamReader((InputStream)input));
    }

    public void load(Reader reader) throws IOException {
        String item;
        Preconditions.checkNotNull(reader);
        BufferedReader input = new BufferedReader((Reader)reader);
        while ((item = input.readLine()) != null) {
            this.internalAdd((CharSequence)item);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void flush() throws IOException {
        Log.trace((Object[])new Object[]{"Flushing history"});
        if (!this.file.exists()) {
            File dir = this.file.getParentFile();
            if (!dir.exists() && !dir.mkdirs()) {
                Log.warn((Object[])new Object[]{"Failed to create directory: ", dir});
            }
            if (!this.file.createNewFile()) {
                Log.warn((Object[])new Object[]{"Failed to create file: ", this.file});
            }
        }
        PrintStream out = new PrintStream((OutputStream)new BufferedOutputStream((OutputStream)new FileOutputStream((File)this.file)));
        try {
            Iterator<History.Entry> i$ = this.iterator();
            while (i$.hasNext()) {
                History.Entry entry = i$.next();
                out.println((Object)entry.value());
            }
            return;
        }
        finally {
            out.close();
        }
    }

    @Override
    public void purge() throws IOException {
        Log.trace((Object[])new Object[]{"Purging history"});
        this.clear();
        if (this.file.delete()) return;
        Log.warn((Object[])new Object[]{"Failed to delete history file: ", this.file});
    }
}

