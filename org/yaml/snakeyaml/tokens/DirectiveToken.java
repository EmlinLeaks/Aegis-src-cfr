/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.tokens;

import java.util.List;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.tokens.Token;

public final class DirectiveToken<T>
extends Token {
    private final String name;
    private final List<T> value;

    public DirectiveToken(String name, List<T> value, Mark startMark, Mark endMark) {
        super((Mark)startMark, (Mark)endMark);
        this.name = name;
        if (value != null && value.size() != 2) {
            throw new YAMLException((String)("Two strings must be provided instead of " + String.valueOf((int)value.size())));
        }
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public List<T> getValue() {
        return this.value;
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.Directive;
    }
}

