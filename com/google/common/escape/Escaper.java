/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.escape;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.escape.Escaper;

@GwtCompatible
public abstract class Escaper {
    private final Function<String, String> asFunction = new Function<String, String>((Escaper)this){
        final /* synthetic */ Escaper this$0;
        {
            this.this$0 = escaper;
        }

        public String apply(String from) {
            return this.this$0.escape((String)from);
        }
    };

    protected Escaper() {
    }

    public abstract String escape(String var1);

    public final Function<String, String> asFunction() {
        return this.asFunction;
    }
}

