/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.ArgumentList;
import joptsimple.OptionMissingRequiredArgumentException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.internal.Messages;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class AlternativeLongOptionSpec
extends ArgumentAcceptingOptionSpec<String> {
    AlternativeLongOptionSpec() {
        super(Collections.singletonList("W"), (boolean)true, (String)Messages.message((Locale)Locale.getDefault(), (String)"joptsimple.HelpFormatterMessages", AlternativeLongOptionSpec.class, (String)"description", (Object[])new Object[0]));
        this.describedAs((String)Messages.message((Locale)Locale.getDefault(), (String)"joptsimple.HelpFormatterMessages", AlternativeLongOptionSpec.class, (String)"arg.description", (Object[])new Object[0]));
    }

    @Override
    protected void detectOptionArgument(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions) {
        if (!arguments.hasMore()) {
            throw new OptionMissingRequiredArgumentException(this);
        }
        arguments.treatNextAsLongOption();
    }
}

