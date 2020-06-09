/*
 * Decompiled with CFR <Could not determine version>.
 */
package gnu.trove.queue;

import gnu.trove.TFloatCollection;

public interface TFloatQueue
extends TFloatCollection {
    public float element();

    public boolean offer(float var1);

    public float peek();

    public float poll();
}

