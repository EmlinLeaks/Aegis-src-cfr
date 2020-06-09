/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.npn.NextProtoNego
 *  org.eclipse.jetty.npn.NextProtoNego$ClientProvider
 *  org.eclipse.jetty.npn.NextProtoNego$Provider
 *  org.eclipse.jetty.npn.NextProtoNego$ServerProvider
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkSslEngine;
import io.netty.handler.ssl.JettyNpnSslEngine;
import io.netty.util.internal.ObjectUtil;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import org.eclipse.jetty.npn.NextProtoNego;

final class JettyNpnSslEngine
extends JdkSslEngine {
    private static boolean available;

    static boolean isAvailable() {
        JettyNpnSslEngine.updateAvailability();
        return available;
    }

    private static void updateAvailability() {
        if (available) {
            return;
        }
        try {
            Class.forName((String)"sun.security.ssl.NextProtoNegoExtension", (boolean)true, null);
            available = true;
            return;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    JettyNpnSslEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator, boolean server) {
        super((SSLEngine)engine);
        ObjectUtil.checkNotNull(applicationNegotiator, (String)"applicationNegotiator");
        if (server) {
            JdkApplicationProtocolNegotiator.ProtocolSelectionListener protocolListener = ObjectUtil.checkNotNull(applicationNegotiator.protocolListenerFactory().newListener((SSLEngine)this, applicationNegotiator.protocols()), (String)"protocolListener");
            NextProtoNego.put((SSLEngine)engine, (NextProtoNego.Provider)new NextProtoNego.ServerProvider((JettyNpnSslEngine)this, (JdkApplicationProtocolNegotiator.ProtocolSelectionListener)protocolListener, (JdkApplicationProtocolNegotiator)applicationNegotiator){
                final /* synthetic */ JdkApplicationProtocolNegotiator.ProtocolSelectionListener val$protocolListener;
                final /* synthetic */ JdkApplicationProtocolNegotiator val$applicationNegotiator;
                final /* synthetic */ JettyNpnSslEngine this$0;
                {
                    this.this$0 = this$0;
                    this.val$protocolListener = protocolSelectionListener;
                    this.val$applicationNegotiator = jdkApplicationProtocolNegotiator;
                }

                public void unsupported() {
                    this.val$protocolListener.unsupported();
                }

                public List<String> protocols() {
                    return this.val$applicationNegotiator.protocols();
                }

                public void protocolSelected(String protocol) {
                    try {
                        this.val$protocolListener.selected((String)protocol);
                        return;
                    }
                    catch (java.lang.Throwable t) {
                        io.netty.util.internal.PlatformDependent.throwException((java.lang.Throwable)t);
                    }
                }
            });
            return;
        }
        JdkApplicationProtocolNegotiator.ProtocolSelector protocolSelector = ObjectUtil.checkNotNull(applicationNegotiator.protocolSelectorFactory().newSelector((SSLEngine)this, new LinkedHashSet<String>(applicationNegotiator.protocols())), (String)"protocolSelector");
        NextProtoNego.put((SSLEngine)engine, (NextProtoNego.Provider)new NextProtoNego.ClientProvider((JettyNpnSslEngine)this, (JdkApplicationProtocolNegotiator.ProtocolSelector)protocolSelector){
            final /* synthetic */ JdkApplicationProtocolNegotiator.ProtocolSelector val$protocolSelector;
            final /* synthetic */ JettyNpnSslEngine this$0;
            {
                this.this$0 = this$0;
                this.val$protocolSelector = protocolSelector;
            }

            public boolean supports() {
                return true;
            }

            public void unsupported() {
                this.val$protocolSelector.unsupported();
            }

            public String selectProtocol(List<String> protocols) {
                try {
                    return this.val$protocolSelector.select(protocols);
                }
                catch (java.lang.Throwable t) {
                    io.netty.util.internal.PlatformDependent.throwException((java.lang.Throwable)t);
                    return null;
                }
            }
        });
    }

    @Override
    public void closeInbound() throws SSLException {
        NextProtoNego.remove((SSLEngine)this.getWrappedEngine());
        super.closeInbound();
    }

    @Override
    public void closeOutbound() {
        NextProtoNego.remove((SSLEngine)this.getWrappedEngine());
        super.closeOutbound();
    }
}

