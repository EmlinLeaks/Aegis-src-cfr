/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

import java.util.List;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.ArgumentList;
import joptsimple.OptionMissingRequiredArgumentException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class RequiredArgumentOptionSpec<V>
extends ArgumentAcceptingOptionSpec<V> {
    RequiredArgumentOptionSpec(String option) {
        super((String)option, (boolean)true);
    }

    RequiredArgumentOptionSpec(List<String> options, String description) {
        super(options, (boolean)true, (String)description);
    }

    @Override
    protected void detectOptionArgument(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions) {
        if (!arguments.hasMore()) {
            throw new OptionMissingRequiredArgumentException(this);
        }
        this.addArguments((OptionSet)detectedOptions, (String)arguments.next());
    }
}

