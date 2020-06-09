/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import joptsimple.internal.Columns;
import joptsimple.internal.Row;
import joptsimple.internal.Strings;

public class Rows {
    private final int overallWidth;
    private final int columnSeparatorWidth;
    private final List<Row> rows = new ArrayList<Row>();
    private int widthOfWidestOption;
    private int widthOfWidestDescription;

    public Rows(int overallWidth, int columnSeparatorWidth) {
        this.overallWidth = overallWidth;
        this.columnSeparatorWidth = columnSeparatorWidth;
    }

    public void add(String option, String description) {
        this.add((Row)new Row((String)option, (String)description));
    }

    private void add(Row row) {
        this.rows.add((Row)row);
        this.widthOfWidestOption = Math.max((int)this.widthOfWidestOption, (int)row.option.length());
        this.widthOfWidestDescription = Math.max((int)this.widthOfWidestDescription, (int)row.description.length());
    }

    private void reset() {
        this.rows.clear();
        this.widthOfWidestOption = 0;
        this.widthOfWidestDescription = 0;
    }

    public void fitToWidth() {
        Columns columns = new Columns((int)this.optionWidth(), (int)this.descriptionWidth());
        ArrayList<Row> fitted = new ArrayList<Row>();
        for (Row each : this.rows) {
            fitted.addAll(columns.fit((Row)each));
        }
        this.reset();
        Iterator<Row> i$ = fitted.iterator();
        while (i$.hasNext()) {
            Row each;
            each = i$.next();
            this.add((Row)each);
        }
    }

    public String render() {
        StringBuilder buffer = new StringBuilder();
        Iterator<Row> i$ = this.rows.iterator();
        while (i$.hasNext()) {
            Row each = i$.next();
            this.pad((StringBuilder)buffer, (String)each.option, (int)this.optionWidth()).append((String)Strings.repeat((char)' ', (int)this.columnSeparatorWidth));
            this.pad((StringBuilder)buffer, (String)each.description, (int)this.descriptionWidth()).append((String)Strings.LINE_SEPARATOR);
        }
        return buffer.toString();
    }

    private int optionWidth() {
        return Math.min((int)((this.overallWidth - this.columnSeparatorWidth) / 2), (int)this.widthOfWidestOption);
    }

    private int descriptionWidth() {
        return Math.min((int)((this.overallWidth - this.columnSeparatorWidth) / 2), (int)this.widthOfWidestDescription);
    }

    private StringBuilder pad(StringBuilder buffer, String s, int length) {
        buffer.append((String)s).append((String)Strings.repeat((char)' ', (int)(length - s.length())));
        return buffer;
    }
}

