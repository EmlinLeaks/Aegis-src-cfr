/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.tokens.Token;

public abstract class Token {
    private final Mark startMark;
    private final Mark endMark;

    public Token(Mark startMark, Mark endMark) {
        if (startMark == null) throw new YAMLException((String)"Token requires marks.");
        if (endMark == null) {
            throw new YAMLException((String)"Token requires marks.");
        }
        this.startMark = startMark;
        this.endMark = endMark;
    }

    public Mark getStartMark() {
        return this.startMark;
    }

    public Mark getEndMark() {
        return this.endMark;
    }

    public abstract ID getTokenId();
}

