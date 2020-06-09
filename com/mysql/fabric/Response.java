/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric;

import com.mysql.fabric.FabricCommunicationException;
import com.mysql.fabric.proto.xmlrpc.ResultSetParser;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Response {
    private int protocolVersion;
    private String fabricUuid;
    private int ttl;
    private String errorMessage;
    private List<Map<String, ?>> resultSet;

    public Response(List<?> responseData) throws FabricCommunicationException {
        List resultSets;
        this.protocolVersion = ((Integer)responseData.get((int)0)).intValue();
        if (this.protocolVersion != 1) {
            throw new FabricCommunicationException((String)("Unknown protocol version: " + this.protocolVersion));
        }
        this.fabricUuid = (String)responseData.get((int)1);
        this.ttl = ((Integer)responseData.get((int)2)).intValue();
        this.errorMessage = (String)responseData.get((int)3);
        if ("".equals((Object)this.errorMessage)) {
            this.errorMessage = null;
        }
        if ((resultSets = (List)responseData.get((int)4)).size() <= 0) return;
        Map resultData = (Map)resultSets.get((int)0);
        this.resultSet = new ResultSetParser().parse((Map)resultData.get((Object)"info"), (List<List<Object>>)((List)resultData.get((Object)"rows")));
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    public String getFabricUuid() {
        return this.fabricUuid;
    }

    public int getTtl() {
        return this.ttl;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public List<Map<String, ?>> getResultSet() {
        return this.resultSet;
    }
}

