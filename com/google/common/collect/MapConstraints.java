/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingMapEntry;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapConstraint;
import com.google.common.collect.MapConstraints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Deprecated
@Beta
@GwtCompatible
public final class MapConstraints {
    private MapConstraints() {
    }

    public static <K, V> Map<K, V> constrainedMap(Map<K, V> map, MapConstraint<? super K, ? super V> constraint) {
        return new ConstrainedMap<K, V>(map, constraint);
    }

    public static <K, V> ListMultimap<K, V> constrainedListMultimap(ListMultimap<K, V> multimap, MapConstraint<? super K, ? super V> constraint) {
        return new ConstrainedListMultimap<K, V>(multimap, constraint);
    }

    private static <K, V> Map.Entry<K, V> constrainedEntry(Map.Entry<K, V> entry, MapConstraint<? super K, ? super V> constraint) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(constraint);
        return new ForwardingMapEntry<K, V>(entry, constraint){
            final /* synthetic */ Map.Entry val$entry;
            final /* synthetic */ MapConstraint val$constraint;
            {
                this.val$entry = entry;
                this.val$constraint = mapConstraint;
            }

            protected Map.Entry<K, V> delegate() {
                return this.val$entry;
            }

            public V setValue(V value) {
                this.val$constraint.checkKeyValue(this.getKey(), value);
                return (V)this.val$entry.setValue(value);
            }
        };
    }

    private static <K, V> Map.Entry<K, Collection<V>> constrainedAsMapEntry(Map.Entry<K, Collection<V>> entry, MapConstraint<? super K, ? super V> constraint) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(constraint);
        return new ForwardingMapEntry<K, Collection<V>>(entry, constraint){
            final /* synthetic */ Map.Entry val$entry;
            final /* synthetic */ MapConstraint val$constraint;
            {
                this.val$entry = entry;
                this.val$constraint = mapConstraint;
            }

            protected Map.Entry<K, Collection<V>> delegate() {
                return this.val$entry;
            }

            public Collection<V> getValue() {
                return com.google.common.collect.Constraints.constrainedTypePreservingCollection((Collection)this.val$entry.getValue(), new com.google.common.collect.Constraint<V>(this){
                    final /* synthetic */ 2 this$0;
                    {
                        this.this$0 = var1_1;
                    }

                    public V checkElement(V value) {
                        this.this$0.val$constraint.checkKeyValue(this.this$0.getKey(), value);
                        return (V)value;
                    }
                });
            }
        };
    }

    private static <K, V> Set<Map.Entry<K, Collection<V>>> constrainedAsMapEntries(Set<Map.Entry<K, Collection<V>>> entries, MapConstraint<? super K, ? super V> constraint) {
        return new ConstrainedAsMapEntries<K, V>(entries, constraint);
    }

    private static <K, V> Collection<Map.Entry<K, V>> constrainedEntries(Collection<Map.Entry<K, V>> entries, MapConstraint<? super K, ? super V> constraint) {
        if (!(entries instanceof Set)) return new ConstrainedEntries<K, V>(entries, constraint);
        return MapConstraints.constrainedEntrySet((Set)entries, constraint);
    }

    private static <K, V> Set<Map.Entry<K, V>> constrainedEntrySet(Set<Map.Entry<K, V>> entries, MapConstraint<? super K, ? super V> constraint) {
        return new ConstrainedEntrySet<K, V>(entries, constraint);
    }

    private static <K, V> Collection<V> checkValues(K key, Iterable<? extends V> values, MapConstraint<? super K, ? super V> constraint) {
        ArrayList<V> copy = Lists.newArrayList(values);
        Iterator<E> i$ = copy.iterator();
        while (i$.hasNext()) {
            E value = i$.next();
            constraint.checkKeyValue(key, value);
        }
        return copy;
    }

    private static <K, V> Map<K, V> checkMap(Map<? extends K, ? extends V> map, MapConstraint<? super K, ? super V> constraint) {
        LinkedHashMap<K, V> copy = new LinkedHashMap<K, V>(map);
        Iterator<Map.Entry<K, V>> i$ = copy.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<K, V> entry = i$.next();
            constraint.checkKeyValue(entry.getKey(), entry.getValue());
        }
        return copy;
    }

    static /* synthetic */ Set access$000(Set x0, MapConstraint x1) {
        return MapConstraints.constrainedEntrySet(x0, x1);
    }

    static /* synthetic */ Map access$100(Map x0, MapConstraint x1) {
        return MapConstraints.checkMap(x0, x1);
    }

    static /* synthetic */ Set access$200(Set x0, MapConstraint x1) {
        return MapConstraints.constrainedAsMapEntries(x0, x1);
    }

    static /* synthetic */ Collection access$300(Collection x0, MapConstraint x1) {
        return MapConstraints.constrainedEntries(x0, x1);
    }

    static /* synthetic */ Collection access$400(Object x0, Iterable x1, MapConstraint x2) {
        return MapConstraints.checkValues(x0, x1, x2);
    }

    static /* synthetic */ Map.Entry access$500(Map.Entry x0, MapConstraint x1) {
        return MapConstraints.constrainedEntry(x0, x1);
    }

    static /* synthetic */ Map.Entry access$700(Map.Entry x0, MapConstraint x1) {
        return MapConstraints.constrainedAsMapEntry(x0, x1);
    }
}

