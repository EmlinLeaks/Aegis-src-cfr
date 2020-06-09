/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.error.Mark;

public class DuplicateKeyException
extends ConstructorException {
    protected DuplicateKeyException(Mark contextMark, Object key, Mark problemMark) {
        super((String)"while constructing a mapping", (Mark)contextMark, (String)("found duplicate key " + String.valueOf((Object)key)), (Mark)problemMark);
    }
}

