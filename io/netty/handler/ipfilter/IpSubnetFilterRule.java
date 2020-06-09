/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ipfilter;

import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.util.internal.SocketUtils;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public final class IpSubnetFilterRule
implements IpFilterRule {
    private final IpFilterRule filterRule;

    public IpSubnetFilterRule(String ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
        try {
            this.filterRule = IpSubnetFilterRule.selectFilterRule((InetAddress)SocketUtils.addressByName((String)ipAddress), (int)cidrPrefix, (IpFilterRuleType)ruleType);
            return;
        }
        catch (UnknownHostException e) {
            throw new IllegalArgumentException((String)"ipAddress", (Throwable)e);
        }
    }

    public IpSubnetFilterRule(InetAddress ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
        this.filterRule = IpSubnetFilterRule.selectFilterRule((InetAddress)ipAddress, (int)cidrPrefix, (IpFilterRuleType)ruleType);
    }

    private static IpFilterRule selectFilterRule(InetAddress ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
        if (ipAddress == null) {
            throw new NullPointerException((String)"ipAddress");
        }
        if (ruleType == null) {
            throw new NullPointerException((String)"ruleType");
        }
        if (ipAddress instanceof Inet4Address) {
            return new Ip4SubnetFilterRule((Inet4Address)((Inet4Address)ipAddress), (int)cidrPrefix, (IpFilterRuleType)ruleType, null);
        }
        if (!(ipAddress instanceof Inet6Address)) throw new IllegalArgumentException((String)"Only IPv4 and IPv6 addresses are supported");
        return new Ip6SubnetFilterRule((Inet6Address)((Inet6Address)ipAddress), (int)cidrPrefix, (IpFilterRuleType)ruleType, null);
    }

    @Override
    public boolean matches(InetSocketAddress remoteAddress) {
        return this.filterRule.matches((InetSocketAddress)remoteAddress);
    }

    @Override
    public IpFilterRuleType ruleType() {
        return this.filterRule.ruleType();
    }
}

