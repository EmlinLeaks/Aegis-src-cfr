/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

import java.util.Collections;
import java.util.List;
import joptsimple.OptionException;

class UnrecognizedOptionException
extends OptionException {
    private static final long serialVersionUID = -1L;

    UnrecognizedOptionException(String option) {
        super(Collections.singletonList(option));
    }

    @Override
    Object[] messageArguments() {
        return new Object[]{this.singleOptionString()};
    }
}

