/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.console.completer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.internal.Log;
import jline.internal.Preconditions;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ArgumentCompleter
implements Completer {
    private final ArgumentDelimiter delimiter;
    private final List<Completer> completers = new ArrayList<Completer>();
    private boolean strict = true;

    public ArgumentCompleter(ArgumentDelimiter delimiter, Collection<Completer> completers) {
        this.delimiter = Preconditions.checkNotNull(delimiter);
        Preconditions.checkNotNull(completers);
        this.completers.addAll(completers);
    }

    public ArgumentCompleter(ArgumentDelimiter delimiter, Completer ... completers) {
        this((ArgumentDelimiter)delimiter, Arrays.asList(completers));
    }

    public ArgumentCompleter(Completer ... completers) {
        this((ArgumentDelimiter)new WhitespaceArgumentDelimiter(), (Completer[])completers);
    }

    public ArgumentCompleter(List<Completer> completers) {
        this((ArgumentDelimiter)new WhitespaceArgumentDelimiter(), completers);
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public boolean isStrict() {
        return this.strict;
    }

    public ArgumentDelimiter getDelimiter() {
        return this.delimiter;
    }

    public List<Completer> getCompleters() {
        return this.completers;
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        Preconditions.checkNotNull(candidates);
        ArgumentDelimiter delim = this.getDelimiter();
        ArgumentList list = delim.delimit((CharSequence)buffer, (int)cursor);
        int argpos = list.getArgumentPosition();
        int argIndex = list.getCursorArgumentIndex();
        if (argIndex < 0) {
            return -1;
        }
        List<Completer> completers = this.getCompleters();
        Completer completer = argIndex >= completers.size() ? completers.get((int)(completers.size() - 1)) : completers.get((int)argIndex);
        for (int i = 0; this.isStrict() && i < argIndex; ++i) {
            Completer sub = completers.get((int)(i >= completers.size() ? completers.size() - 1 : i));
            String[] args = list.getArguments();
            String arg = args == null || i >= args.length ? "" : args[i];
            LinkedList<CharSequence> subCandidates = new LinkedList<CharSequence>();
            if (sub.complete((String)arg, (int)arg.length(), subCandidates) == -1) {
                return -1;
            }
            if (subCandidates.size() != 0) continue;
            return -1;
        }
        int ret = completer.complete((String)list.getCursorArgument(), (int)argpos, candidates);
        if (ret == -1) {
            return -1;
        }
        int pos = ret + list.getBufferPosition() - argpos;
        if (cursor != buffer.length() && delim.isDelimiter((CharSequence)buffer, (int)cursor)) {
            for (int i = 0; i < candidates.size(); ++i) {
                CharSequence val = candidates.get((int)i);
                while (val.length() > 0 && delim.isDelimiter((CharSequence)val, (int)(val.length() - 1))) {
                    val = val.subSequence((int)0, (int)(val.length() - 1));
                }
                candidates.set((int)i, (CharSequence)val);
            }
        }
        Log.trace((Object[])new Object[]{"Completing ", buffer, " (pos=", Integer.valueOf((int)cursor), ") with: ", candidates, ": offset=", Integer.valueOf((int)pos)});
        return pos;
    }
}

