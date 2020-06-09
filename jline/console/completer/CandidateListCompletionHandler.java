/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.console.completer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import jline.console.ConsoleReader;
import jline.console.CursorBuffer;
import jline.console.completer.CandidateListCompletionHandler;
import jline.console.completer.CompletionHandler;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CandidateListCompletionHandler
implements CompletionHandler {
    @Override
    public boolean complete(ConsoleReader reader, List<CharSequence> candidates, int pos) throws IOException {
        CursorBuffer buf = reader.getCursorBuffer();
        if (candidates.size() == 1) {
            CharSequence value = candidates.get((int)0);
            if (value.equals((Object)buf.toString())) {
                return false;
            }
            CandidateListCompletionHandler.setBuffer((ConsoleReader)reader, (CharSequence)value, (int)pos);
            return true;
        }
        if (candidates.size() > 1) {
            String value = this.getUnambiguousCompletions(candidates);
            CandidateListCompletionHandler.setBuffer((ConsoleReader)reader, (CharSequence)value, (int)pos);
        }
        CandidateListCompletionHandler.printCandidates((ConsoleReader)reader, candidates);
        reader.drawLine();
        return true;
    }

    public static void setBuffer(ConsoleReader reader, CharSequence value, int offset) throws IOException {
        while (reader.getCursorBuffer().cursor > offset && reader.backspace()) {
        }
        reader.putString((CharSequence)value);
        reader.setCursorPosition((int)(offset + value.length()));
    }

    public static void printCandidates(ConsoleReader reader, Collection<CharSequence> candidates) throws IOException {
        HashSet<CharSequence> distinct = new HashSet<CharSequence>(candidates);
        if (distinct.size() > reader.getAutoprintThreshold()) {
            int c;
            reader.print((CharSequence)Messages.DISPLAY_CANDIDATES.format((Object[])new Object[]{Integer.valueOf((int)candidates.size())}));
            reader.flush();
            String noOpt = Messages.DISPLAY_CANDIDATES_NO.format((Object[])new Object[0]);
            String yesOpt = Messages.DISPLAY_CANDIDATES_YES.format((Object[])new Object[0]);
            char[] allowed = new char[]{yesOpt.charAt((int)0), noOpt.charAt((int)0)};
            while ((c = reader.readCharacter((char[])allowed)) != -1) {
                String tmp = new String((char[])new char[]{(char)c});
                if (noOpt.startsWith((String)tmp)) {
                    reader.println();
                    return;
                }
                if (yesOpt.startsWith((String)tmp)) break;
                reader.beep();
            }
        }
        if (distinct.size() != candidates.size()) {
            ArrayList<CharSequence> copy = new ArrayList<CharSequence>();
            for (CharSequence next : candidates) {
                if (copy.contains((Object)next)) continue;
                copy.add((CharSequence)next);
            }
            candidates = copy;
        }
        reader.println();
        reader.printColumns(candidates);
    }

    private String getUnambiguousCompletions(List<CharSequence> candidates) {
        if (candidates == null) return null;
        if (candidates.isEmpty()) {
            return null;
        }
        String[] strings = candidates.toArray(new String[candidates.size()]);
        String first = strings[0];
        StringBuilder candidate = new StringBuilder();
        int i = 0;
        while (i < first.length()) {
            if (!this.startsWith((String)first.substring((int)0, (int)(i + 1)), (String[])strings)) return candidate.toString();
            candidate.append((char)first.charAt((int)i));
            ++i;
        }
        return candidate.toString();
    }

    private boolean startsWith(String starts, String[] candidates) {
        String[] arr$ = candidates;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            String candidate = arr$[i$];
            if (!candidate.startsWith((String)starts)) {
                return false;
            }
            ++i$;
        }
        return true;
    }
}

