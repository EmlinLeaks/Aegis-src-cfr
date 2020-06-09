/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.scanner;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

public class ScannerException
extends MarkedYAMLException {
    private static final long serialVersionUID = 4782293188600445954L;

    public ScannerException(String context, Mark contextMark, String problem, Mark problemMark, String note) {
        super((String)context, (Mark)contextMark, (String)problem, (Mark)problemMark, (String)note);
    }

    public ScannerException(String context, Mark contextMark, String problem, Mark problemMark) {
        this((String)context, (Mark)contextMark, (String)problem, (Mark)problemMark, null);
    }
}

