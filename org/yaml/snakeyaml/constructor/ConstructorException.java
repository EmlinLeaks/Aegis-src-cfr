/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

public class ConstructorException
extends MarkedYAMLException {
    private static final long serialVersionUID = -8816339931365239910L;

    protected ConstructorException(String context, Mark contextMark, String problem, Mark problemMark, Throwable cause) {
        super((String)context, (Mark)contextMark, (String)problem, (Mark)problemMark, (Throwable)cause);
    }

    protected ConstructorException(String context, Mark contextMark, String problem, Mark problemMark) {
        this((String)context, (Mark)contextMark, (String)problem, (Mark)problemMark, null);
    }
}

