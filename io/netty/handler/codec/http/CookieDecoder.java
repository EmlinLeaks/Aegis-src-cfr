/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieUtil;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Deprecated
public final class CookieDecoder {
    private final InternalLogger logger = InternalLoggerFactory.getInstance(this.getClass());
    private static final CookieDecoder STRICT = new CookieDecoder((boolean)true);
    private static final CookieDecoder LAX = new CookieDecoder((boolean)false);
    private static final String COMMENT = "Comment";
    private static final String COMMENTURL = "CommentURL";
    private static final String DISCARD = "Discard";
    private static final String PORT = "Port";
    private static final String VERSION = "Version";
    private final boolean strict;

    public static Set<Cookie> decode(String header) {
        return CookieDecoder.decode((String)header, (boolean)true);
    }

    public static Set<Cookie> decode(String header, boolean strict) {
        CookieDecoder cookieDecoder;
        if (strict) {
            cookieDecoder = STRICT;
            return cookieDecoder.doDecode((String)header);
        }
        cookieDecoder = LAX;
        return cookieDecoder.doDecode((String)header);
    }

    /*
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    private Set<Cookie> doDecode(String header) {
        names = new ArrayList<String>((int)8);
        values = new ArrayList<String>((int)8);
        CookieDecoder.extractKeyValuePairs((String)header, names, values);
        if (names.isEmpty()) {
            return Collections.emptySet();
        }
        version = 0;
        if (((String)names.get((int)0)).equalsIgnoreCase((String)"Version")) {
            try {
                version = Integer.parseInt((String)((String)values.get((int)0)));
            }
            catch (NumberFormatException var6_5) {
                // empty catch block
            }
            i = 1;
        } else {
            i = 0;
        }
        if (names.size() <= i) {
            return Collections.emptySet();
        }
        cookies = new TreeSet<Cookie>();
        block4 : do {
            if (i >= names.size()) return cookies;
            name = (String)names.get((int)i);
            value = (String)values.get((int)i);
            if (value == null) {
                value = "";
            }
            if ((c = this.initCookie((String)name, (String)value)) == null) {
                return cookies;
            }
            discard = false;
            secure = false;
            httpOnly = false;
            comment = null;
            commentURL = null;
            domain = null;
            path = null;
            maxAge = Long.MIN_VALUE;
            ports = new ArrayList<Integer>((int)2);
            j = i + 1;
            do {
                if (j >= names.size()) ** GOTO lbl-1000
                name = (String)names.get((int)j);
                value = (String)values.get((int)j);
                if ("Discard".equalsIgnoreCase((String)name)) {
                    discard = true;
                } else if ("Secure".equalsIgnoreCase((String)name)) {
                    secure = true;
                } else if ("HTTPOnly".equalsIgnoreCase((String)name)) {
                    httpOnly = true;
                } else if ("Comment".equalsIgnoreCase((String)name)) {
                    comment = value;
                } else if ("CommentURL".equalsIgnoreCase((String)name)) {
                    commentURL = value;
                } else if ("Domain".equalsIgnoreCase((String)name)) {
                    domain = value;
                } else if ("Path".equalsIgnoreCase((String)name)) {
                    path = value;
                } else if ("Expires".equalsIgnoreCase((String)name)) {
                    date = DateFormatter.parseHttpDate((CharSequence)value);
                    if (date != null) {
                        maxAge = maxAgeMillis / 1000L + (long)((maxAgeMillis = date.getTime() - System.currentTimeMillis()) % 1000L != 0L ? 1 : 0);
                    }
                } else if ("Max-Age".equalsIgnoreCase((String)name)) {
                    maxAge = (long)Integer.parseInt((String)value);
                } else if ("Version".equalsIgnoreCase((String)name)) {
                    version = Integer.parseInt((String)value);
                } else {
                    if ("Port".equalsIgnoreCase((String)name)) {
                        maxAgeMillis = portList = value.split((String)",");
                        var23_24 = maxAgeMillis.length;
                    } else lbl-1000: // 2 sources:
                    {
                        c.setVersion((int)version);
                        c.setMaxAge((long)maxAge);
                        c.setPath(path);
                        c.setDomain(domain);
                        c.setSecure((boolean)secure);
                        c.setHttpOnly((boolean)httpOnly);
                        if (version > 0) {
                            c.setComment(comment);
                        }
                        if (version > 1) {
                            c.setCommentUrl(commentURL);
                            c.setPorts(ports);
                            c.setDiscard((boolean)discard);
                        }
                        cookies.add((Cookie)c);
                        ++i;
                        continue block4;
                    }
                    for (var24_25 = 0; var24_25 < var23_24; ++var24_25) {
                        s1 = maxAgeMillis[var24_25];
                        try {
                            ports.add((Integer)Integer.valueOf((String)s1));
                            continue;
                        }
                        catch (NumberFormatException var26_27) {
                            // empty catch block
                        }
                    }
                }
                ++j;
                ++i;
            } while (true);
            break;
        } while (true);
    }

    /*
     * Unable to fully structure code
     */
    private static void extractKeyValuePairs(String header, List<String> names, List<String> values) {
        headerLen = header.length();
        i = 0;
        block10 : do {
            if (i == headerLen) {
                return;
            }
            switch (header.charAt((int)i)) {
                case '\t': 
                case '\n': 
                case '\u000b': 
                case '\f': 
                case '\r': 
                case ' ': 
                case ',': 
                case ';': {
                    ++i;
                    continue block10;
                }
            }
            do {
                if (i == headerLen) {
                    return;
                }
                if (header.charAt((int)i) != '$') break;
                ++i;
            } while (true);
            if (i == headerLen) {
                name = null;
                value = null;
            } else {
                newNameStart = i;
                block12 : do {
                    switch (header.charAt((int)i)) {
                        case ';': {
                            name = header.substring((int)newNameStart, (int)i);
                            value = null;
                            ** break;
                        }
                        case '=': {
                            name = header.substring((int)newNameStart, (int)i);
                            if (++i == headerLen) {
                                value = "";
                                ** break;
                            }
                            newValueStart = i;
                            c = header.charAt((int)i);
                            if (c == '\"' || c == '\'') {
                                newValueBuf = new StringBuilder((int)(header.length() - i));
                                q = c;
                                hadBackslash = false;
                                ++i;
                                break block12;
                            }
                            semiPos = header.indexOf((int)59, (int)i);
                            if (semiPos > 0) {
                                value = header.substring((int)newValueStart, (int)semiPos);
                                i = semiPos;
                                ** break;
                            }
                            value = header.substring((int)newValueStart);
                            i = headerLen;
                            ** break;
                        }
                        default: {
                            if (++i != headerLen) continue block12;
                            name = header.substring((int)newNameStart);
                            value = null;
                            ** break;
                        }
                    }
                    break;
                } while (true);
                block13 : do {
                    if (i == headerLen) {
                        value = newValueBuf.toString();
                        break;
                    }
                    if (hadBackslash) {
                        hadBackslash = false;
                        c = header.charAt((int)i++);
                        switch (c) {
                            case '\"': 
                            case '\'': 
                            case '\\': {
                                newValueBuf.setCharAt((int)(newValueBuf.length() - 1), (char)c);
                                continue block13;
                            }
                        }
                        newValueBuf.append((char)c);
                        continue;
                    }
                    if ((c = header.charAt((int)i++)) == q) {
                        value = newValueBuf.toString();
                        break;
                    }
                    newValueBuf.append((char)c);
                    if (c != '\\') continue;
                    hadBackslash = true;
                } while (true);
            }
lbl75: // 8 sources:
            names.add(name);
            values.add(value);
        } while (true);
    }

    private CookieDecoder(boolean strict) {
        this.strict = strict;
    }

    private DefaultCookie initCookie(String name, String value) {
        int invalidOctetPos;
        boolean wrap;
        if (name == null || name.length() == 0) {
            this.logger.debug((String)"Skipping cookie with null name");
            return null;
        }
        if (value == null) {
            this.logger.debug((String)"Skipping cookie with null value");
            return null;
        }
        CharSequence unwrappedValue = CookieUtil.unwrapValue((CharSequence)value);
        if (unwrappedValue == null) {
            this.logger.debug((String)"Skipping cookie because starting quotes are not properly balanced in '{}'", (Object)unwrappedValue);
            return null;
        }
        if (this.strict && (invalidOctetPos = CookieUtil.firstInvalidCookieNameOctet((CharSequence)name)) >= 0) {
            if (!this.logger.isDebugEnabled()) return null;
            this.logger.debug((String)"Skipping cookie because name '{}' contains invalid char '{}'", (Object)name, (Object)Character.valueOf((char)name.charAt((int)invalidOctetPos)));
            return null;
        }
        boolean bl = wrap = unwrappedValue.length() != value.length();
        if (this.strict && (invalidOctetPos = CookieUtil.firstInvalidCookieValueOctet((CharSequence)unwrappedValue)) >= 0) {
            if (!this.logger.isDebugEnabled()) return null;
            this.logger.debug((String)"Skipping cookie because value '{}' contains invalid char '{}'", (Object)unwrappedValue, (Object)Character.valueOf((char)unwrappedValue.charAt((int)invalidOctetPos)));
            return null;
        }
        DefaultCookie cookie = new DefaultCookie((String)name, (String)unwrappedValue.toString());
        cookie.setWrap((boolean)wrap);
        return cookie;
    }
}

