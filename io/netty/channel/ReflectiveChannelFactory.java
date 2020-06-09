/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFactory;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.lang.reflect.Constructor;

public class ReflectiveChannelFactory<T extends Channel>
implements ChannelFactory<T> {
    private final Constructor<? extends T> constructor;

    public ReflectiveChannelFactory(Class<? extends T> clazz) {
        ObjectUtil.checkNotNull(clazz, (String)"clazz");
        try {
            this.constructor = clazz.getConstructor(new Class[0]);
            return;
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException((String)("Class " + StringUtil.simpleClassName(clazz) + " does not have a public non-arg constructor"), (Throwable)e);
        }
    }

    @Override
    public T newChannel() {
        try {
            return (T)((Channel)this.constructor.newInstance((Object[])new Object[0]));
        }
        catch (Throwable t) {
            throw new ChannelException((String)("Unable to create Channel from class " + this.constructor.getDeclaringClass()), (Throwable)t);
        }
    }

    public String toString() {
        return StringUtil.simpleClassName(ReflectiveChannelFactory.class) + '(' + StringUtil.simpleClassName(this.constructor.getDeclaringClass()) + ".class)";
    }
}

