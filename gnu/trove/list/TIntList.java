/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.list;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.procedure.TIntProcedure;
import java.util.Random;

public interface TIntList
extends TIntCollection {
    @Override
    public int getNoEntryValue();

    @Override
    public int size();

    @Override
    public boolean isEmpty();

    @Override
    public boolean add(int var1);

    public void add(int[] var1);

    public void add(int[] var1, int var2, int var3);

    public void insert(int var1, int var2);

    public void insert(int var1, int[] var2);

    public void insert(int var1, int[] var2, int var3, int var4);

    public int get(int var1);

    public int set(int var1, int var2);

    public void set(int var1, int[] var2);

    public void set(int var1, int[] var2, int var3, int var4);

    public int replace(int var1, int var2);

    @Override
    public void clear();

    @Override
    public boolean remove(int var1);

    public int removeAt(int var1);

    public void remove(int var1, int var2);

    public void transformValues(TIntFunction var1);

    public void reverse();

    public void reverse(int var1, int var2);

    public void shuffle(Random var1);

    public TIntList subList(int var1, int var2);

    @Override
    public int[] toArray();

    public int[] toArray(int var1, int var2);

    @Override
    public int[] toArray(int[] var1);

    public int[] toArray(int[] var1, int var2, int var3);

    public int[] toArray(int[] var1, int var2, int var3, int var4);

    @Override
    public boolean forEach(TIntProcedure var1);

    public boolean forEachDescending(TIntProcedure var1);

    public void sort();

    public void sort(int var1, int var2);

    public void fill(int var1);

    public void fill(int var1, int var2, int var3);

    public int binarySearch(int var1);

    public int binarySearch(int var1, int var2, int var3);

    public int indexOf(int var1);

    public int indexOf(int var1, int var2);

    public int lastIndexOf(int var1);

    public int lastIndexOf(int var1, int var2);

    @Override
    public boolean contains(int var1);

    public TIntList grep(TIntProcedure var1);

    public TIntList inverseGrep(TIntProcedure var1);

    public int max();

    public int min();

    public int sum();
}

