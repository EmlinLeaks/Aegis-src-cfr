/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import jline.console.KeyMap;
import jline.console.Operation;
import jline.internal.Log;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ConsoleKeys {
    private KeyMap keys;
    private Map<String, KeyMap> keyMaps = KeyMap.keyMaps();
    private Map<String, String> variables = new HashMap<String, String>();

    public ConsoleKeys(String appName, URL inputrcUrl) {
        this.loadKeys((String)appName, (URL)inputrcUrl);
    }

    protected boolean isViEditMode() {
        return this.keys.isViKeyMap();
    }

    protected boolean setKeyMap(String name) {
        KeyMap map = this.keyMaps.get((Object)name);
        if (map == null) {
            return false;
        }
        this.keys = map;
        return true;
    }

    protected Map<String, KeyMap> getKeyMaps() {
        return this.keyMaps;
    }

    protected KeyMap getKeys() {
        return this.keys;
    }

    protected void setKeys(KeyMap keys) {
        this.keys = keys;
    }

    protected boolean getViEditMode() {
        return this.keys.isViKeyMap();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void loadKeys(String appName, URL inputrcUrl) {
        this.keys = this.keyMaps.get((Object)"emacs");
        try {
            InputStream input = inputrcUrl.openStream();
            try {
                this.loadKeys((InputStream)input, (String)appName);
                Log.debug((Object[])new Object[]{"Loaded user configuration: ", inputrcUrl});
                return;
            }
            finally {
                try {
                    input.close();
                }
                catch (IOException e) {}
            }
        }
        catch (IOException e) {
            if (inputrcUrl.getProtocol().equals((Object)"file")) {
                File file = new File((String)inputrcUrl.getPath());
                if (!file.exists()) return;
                Log.warn((Object[])new Object[]{"Unable to read user configuration: ", inputrcUrl, e});
                return;
            }
            Log.warn((Object[])new Object[]{"Unable to read user configuration: ", inputrcUrl, e});
        }
    }

    private void loadKeys(InputStream input, String appName) throws IOException {
        String line;
        BufferedReader reader = new BufferedReader((Reader)new InputStreamReader((InputStream)input));
        boolean parsing = true;
        ArrayList<Boolean> ifsStack = new ArrayList<Boolean>();
        while ((line = reader.readLine()) != null) {
            try {
                String val;
                if ((line = line.trim()).length() == 0 || line.charAt((int)0) == '#') continue;
                int i = 0;
                if (line.charAt((int)i) == '$') {
                    ++i;
                    while (i < line.length() && (line.charAt((int)i) == ' ' || line.charAt((int)i) == '\t')) {
                        ++i;
                    }
                    int s = i;
                    while (i < line.length() && line.charAt((int)i) != ' ' && line.charAt((int)i) != '\t') {
                        ++i;
                    }
                    String cmd = line.substring((int)s, (int)i);
                    while (i < line.length() && (line.charAt((int)i) == ' ' || line.charAt((int)i) == '\t')) {
                        ++i;
                    }
                    s = i;
                    while (i < line.length() && line.charAt((int)i) != ' ' && line.charAt((int)i) != '\t') {
                        ++i;
                    }
                    String args = line.substring((int)s, (int)i);
                    if ("if".equalsIgnoreCase((String)cmd)) {
                        ifsStack.add(Boolean.valueOf((boolean)parsing));
                        if (!parsing || args.startsWith((String)"term=")) continue;
                        if (args.startsWith((String)"mode=")) {
                            if (args.equalsIgnoreCase((String)"mode=vi")) {
                                parsing = this.isViEditMode();
                                continue;
                            }
                            if (args.equals((Object)"mode=emacs")) {
                                parsing = !this.isViEditMode();
                                continue;
                            }
                            parsing = false;
                            continue;
                        }
                        parsing = args.equalsIgnoreCase((String)appName);
                        continue;
                    }
                    if ("else".equalsIgnoreCase((String)cmd)) {
                        if (ifsStack.isEmpty()) {
                            throw new IllegalArgumentException((String)"$else found without matching $if");
                        }
                        boolean invert = true;
                        Iterator<E> i$ = ifsStack.iterator();
                        while (i$.hasNext()) {
                            boolean b = ((Boolean)i$.next()).booleanValue();
                            if (b) continue;
                            invert = false;
                            break;
                        }
                        if (!invert) continue;
                        parsing = !parsing;
                        continue;
                    }
                    if ("endif".equalsIgnoreCase((String)cmd)) {
                        if (ifsStack.isEmpty()) {
                            throw new IllegalArgumentException((String)"endif found without matching $if");
                        }
                        parsing = ((Boolean)ifsStack.remove((int)(ifsStack.size() - 1))).booleanValue();
                        continue;
                    }
                    if ("include".equalsIgnoreCase((String)cmd)) continue;
                    continue;
                }
                if (!parsing) continue;
                String keySeq = "";
                if (line.charAt((int)i++) == '\"') {
                    boolean esc = false;
                    do {
                        if (i >= line.length()) {
                            throw new IllegalArgumentException((String)("Missing closing quote on line '" + line + "'"));
                        }
                        if (esc) {
                            esc = false;
                        } else if (line.charAt((int)i) == '\\') {
                            esc = true;
                        } else if (line.charAt((int)i) == '\"') break;
                        ++i;
                    } while (true);
                }
                while (i < line.length() && line.charAt((int)i) != ':' && line.charAt((int)i) != ' ' && line.charAt((int)i) != '\t') {
                    ++i;
                }
                keySeq = line.substring((int)0, (int)i);
                boolean equivalency = i + 1 < line.length() && line.charAt((int)i) == ':' && line.charAt((int)(i + 1)) == '=';
                ++i;
                if (equivalency) {
                    ++i;
                }
                if (keySeq.equalsIgnoreCase((String)"set")) {
                    while (i < line.length() && (line.charAt((int)i) == ' ' || line.charAt((int)i) == '\t')) {
                        ++i;
                    }
                    int s = i;
                    while (i < line.length() && line.charAt((int)i) != ' ' && line.charAt((int)i) != '\t') {
                        ++i;
                    }
                    String key = line.substring((int)s, (int)i);
                    while (i < line.length() && (line.charAt((int)i) == ' ' || line.charAt((int)i) == '\t')) {
                        ++i;
                    }
                    s = i;
                    while (i < line.length() && line.charAt((int)i) != ' ' && line.charAt((int)i) != '\t') {
                        ++i;
                    }
                    val = line.substring((int)s, (int)i);
                    this.setVar((String)key, (String)val);
                    continue;
                }
                while (i < line.length() && (line.charAt((int)i) == ' ' || line.charAt((int)i) == '\t')) {
                    ++i;
                }
                int start = i;
                if (i < line.length() && (line.charAt((int)i) == '\'' || line.charAt((int)i) == '\"')) {
                    char delim = line.charAt((int)i++);
                    boolean esc = false;
                    while (i < line.length()) {
                        if (esc) {
                            esc = false;
                        } else if (line.charAt((int)i) == '\\') {
                            esc = true;
                        } else if (line.charAt((int)i) == delim) break;
                        ++i;
                    }
                }
                while (i < line.length() && line.charAt((int)i) != ' ' && line.charAt((int)i) != '\t') {
                    ++i;
                }
                val = line.substring((int)Math.min((int)start, (int)line.length()), (int)Math.min((int)i, (int)line.length()));
                if (keySeq.charAt((int)0) == '\"') {
                    keySeq = this.translateQuoted((String)keySeq);
                } else {
                    String keyName = keySeq.lastIndexOf((int)45) > 0 ? keySeq.substring((int)(keySeq.lastIndexOf((int)45) + 1)) : keySeq;
                    char key = this.getKeyFromName((String)keyName);
                    keyName = keySeq.toLowerCase();
                    keySeq = "";
                    if (keyName.contains((CharSequence)"meta-") || keyName.contains((CharSequence)"m-")) {
                        keySeq = keySeq + "\u001b";
                    }
                    if (keyName.contains((CharSequence)"control-") || keyName.contains((CharSequence)"c-") || keyName.contains((CharSequence)"ctrl-")) {
                        key = (char)(Character.toUpperCase((char)key) & 31);
                    }
                    keySeq = keySeq + key;
                }
                if (val.length() > 0 && (val.charAt((int)0) == '\'' || val.charAt((int)0) == '\"')) {
                    this.keys.bind((CharSequence)keySeq, (Object)this.translateQuoted((String)val));
                    continue;
                }
                String operationName = val.replace((char)'-', (char)'_').toUpperCase();
                try {
                    this.keys.bind((CharSequence)keySeq, (Object)((Object)Operation.valueOf((String)operationName)));
                }
                catch (IllegalArgumentException e) {
                    Log.info((Object[])new Object[]{"Unable to bind key for unsupported operation: ", val});
                }
            }
            catch (IllegalArgumentException e) {
                Log.warn((Object[])new Object[]{"Unable to parse user configuration: ", e});
            }
        }
    }

    private String translateQuoted(String keySeq) {
        String str = keySeq.substring((int)1, (int)(keySeq.length() - 1));
        keySeq = "";
        int i = 0;
        while (i < str.length()) {
            char c = str.charAt((int)i);
            if (c == '\\') {
                boolean meta;
                boolean ctrl = str.regionMatches((int)i, (String)"\\C-", (int)0, (int)3) || str.regionMatches((int)i, (String)"\\M-\\C-", (int)0, (int)6);
                if ((i += ((meta = str.regionMatches((int)i, (String)"\\M-", (int)0, (int)3) || str.regionMatches((int)i, (String)"\\C-\\M-", (int)0, (int)6)) ? 3 : 0) + (ctrl ? 3 : 0) + (!meta && !ctrl ? 1 : 0)) >= str.length()) {
                    return keySeq;
                }
                c = str.charAt((int)i);
                if (meta) {
                    keySeq = keySeq + "\u001b";
                }
                if (ctrl) {
                    char c2 = c = c == '?' ? (char)'' : (char)(Character.toUpperCase((char)c) & 31);
                }
                if (!meta && !ctrl) {
                    switch (c) {
                        case 'a': {
                            c = '\u0007';
                            break;
                        }
                        case 'b': {
                            c = '\b';
                            break;
                        }
                        case 'd': {
                            c = '';
                            break;
                        }
                        case 'e': {
                            c = '\u001b';
                            break;
                        }
                        case 'f': {
                            c = '\f';
                            break;
                        }
                        case 'n': {
                            c = '\n';
                            break;
                        }
                        case 'r': {
                            c = '\r';
                            break;
                        }
                        case 't': {
                            c = '\t';
                            break;
                        }
                        case 'v': {
                            c = '\u000b';
                            break;
                        }
                        case '\\': {
                            c = '\\';
                            break;
                        }
                        case '0': 
                        case '1': 
                        case '2': 
                        case '3': 
                        case '4': 
                        case '5': 
                        case '6': 
                        case '7': {
                            int j;
                            int k;
                            c = '\u0000';
                            for (j = 0; j < 3 && i < str.length() && (k = Character.digit((char)str.charAt((int)i), (int)8)) >= 0; ++j, ++i) {
                                c = (char)(c * 8 + k);
                            }
                            c = (char)(c & 255);
                            break;
                        }
                        case 'x': {
                            int j;
                            int k;
                            ++i;
                            c = '\u0000';
                            for (j = 0; j < 2 && i < str.length() && (k = Character.digit((char)str.charAt((int)i), (int)16)) >= 0; ++j, ++i) {
                                c = (char)(c * 16 + k);
                            }
                            c = (char)(c & 255);
                            break;
                        }
                        case 'u': {
                            int j;
                            int k;
                            ++i;
                            c = '\u0000';
                            for (j = 0; j < 4 && i < str.length() && (k = Character.digit((char)str.charAt((int)i), (int)16)) >= 0; ++j, ++i) {
                                c = (char)(c * 16 + k);
                            }
                            break;
                        }
                    }
                }
                keySeq = keySeq + c;
            } else {
                keySeq = keySeq + c;
            }
            ++i;
        }
        return keySeq;
    }

    private char getKeyFromName(String name) {
        if ("DEL".equalsIgnoreCase((String)name)) return '';
        if ("Rubout".equalsIgnoreCase((String)name)) {
            return '';
        }
        if ("ESC".equalsIgnoreCase((String)name)) return '\u001b';
        if ("Escape".equalsIgnoreCase((String)name)) {
            return '\u001b';
        }
        if ("LFD".equalsIgnoreCase((String)name)) return '\n';
        if ("NewLine".equalsIgnoreCase((String)name)) {
            return '\n';
        }
        if ("RET".equalsIgnoreCase((String)name)) return '\r';
        if ("Return".equalsIgnoreCase((String)name)) {
            return '\r';
        }
        if ("SPC".equalsIgnoreCase((String)name)) return ' ';
        if ("Space".equalsIgnoreCase((String)name)) {
            return ' ';
        }
        if (!"Tab".equalsIgnoreCase((String)name)) return name.charAt((int)0);
        return '\t';
    }

    private void setVar(String key, String val) {
        if ("keymap".equalsIgnoreCase((String)key)) {
            if (this.keyMaps.containsKey((Object)val)) {
                this.keys = this.keyMaps.get((Object)val);
            }
        } else if ("editing-mode".equals((Object)key)) {
            if ("vi".equalsIgnoreCase((String)val)) {
                this.keys = this.keyMaps.get((Object)"vi-insert");
            } else if ("emacs".equalsIgnoreCase((String)key)) {
                this.keys = this.keyMaps.get((Object)"emacs");
            }
        } else if ("blink-matching-paren".equals((Object)key)) {
            if ("on".equalsIgnoreCase((String)val)) {
                this.keys.setBlinkMatchingParen((boolean)true);
            } else if ("off".equalsIgnoreCase((String)val)) {
                this.keys.setBlinkMatchingParen((boolean)false);
            }
        }
        this.variables.put((String)key, (String)val);
    }

    public String getVariable(String var) {
        return this.variables.get((Object)var);
    }
}

