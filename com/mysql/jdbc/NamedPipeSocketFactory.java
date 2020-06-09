/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.NamedPipeSocketFactory;
import com.mysql.jdbc.SocketFactory;
import com.mysql.jdbc.SocketMetadata;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Properties;

public class NamedPipeSocketFactory
implements SocketFactory,
SocketMetadata {
    public static final String NAMED_PIPE_PROP_NAME = "namedPipePath";
    private Socket namedPipeSocket;

    @Override
    public Socket afterHandshake() throws SocketException, IOException {
        return this.namedPipeSocket;
    }

    @Override
    public Socket beforeHandshake() throws SocketException, IOException {
        return this.namedPipeSocket;
    }

    @Override
    public Socket connect(String host, int portNumber, Properties props) throws SocketException, IOException {
        String namedPipePath = props.getProperty((String)NAMED_PIPE_PROP_NAME);
        if (namedPipePath == null) {
            namedPipePath = "\\\\.\\pipe\\MySQL";
        } else if (namedPipePath.length() == 0) {
            throw new SocketException((String)(Messages.getString((String)"NamedPipeSocketFactory.2") + NAMED_PIPE_PROP_NAME + Messages.getString((String)"NamedPipeSocketFactory.3")));
        }
        this.namedPipeSocket = new NamedPipeSocket((NamedPipeSocketFactory)this, (String)namedPipePath);
        return this.namedPipeSocket;
    }

    @Override
    public boolean isLocallyConnected(ConnectionImpl conn) throws SQLException {
        return true;
    }
}

