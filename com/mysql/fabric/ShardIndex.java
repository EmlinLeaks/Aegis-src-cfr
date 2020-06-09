/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric;

public class ShardIndex {
    private String bound;
    private Integer shardId;
    private String groupName;

    public ShardIndex(String bound, Integer shardId, String groupName) {
        this.bound = bound;
        this.shardId = shardId;
        this.groupName = groupName;
    }

    public String getBound() {
        return this.bound;
    }

    public Integer getShardId() {
        return this.shardId;
    }

    public String getGroupName() {
        return this.groupName;
    }
}

