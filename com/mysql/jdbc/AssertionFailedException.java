/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Messages;

public class AssertionFailedException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public static void shouldNotHappen(Exception ex) throws AssertionFailedException {
        throw new AssertionFailedException((Exception)ex);
    }

    public AssertionFailedException(Exception ex) {
        super((String)(Messages.getString((String)"AssertionFailedException.0") + ex.toString() + Messages.getString((String)"AssertionFailedException.1")));
    }
}

