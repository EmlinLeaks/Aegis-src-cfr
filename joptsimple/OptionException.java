/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import joptsimple.OptionSpec;
import joptsimple.UnrecognizedOptionException;
import joptsimple.internal.Messages;
import joptsimple.internal.Strings;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class OptionException
extends RuntimeException {
    private static final long serialVersionUID = -1L;
    private final List<String> options = new ArrayList<String>();

    protected OptionException(List<String> options) {
        this.options.addAll(options);
    }

    protected OptionException(Collection<? extends OptionSpec<?>> options) {
        this.options.addAll(this.specsToStrings(options));
    }

    protected OptionException(Collection<? extends OptionSpec<?>> options, Throwable cause) {
        super((Throwable)cause);
        this.options.addAll(this.specsToStrings(options));
    }

    private List<String> specsToStrings(Collection<? extends OptionSpec<?>> options) {
        ArrayList<String> strings = new ArrayList<String>();
        Iterator<OptionSpec<?>> i$ = options.iterator();
        while (i$.hasNext()) {
            OptionSpec<?> each = i$.next();
            strings.add((String)this.specToString(each));
        }
        return strings;
    }

    private String specToString(OptionSpec<?> option) {
        return Strings.join(new ArrayList<String>(option.options()), (String)"/");
    }

    public List<String> options() {
        return Collections.unmodifiableList(this.options);
    }

    protected final String singleOptionString() {
        return this.singleOptionString((String)this.options.get((int)0));
    }

    protected final String singleOptionString(String option) {
        return option;
    }

    protected final String multipleOptionString() {
        StringBuilder buffer = new StringBuilder((String)"[");
        LinkedHashSet<String> asSet = new LinkedHashSet<String>(this.options);
        Iterator<E> iter = asSet.iterator();
        do {
            if (!iter.hasNext()) {
                buffer.append((char)']');
                return buffer.toString();
            }
            buffer.append((String)this.singleOptionString((String)((String)iter.next())));
            if (!iter.hasNext()) continue;
            buffer.append((String)", ");
        } while (true);
    }

    static OptionException unrecognizedOption(String option) {
        return new UnrecognizedOptionException((String)option);
    }

    @Override
    public final String getMessage() {
        return this.localizedMessage((Locale)Locale.getDefault());
    }

    final String localizedMessage(Locale locale) {
        return this.formattedMessage((Locale)locale);
    }

    private String formattedMessage(Locale locale) {
        return Messages.message((Locale)locale, (String)"joptsimple.ExceptionMessages", this.getClass(), (String)"message", (Object[])this.messageArguments());
    }

    abstract Object[] messageArguments();
}

