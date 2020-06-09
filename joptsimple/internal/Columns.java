/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple.internal;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import joptsimple.internal.Row;
import joptsimple.internal.Strings;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class Columns {
    private static final int INDENT_WIDTH = 2;
    private final int optionWidth;
    private final int descriptionWidth;

    Columns(int optionWidth, int descriptionWidth) {
        this.optionWidth = optionWidth;
        this.descriptionWidth = descriptionWidth;
    }

    List<Row> fit(Row row) {
        List<String> options = this.piecesOf((String)row.option, (int)this.optionWidth);
        List<String> descriptions = this.piecesOf((String)row.description, (int)this.descriptionWidth);
        ArrayList<Row> rows = new ArrayList<Row>();
        int i = 0;
        while (i < Math.max((int)options.size(), (int)descriptions.size())) {
            rows.add((Row)new Row((String)Columns.itemOrEmpty(options, (int)i), (String)Columns.itemOrEmpty(descriptions, (int)i)));
            ++i;
        }
        return rows;
    }

    private static String itemOrEmpty(List<String> items, int index) {
        if (index >= items.size()) {
            return "";
        }
        String string = items.get((int)index);
        return string;
    }

    private List<String> piecesOf(String raw, int width) {
        ArrayList<String> pieces = new ArrayList<String>();
        String[] arr$ = raw.trim().split((String)Strings.LINE_SEPARATOR);
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            String each = arr$[i$];
            pieces.addAll(this.piecesOfEmbeddedLine((String)each, (int)width));
            ++i$;
        }
        return pieces;
    }

    private List<String> piecesOfEmbeddedLine(String line, int width) {
        ArrayList<String> pieces = new ArrayList<String>();
        BreakIterator words = BreakIterator.getLineInstance((Locale)Locale.US);
        words.setText((String)line);
        StringBuilder nextPiece = new StringBuilder();
        int start = words.first();
        int end = words.next();
        do {
            if (end == -1) {
                if (nextPiece.length() <= 0) return pieces;
                pieces.add((String)nextPiece.toString());
                return pieces;
            }
            nextPiece = this.processNextWord((String)line, (StringBuilder)nextPiece, (int)start, (int)end, (int)width, pieces);
            start = end;
            end = words.next();
        } while (true);
    }

    private StringBuilder processNextWord(String source, StringBuilder nextPiece, int start, int end, int width, List<String> pieces) {
        StringBuilder augmented = nextPiece;
        String word = source.substring((int)start, (int)end);
        if (augmented.length() + word.length() > width) {
            pieces.add((String)augmented.toString().replaceAll((String)"\\s+$", (String)""));
            return new StringBuilder((String)Strings.repeat((char)' ', (int)2)).append((String)word);
        }
        augmented.append((String)word);
        return augmented;
    }
}

