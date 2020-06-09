/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.console.completer;

import java.util.Collection;
import jline.console.completer.StringsCompleter;
import jline.internal.Preconditions;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EnumCompleter
extends StringsCompleter {
    public EnumCompleter(Class<? extends Enum> source) {
        Preconditions.checkNotNull(source);
        Enum[] arr$ = source.getEnumConstants();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Enum n = arr$[i$];
            this.getStrings().add((String)n.name().toLowerCase());
            ++i$;
        }
    }
}

