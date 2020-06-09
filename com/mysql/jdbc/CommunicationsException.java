/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.StreamingNotifiable;
import java.sql.SQLException;

public class CommunicationsException
extends SQLException
implements StreamingNotifiable {
    static final long serialVersionUID = 3193864990663398317L;
    private String exceptionMessage = null;

    public CommunicationsException(MySQLConnection conn, long lastPacketSentTimeMs, long lastPacketReceivedTimeMs, Exception underlyingException) {
        this.exceptionMessage = SQLError.createLinkFailureMessageBasedOnHeuristics((MySQLConnection)conn, (long)lastPacketSentTimeMs, (long)lastPacketReceivedTimeMs, (Exception)underlyingException);
        if (underlyingException == null) return;
        this.initCause((Throwable)underlyingException);
    }

    @Override
    public String getMessage() {
        return this.exceptionMessage;
    }

    @Override
    public String getSQLState() {
        return "08S01";
    }

    @Override
    public void setWasStreamingResults() {
        this.exceptionMessage = Messages.getString((String)"CommunicationsException.ClientWasStreaming");
    }
}

