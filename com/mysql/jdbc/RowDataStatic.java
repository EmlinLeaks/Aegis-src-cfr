/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Field;
import com.mysql.jdbc.ResultSetImpl;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.ResultSetRow;
import com.mysql.jdbc.RowData;
import java.sql.SQLException;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RowDataStatic
implements RowData {
    private Field[] metadata;
    private int index = -1;
    ResultSetImpl owner;
    private List<ResultSetRow> rows;

    public RowDataStatic(List<ResultSetRow> rows) {
        this.rows = rows;
    }

    @Override
    public void addRow(ResultSetRow row) {
        this.rows.add((ResultSetRow)row);
    }

    @Override
    public void afterLast() {
        if (this.rows.size() <= 0) return;
        this.index = this.rows.size();
    }

    @Override
    public void beforeFirst() {
        if (this.rows.size() <= 0) return;
        this.index = -1;
    }

    @Override
    public void beforeLast() {
        if (this.rows.size() <= 0) return;
        this.index = this.rows.size() - 2;
    }

    @Override
    public void close() {
    }

    @Override
    public ResultSetRow getAt(int atIndex) throws SQLException {
        if (atIndex < 0) return null;
        if (atIndex < this.rows.size()) return this.rows.get((int)atIndex).setMetadata((Field[])this.metadata);
        return null;
    }

    @Override
    public int getCurrentRowNumber() {
        return this.index;
    }

    @Override
    public ResultSetInternalMethods getOwner() {
        return this.owner;
    }

    @Override
    public boolean hasNext() {
        if (this.index + 1 >= this.rows.size()) return false;
        return true;
    }

    @Override
    public boolean isAfterLast() {
        if (this.index < this.rows.size()) return false;
        if (this.rows.size() == 0) return false;
        return true;
    }

    @Override
    public boolean isBeforeFirst() {
        if (this.index != -1) return false;
        if (this.rows.size() == 0) return false;
        return true;
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        if (this.rows.size() != 0) return false;
        return true;
    }

    @Override
    public boolean isFirst() {
        if (this.index != 0) return false;
        return true;
    }

    @Override
    public boolean isLast() {
        if (this.rows.size() == 0) {
            return false;
        }
        if (this.index != this.rows.size() - 1) return false;
        return true;
    }

    @Override
    public void moveRowRelative(int rowsToMove) {
        if (this.rows.size() <= 0) return;
        this.index += rowsToMove;
        if (this.index < -1) {
            this.beforeFirst();
            return;
        }
        if (this.index <= this.rows.size()) return;
        this.afterLast();
    }

    @Override
    public ResultSetRow next() throws SQLException {
        ++this.index;
        if (this.index > this.rows.size()) {
            this.afterLast();
            return null;
        }
        if (this.index >= this.rows.size()) return null;
        ResultSetRow row = this.rows.get((int)this.index);
        return row.setMetadata((Field[])this.metadata);
    }

    @Override
    public void removeRow(int atIndex) {
        this.rows.remove((int)atIndex);
    }

    @Override
    public void setCurrentRow(int newIndex) {
        this.index = newIndex;
    }

    @Override
    public void setOwner(ResultSetImpl rs) {
        this.owner = rs;
    }

    @Override
    public int size() {
        return this.rows.size();
    }

    @Override
    public boolean wasEmpty() {
        if (this.rows == null) return false;
        if (this.rows.size() != 0) return false;
        return true;
    }

    @Override
    public void setMetadata(Field[] metadata) {
        this.metadata = metadata;
    }
}

