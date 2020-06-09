/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.html;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;

@Beta
@GwtCompatible
public final class HtmlEscapers {
    private static final Escaper HTML_ESCAPER = Escapers.builder().addEscape((char)'\"', (String)"&quot;").addEscape((char)'\'', (String)"&#39;").addEscape((char)'&', (String)"&amp;").addEscape((char)'<', (String)"&lt;").addEscape((char)'>', (String)"&gt;").build();

    public static Escaper htmlEscaper() {
        return HTML_ESCAPER;
    }

    private HtmlEscapers() {
    }
}

