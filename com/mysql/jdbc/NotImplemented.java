/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Messages;
import java.sql.SQLException;

public class NotImplemented
extends SQLException {
    static final long serialVersionUID = 7768433826547599990L;

    public NotImplemented() {
        super((String)Messages.getString((String)"NotImplemented.0"), (String)"S1C00");
    }
}

