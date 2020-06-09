/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NoArgumentOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSpec;
import joptsimple.OptionalArgumentOptionSpec;
import joptsimple.RequiredArgumentOptionSpec;
import joptsimple.UnconfiguredOptionException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class OptionSpecBuilder
extends NoArgumentOptionSpec {
    private final OptionParser parser;

    OptionSpecBuilder(OptionParser parser, List<String> options, String description) {
        super(options, (String)description);
        this.parser = parser;
        this.attachToParser();
    }

    private void attachToParser() {
        this.parser.recognize(this);
    }

    public ArgumentAcceptingOptionSpec<String> withRequiredArg() {
        RequiredArgumentOptionSpec<String> newSpec = new RequiredArgumentOptionSpec<String>(this.options(), (String)this.description());
        this.parser.recognize(newSpec);
        return newSpec;
    }

    public ArgumentAcceptingOptionSpec<String> withOptionalArg() {
        OptionalArgumentOptionSpec<String> newSpec = new OptionalArgumentOptionSpec<String>(this.options(), (String)this.description());
        this.parser.recognize(newSpec);
        return newSpec;
    }

    public OptionSpecBuilder requiredIf(String dependent, String ... otherDependents) {
        List<String> dependents = this.validatedDependents((String)dependent, (String[])otherDependents);
        Iterator<String> i$ = dependents.iterator();
        while (i$.hasNext()) {
            String each = i$.next();
            this.parser.requiredIf(this.options(), (String)each);
        }
        return this;
    }

    public OptionSpecBuilder requiredIf(OptionSpec<?> dependent, OptionSpec<?> ... otherDependents) {
        this.parser.requiredIf(this.options(), dependent);
        OptionSpec<?>[] arr$ = otherDependents;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            OptionSpec<?> each = arr$[i$];
            this.parser.requiredIf(this.options(), each);
            ++i$;
        }
        return this;
    }

    public OptionSpecBuilder requiredUnless(String dependent, String ... otherDependents) {
        List<String> dependents = this.validatedDependents((String)dependent, (String[])otherDependents);
        Iterator<String> i$ = dependents.iterator();
        while (i$.hasNext()) {
            String each = i$.next();
            this.parser.requiredUnless(this.options(), (String)each);
        }
        return this;
    }

    public OptionSpecBuilder requiredUnless(OptionSpec<?> dependent, OptionSpec<?> ... otherDependents) {
        this.parser.requiredUnless(this.options(), dependent);
        OptionSpec<?>[] arr$ = otherDependents;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            OptionSpec<?> each = arr$[i$];
            this.parser.requiredUnless(this.options(), each);
            ++i$;
        }
        return this;
    }

    private List<String> validatedDependents(String dependent, String ... otherDependents) {
        String each;
        ArrayList<String> dependents = new ArrayList<String>();
        dependents.add(dependent);
        Collections.addAll(dependents, otherDependents);
        Iterator<E> i$ = dependents.iterator();
        do {
            if (!i$.hasNext()) return dependents;
        } while (this.parser.isRecognized((String)(each = (String)i$.next())));
        throw new UnconfiguredOptionException((String)each);
    }
}

