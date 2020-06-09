/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.xml;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;

@Beta
@GwtCompatible
public class XmlEscapers {
    private static final char MIN_ASCII_CONTROL_CHAR = '\u0000';
    private static final char MAX_ASCII_CONTROL_CHAR = '\u001f';
    private static final Escaper XML_ESCAPER;
    private static final Escaper XML_CONTENT_ESCAPER;
    private static final Escaper XML_ATTRIBUTE_ESCAPER;

    private XmlEscapers() {
    }

    public static Escaper xmlContentEscaper() {
        return XML_CONTENT_ESCAPER;
    }

    public static Escaper xmlAttributeEscaper() {
        return XML_ATTRIBUTE_ESCAPER;
    }

    static {
        Escapers.Builder builder = Escapers.builder();
        builder.setSafeRange((char)'\u0000', (char)'\ufffd');
        builder.setUnsafeReplacement((String)"\ufffd");
        char c = '\u0000';
        do {
            if (c > '\u001f') {
                builder.addEscape((char)'&', (String)"&amp;");
                builder.addEscape((char)'<', (String)"&lt;");
                builder.addEscape((char)'>', (String)"&gt;");
                XML_CONTENT_ESCAPER = builder.build();
                builder.addEscape((char)'\'', (String)"&apos;");
                builder.addEscape((char)'\"', (String)"&quot;");
                XML_ESCAPER = builder.build();
                builder.addEscape((char)'\t', (String)"&#x9;");
                builder.addEscape((char)'\n', (String)"&#xA;");
                builder.addEscape((char)'\r', (String)"&#xD;");
                XML_ATTRIBUTE_ESCAPER = builder.build();
                return;
            }
            if (c != '\t' && c != '\n' && c != '\r') {
                builder.addEscape((char)c, (String)"\ufffd");
            }
            c = (char)((char)(c + 1));
        } while (true);
    }
}

