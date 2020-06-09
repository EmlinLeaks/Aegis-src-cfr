/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

import java.util.Collection;
import java.util.Collections;
import joptsimple.OptionException;
import joptsimple.OptionSpec;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class OptionArgumentConversionException
extends OptionException {
    private static final long serialVersionUID = -1L;
    private final String argument;

    OptionArgumentConversionException(OptionSpec<?> options, String argument, Throwable cause) {
        super(Collections.singleton(options), (Throwable)cause);
        this.argument = argument;
    }

    @Override
    Object[] messageArguments() {
        return new Object[]{this.argument, this.singleOptionString()};
    }
}

