/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Messages;
import java.sql.SQLException;

public class NotUpdatable
extends SQLException {
    private static final long serialVersionUID = 8084742846039782258L;
    public static final String NOT_UPDATEABLE_MESSAGE = Messages.getString((String)"NotUpdatable.0") + Messages.getString((String)"NotUpdatable.1") + Messages.getString((String)"NotUpdatable.2") + Messages.getString((String)"NotUpdatable.3") + Messages.getString((String)"NotUpdatable.4") + Messages.getString((String)"NotUpdatable.5");

    public NotUpdatable() {
        this((String)NOT_UPDATEABLE_MESSAGE);
    }

    public NotUpdatable(String reason) {
        super((String)(reason + Messages.getString((String)"NotUpdatable.1") + Messages.getString((String)"NotUpdatable.2") + Messages.getString((String)"NotUpdatable.3") + Messages.getString((String)"NotUpdatable.4") + Messages.getString((String)"NotUpdatable.5")), (String)"S1000");
    }
}

