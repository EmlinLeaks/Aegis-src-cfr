/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.util;

import java.util.Collections;
import java.util.List;
import org.yaml.snakeyaml.util.ArrayUtils;

public class ArrayUtils {
    private ArrayUtils() {
    }

    public static <E> List<E> toUnmodifiableList(E[] elements) {
        UnmodifiableArrayList<E> unmodifiableArrayList;
        if (elements.length == 0) {
            unmodifiableArrayList = Collections.emptyList();
            return unmodifiableArrayList;
        }
        unmodifiableArrayList = new UnmodifiableArrayList<E>(elements);
        return unmodifiableArrayList;
    }

    public static <E> List<E> toUnmodifiableCompositeList(E[] array1, E[] array2) {
        if (array1.length == 0) {
            return ArrayUtils.toUnmodifiableList(array2);
        }
        if (array2.length != 0) return new CompositeUnmodifiableArrayList<E>(array1, array2);
        return ArrayUtils.toUnmodifiableList(array1);
    }
}

