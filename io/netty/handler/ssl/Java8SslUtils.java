/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.util.internal.SuppressJava6Requirement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLParameters;

@SuppressJava6Requirement(reason="Usage guarded by java version check")
final class Java8SslUtils {
    private Java8SslUtils() {
    }

    static List<String> getSniHostNames(SSLParameters sslParameters) {
        List<SNIServerName> names = sslParameters.getServerNames();
        if (names == null) return Collections.emptyList();
        if (names.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<String> strings = new ArrayList<String>((int)names.size());
        Iterator<SNIServerName> iterator = names.iterator();
        while (iterator.hasNext()) {
            SNIServerName serverName = iterator.next();
            if (!(serverName instanceof SNIHostName)) throw new IllegalArgumentException((String)("Only " + SNIHostName.class.getName() + " instances are supported, but found: " + serverName));
            strings.add((String)((SNIHostName)serverName).getAsciiName());
        }
        return strings;
    }

    static void setSniHostNames(SSLParameters sslParameters, List<String> names) {
        sslParameters.setServerNames((List<SNIServerName>)Java8SslUtils.getSniHostNames(names));
    }

    static List getSniHostNames(List<String> names) {
        if (names == null) return Collections.emptyList();
        if (names.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<SNIHostName> sniServerNames = new ArrayList<SNIHostName>((int)names.size());
        Iterator<String> iterator = names.iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            sniServerNames.add(new SNIHostName((String)name));
        }
        return sniServerNames;
    }

    static List getSniHostName(byte[] hostname) {
        if (hostname == null) return Collections.emptyList();
        if (hostname.length != 0) return Collections.singletonList(new SNIHostName((byte[])hostname));
        return Collections.emptyList();
    }

    static boolean getUseCipherSuitesOrder(SSLParameters sslParameters) {
        return sslParameters.getUseCipherSuitesOrder();
    }

    static void setUseCipherSuitesOrder(SSLParameters sslParameters, boolean useOrder) {
        sslParameters.setUseCipherSuitesOrder((boolean)useOrder);
    }

    static void setSNIMatchers(SSLParameters sslParameters, Collection<?> matchers) {
        sslParameters.setSNIMatchers(matchers);
    }

    static boolean checkSniHostnameMatch(Collection<?> matchers, byte[] hostname) {
        SNIMatcher matcher;
        if (matchers == null) return true;
        if (matchers.isEmpty()) return true;
        SNIHostName name = new SNIHostName((byte[])hostname);
        Iterator<?> matcherIt = matchers.iterator();
        do {
            if (!matcherIt.hasNext()) return false;
        } while ((matcher = (SNIMatcher)matcherIt.next()).getType() != 0 || !matcher.matches((SNIServerName)name));
        return true;
    }
}

