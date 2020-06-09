/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.List;

public final class ApplicationProtocolConfig {
    public static final ApplicationProtocolConfig DISABLED = new ApplicationProtocolConfig();
    private final List<String> supportedProtocols;
    private final Protocol protocol;
    private final SelectorFailureBehavior selectorBehavior;
    private final SelectedListenerFailureBehavior selectedBehavior;

    public ApplicationProtocolConfig(Protocol protocol, SelectorFailureBehavior selectorBehavior, SelectedListenerFailureBehavior selectedBehavior, Iterable<String> supportedProtocols) {
        this((Protocol)protocol, (SelectorFailureBehavior)selectorBehavior, (SelectedListenerFailureBehavior)selectedBehavior, ApplicationProtocolUtil.toList(supportedProtocols));
    }

    public ApplicationProtocolConfig(Protocol protocol, SelectorFailureBehavior selectorBehavior, SelectedListenerFailureBehavior selectedBehavior, String ... supportedProtocols) {
        this((Protocol)protocol, (SelectorFailureBehavior)selectorBehavior, (SelectedListenerFailureBehavior)selectedBehavior, ApplicationProtocolUtil.toList((String[])supportedProtocols));
    }

    private ApplicationProtocolConfig(Protocol protocol, SelectorFailureBehavior selectorBehavior, SelectedListenerFailureBehavior selectedBehavior, List<String> supportedProtocols) {
        this.supportedProtocols = Collections.unmodifiableList(ObjectUtil.checkNotNull(supportedProtocols, (String)"supportedProtocols"));
        this.protocol = ObjectUtil.checkNotNull(protocol, (String)"protocol");
        this.selectorBehavior = ObjectUtil.checkNotNull(selectorBehavior, (String)"selectorBehavior");
        this.selectedBehavior = ObjectUtil.checkNotNull(selectedBehavior, (String)"selectedBehavior");
        if (protocol == Protocol.NONE) {
            throw new IllegalArgumentException((String)("protocol (" + (Object)((Object)Protocol.NONE) + ") must not be " + (Object)((Object)Protocol.NONE) + '.'));
        }
        if (!supportedProtocols.isEmpty()) return;
        throw new IllegalArgumentException((String)"supportedProtocols must be not empty");
    }

    private ApplicationProtocolConfig() {
        this.supportedProtocols = Collections.emptyList();
        this.protocol = Protocol.NONE;
        this.selectorBehavior = SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL;
        this.selectedBehavior = SelectedListenerFailureBehavior.ACCEPT;
    }

    public List<String> supportedProtocols() {
        return this.supportedProtocols;
    }

    public Protocol protocol() {
        return this.protocol;
    }

    public SelectorFailureBehavior selectorFailureBehavior() {
        return this.selectorBehavior;
    }

    public SelectedListenerFailureBehavior selectedListenerFailureBehavior() {
        return this.selectedBehavior;
    }
}

