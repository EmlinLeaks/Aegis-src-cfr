/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

import java.util.List;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.ArgumentList;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class OptionalArgumentOptionSpec<V>
extends ArgumentAcceptingOptionSpec<V> {
    OptionalArgumentOptionSpec(String option) {
        super((String)option, (boolean)false);
    }

    OptionalArgumentOptionSpec(List<String> options, String description) {
        super(options, (boolean)false, (String)description);
    }

    @Override
    protected void detectOptionArgument(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions) {
        if (!arguments.hasMore()) {
            detectedOptions.add(this);
            return;
        }
        String nextArgument = arguments.peek();
        if (!parser.looksLikeAnOption((String)nextArgument) && this.canConvertArgument((String)nextArgument)) {
            this.handleOptionArgument((OptionParser)parser, (OptionSet)detectedOptions, (ArgumentList)arguments);
            return;
        }
        if (this.isArgumentOfNumberType() && this.canConvertArgument((String)nextArgument)) {
            this.addArguments((OptionSet)detectedOptions, (String)arguments.next());
            return;
        }
        detectedOptions.add(this);
    }

    private void handleOptionArgument(OptionParser parser, OptionSet detectedOptions, ArgumentList arguments) {
        if (parser.posixlyCorrect()) {
            detectedOptions.add(this);
            parser.noMoreOptions();
            return;
        }
        this.addArguments((OptionSet)detectedOptions, (String)arguments.next());
    }
}

