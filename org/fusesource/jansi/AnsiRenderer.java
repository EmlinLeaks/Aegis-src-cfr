/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.fusesource.jansi;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiRenderer;

public class AnsiRenderer {
    public static final String BEGIN_TOKEN = "@|";
    private static final int BEGIN_TOKEN_LEN = 2;
    public static final String END_TOKEN = "|@";
    private static final int END_TOKEN_LEN = 2;
    public static final String CODE_TEXT_SEPARATOR = " ";
    public static final String CODE_LIST_SEPARATOR = ",";

    public static String render(String input) throws IllegalArgumentException {
        StringBuffer buff = new StringBuffer();
        int i = 0;
        do {
            int j;
            if ((j = input.indexOf((String)BEGIN_TOKEN, (int)i)) == -1) {
                if (i != 0) break;
                return input;
            }
            buff.append((String)input.substring((int)i, (int)j));
            int k = input.indexOf((String)END_TOKEN, (int)j);
            if (k == -1) {
                return input;
            }
            String spec = input.substring((int)(j += 2), (int)k);
            String[] items = spec.split((String)CODE_TEXT_SEPARATOR, (int)2);
            if (items.length == 1) {
                return input;
            }
            String replacement = AnsiRenderer.render((String)items[1], (String[])items[0].split((String)CODE_LIST_SEPARATOR));
            buff.append((String)replacement);
            i = k + 2;
        } while (true);
        buff.append((String)input.substring((int)i, (int)input.length()));
        return buff.toString();
    }

    private static String render(String text, String ... codes) {
        Ansi ansi = Ansi.ansi();
        String[] arr$ = codes;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            String name = arr$[i$];
            Code code = Code.valueOf((String)name.toUpperCase());
            if (code.isColor()) {
                ansi = code.isBackground() ? ansi.bg((Ansi.Color)code.getColor()) : ansi.fg((Ansi.Color)code.getColor());
            } else if (code.isAttribute()) {
                ansi = ansi.a((Ansi.Attribute)code.getAttribute());
            }
            ++i$;
        }
        return ansi.a((String)text).reset().toString();
    }

    public static boolean test(String text) {
        if (text == null) return false;
        if (!text.contains((CharSequence)BEGIN_TOKEN)) return false;
        return true;
    }
}

