/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.stack;

public interface TCharStack {
    public char getNoEntryValue();

    public void push(char var1);

    public char pop();

    public char peek();

    public int size();

    public void clear();

    public char[] toArray();

    public void toArray(char[] var1);
}

