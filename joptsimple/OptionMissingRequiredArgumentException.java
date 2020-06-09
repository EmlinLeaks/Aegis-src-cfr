/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

import java.util.Arrays;
import java.util.Collection;
import joptsimple.OptionException;
import joptsimple.OptionSpec;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class OptionMissingRequiredArgumentException
extends OptionException {
    private static final long serialVersionUID = -1L;

    OptionMissingRequiredArgumentException(OptionSpec<?> option) {
        super(Arrays.asList(option));
    }

    @Override
    Object[] messageArguments() {
        return new Object[]{this.singleOptionString()};
    }
}

