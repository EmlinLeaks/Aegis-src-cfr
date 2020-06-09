/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove;

import gnu.trove.TByteCollection;
import gnu.trove.TCharCollection;
import gnu.trove.TDoubleCollection;
import gnu.trove.TFloatCollection;
import gnu.trove.TIntCollection;
import gnu.trove.TLongCollection;
import gnu.trove.TShortCollection;
import gnu.trove.impl.sync.TSynchronizedByteByteMap;
import gnu.trove.impl.sync.TSynchronizedByteCharMap;
import gnu.trove.impl.sync.TSynchronizedByteCollection;
import gnu.trove.impl.sync.TSynchronizedByteDoubleMap;
import gnu.trove.impl.sync.TSynchronizedByteFloatMap;
import gnu.trove.impl.sync.TSynchronizedByteIntMap;
import gnu.trove.impl.sync.TSynchronizedByteList;
import gnu.trove.impl.sync.TSynchronizedByteLongMap;
import gnu.trove.impl.sync.TSynchronizedByteObjectMap;
import gnu.trove.impl.sync.TSynchronizedByteSet;
import gnu.trove.impl.sync.TSynchronizedByteShortMap;
import gnu.trove.impl.sync.TSynchronizedCharByteMap;
import gnu.trove.impl.sync.TSynchronizedCharCharMap;
import gnu.trove.impl.sync.TSynchronizedCharCollection;
import gnu.trove.impl.sync.TSynchronizedCharDoubleMap;
import gnu.trove.impl.sync.TSynchronizedCharFloatMap;
import gnu.trove.impl.sync.TSynchronizedCharIntMap;
import gnu.trove.impl.sync.TSynchronizedCharList;
import gnu.trove.impl.sync.TSynchronizedCharLongMap;
import gnu.trove.impl.sync.TSynchronizedCharObjectMap;
import gnu.trove.impl.sync.TSynchronizedCharSet;
import gnu.trove.impl.sync.TSynchronizedCharShortMap;
import gnu.trove.impl.sync.TSynchronizedDoubleByteMap;
import gnu.trove.impl.sync.TSynchronizedDoubleCharMap;
import gnu.trove.impl.sync.TSynchronizedDoubleCollection;
import gnu.trove.impl.sync.TSynchronizedDoubleDoubleMap;
import gnu.trove.impl.sync.TSynchronizedDoubleFloatMap;
import gnu.trove.impl.sync.TSynchronizedDoubleIntMap;
import gnu.trove.impl.sync.TSynchronizedDoubleList;
import gnu.trove.impl.sync.TSynchronizedDoubleLongMap;
import gnu.trove.impl.sync.TSynchronizedDoubleObjectMap;
import gnu.trove.impl.sync.TSynchronizedDoubleSet;
import gnu.trove.impl.sync.TSynchronizedDoubleShortMap;
import gnu.trove.impl.sync.TSynchronizedFloatByteMap;
import gnu.trove.impl.sync.TSynchronizedFloatCharMap;
import gnu.trove.impl.sync.TSynchronizedFloatCollection;
import gnu.trove.impl.sync.TSynchronizedFloatDoubleMap;
import gnu.trove.impl.sync.TSynchronizedFloatFloatMap;
import gnu.trove.impl.sync.TSynchronizedFloatIntMap;
import gnu.trove.impl.sync.TSynchronizedFloatList;
import gnu.trove.impl.sync.TSynchronizedFloatLongMap;
import gnu.trove.impl.sync.TSynchronizedFloatObjectMap;
import gnu.trove.impl.sync.TSynchronizedFloatSet;
import gnu.trove.impl.sync.TSynchronizedFloatShortMap;
import gnu.trove.impl.sync.TSynchronizedIntByteMap;
import gnu.trove.impl.sync.TSynchronizedIntCharMap;
import gnu.trove.impl.sync.TSynchronizedIntCollection;
import gnu.trove.impl.sync.TSynchronizedIntDoubleMap;
import gnu.trove.impl.sync.TSynchronizedIntFloatMap;
import gnu.trove.impl.sync.TSynchronizedIntIntMap;
import gnu.trove.impl.sync.TSynchronizedIntList;
import gnu.trove.impl.sync.TSynchronizedIntLongMap;
import gnu.trove.impl.sync.TSynchronizedIntObjectMap;
import gnu.trove.impl.sync.TSynchronizedIntSet;
import gnu.trove.impl.sync.TSynchronizedIntShortMap;
import gnu.trove.impl.sync.TSynchronizedLongByteMap;
import gnu.trove.impl.sync.TSynchronizedLongCharMap;
import gnu.trove.impl.sync.TSynchronizedLongCollection;
import gnu.trove.impl.sync.TSynchronizedLongDoubleMap;
import gnu.trove.impl.sync.TSynchronizedLongFloatMap;
import gnu.trove.impl.sync.TSynchronizedLongIntMap;
import gnu.trove.impl.sync.TSynchronizedLongList;
import gnu.trove.impl.sync.TSynchronizedLongLongMap;
import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
import gnu.trove.impl.sync.TSynchronizedLongSet;
import gnu.trove.impl.sync.TSynchronizedLongShortMap;
import gnu.trove.impl.sync.TSynchronizedObjectByteMap;
import gnu.trove.impl.sync.TSynchronizedObjectCharMap;
import gnu.trove.impl.sync.TSynchronizedObjectDoubleMap;
import gnu.trove.impl.sync.TSynchronizedObjectFloatMap;
import gnu.trove.impl.sync.TSynchronizedObjectIntMap;
import gnu.trove.impl.sync.TSynchronizedObjectLongMap;
import gnu.trove.impl.sync.TSynchronizedObjectShortMap;
import gnu.trove.impl.sync.TSynchronizedRandomAccessByteList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessCharList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessDoubleList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessFloatList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessIntList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessLongList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessShortList;
import gnu.trove.impl.sync.TSynchronizedShortByteMap;
import gnu.trove.impl.sync.TSynchronizedShortCharMap;
import gnu.trove.impl.sync.TSynchronizedShortCollection;
import gnu.trove.impl.sync.TSynchronizedShortDoubleMap;
import gnu.trove.impl.sync.TSynchronizedShortFloatMap;
import gnu.trove.impl.sync.TSynchronizedShortIntMap;
import gnu.trove.impl.sync.TSynchronizedShortList;
import gnu.trove.impl.sync.TSynchronizedShortLongMap;
import gnu.trove.impl.sync.TSynchronizedShortObjectMap;
import gnu.trove.impl.sync.TSynchronizedShortSet;
import gnu.trove.impl.sync.TSynchronizedShortShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteList;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharList;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleList;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatList;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntList;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongList;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessByteList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessCharList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessDoubleList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessFloatList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessIntList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessLongList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessShortList;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortList;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortShortMap;
import gnu.trove.list.TByteList;
import gnu.trove.list.TCharList;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;
import gnu.trove.list.TShortList;
import gnu.trove.map.TByteByteMap;
import gnu.trove.map.TByteCharMap;
import gnu.trove.map.TByteDoubleMap;
import gnu.trove.map.TByteFloatMap;
import gnu.trove.map.TByteIntMap;
import gnu.trove.map.TByteLongMap;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.map.TByteShortMap;
import gnu.trove.map.TCharByteMap;
import gnu.trove.map.TCharCharMap;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.map.TCharFloatMap;
import gnu.trove.map.TCharIntMap;
import gnu.trove.map.TCharLongMap;
import gnu.trove.map.TCharObjectMap;
import gnu.trove.map.TCharShortMap;
import gnu.trove.map.TDoubleByteMap;
import gnu.trove.map.TDoubleCharMap;
import gnu.trove.map.TDoubleDoubleMap;
import gnu.trove.map.TDoubleFloatMap;
import gnu.trove.map.TDoubleIntMap;
import gnu.trove.map.TDoubleLongMap;
import gnu.trove.map.TDoubleObjectMap;
import gnu.trove.map.TDoubleShortMap;
import gnu.trove.map.TFloatByteMap;
import gnu.trove.map.TFloatCharMap;
import gnu.trove.map.TFloatDoubleMap;
import gnu.trove.map.TFloatFloatMap;
import gnu.trove.map.TFloatIntMap;
import gnu.trove.map.TFloatLongMap;
import gnu.trove.map.TFloatObjectMap;
import gnu.trove.map.TFloatShortMap;
import gnu.trove.map.TIntByteMap;
import gnu.trove.map.TIntCharMap;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TIntShortMap;
import gnu.trove.map.TLongByteMap;
import gnu.trove.map.TLongCharMap;
import gnu.trove.map.TLongDoubleMap;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TLongShortMap;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.map.TObjectCharMap;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.TShortByteMap;
import gnu.trove.map.TShortCharMap;
import gnu.trove.map.TShortDoubleMap;
import gnu.trove.map.TShortFloatMap;
import gnu.trove.map.TShortIntMap;
import gnu.trove.map.TShortLongMap;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.TShortShortMap;
import gnu.trove.set.TByteSet;
import gnu.trove.set.TCharSet;
import gnu.trove.set.TDoubleSet;
import gnu.trove.set.TFloatSet;
import gnu.trove.set.TIntSet;
import gnu.trove.set.TLongSet;
import gnu.trove.set.TShortSet;
import java.util.RandomAccess;

public class TCollections {
    private TCollections() {
    }

    public static TDoubleCollection unmodifiableCollection(TDoubleCollection c) {
        return new TUnmodifiableDoubleCollection((TDoubleCollection)c);
    }

    public static TFloatCollection unmodifiableCollection(TFloatCollection c) {
        return new TUnmodifiableFloatCollection((TFloatCollection)c);
    }

    public static TIntCollection unmodifiableCollection(TIntCollection c) {
        return new TUnmodifiableIntCollection((TIntCollection)c);
    }

    public static TLongCollection unmodifiableCollection(TLongCollection c) {
        return new TUnmodifiableLongCollection((TLongCollection)c);
    }

    public static TByteCollection unmodifiableCollection(TByteCollection c) {
        return new TUnmodifiableByteCollection((TByteCollection)c);
    }

    public static TShortCollection unmodifiableCollection(TShortCollection c) {
        return new TUnmodifiableShortCollection((TShortCollection)c);
    }

    public static TCharCollection unmodifiableCollection(TCharCollection c) {
        return new TUnmodifiableCharCollection((TCharCollection)c);
    }

    public static TDoubleSet unmodifiableSet(TDoubleSet s) {
        return new TUnmodifiableDoubleSet((TDoubleSet)s);
    }

    public static TFloatSet unmodifiableSet(TFloatSet s) {
        return new TUnmodifiableFloatSet((TFloatSet)s);
    }

    public static TIntSet unmodifiableSet(TIntSet s) {
        return new TUnmodifiableIntSet((TIntSet)s);
    }

    public static TLongSet unmodifiableSet(TLongSet s) {
        return new TUnmodifiableLongSet((TLongSet)s);
    }

    public static TByteSet unmodifiableSet(TByteSet s) {
        return new TUnmodifiableByteSet((TByteSet)s);
    }

    public static TShortSet unmodifiableSet(TShortSet s) {
        return new TUnmodifiableShortSet((TShortSet)s);
    }

    public static TCharSet unmodifiableSet(TCharSet s) {
        return new TUnmodifiableCharSet((TCharSet)s);
    }

    public static TDoubleList unmodifiableList(TDoubleList list) {
        TUnmodifiableDoubleList tUnmodifiableDoubleList;
        if (list instanceof RandomAccess) {
            tUnmodifiableDoubleList = new TUnmodifiableRandomAccessDoubleList((TDoubleList)list);
            return tUnmodifiableDoubleList;
        }
        tUnmodifiableDoubleList = new TUnmodifiableDoubleList((TDoubleList)list);
        return tUnmodifiableDoubleList;
    }

    public static TFloatList unmodifiableList(TFloatList list) {
        TUnmodifiableFloatList tUnmodifiableFloatList;
        if (list instanceof RandomAccess) {
            tUnmodifiableFloatList = new TUnmodifiableRandomAccessFloatList((TFloatList)list);
            return tUnmodifiableFloatList;
        }
        tUnmodifiableFloatList = new TUnmodifiableFloatList((TFloatList)list);
        return tUnmodifiableFloatList;
    }

    public static TIntList unmodifiableList(TIntList list) {
        TUnmodifiableIntList tUnmodifiableIntList;
        if (list instanceof RandomAccess) {
            tUnmodifiableIntList = new TUnmodifiableRandomAccessIntList((TIntList)list);
            return tUnmodifiableIntList;
        }
        tUnmodifiableIntList = new TUnmodifiableIntList((TIntList)list);
        return tUnmodifiableIntList;
    }

    public static TLongList unmodifiableList(TLongList list) {
        TUnmodifiableLongList tUnmodifiableLongList;
        if (list instanceof RandomAccess) {
            tUnmodifiableLongList = new TUnmodifiableRandomAccessLongList((TLongList)list);
            return tUnmodifiableLongList;
        }
        tUnmodifiableLongList = new TUnmodifiableLongList((TLongList)list);
        return tUnmodifiableLongList;
    }

    public static TByteList unmodifiableList(TByteList list) {
        TUnmodifiableByteList tUnmodifiableByteList;
        if (list instanceof RandomAccess) {
            tUnmodifiableByteList = new TUnmodifiableRandomAccessByteList((TByteList)list);
            return tUnmodifiableByteList;
        }
        tUnmodifiableByteList = new TUnmodifiableByteList((TByteList)list);
        return tUnmodifiableByteList;
    }

    public static TShortList unmodifiableList(TShortList list) {
        TUnmodifiableShortList tUnmodifiableShortList;
        if (list instanceof RandomAccess) {
            tUnmodifiableShortList = new TUnmodifiableRandomAccessShortList((TShortList)list);
            return tUnmodifiableShortList;
        }
        tUnmodifiableShortList = new TUnmodifiableShortList((TShortList)list);
        return tUnmodifiableShortList;
    }

    public static TCharList unmodifiableList(TCharList list) {
        TUnmodifiableCharList tUnmodifiableCharList;
        if (list instanceof RandomAccess) {
            tUnmodifiableCharList = new TUnmodifiableRandomAccessCharList((TCharList)list);
            return tUnmodifiableCharList;
        }
        tUnmodifiableCharList = new TUnmodifiableCharList((TCharList)list);
        return tUnmodifiableCharList;
    }

    public static TDoubleDoubleMap unmodifiableMap(TDoubleDoubleMap m) {
        return new TUnmodifiableDoubleDoubleMap((TDoubleDoubleMap)m);
    }

    public static TDoubleFloatMap unmodifiableMap(TDoubleFloatMap m) {
        return new TUnmodifiableDoubleFloatMap((TDoubleFloatMap)m);
    }

    public static TDoubleIntMap unmodifiableMap(TDoubleIntMap m) {
        return new TUnmodifiableDoubleIntMap((TDoubleIntMap)m);
    }

    public static TDoubleLongMap unmodifiableMap(TDoubleLongMap m) {
        return new TUnmodifiableDoubleLongMap((TDoubleLongMap)m);
    }

    public static TDoubleByteMap unmodifiableMap(TDoubleByteMap m) {
        return new TUnmodifiableDoubleByteMap((TDoubleByteMap)m);
    }

    public static TDoubleShortMap unmodifiableMap(TDoubleShortMap m) {
        return new TUnmodifiableDoubleShortMap((TDoubleShortMap)m);
    }

    public static TDoubleCharMap unmodifiableMap(TDoubleCharMap m) {
        return new TUnmodifiableDoubleCharMap((TDoubleCharMap)m);
    }

    public static TFloatDoubleMap unmodifiableMap(TFloatDoubleMap m) {
        return new TUnmodifiableFloatDoubleMap((TFloatDoubleMap)m);
    }

    public static TFloatFloatMap unmodifiableMap(TFloatFloatMap m) {
        return new TUnmodifiableFloatFloatMap((TFloatFloatMap)m);
    }

    public static TFloatIntMap unmodifiableMap(TFloatIntMap m) {
        return new TUnmodifiableFloatIntMap((TFloatIntMap)m);
    }

    public static TFloatLongMap unmodifiableMap(TFloatLongMap m) {
        return new TUnmodifiableFloatLongMap((TFloatLongMap)m);
    }

    public static TFloatByteMap unmodifiableMap(TFloatByteMap m) {
        return new TUnmodifiableFloatByteMap((TFloatByteMap)m);
    }

    public static TFloatShortMap unmodifiableMap(TFloatShortMap m) {
        return new TUnmodifiableFloatShortMap((TFloatShortMap)m);
    }

    public static TFloatCharMap unmodifiableMap(TFloatCharMap m) {
        return new TUnmodifiableFloatCharMap((TFloatCharMap)m);
    }

    public static TIntDoubleMap unmodifiableMap(TIntDoubleMap m) {
        return new TUnmodifiableIntDoubleMap((TIntDoubleMap)m);
    }

    public static TIntFloatMap unmodifiableMap(TIntFloatMap m) {
        return new TUnmodifiableIntFloatMap((TIntFloatMap)m);
    }

    public static TIntIntMap unmodifiableMap(TIntIntMap m) {
        return new TUnmodifiableIntIntMap((TIntIntMap)m);
    }

    public static TIntLongMap unmodifiableMap(TIntLongMap m) {
        return new TUnmodifiableIntLongMap((TIntLongMap)m);
    }

    public static TIntByteMap unmodifiableMap(TIntByteMap m) {
        return new TUnmodifiableIntByteMap((TIntByteMap)m);
    }

    public static TIntShortMap unmodifiableMap(TIntShortMap m) {
        return new TUnmodifiableIntShortMap((TIntShortMap)m);
    }

    public static TIntCharMap unmodifiableMap(TIntCharMap m) {
        return new TUnmodifiableIntCharMap((TIntCharMap)m);
    }

    public static TLongDoubleMap unmodifiableMap(TLongDoubleMap m) {
        return new TUnmodifiableLongDoubleMap((TLongDoubleMap)m);
    }

    public static TLongFloatMap unmodifiableMap(TLongFloatMap m) {
        return new TUnmodifiableLongFloatMap((TLongFloatMap)m);
    }

    public static TLongIntMap unmodifiableMap(TLongIntMap m) {
        return new TUnmodifiableLongIntMap((TLongIntMap)m);
    }

    public static TLongLongMap unmodifiableMap(TLongLongMap m) {
        return new TUnmodifiableLongLongMap((TLongLongMap)m);
    }

    public static TLongByteMap unmodifiableMap(TLongByteMap m) {
        return new TUnmodifiableLongByteMap((TLongByteMap)m);
    }

    public static TLongShortMap unmodifiableMap(TLongShortMap m) {
        return new TUnmodifiableLongShortMap((TLongShortMap)m);
    }

    public static TLongCharMap unmodifiableMap(TLongCharMap m) {
        return new TUnmodifiableLongCharMap((TLongCharMap)m);
    }

    public static TByteDoubleMap unmodifiableMap(TByteDoubleMap m) {
        return new TUnmodifiableByteDoubleMap((TByteDoubleMap)m);
    }

    public static TByteFloatMap unmodifiableMap(TByteFloatMap m) {
        return new TUnmodifiableByteFloatMap((TByteFloatMap)m);
    }

    public static TByteIntMap unmodifiableMap(TByteIntMap m) {
        return new TUnmodifiableByteIntMap((TByteIntMap)m);
    }

    public static TByteLongMap unmodifiableMap(TByteLongMap m) {
        return new TUnmodifiableByteLongMap((TByteLongMap)m);
    }

    public static TByteByteMap unmodifiableMap(TByteByteMap m) {
        return new TUnmodifiableByteByteMap((TByteByteMap)m);
    }

    public static TByteShortMap unmodifiableMap(TByteShortMap m) {
        return new TUnmodifiableByteShortMap((TByteShortMap)m);
    }

    public static TByteCharMap unmodifiableMap(TByteCharMap m) {
        return new TUnmodifiableByteCharMap((TByteCharMap)m);
    }

    public static TShortDoubleMap unmodifiableMap(TShortDoubleMap m) {
        return new TUnmodifiableShortDoubleMap((TShortDoubleMap)m);
    }

    public static TShortFloatMap unmodifiableMap(TShortFloatMap m) {
        return new TUnmodifiableShortFloatMap((TShortFloatMap)m);
    }

    public static TShortIntMap unmodifiableMap(TShortIntMap m) {
        return new TUnmodifiableShortIntMap((TShortIntMap)m);
    }

    public static TShortLongMap unmodifiableMap(TShortLongMap m) {
        return new TUnmodifiableShortLongMap((TShortLongMap)m);
    }

    public static TShortByteMap unmodifiableMap(TShortByteMap m) {
        return new TUnmodifiableShortByteMap((TShortByteMap)m);
    }

    public static TShortShortMap unmodifiableMap(TShortShortMap m) {
        return new TUnmodifiableShortShortMap((TShortShortMap)m);
    }

    public static TShortCharMap unmodifiableMap(TShortCharMap m) {
        return new TUnmodifiableShortCharMap((TShortCharMap)m);
    }

    public static TCharDoubleMap unmodifiableMap(TCharDoubleMap m) {
        return new TUnmodifiableCharDoubleMap((TCharDoubleMap)m);
    }

    public static TCharFloatMap unmodifiableMap(TCharFloatMap m) {
        return new TUnmodifiableCharFloatMap((TCharFloatMap)m);
    }

    public static TCharIntMap unmodifiableMap(TCharIntMap m) {
        return new TUnmodifiableCharIntMap((TCharIntMap)m);
    }

    public static TCharLongMap unmodifiableMap(TCharLongMap m) {
        return new TUnmodifiableCharLongMap((TCharLongMap)m);
    }

    public static TCharByteMap unmodifiableMap(TCharByteMap m) {
        return new TUnmodifiableCharByteMap((TCharByteMap)m);
    }

    public static TCharShortMap unmodifiableMap(TCharShortMap m) {
        return new TUnmodifiableCharShortMap((TCharShortMap)m);
    }

    public static TCharCharMap unmodifiableMap(TCharCharMap m) {
        return new TUnmodifiableCharCharMap((TCharCharMap)m);
    }

    public static <V> TDoubleObjectMap<V> unmodifiableMap(TDoubleObjectMap<V> m) {
        return new TUnmodifiableDoubleObjectMap<V>(m);
    }

    public static <V> TFloatObjectMap<V> unmodifiableMap(TFloatObjectMap<V> m) {
        return new TUnmodifiableFloatObjectMap<V>(m);
    }

    public static <V> TIntObjectMap<V> unmodifiableMap(TIntObjectMap<V> m) {
        return new TUnmodifiableIntObjectMap<V>(m);
    }

    public static <V> TLongObjectMap<V> unmodifiableMap(TLongObjectMap<V> m) {
        return new TUnmodifiableLongObjectMap<V>(m);
    }

    public static <V> TByteObjectMap<V> unmodifiableMap(TByteObjectMap<V> m) {
        return new TUnmodifiableByteObjectMap<V>(m);
    }

    public static <V> TShortObjectMap<V> unmodifiableMap(TShortObjectMap<V> m) {
        return new TUnmodifiableShortObjectMap<V>(m);
    }

    public static <V> TCharObjectMap<V> unmodifiableMap(TCharObjectMap<V> m) {
        return new TUnmodifiableCharObjectMap<V>(m);
    }

    public static <K> TObjectDoubleMap<K> unmodifiableMap(TObjectDoubleMap<K> m) {
        return new TUnmodifiableObjectDoubleMap<K>(m);
    }

    public static <K> TObjectFloatMap<K> unmodifiableMap(TObjectFloatMap<K> m) {
        return new TUnmodifiableObjectFloatMap<K>(m);
    }

    public static <K> TObjectIntMap<K> unmodifiableMap(TObjectIntMap<K> m) {
        return new TUnmodifiableObjectIntMap<K>(m);
    }

    public static <K> TObjectLongMap<K> unmodifiableMap(TObjectLongMap<K> m) {
        return new TUnmodifiableObjectLongMap<K>(m);
    }

    public static <K> TObjectByteMap<K> unmodifiableMap(TObjectByteMap<K> m) {
        return new TUnmodifiableObjectByteMap<K>(m);
    }

    public static <K> TObjectShortMap<K> unmodifiableMap(TObjectShortMap<K> m) {
        return new TUnmodifiableObjectShortMap<K>(m);
    }

    public static <K> TObjectCharMap<K> unmodifiableMap(TObjectCharMap<K> m) {
        return new TUnmodifiableObjectCharMap<K>(m);
    }

    public static TDoubleCollection synchronizedCollection(TDoubleCollection c) {
        return new TSynchronizedDoubleCollection((TDoubleCollection)c);
    }

    static TDoubleCollection synchronizedCollection(TDoubleCollection c, Object mutex) {
        return new TSynchronizedDoubleCollection((TDoubleCollection)c, (Object)mutex);
    }

    public static TFloatCollection synchronizedCollection(TFloatCollection c) {
        return new TSynchronizedFloatCollection((TFloatCollection)c);
    }

    static TFloatCollection synchronizedCollection(TFloatCollection c, Object mutex) {
        return new TSynchronizedFloatCollection((TFloatCollection)c, (Object)mutex);
    }

    public static TIntCollection synchronizedCollection(TIntCollection c) {
        return new TSynchronizedIntCollection((TIntCollection)c);
    }

    static TIntCollection synchronizedCollection(TIntCollection c, Object mutex) {
        return new TSynchronizedIntCollection((TIntCollection)c, (Object)mutex);
    }

    public static TLongCollection synchronizedCollection(TLongCollection c) {
        return new TSynchronizedLongCollection((TLongCollection)c);
    }

    static TLongCollection synchronizedCollection(TLongCollection c, Object mutex) {
        return new TSynchronizedLongCollection((TLongCollection)c, (Object)mutex);
    }

    public static TByteCollection synchronizedCollection(TByteCollection c) {
        return new TSynchronizedByteCollection((TByteCollection)c);
    }

    static TByteCollection synchronizedCollection(TByteCollection c, Object mutex) {
        return new TSynchronizedByteCollection((TByteCollection)c, (Object)mutex);
    }

    public static TShortCollection synchronizedCollection(TShortCollection c) {
        return new TSynchronizedShortCollection((TShortCollection)c);
    }

    static TShortCollection synchronizedCollection(TShortCollection c, Object mutex) {
        return new TSynchronizedShortCollection((TShortCollection)c, (Object)mutex);
    }

    public static TCharCollection synchronizedCollection(TCharCollection c) {
        return new TSynchronizedCharCollection((TCharCollection)c);
    }

    static TCharCollection synchronizedCollection(TCharCollection c, Object mutex) {
        return new TSynchronizedCharCollection((TCharCollection)c, (Object)mutex);
    }

    public static TDoubleSet synchronizedSet(TDoubleSet s) {
        return new TSynchronizedDoubleSet((TDoubleSet)s);
    }

    static TDoubleSet synchronizedSet(TDoubleSet s, Object mutex) {
        return new TSynchronizedDoubleSet((TDoubleSet)s, (Object)mutex);
    }

    public static TFloatSet synchronizedSet(TFloatSet s) {
        return new TSynchronizedFloatSet((TFloatSet)s);
    }

    static TFloatSet synchronizedSet(TFloatSet s, Object mutex) {
        return new TSynchronizedFloatSet((TFloatSet)s, (Object)mutex);
    }

    public static TIntSet synchronizedSet(TIntSet s) {
        return new TSynchronizedIntSet((TIntSet)s);
    }

    static TIntSet synchronizedSet(TIntSet s, Object mutex) {
        return new TSynchronizedIntSet((TIntSet)s, (Object)mutex);
    }

    public static TLongSet synchronizedSet(TLongSet s) {
        return new TSynchronizedLongSet((TLongSet)s);
    }

    static TLongSet synchronizedSet(TLongSet s, Object mutex) {
        return new TSynchronizedLongSet((TLongSet)s, (Object)mutex);
    }

    public static TByteSet synchronizedSet(TByteSet s) {
        return new TSynchronizedByteSet((TByteSet)s);
    }

    static TByteSet synchronizedSet(TByteSet s, Object mutex) {
        return new TSynchronizedByteSet((TByteSet)s, (Object)mutex);
    }

    public static TShortSet synchronizedSet(TShortSet s) {
        return new TSynchronizedShortSet((TShortSet)s);
    }

    static TShortSet synchronizedSet(TShortSet s, Object mutex) {
        return new TSynchronizedShortSet((TShortSet)s, (Object)mutex);
    }

    public static TCharSet synchronizedSet(TCharSet s) {
        return new TSynchronizedCharSet((TCharSet)s);
    }

    static TCharSet synchronizedSet(TCharSet s, Object mutex) {
        return new TSynchronizedCharSet((TCharSet)s, (Object)mutex);
    }

    public static TDoubleList synchronizedList(TDoubleList list) {
        TSynchronizedDoubleList tSynchronizedDoubleList;
        if (list instanceof RandomAccess) {
            tSynchronizedDoubleList = new TSynchronizedRandomAccessDoubleList((TDoubleList)list);
            return tSynchronizedDoubleList;
        }
        tSynchronizedDoubleList = new TSynchronizedDoubleList((TDoubleList)list);
        return tSynchronizedDoubleList;
    }

    static TDoubleList synchronizedList(TDoubleList list, Object mutex) {
        TSynchronizedDoubleList tSynchronizedDoubleList;
        if (list instanceof RandomAccess) {
            tSynchronizedDoubleList = new TSynchronizedRandomAccessDoubleList((TDoubleList)list, (Object)mutex);
            return tSynchronizedDoubleList;
        }
        tSynchronizedDoubleList = new TSynchronizedDoubleList((TDoubleList)list, (Object)mutex);
        return tSynchronizedDoubleList;
    }

    public static TFloatList synchronizedList(TFloatList list) {
        TSynchronizedFloatList tSynchronizedFloatList;
        if (list instanceof RandomAccess) {
            tSynchronizedFloatList = new TSynchronizedRandomAccessFloatList((TFloatList)list);
            return tSynchronizedFloatList;
        }
        tSynchronizedFloatList = new TSynchronizedFloatList((TFloatList)list);
        return tSynchronizedFloatList;
    }

    static TFloatList synchronizedList(TFloatList list, Object mutex) {
        TSynchronizedFloatList tSynchronizedFloatList;
        if (list instanceof RandomAccess) {
            tSynchronizedFloatList = new TSynchronizedRandomAccessFloatList((TFloatList)list, (Object)mutex);
            return tSynchronizedFloatList;
        }
        tSynchronizedFloatList = new TSynchronizedFloatList((TFloatList)list, (Object)mutex);
        return tSynchronizedFloatList;
    }

    public static TIntList synchronizedList(TIntList list) {
        TSynchronizedIntList tSynchronizedIntList;
        if (list instanceof RandomAccess) {
            tSynchronizedIntList = new TSynchronizedRandomAccessIntList((TIntList)list);
            return tSynchronizedIntList;
        }
        tSynchronizedIntList = new TSynchronizedIntList((TIntList)list);
        return tSynchronizedIntList;
    }

    static TIntList synchronizedList(TIntList list, Object mutex) {
        TSynchronizedIntList tSynchronizedIntList;
        if (list instanceof RandomAccess) {
            tSynchronizedIntList = new TSynchronizedRandomAccessIntList((TIntList)list, (Object)mutex);
            return tSynchronizedIntList;
        }
        tSynchronizedIntList = new TSynchronizedIntList((TIntList)list, (Object)mutex);
        return tSynchronizedIntList;
    }

    public static TLongList synchronizedList(TLongList list) {
        TSynchronizedLongList tSynchronizedLongList;
        if (list instanceof RandomAccess) {
            tSynchronizedLongList = new TSynchronizedRandomAccessLongList((TLongList)list);
            return tSynchronizedLongList;
        }
        tSynchronizedLongList = new TSynchronizedLongList((TLongList)list);
        return tSynchronizedLongList;
    }

    static TLongList synchronizedList(TLongList list, Object mutex) {
        TSynchronizedLongList tSynchronizedLongList;
        if (list instanceof RandomAccess) {
            tSynchronizedLongList = new TSynchronizedRandomAccessLongList((TLongList)list, (Object)mutex);
            return tSynchronizedLongList;
        }
        tSynchronizedLongList = new TSynchronizedLongList((TLongList)list, (Object)mutex);
        return tSynchronizedLongList;
    }

    public static TByteList synchronizedList(TByteList list) {
        TSynchronizedByteList tSynchronizedByteList;
        if (list instanceof RandomAccess) {
            tSynchronizedByteList = new TSynchronizedRandomAccessByteList((TByteList)list);
            return tSynchronizedByteList;
        }
        tSynchronizedByteList = new TSynchronizedByteList((TByteList)list);
        return tSynchronizedByteList;
    }

    static TByteList synchronizedList(TByteList list, Object mutex) {
        TSynchronizedByteList tSynchronizedByteList;
        if (list instanceof RandomAccess) {
            tSynchronizedByteList = new TSynchronizedRandomAccessByteList((TByteList)list, (Object)mutex);
            return tSynchronizedByteList;
        }
        tSynchronizedByteList = new TSynchronizedByteList((TByteList)list, (Object)mutex);
        return tSynchronizedByteList;
    }

    public static TShortList synchronizedList(TShortList list) {
        TSynchronizedShortList tSynchronizedShortList;
        if (list instanceof RandomAccess) {
            tSynchronizedShortList = new TSynchronizedRandomAccessShortList((TShortList)list);
            return tSynchronizedShortList;
        }
        tSynchronizedShortList = new TSynchronizedShortList((TShortList)list);
        return tSynchronizedShortList;
    }

    static TShortList synchronizedList(TShortList list, Object mutex) {
        TSynchronizedShortList tSynchronizedShortList;
        if (list instanceof RandomAccess) {
            tSynchronizedShortList = new TSynchronizedRandomAccessShortList((TShortList)list, (Object)mutex);
            return tSynchronizedShortList;
        }
        tSynchronizedShortList = new TSynchronizedShortList((TShortList)list, (Object)mutex);
        return tSynchronizedShortList;
    }

    public static TCharList synchronizedList(TCharList list) {
        TSynchronizedCharList tSynchronizedCharList;
        if (list instanceof RandomAccess) {
            tSynchronizedCharList = new TSynchronizedRandomAccessCharList((TCharList)list);
            return tSynchronizedCharList;
        }
        tSynchronizedCharList = new TSynchronizedCharList((TCharList)list);
        return tSynchronizedCharList;
    }

    static TCharList synchronizedList(TCharList list, Object mutex) {
        TSynchronizedCharList tSynchronizedCharList;
        if (list instanceof RandomAccess) {
            tSynchronizedCharList = new TSynchronizedRandomAccessCharList((TCharList)list, (Object)mutex);
            return tSynchronizedCharList;
        }
        tSynchronizedCharList = new TSynchronizedCharList((TCharList)list, (Object)mutex);
        return tSynchronizedCharList;
    }

    public static TDoubleDoubleMap synchronizedMap(TDoubleDoubleMap m) {
        return new TSynchronizedDoubleDoubleMap((TDoubleDoubleMap)m);
    }

    public static TDoubleFloatMap synchronizedMap(TDoubleFloatMap m) {
        return new TSynchronizedDoubleFloatMap((TDoubleFloatMap)m);
    }

    public static TDoubleIntMap synchronizedMap(TDoubleIntMap m) {
        return new TSynchronizedDoubleIntMap((TDoubleIntMap)m);
    }

    public static TDoubleLongMap synchronizedMap(TDoubleLongMap m) {
        return new TSynchronizedDoubleLongMap((TDoubleLongMap)m);
    }

    public static TDoubleByteMap synchronizedMap(TDoubleByteMap m) {
        return new TSynchronizedDoubleByteMap((TDoubleByteMap)m);
    }

    public static TDoubleShortMap synchronizedMap(TDoubleShortMap m) {
        return new TSynchronizedDoubleShortMap((TDoubleShortMap)m);
    }

    public static TDoubleCharMap synchronizedMap(TDoubleCharMap m) {
        return new TSynchronizedDoubleCharMap((TDoubleCharMap)m);
    }

    public static TFloatDoubleMap synchronizedMap(TFloatDoubleMap m) {
        return new TSynchronizedFloatDoubleMap((TFloatDoubleMap)m);
    }

    public static TFloatFloatMap synchronizedMap(TFloatFloatMap m) {
        return new TSynchronizedFloatFloatMap((TFloatFloatMap)m);
    }

    public static TFloatIntMap synchronizedMap(TFloatIntMap m) {
        return new TSynchronizedFloatIntMap((TFloatIntMap)m);
    }

    public static TFloatLongMap synchronizedMap(TFloatLongMap m) {
        return new TSynchronizedFloatLongMap((TFloatLongMap)m);
    }

    public static TFloatByteMap synchronizedMap(TFloatByteMap m) {
        return new TSynchronizedFloatByteMap((TFloatByteMap)m);
    }

    public static TFloatShortMap synchronizedMap(TFloatShortMap m) {
        return new TSynchronizedFloatShortMap((TFloatShortMap)m);
    }

    public static TFloatCharMap synchronizedMap(TFloatCharMap m) {
        return new TSynchronizedFloatCharMap((TFloatCharMap)m);
    }

    public static TIntDoubleMap synchronizedMap(TIntDoubleMap m) {
        return new TSynchronizedIntDoubleMap((TIntDoubleMap)m);
    }

    public static TIntFloatMap synchronizedMap(TIntFloatMap m) {
        return new TSynchronizedIntFloatMap((TIntFloatMap)m);
    }

    public static TIntIntMap synchronizedMap(TIntIntMap m) {
        return new TSynchronizedIntIntMap((TIntIntMap)m);
    }

    public static TIntLongMap synchronizedMap(TIntLongMap m) {
        return new TSynchronizedIntLongMap((TIntLongMap)m);
    }

    public static TIntByteMap synchronizedMap(TIntByteMap m) {
        return new TSynchronizedIntByteMap((TIntByteMap)m);
    }

    public static TIntShortMap synchronizedMap(TIntShortMap m) {
        return new TSynchronizedIntShortMap((TIntShortMap)m);
    }

    public static TIntCharMap synchronizedMap(TIntCharMap m) {
        return new TSynchronizedIntCharMap((TIntCharMap)m);
    }

    public static TLongDoubleMap synchronizedMap(TLongDoubleMap m) {
        return new TSynchronizedLongDoubleMap((TLongDoubleMap)m);
    }

    public static TLongFloatMap synchronizedMap(TLongFloatMap m) {
        return new TSynchronizedLongFloatMap((TLongFloatMap)m);
    }

    public static TLongIntMap synchronizedMap(TLongIntMap m) {
        return new TSynchronizedLongIntMap((TLongIntMap)m);
    }

    public static TLongLongMap synchronizedMap(TLongLongMap m) {
        return new TSynchronizedLongLongMap((TLongLongMap)m);
    }

    public static TLongByteMap synchronizedMap(TLongByteMap m) {
        return new TSynchronizedLongByteMap((TLongByteMap)m);
    }

    public static TLongShortMap synchronizedMap(TLongShortMap m) {
        return new TSynchronizedLongShortMap((TLongShortMap)m);
    }

    public static TLongCharMap synchronizedMap(TLongCharMap m) {
        return new TSynchronizedLongCharMap((TLongCharMap)m);
    }

    public static TByteDoubleMap synchronizedMap(TByteDoubleMap m) {
        return new TSynchronizedByteDoubleMap((TByteDoubleMap)m);
    }

    public static TByteFloatMap synchronizedMap(TByteFloatMap m) {
        return new TSynchronizedByteFloatMap((TByteFloatMap)m);
    }

    public static TByteIntMap synchronizedMap(TByteIntMap m) {
        return new TSynchronizedByteIntMap((TByteIntMap)m);
    }

    public static TByteLongMap synchronizedMap(TByteLongMap m) {
        return new TSynchronizedByteLongMap((TByteLongMap)m);
    }

    public static TByteByteMap synchronizedMap(TByteByteMap m) {
        return new TSynchronizedByteByteMap((TByteByteMap)m);
    }

    public static TByteShortMap synchronizedMap(TByteShortMap m) {
        return new TSynchronizedByteShortMap((TByteShortMap)m);
    }

    public static TByteCharMap synchronizedMap(TByteCharMap m) {
        return new TSynchronizedByteCharMap((TByteCharMap)m);
    }

    public static TShortDoubleMap synchronizedMap(TShortDoubleMap m) {
        return new TSynchronizedShortDoubleMap((TShortDoubleMap)m);
    }

    public static TShortFloatMap synchronizedMap(TShortFloatMap m) {
        return new TSynchronizedShortFloatMap((TShortFloatMap)m);
    }

    public static TShortIntMap synchronizedMap(TShortIntMap m) {
        return new TSynchronizedShortIntMap((TShortIntMap)m);
    }

    public static TShortLongMap synchronizedMap(TShortLongMap m) {
        return new TSynchronizedShortLongMap((TShortLongMap)m);
    }

    public static TShortByteMap synchronizedMap(TShortByteMap m) {
        return new TSynchronizedShortByteMap((TShortByteMap)m);
    }

    public static TShortShortMap synchronizedMap(TShortShortMap m) {
        return new TSynchronizedShortShortMap((TShortShortMap)m);
    }

    public static TShortCharMap synchronizedMap(TShortCharMap m) {
        return new TSynchronizedShortCharMap((TShortCharMap)m);
    }

    public static TCharDoubleMap synchronizedMap(TCharDoubleMap m) {
        return new TSynchronizedCharDoubleMap((TCharDoubleMap)m);
    }

    public static TCharFloatMap synchronizedMap(TCharFloatMap m) {
        return new TSynchronizedCharFloatMap((TCharFloatMap)m);
    }

    public static TCharIntMap synchronizedMap(TCharIntMap m) {
        return new TSynchronizedCharIntMap((TCharIntMap)m);
    }

    public static TCharLongMap synchronizedMap(TCharLongMap m) {
        return new TSynchronizedCharLongMap((TCharLongMap)m);
    }

    public static TCharByteMap synchronizedMap(TCharByteMap m) {
        return new TSynchronizedCharByteMap((TCharByteMap)m);
    }

    public static TCharShortMap synchronizedMap(TCharShortMap m) {
        return new TSynchronizedCharShortMap((TCharShortMap)m);
    }

    public static TCharCharMap synchronizedMap(TCharCharMap m) {
        return new TSynchronizedCharCharMap((TCharCharMap)m);
    }

    public static <V> TDoubleObjectMap<V> synchronizedMap(TDoubleObjectMap<V> m) {
        return new TSynchronizedDoubleObjectMap<V>(m);
    }

    public static <V> TFloatObjectMap<V> synchronizedMap(TFloatObjectMap<V> m) {
        return new TSynchronizedFloatObjectMap<V>(m);
    }

    public static <V> TIntObjectMap<V> synchronizedMap(TIntObjectMap<V> m) {
        return new TSynchronizedIntObjectMap<V>(m);
    }

    public static <V> TLongObjectMap<V> synchronizedMap(TLongObjectMap<V> m) {
        return new TSynchronizedLongObjectMap<V>(m);
    }

    public static <V> TByteObjectMap<V> synchronizedMap(TByteObjectMap<V> m) {
        return new TSynchronizedByteObjectMap<V>(m);
    }

    public static <V> TShortObjectMap<V> synchronizedMap(TShortObjectMap<V> m) {
        return new TSynchronizedShortObjectMap<V>(m);
    }

    public static <V> TCharObjectMap<V> synchronizedMap(TCharObjectMap<V> m) {
        return new TSynchronizedCharObjectMap<V>(m);
    }

    public static <K> TObjectDoubleMap<K> synchronizedMap(TObjectDoubleMap<K> m) {
        return new TSynchronizedObjectDoubleMap<K>(m);
    }

    public static <K> TObjectFloatMap<K> synchronizedMap(TObjectFloatMap<K> m) {
        return new TSynchronizedObjectFloatMap<K>(m);
    }

    public static <K> TObjectIntMap<K> synchronizedMap(TObjectIntMap<K> m) {
        return new TSynchronizedObjectIntMap<K>(m);
    }

    public static <K> TObjectLongMap<K> synchronizedMap(TObjectLongMap<K> m) {
        return new TSynchronizedObjectLongMap<K>(m);
    }

    public static <K> TObjectByteMap<K> synchronizedMap(TObjectByteMap<K> m) {
        return new TSynchronizedObjectByteMap<K>(m);
    }

    public static <K> TObjectShortMap<K> synchronizedMap(TObjectShortMap<K> m) {
        return new TSynchronizedObjectShortMap<K>(m);
    }

    public static <K> TObjectCharMap<K> synchronizedMap(TObjectCharMap<K> m) {
        return new TSynchronizedObjectCharMap<K>(m);
    }
}

