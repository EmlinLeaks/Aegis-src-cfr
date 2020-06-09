/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.HttpMethod;
import java.util.HashMap;
import java.util.Map;

public final class RtspMethods {
    public static final HttpMethod OPTIONS = HttpMethod.OPTIONS;
    public static final HttpMethod DESCRIBE = HttpMethod.valueOf((String)"DESCRIBE");
    public static final HttpMethod ANNOUNCE = HttpMethod.valueOf((String)"ANNOUNCE");
    public static final HttpMethod SETUP = HttpMethod.valueOf((String)"SETUP");
    public static final HttpMethod PLAY = HttpMethod.valueOf((String)"PLAY");
    public static final HttpMethod PAUSE = HttpMethod.valueOf((String)"PAUSE");
    public static final HttpMethod TEARDOWN = HttpMethod.valueOf((String)"TEARDOWN");
    public static final HttpMethod GET_PARAMETER = HttpMethod.valueOf((String)"GET_PARAMETER");
    public static final HttpMethod SET_PARAMETER = HttpMethod.valueOf((String)"SET_PARAMETER");
    public static final HttpMethod REDIRECT = HttpMethod.valueOf((String)"REDIRECT");
    public static final HttpMethod RECORD = HttpMethod.valueOf((String)"RECORD");
    private static final Map<String, HttpMethod> methodMap = new HashMap<String, HttpMethod>();

    public static HttpMethod valueOf(String name) {
        if (name == null) {
            throw new NullPointerException((String)"name");
        }
        if ((name = name.trim().toUpperCase()).isEmpty()) {
            throw new IllegalArgumentException((String)"empty name");
        }
        HttpMethod result = methodMap.get((Object)name);
        if (result == null) return HttpMethod.valueOf((String)name);
        return result;
    }

    private RtspMethods() {
    }

    static {
        methodMap.put((String)DESCRIBE.toString(), (HttpMethod)DESCRIBE);
        methodMap.put((String)ANNOUNCE.toString(), (HttpMethod)ANNOUNCE);
        methodMap.put((String)GET_PARAMETER.toString(), (HttpMethod)GET_PARAMETER);
        methodMap.put((String)OPTIONS.toString(), (HttpMethod)OPTIONS);
        methodMap.put((String)PAUSE.toString(), (HttpMethod)PAUSE);
        methodMap.put((String)PLAY.toString(), (HttpMethod)PLAY);
        methodMap.put((String)RECORD.toString(), (HttpMethod)RECORD);
        methodMap.put((String)REDIRECT.toString(), (HttpMethod)REDIRECT);
        methodMap.put((String)SETUP.toString(), (HttpMethod)SETUP);
        methodMap.put((String)SET_PARAMETER.toString(), (HttpMethod)SET_PARAMETER);
        methodMap.put((String)TEARDOWN.toString(), (HttpMethod)TEARDOWN);
    }
}

