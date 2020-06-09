/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

public final class ScalarToken
extends Token {
    private final String value;
    private final boolean plain;
    private final DumperOptions.ScalarStyle style;

    public ScalarToken(String value, Mark startMark, Mark endMark, boolean plain) {
        this((String)value, (boolean)plain, (Mark)startMark, (Mark)endMark, (DumperOptions.ScalarStyle)DumperOptions.ScalarStyle.PLAIN);
    }

    public ScalarToken(String value, boolean plain, Mark startMark, Mark endMark, DumperOptions.ScalarStyle style) {
        super((Mark)startMark, (Mark)endMark);
        this.value = value;
        this.plain = plain;
        if (style == null) {
            throw new NullPointerException((String)"Style must be provided.");
        }
        this.style = style;
    }

    public boolean getPlain() {
        return this.plain;
    }

    public String getValue() {
        return this.value;
    }

    public DumperOptions.ScalarStyle getStyle() {
        return this.style;
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.Scalar;
    }
}

