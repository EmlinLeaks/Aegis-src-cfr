/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.console.completer;

import java.io.File;
import java.util.List;
import jline.console.completer.Completer;
import jline.internal.Configuration;
import jline.internal.Preconditions;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FileNameCompleter
implements Completer {
    private static final boolean OS_IS_WINDOWS;

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        Preconditions.checkNotNull(candidates);
        if (buffer == null) {
            buffer = "";
        }
        if (OS_IS_WINDOWS) {
            buffer = buffer.replace((char)'/', (char)'\\');
        }
        String translated = buffer;
        File homeDir = this.getUserHome();
        if (translated.startsWith((String)("~" + this.separator()))) {
            translated = homeDir.getPath() + translated.substring((int)1);
        } else if (translated.startsWith((String)"~")) {
            translated = homeDir.getParentFile().getAbsolutePath();
        } else if (!new File((String)translated).isAbsolute()) {
            String cwd = this.getUserDir().getAbsolutePath();
            translated = cwd + this.separator() + translated;
        }
        File file = new File((String)translated);
        File dir = translated.endsWith((String)this.separator()) ? file : file.getParentFile();
        File[] entries = dir == null ? new File[0] : dir.listFiles();
        return this.matchFiles((String)buffer, (String)translated, (File[])entries, candidates);
    }

    protected String separator() {
        return File.separator;
    }

    protected File getUserHome() {
        return Configuration.getUserHome();
    }

    protected File getUserDir() {
        return new File((String)".");
    }

    protected int matchFiles(String buffer, String translated, File[] files, List<CharSequence> candidates) {
        if (files == null) {
            return -1;
        }
        int matches = 0;
        for (File file : files) {
            if (!file.getAbsolutePath().startsWith((String)translated)) continue;
            ++matches;
        }
        File[] arr$ = files;
        int len$ = arr$.length;
        int i$ = 0;
        do {
            File file;
            if (i$ >= len$) {
                int index = buffer.lastIndexOf((String)this.separator());
                return index + this.separator().length();
            }
            file = arr$[i$];
            if (file.getAbsolutePath().startsWith((String)translated)) {
                String name = file.getName() + (matches == 1 && file.isDirectory() ? this.separator() : " ");
                candidates.add((CharSequence)this.render((File)file, (CharSequence)name).toString());
            }
            ++i$;
        } while (true);
    }

    protected CharSequence render(File file, CharSequence name) {
        return name;
    }

    static {
        String os = Configuration.getOsName();
        OS_IS_WINDOWS = os.contains((CharSequence)"windows");
    }
}

