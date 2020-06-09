/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.parser;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

public class ParserException
extends MarkedYAMLException {
    private static final long serialVersionUID = -2349253802798398038L;

    public ParserException(String context, Mark contextMark, String problem, Mark problemMark) {
        super((String)context, (Mark)contextMark, (String)problem, (Mark)problemMark, null, null);
    }
}

