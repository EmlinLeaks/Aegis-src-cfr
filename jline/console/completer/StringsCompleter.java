/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.console.completer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import jline.console.completer.Completer;
import jline.internal.Preconditions;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StringsCompleter
implements Completer {
    private final SortedSet<String> strings = new TreeSet<String>();

    public StringsCompleter() {
    }

    public StringsCompleter(Collection<String> strings) {
        Preconditions.checkNotNull(strings);
        this.getStrings().addAll(strings);
    }

    public StringsCompleter(String ... strings) {
        this(Arrays.asList(strings));
    }

    public Collection<String> getStrings() {
        return this.strings;
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        Preconditions.checkNotNull(candidates);
        if (buffer == null) {
            candidates.addAll(this.strings);
        } else {
            String match;
            Iterator<E> i$ = this.strings.tailSet((String)buffer).iterator();
            while (i$.hasNext() && (match = (String)i$.next()).startsWith((String)buffer)) {
                candidates.add((CharSequence)match);
            }
        }
        if (candidates.size() == 1) {
            candidates.set((int)0, (CharSequence)(candidates.get((int)0) + " "));
        }
        if (!candidates.isEmpty()) return 0;
        return -1;
    }
}

