/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline;

import jline.TerminalSupport;

public class UnsupportedTerminal
extends TerminalSupport {
    public UnsupportedTerminal() {
        super((boolean)false);
        this.setAnsiSupported((boolean)false);
        this.setEchoEnabled((boolean)true);
    }
}

