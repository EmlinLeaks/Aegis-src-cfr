/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.console.history;

import java.io.IOException;
import jline.console.history.History;

public interface PersistentHistory
extends History {
    public void flush() throws IOException;

    public void purge() throws IOException;
}

