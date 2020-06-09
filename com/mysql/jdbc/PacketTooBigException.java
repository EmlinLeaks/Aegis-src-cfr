/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Messages;
import java.sql.SQLException;

public class PacketTooBigException
extends SQLException {
    static final long serialVersionUID = 7248633977685452174L;

    public PacketTooBigException(long packetSize, long maximumPacketSize) {
        super((String)(Messages.getString((String)"PacketTooBigException.0") + packetSize + Messages.getString((String)"PacketTooBigException.1") + maximumPacketSize + Messages.getString((String)"PacketTooBigException.2") + Messages.getString((String)"PacketTooBigException.3") + Messages.getString((String)"PacketTooBigException.4")), (String)"S1000");
    }
}

