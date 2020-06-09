/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;
import java.net.InetAddress;
import java.text.ParseException;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible
public final class HostSpecifier {
    private final String canonicalForm;

    private HostSpecifier(String canonicalForm) {
        this.canonicalForm = canonicalForm;
    }

    public static HostSpecifier fromValid(String specifier) {
        HostAndPort parsedHost = HostAndPort.fromString((String)specifier);
        Preconditions.checkArgument((boolean)(!parsedHost.hasPort()));
        String host = parsedHost.getHost();
        InetAddress addr = null;
        try {
            addr = InetAddresses.forString((String)host);
        }
        catch (IllegalArgumentException e) {
            // empty catch block
        }
        if (addr != null) {
            return new HostSpecifier((String)InetAddresses.toUriString((InetAddress)addr));
        }
        InternetDomainName domain = InternetDomainName.from((String)host);
        if (!domain.hasPublicSuffix()) throw new IllegalArgumentException((String)("Domain name does not have a recognized public suffix: " + host));
        return new HostSpecifier((String)domain.toString());
    }

    public static HostSpecifier from(String specifier) throws ParseException {
        try {
            return HostSpecifier.fromValid((String)specifier);
        }
        catch (IllegalArgumentException e) {
            ParseException parseException = new ParseException((String)("Invalid host specifier: " + specifier), (int)0);
            parseException.initCause((Throwable)e);
            throw parseException;
        }
    }

    public static boolean isValid(String specifier) {
        try {
            HostSpecifier.fromValid((String)specifier);
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HostSpecifier)) return false;
        HostSpecifier that = (HostSpecifier)other;
        return this.canonicalForm.equals((Object)that.canonicalForm);
    }

    public int hashCode() {
        return this.canonicalForm.hashCode();
    }

    public String toString() {
        return this.canonicalForm;
    }
}

