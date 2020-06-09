/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.console.completer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import jline.console.completer.AggregateCompleter;
import jline.console.completer.Completer;
import jline.internal.Preconditions;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AggregateCompleter
implements Completer {
    private final List<Completer> completers = new ArrayList<Completer>();

    public AggregateCompleter() {
    }

    public AggregateCompleter(Collection<Completer> completers) {
        Preconditions.checkNotNull(completers);
        this.completers.addAll(completers);
    }

    public AggregateCompleter(Completer ... completers) {
        this(Arrays.asList(completers));
    }

    public Collection<Completer> getCompleters() {
        return this.completers;
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        Preconditions.checkNotNull(candidates);
        ArrayList<Completion> completions = new ArrayList<Completion>((int)this.completers.size());
        int max = -1;
        for (Completer completer : this.completers) {
            Completion completion = new Completion((AggregateCompleter)this, candidates);
            completion.complete((Completer)completer, (String)buffer, (int)cursor);
            max = Math.max((int)max, (int)completion.cursor);
            completions.add(completion);
        }
        Iterator<Completer> i$ = completions.iterator();
        while (i$.hasNext()) {
            Completion completion = (Completion)((Object)i$.next());
            if (completion.cursor != max) continue;
            candidates.addAll(completion.candidates);
        }
        return max;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{" + "completers=" + this.completers + '}';
    }
}

