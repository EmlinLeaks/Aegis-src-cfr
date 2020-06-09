/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.proto.xmlrpc;

import com.mysql.fabric.FabricCommunicationException;
import com.mysql.fabric.FabricStateResponse;
import com.mysql.fabric.Response;
import com.mysql.fabric.Server;
import com.mysql.fabric.ServerGroup;
import com.mysql.fabric.ServerMode;
import com.mysql.fabric.ServerRole;
import com.mysql.fabric.ShardIndex;
import com.mysql.fabric.ShardMapping;
import com.mysql.fabric.ShardMappingFactory;
import com.mysql.fabric.ShardTable;
import com.mysql.fabric.ShardingType;
import com.mysql.fabric.proto.xmlrpc.AuthenticatedXmlRpcMethodCaller;
import com.mysql.fabric.proto.xmlrpc.InternalXmlRpcMethodCaller;
import com.mysql.fabric.proto.xmlrpc.XmlRpcMethodCaller;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class XmlRpcClient {
    private static final String THREAT_REPORTER_NAME = "MySQL Connector/J";
    private static final String METHOD_DUMP_FABRIC_NODES = "dump.fabric_nodes";
    private static final String METHOD_DUMP_SERVERS = "dump.servers";
    private static final String METHOD_DUMP_SHARD_TABLES = "dump.shard_tables";
    private static final String METHOD_DUMP_SHARD_INDEX = "dump.shard_index";
    private static final String METHOD_DUMP_SHARD_MAPS = "dump.shard_maps";
    private static final String METHOD_SHARDING_LOOKUP_SERVERS = "sharding.lookup_servers";
    private static final String METHOD_SHARDING_CREATE_DEFINITION = "sharding.create_definition";
    private static final String METHOD_SHARDING_ADD_TABLE = "sharding.add_table";
    private static final String METHOD_SHARDING_ADD_SHARD = "sharding.add_shard";
    private static final String METHOD_GROUP_LOOKUP_GROUPS = "group.lookup_groups";
    private static final String METHOD_GROUP_CREATE = "group.create";
    private static final String METHOD_GROUP_ADD = "group.add";
    private static final String METHOD_GROUP_REMOVE = "group.remove";
    private static final String METHOD_GROUP_PROMOTE = "group.promote";
    private static final String METHOD_GROUP_DESTROY = "group.destroy";
    private static final String METHOD_THREAT_REPORT_ERROR = "threat.report_error";
    private static final String METHOD_THREAT_REPORT_FAILURE = "threat.report_failure";
    private static final String FIELD_MODE = "mode";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_HOST = "host";
    private static final String FIELD_PORT = "port";
    private static final String FIELD_ADDRESS = "address";
    private static final String FIELD_GROUP_ID = "group_id";
    private static final String FIELD_SERVER_UUID = "server_uuid";
    private static final String FIELD_WEIGHT = "weight";
    private static final String FIELD_SCHEMA_NAME = "schema_name";
    private static final String FIELD_TABLE_NAME = "table_name";
    private static final String FIELD_COLUMN_NAME = "column_name";
    private static final String FIELD_LOWER_BOUND = "lower_bound";
    private static final String FIELD_SHARD_ID = "shard_id";
    private static final String FIELD_MAPPING_ID = "mapping_id";
    private static final String FIELD_GLOBAL_GROUP_ID = "global_group_id";
    private static final String FIELD_TYPE_NAME = "type_name";
    private static final String FIELD_RESULT = "result";
    private XmlRpcMethodCaller methodCaller;

    public XmlRpcClient(String url, String username, String password) throws FabricCommunicationException {
        this.methodCaller = new InternalXmlRpcMethodCaller((String)url);
        if (username == null) return;
        if ("".equals((Object)username)) return;
        if (password == null) return;
        this.methodCaller = new AuthenticatedXmlRpcMethodCaller((XmlRpcMethodCaller)this.methodCaller, (String)url, (String)username, (String)password);
    }

    private static Server unmarshallServer(Map<String, ?> serverData) throws FabricCommunicationException {
        try {
            ServerRole role;
            int port;
            ServerMode mode;
            String host;
            if (Integer.class.equals(serverData.get((Object)FIELD_MODE).getClass())) {
                mode = ServerMode.getFromConstant((Integer)((Integer)serverData.get((Object)FIELD_MODE)));
                role = ServerRole.getFromConstant((Integer)((Integer)serverData.get((Object)FIELD_STATUS)));
                host = (String)serverData.get((Object)FIELD_HOST);
                port = ((Integer)serverData.get((Object)FIELD_PORT)).intValue();
                return new Server((String)((String)serverData.get((Object)FIELD_GROUP_ID)), (String)((String)serverData.get((Object)FIELD_SERVER_UUID)), (String)host, (int)port, (ServerMode)mode, (ServerRole)role, (double)((Double)serverData.get((Object)FIELD_WEIGHT)).doubleValue());
            } else {
                mode = ServerMode.valueOf((String)((String)serverData.get((Object)FIELD_MODE)));
                role = ServerRole.valueOf((String)((String)serverData.get((Object)FIELD_STATUS)));
                String[] hostnameAndPort = ((String)serverData.get((Object)FIELD_ADDRESS)).split((String)":");
                host = hostnameAndPort[0];
                port = Integer.valueOf((String)hostnameAndPort[1]).intValue();
            }
            return new Server((String)((String)serverData.get((Object)FIELD_GROUP_ID)), (String)((String)serverData.get((Object)FIELD_SERVER_UUID)), (String)host, (int)port, (ServerMode)mode, (ServerRole)role, (double)((Double)serverData.get((Object)FIELD_WEIGHT)).doubleValue());
        }
        catch (Exception ex) {
            throw new FabricCommunicationException((String)"Unable to parse server definition", (Throwable)ex);
        }
    }

    private static Set<Server> toServerSet(List<Map<String, ?>> l) throws FabricCommunicationException {
        HashSet<Server> servers = new HashSet<Server>();
        Iterator<Map<String, ?>> i$ = l.iterator();
        while (i$.hasNext()) {
            Map<String, ?> serverData = i$.next();
            servers.add((Server)XmlRpcClient.unmarshallServer(serverData));
        }
        return servers;
    }

    private Response errorSafeCallMethod(String methodName, Object[] args) throws FabricCommunicationException {
        List<?> responseData = this.methodCaller.call((String)methodName, (Object[])args);
        Response response = new Response(responseData);
        if (response.getErrorMessage() == null) return response;
        throw new FabricCommunicationException((String)("Call failed to method `" + methodName + "':\n" + response.getErrorMessage()));
    }

    public Set<String> getFabricNames() throws FabricCommunicationException {
        Response resp = this.errorSafeCallMethod((String)METHOD_DUMP_FABRIC_NODES, (Object[])new Object[0]);
        HashSet<String> names = new HashSet<String>();
        Iterator<Map<String, ?>> i$ = resp.getResultSet().iterator();
        while (i$.hasNext()) {
            Map<String, ?> node = i$.next();
            names.add((String)(node.get((Object)FIELD_HOST) + ":" + node.get((Object)FIELD_PORT)));
        }
        return names;
    }

    public Set<String> getGroupNames() throws FabricCommunicationException {
        HashSet<String> groupNames = new HashSet<String>();
        Iterator<Map<String, ?>> i$ = this.errorSafeCallMethod((String)METHOD_GROUP_LOOKUP_GROUPS, null).getResultSet().iterator();
        while (i$.hasNext()) {
            Map<String, ?> row = i$.next();
            groupNames.add((String)((String)row.get((Object)FIELD_GROUP_ID)));
        }
        return groupNames;
    }

    public ServerGroup getServerGroup(String groupName) throws FabricCommunicationException {
        Set<ServerGroup> groups = this.getServerGroups((String)groupName).getData();
        if (groups.size() != 1) return null;
        return groups.iterator().next();
    }

    public Set<Server> getServersForKey(String tableName, int key) throws FabricCommunicationException {
        Response r = this.errorSafeCallMethod((String)METHOD_SHARDING_LOOKUP_SERVERS, (Object[])new Object[]{tableName, Integer.valueOf((int)key)});
        return XmlRpcClient.toServerSet(r.getResultSet());
    }

    public FabricStateResponse<Set<ServerGroup>> getServerGroups(String groupPattern) throws FabricCommunicationException {
        int version = 0;
        Response response = this.errorSafeCallMethod((String)METHOD_DUMP_SERVERS, (Object[])new Object[]{Integer.valueOf((int)version), groupPattern});
        HashMap<String, HashSet<E>> serversByGroupName = new HashMap<String, HashSet<E>>();
        for (Map<String, ?> server : response.getResultSet()) {
            Server s = XmlRpcClient.unmarshallServer(server);
            if (serversByGroupName.get((Object)s.getGroupName()) == null) {
                serversByGroupName.put(s.getGroupName(), new HashSet<E>());
            }
            ((Set)serversByGroupName.get((Object)s.getGroupName())).add(s);
        }
        HashSet<ServerGroup> serverGroups = new HashSet<ServerGroup>();
        Iterator<Map.Entry<K, V>> i$ = serversByGroupName.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<K, V> entry = i$.next();
            ServerGroup g = new ServerGroup((String)((String)entry.getKey()), (Set<Server>)((Set)entry.getValue()));
            serverGroups.add(g);
        }
        return new FabricStateResponse<Set<ServerGroup>>(serverGroups, (int)response.getTtl());
    }

    public FabricStateResponse<Set<ServerGroup>> getServerGroups() throws FabricCommunicationException {
        return this.getServerGroups((String)"");
    }

    private FabricStateResponse<Set<ShardTable>> getShardTables(int shardMappingId) throws FabricCommunicationException {
        int version = 0;
        Object[] args = new Object[]{Integer.valueOf((int)version), String.valueOf((int)shardMappingId)};
        Response tablesResponse = this.errorSafeCallMethod((String)METHOD_DUMP_SHARD_TABLES, (Object[])args);
        HashSet<ShardTable> tables = new HashSet<ShardTable>();
        Iterator<Map<String, ?>> i$ = tablesResponse.getResultSet().iterator();
        while (i$.hasNext()) {
            Map<String, ?> rawTable = i$.next();
            String database = (String)rawTable.get((Object)FIELD_SCHEMA_NAME);
            String table = (String)rawTable.get((Object)FIELD_TABLE_NAME);
            String column = (String)rawTable.get((Object)FIELD_COLUMN_NAME);
            ShardTable st = new ShardTable((String)database, (String)table, (String)column);
            tables.add(st);
        }
        return new FabricStateResponse<Set<ShardTable>>(tables, (int)tablesResponse.getTtl());
    }

    private FabricStateResponse<Set<ShardIndex>> getShardIndices(int shardMappingId) throws FabricCommunicationException {
        int version = 0;
        Object[] args = new Object[]{Integer.valueOf((int)version), String.valueOf((int)shardMappingId)};
        Response indexResponse = this.errorSafeCallMethod((String)METHOD_DUMP_SHARD_INDEX, (Object[])args);
        HashSet<ShardIndex> indices = new HashSet<ShardIndex>();
        Iterator<Map<String, ?>> i$ = indexResponse.getResultSet().iterator();
        while (i$.hasNext()) {
            Map<String, ?> rawIndexEntry = i$.next();
            String bound = (String)rawIndexEntry.get((Object)FIELD_LOWER_BOUND);
            int shardId = ((Integer)rawIndexEntry.get((Object)FIELD_SHARD_ID)).intValue();
            String groupName = (String)rawIndexEntry.get((Object)FIELD_GROUP_ID);
            ShardIndex si = new ShardIndex((String)bound, (Integer)Integer.valueOf((int)shardId), (String)groupName);
            indices.add(si);
        }
        return new FabricStateResponse<Set<ShardIndex>>(indices, (int)indexResponse.getTtl());
    }

    public FabricStateResponse<Set<ShardMapping>> getShardMappings(String shardMappingIdPattern) throws FabricCommunicationException {
        int version = 0;
        Object[] args = new Object[]{Integer.valueOf((int)version), shardMappingIdPattern};
        Response mapsResponse = this.errorSafeCallMethod((String)METHOD_DUMP_SHARD_MAPS, (Object[])args);
        long minExpireTimeMillis = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis((long)((long)mapsResponse.getTtl()));
        int baseTtl = mapsResponse.getTtl();
        HashSet<ShardMapping> mappings = new HashSet<ShardMapping>();
        Iterator<Map<String, ?>> i$ = mapsResponse.getResultSet().iterator();
        while (i$.hasNext()) {
            Map<String, ?> rawMapping = i$.next();
            int mappingId = ((Integer)rawMapping.get((Object)FIELD_MAPPING_ID)).intValue();
            ShardingType shardingType = ShardingType.valueOf((String)((String)rawMapping.get((Object)FIELD_TYPE_NAME)));
            String globalGroupName = (String)rawMapping.get((Object)FIELD_GLOBAL_GROUP_ID);
            FabricStateResponse<Set<ShardTable>> tables = this.getShardTables((int)mappingId);
            FabricStateResponse<Set<ShardIndex>> indices = this.getShardIndices((int)mappingId);
            if (tables.getExpireTimeMillis() < minExpireTimeMillis) {
                minExpireTimeMillis = tables.getExpireTimeMillis();
            }
            if (indices.getExpireTimeMillis() < minExpireTimeMillis) {
                minExpireTimeMillis = indices.getExpireTimeMillis();
            }
            ShardMapping m = new ShardMappingFactory().createShardMapping((int)mappingId, (ShardingType)shardingType, (String)globalGroupName, tables.getData(), indices.getData());
            mappings.add(m);
        }
        return new FabricStateResponse<Set<ShardMapping>>(mappings, (int)baseTtl, (long)minExpireTimeMillis);
    }

    public FabricStateResponse<Set<ShardMapping>> getShardMappings() throws FabricCommunicationException {
        return this.getShardMappings((String)"");
    }

    public void createGroup(String groupName) throws FabricCommunicationException {
        this.errorSafeCallMethod((String)METHOD_GROUP_CREATE, (Object[])new Object[]{groupName});
    }

    public void destroyGroup(String groupName) throws FabricCommunicationException {
        this.errorSafeCallMethod((String)METHOD_GROUP_DESTROY, (Object[])new Object[]{groupName});
    }

    public void createServerInGroup(String groupName, String hostname, int port) throws FabricCommunicationException {
        this.errorSafeCallMethod((String)METHOD_GROUP_ADD, (Object[])new Object[]{groupName, hostname + ":" + port});
    }

    public int createShardMapping(ShardingType type, String globalGroupName) throws FabricCommunicationException {
        Response r = this.errorSafeCallMethod((String)METHOD_SHARDING_CREATE_DEFINITION, (Object[])new Object[]{type.toString(), globalGroupName});
        return ((Integer)r.getResultSet().get((int)0).get((Object)FIELD_RESULT)).intValue();
    }

    public void createShardTable(int shardMappingId, String database, String table, String column) throws FabricCommunicationException {
        this.errorSafeCallMethod((String)METHOD_SHARDING_ADD_TABLE, (Object[])new Object[]{Integer.valueOf((int)shardMappingId), database + "." + table, column});
    }

    public void createShardIndex(int shardMappingId, String groupNameLowerBoundList) throws FabricCommunicationException {
        String status = "ENABLED";
        this.errorSafeCallMethod((String)METHOD_SHARDING_ADD_SHARD, (Object[])new Object[]{Integer.valueOf((int)shardMappingId), groupNameLowerBoundList, status});
    }

    public void addServerToGroup(String groupName, String hostname, int port) throws FabricCommunicationException {
        this.errorSafeCallMethod((String)METHOD_GROUP_ADD, (Object[])new Object[]{groupName, hostname + ":" + port});
    }

    public void removeServerFromGroup(String groupName, String hostname, int port) throws FabricCommunicationException {
        this.errorSafeCallMethod((String)METHOD_GROUP_REMOVE, (Object[])new Object[]{groupName, hostname + ":" + port});
    }

    public void promoteServerInGroup(String groupName, String hostname, int port) throws FabricCommunicationException {
        Server s;
        ServerGroup serverGroup = this.getServerGroup((String)groupName);
        Iterator<Server> i$ = serverGroup.getServers().iterator();
        do {
            if (!i$.hasNext()) return;
        } while (!(s = i$.next()).getHostname().equals((Object)hostname) || s.getPort() != port);
        this.errorSafeCallMethod((String)METHOD_GROUP_PROMOTE, (Object[])new Object[]{groupName, s.getUuid()});
    }

    public void reportServerError(Server server, String errorDescription, boolean forceFaulty) throws FabricCommunicationException {
        String reporter = THREAT_REPORTER_NAME;
        String command = METHOD_THREAT_REPORT_ERROR;
        if (forceFaulty) {
            command = METHOD_THREAT_REPORT_FAILURE;
        }
        this.errorSafeCallMethod((String)command, (Object[])new Object[]{server.getUuid(), reporter, errorDescription});
    }
}

