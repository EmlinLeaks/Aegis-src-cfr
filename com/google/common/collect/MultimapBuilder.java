/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Ordering;
import java.util.Comparator;

@Beta
@GwtCompatible
public abstract class MultimapBuilder<K0, V0> {
    private static final int DEFAULT_EXPECTED_KEYS = 8;

    private MultimapBuilder() {
    }

    public static MultimapBuilderWithKeys<Object> hashKeys() {
        return MultimapBuilder.hashKeys((int)8);
    }

    public static MultimapBuilderWithKeys<Object> hashKeys(int expectedKeys) {
        CollectPreconditions.checkNonnegative((int)expectedKeys, (String)"expectedKeys");
        return new MultimapBuilderWithKeys<Object>((int)expectedKeys){
            final /* synthetic */ int val$expectedKeys;
            {
                this.val$expectedKeys = n;
            }

            <K, V> java.util.Map<K, java.util.Collection<V>> createMap() {
                return com.google.common.collect.Maps.newHashMapWithExpectedSize((int)this.val$expectedKeys);
            }
        };
    }

    public static MultimapBuilderWithKeys<Object> linkedHashKeys() {
        return MultimapBuilder.linkedHashKeys((int)8);
    }

    public static MultimapBuilderWithKeys<Object> linkedHashKeys(int expectedKeys) {
        CollectPreconditions.checkNonnegative((int)expectedKeys, (String)"expectedKeys");
        return new MultimapBuilderWithKeys<Object>((int)expectedKeys){
            final /* synthetic */ int val$expectedKeys;
            {
                this.val$expectedKeys = n;
            }

            <K, V> java.util.Map<K, java.util.Collection<V>> createMap() {
                return com.google.common.collect.Maps.newLinkedHashMapWithExpectedSize((int)this.val$expectedKeys);
            }
        };
    }

    public static MultimapBuilderWithKeys<Comparable> treeKeys() {
        return MultimapBuilder.treeKeys(Ordering.<C>natural());
    }

    public static <K0> MultimapBuilderWithKeys<K0> treeKeys(Comparator<K0> comparator) {
        Preconditions.checkNotNull(comparator);
        return new MultimapBuilderWithKeys<K0>(comparator){
            final /* synthetic */ Comparator val$comparator;
            {
                this.val$comparator = comparator;
            }

            <K extends K0, V> java.util.Map<K, java.util.Collection<V>> createMap() {
                return new java.util.TreeMap<K, V>(this.val$comparator);
            }
        };
    }

    public static <K0 extends Enum<K0>> MultimapBuilderWithKeys<K0> enumKeys(Class<K0> keyClass) {
        Preconditions.checkNotNull(keyClass);
        return new MultimapBuilderWithKeys<K0>(keyClass){
            final /* synthetic */ Class val$keyClass;
            {
                this.val$keyClass = class_;
            }

            <K extends K0, V> java.util.Map<K, java.util.Collection<V>> createMap() {
                return new java.util.EnumMap<K, V>(this.val$keyClass);
            }
        };
    }

    public abstract <K extends K0, V extends V0> Multimap<K, V> build();

    public <K extends K0, V extends V0> Multimap<K, V> build(Multimap<? extends K, ? extends V> multimap) {
        Multimap<? extends K, ? extends V> result = this.build();
        result.putAll(multimap);
        return result;
    }
}

