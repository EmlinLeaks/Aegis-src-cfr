/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;
import io.netty.util.DefaultAttributeMap;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultAttributeMap
implements AttributeMap {
    private static final AtomicReferenceFieldUpdater<DefaultAttributeMap, AtomicReferenceArray> updater = AtomicReferenceFieldUpdater.newUpdater(DefaultAttributeMap.class, AtomicReferenceArray.class, (String)"attributes");
    private static final int BUCKET_SIZE = 4;
    private static final int MASK = 3;
    private volatile AtomicReferenceArray<DefaultAttribute<?>> attributes;

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        int i;
        DefaultAttribute<Object> attr;
        DefaultAttribute<Object> head;
        if (key == null) {
            throw new NullPointerException((String)"key");
        }
        AtomicReferenceArray<DefaultAttribute<Object>> attributes = this.attributes;
        if (attributes == null && !updater.compareAndSet((DefaultAttributeMap)this, null, attributes = new AtomicReferenceArray<E>((int)4))) {
            attributes = this.attributes;
        }
        if ((head = attributes.get((int)(i = DefaultAttributeMap.index(key)))) == null) {
            head = new DefaultAttribute<T>();
            attr = new DefaultAttribute<T>(head, key);
            head.next = attr;
            attr.prev = head;
            if (attributes.compareAndSet((int)i, null, head)) {
                return attr;
            }
            head = attributes.get((int)i);
        }
        attr = head;
        // MONITORENTER : attr
        DefaultAttribute curr = head;
        do {
            DefaultAttribute next;
            if ((next = curr.next) == null) {
                DefaultAttribute<T> attr2 = new DefaultAttribute<T>(head, key);
                ((DefaultAttribute)curr).next = attr2;
                attr2.prev = (DefaultAttribute)curr;
                // MONITOREXIT : attr
                return attr2;
            }
            if (((DefaultAttribute)next).key == key && !((DefaultAttribute)next).removed) {
                // MONITOREXIT : attr
                return next;
            }
            curr = next;
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> boolean hasAttr(AttributeKey<T> key) {
        if (key == null) {
            throw new NullPointerException((String)"key");
        }
        AtomicReferenceArray<DefaultAttribute<?>> attributes = this.attributes;
        if (attributes == null) {
            return false;
        }
        int i = DefaultAttributeMap.index(key);
        DefaultAttribute<?> head = attributes.get((int)i);
        if (head == null) {
            return false;
        }
        DefaultAttribute<?> defaultAttribute = head;
        // MONITORENTER : defaultAttribute
        DefaultAttribute curr = head.next;
        do {
            if (curr == null) {
                // MONITOREXIT : defaultAttribute
                return false;
            }
            if (((DefaultAttribute)curr).key == key && !((DefaultAttribute)curr).removed) {
                // MONITOREXIT : defaultAttribute
                return true;
            }
            curr = ((DefaultAttribute)curr).next;
        } while (true);
    }

    private static int index(AttributeKey<?> key) {
        return key.id() & 3;
    }
}

