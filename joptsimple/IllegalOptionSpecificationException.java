/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

import java.util.Collections;
import java.util.List;
import joptsimple.OptionException;

class IllegalOptionSpecificationException
extends OptionException {
    private static final long serialVersionUID = -1L;

    IllegalOptionSpecificationException(String option) {
        super(Collections.singletonList(option));
    }

    @Override
    Object[] messageArguments() {
        return new Object[]{this.singleOptionString()};
    }
}

