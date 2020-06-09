/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.api;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ChatColor {
    BLACK((char)'0', (String)"black"),
    DARK_BLUE((char)'1', (String)"dark_blue"),
    DARK_GREEN((char)'2', (String)"dark_green"),
    DARK_AQUA((char)'3', (String)"dark_aqua"),
    DARK_RED((char)'4', (String)"dark_red"),
    DARK_PURPLE((char)'5', (String)"dark_purple"),
    GOLD((char)'6', (String)"gold"),
    GRAY((char)'7', (String)"gray"),
    DARK_GRAY((char)'8', (String)"dark_gray"),
    BLUE((char)'9', (String)"blue"),
    GREEN((char)'a', (String)"green"),
    AQUA((char)'b', (String)"aqua"),
    RED((char)'c', (String)"red"),
    LIGHT_PURPLE((char)'d', (String)"light_purple"),
    YELLOW((char)'e', (String)"yellow"),
    WHITE((char)'f', (String)"white"),
    MAGIC((char)'k', (String)"obfuscated"),
    BOLD((char)'l', (String)"bold"),
    STRIKETHROUGH((char)'m', (String)"strikethrough"),
    UNDERLINE((char)'n', (String)"underline"),
    ITALIC((char)'o', (String)"italic"),
    RESET((char)'r', (String)"reset");
    
    public static final char COLOR_CHAR = '\u00a7';
    public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
    public static final Pattern STRIP_COLOR_PATTERN;
    private static final Map<Character, ChatColor> BY_CHAR;
    private final char code;
    private final String toString;
    private final String name;

    private ChatColor(char code, String name) {
        this.code = code;
        this.name = name;
        this.toString = new String((char[])new char[]{'\u00a7', code});
    }

    public String toString() {
        return this.toString;
    }

    public static String stripColor(String input) {
        if (input != null) return STRIP_COLOR_PATTERN.matcher((CharSequence)input).replaceAll((String)"");
        return null;
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        int i = 0;
        while (i < b.length - 1) {
            if (b[i] == altColorChar && ALL_CODES.indexOf((int)b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase((char)b[i + 1]);
            }
            ++i;
        }
        return new String((char[])b);
    }

    public static ChatColor getByChar(char code) {
        return BY_CHAR.get((Object)Character.valueOf((char)code));
    }

    public String getName() {
        return this.name;
    }

    static {
        STRIP_COLOR_PATTERN = Pattern.compile((String)("(?i)" + String.valueOf((char)'\u00a7') + "[0-9A-FK-OR]"));
        BY_CHAR = new HashMap<Character, ChatColor>();
        ChatColor[] arrchatColor = ChatColor.values();
        int n = arrchatColor.length;
        int n2 = 0;
        while (n2 < n) {
            ChatColor colour = arrchatColor[n2];
            BY_CHAR.put((Character)Character.valueOf((char)colour.code), (ChatColor)colour);
            ++n2;
        }
    }
}

