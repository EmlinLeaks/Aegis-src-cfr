/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel.group;

import io.netty.channel.Channel;
import io.netty.channel.ServerChannel;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.ChannelMatchers;

public final class ChannelMatchers {
    private static final ChannelMatcher ALL_MATCHER = new ChannelMatcher(){

        public boolean matches(Channel channel) {
            return true;
        }
    };
    private static final ChannelMatcher SERVER_CHANNEL_MATCHER = ChannelMatchers.isInstanceOf(ServerChannel.class);
    private static final ChannelMatcher NON_SERVER_CHANNEL_MATCHER = ChannelMatchers.isNotInstanceOf(ServerChannel.class);

    private ChannelMatchers() {
    }

    public static ChannelMatcher all() {
        return ALL_MATCHER;
    }

    public static ChannelMatcher isNot(Channel channel) {
        return ChannelMatchers.invert((ChannelMatcher)ChannelMatchers.is((Channel)channel));
    }

    public static ChannelMatcher is(Channel channel) {
        return new InstanceMatcher((Channel)channel);
    }

    public static ChannelMatcher isInstanceOf(Class<? extends Channel> clazz) {
        return new ClassMatcher(clazz);
    }

    public static ChannelMatcher isNotInstanceOf(Class<? extends Channel> clazz) {
        return ChannelMatchers.invert((ChannelMatcher)ChannelMatchers.isInstanceOf(clazz));
    }

    public static ChannelMatcher isServerChannel() {
        return SERVER_CHANNEL_MATCHER;
    }

    public static ChannelMatcher isNonServerChannel() {
        return NON_SERVER_CHANNEL_MATCHER;
    }

    public static ChannelMatcher invert(ChannelMatcher matcher) {
        return new InvertMatcher((ChannelMatcher)matcher);
    }

    public static ChannelMatcher compose(ChannelMatcher ... matchers) {
        if (matchers.length < 1) {
            throw new IllegalArgumentException((String)"matchers must at least contain one element");
        }
        if (matchers.length != 1) return new CompositeMatcher((ChannelMatcher[])matchers);
        return matchers[0];
    }
}

