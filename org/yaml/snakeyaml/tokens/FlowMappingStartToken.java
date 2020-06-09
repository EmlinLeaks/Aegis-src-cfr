/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

public final class FlowMappingStartToken
extends Token {
    public FlowMappingStartToken(Mark startMark, Mark endMark) {
        super((Mark)startMark, (Mark)endMark);
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.FlowMappingStart;
    }
}

