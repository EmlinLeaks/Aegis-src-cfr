/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.CommonsLogger;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Deprecated
public class CommonsLoggerFactory
extends InternalLoggerFactory {
    public static final InternalLoggerFactory INSTANCE = new CommonsLoggerFactory();

    @Override
    public InternalLogger newInstance(String name) {
        return new CommonsLogger((Log)LogFactory.getLog((String)name), (String)name);
    }
}

