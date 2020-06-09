/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.util.AsciiString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WebSocketExtensionUtil {
    private static final String EXTENSION_SEPARATOR = ",";
    private static final String PARAMETER_SEPARATOR = ";";
    private static final char PARAMETER_EQUAL = '=';
    private static final Pattern PARAMETER = Pattern.compile((String)"^([^=]+)(=[\\\"]?([^\\\"]+)[\\\"]?)?$");

    static boolean isWebsocketUpgrade(HttpHeaders headers) {
        if (!headers.containsValue((CharSequence)HttpHeaderNames.CONNECTION, (CharSequence)HttpHeaderValues.UPGRADE, (boolean)true)) return false;
        if (!headers.contains((CharSequence)HttpHeaderNames.UPGRADE, (CharSequence)HttpHeaderValues.WEBSOCKET, (boolean)true)) return false;
        return true;
    }

    public static List<WebSocketExtensionData> extractExtensions(String extensionHeader) {
        String[] rawExtensions = extensionHeader.split((String)EXTENSION_SEPARATOR);
        if (rawExtensions.length <= 0) return Collections.emptyList();
        ArrayList<WebSocketExtensionData> extensions = new ArrayList<WebSocketExtensionData>((int)rawExtensions.length);
        String[] arrstring = rawExtensions;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            Map<String, String> parameters;
            String rawExtension = arrstring[n2];
            String[] extensionParameters = rawExtension.split((String)PARAMETER_SEPARATOR);
            String name = extensionParameters[0].trim();
            if (extensionParameters.length > 1) {
                parameters = new HashMap<K, V>((int)(extensionParameters.length - 1));
                for (int i = 1; i < extensionParameters.length; ++i) {
                    String parameter = extensionParameters[i].trim();
                    Matcher parameterMatcher = PARAMETER.matcher((CharSequence)parameter);
                    if (!parameterMatcher.matches() || parameterMatcher.group((int)1) == null) continue;
                    parameters.put((String)parameterMatcher.group((int)1), (String)parameterMatcher.group((int)3));
                }
            } else {
                parameters = Collections.emptyMap();
            }
            extensions.add((WebSocketExtensionData)new WebSocketExtensionData((String)name, parameters));
            ++n2;
        }
        return extensions;
    }

    static String appendExtension(String currentHeaderValue, String extensionName, Map<String, String> extensionParameters) {
        StringBuilder newHeaderValue = new StringBuilder((int)(currentHeaderValue != null ? currentHeaderValue.length() : extensionName.length() + 1));
        if (currentHeaderValue != null && !currentHeaderValue.trim().isEmpty()) {
            newHeaderValue.append((String)currentHeaderValue);
            newHeaderValue.append((String)EXTENSION_SEPARATOR);
        }
        newHeaderValue.append((String)extensionName);
        Iterator<Map.Entry<String, String>> iterator = extensionParameters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> extensionParameter = iterator.next();
            newHeaderValue.append((String)PARAMETER_SEPARATOR);
            newHeaderValue.append((String)extensionParameter.getKey());
            if (extensionParameter.getValue() == null) continue;
            newHeaderValue.append((char)'=');
            newHeaderValue.append((String)extensionParameter.getValue());
        }
        return newHeaderValue.toString();
    }

    private WebSocketExtensionUtil() {
    }
}

