/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc.exceptions.jdbc4;

import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.StreamingNotifiable;
import java.sql.SQLRecoverableException;

public class CommunicationsException
extends SQLRecoverableException
implements StreamingNotifiable {
    static final long serialVersionUID = 4317904269797988677L;
    private String exceptionMessage;

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

