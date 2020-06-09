/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ipfilter;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ipfilter.AbstractRemoteAddressFilter;
import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

@ChannelHandler.Sharable
public class RuleBasedIpFilter
extends AbstractRemoteAddressFilter<InetSocketAddress> {
    private final IpFilterRule[] rules;

    public RuleBasedIpFilter(IpFilterRule ... rules) {
        if (rules == null) {
            throw new NullPointerException((String)"rules");
        }
        this.rules = rules;
    }

    @Override
    protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) throws Exception {
        IpFilterRule[] arripFilterRule = this.rules;
        int n = arripFilterRule.length;
        int n2 = 0;
        while (n2 < n) {
            IpFilterRule rule = arripFilterRule[n2];
            if (rule == null) {
                return true;
            }
            if (rule.matches((InetSocketAddress)remoteAddress)) {
                if (rule.ruleType() != IpFilterRuleType.ACCEPT) return false;
                return true;
            }
            ++n2;
        }
        return true;
    }
}

