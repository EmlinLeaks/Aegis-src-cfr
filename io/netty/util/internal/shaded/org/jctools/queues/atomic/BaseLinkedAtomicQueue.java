/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.BaseLinkedAtomicQueuePad2;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.LinkedQueueAtomicNode;
import java.util.Iterator;

abstract class BaseLinkedAtomicQueue<E>
extends BaseLinkedAtomicQueuePad2<E> {
    BaseLinkedAtomicQueue() {
    }

    @Override
    public final Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

    protected final LinkedQueueAtomicNode<E> newNode() {
        return new LinkedQueueAtomicNode<E>();
    }

    protected final LinkedQueueAtomicNode<E> newNode(E e) {
        return new LinkedQueueAtomicNode<E>(e);
    }

    @Override
    public final int size() {
        LinkedQueueAtomicNode<E> chaserNode = this.lvConsumerNode();
        LinkedQueueAtomicNode<E> producerNode = this.lvProducerNode();
        int size = 0;
        while (chaserNode != producerNode) {
            if (chaserNode == null) return size;
            if (size >= Integer.MAX_VALUE) return size;
            LinkedQueueAtomicNode<E> next = chaserNode.lvNext();
            if (next == chaserNode) {
                return size;
            }
            chaserNode = next;
            ++size;
        }
        return size;
    }

    @Override
    public final boolean isEmpty() {
        if (this.lvConsumerNode() != this.lvProducerNode()) return false;
        return true;
    }

    protected E getSingleConsumerNodeValue(LinkedQueueAtomicNode<E> currConsumerNode, LinkedQueueAtomicNode<E> nextNode) {
        E nextValue = nextNode.getAndNullValue();
        currConsumerNode.soNext(currConsumerNode);
        this.spConsumerNode(nextNode);
        return (E)nextValue;
    }

    @Override
    public E relaxedPoll() {
        LinkedQueueAtomicNode<E> currConsumerNode = this.lpConsumerNode();
        LinkedQueueAtomicNode<E> nextNode = currConsumerNode.lvNext();
        if (nextNode == null) return (E)null;
        return (E)this.getSingleConsumerNodeValue(currConsumerNode, nextNode);
    }

    @Override
    public E relaxedPeek() {
        LinkedQueueAtomicNode<E> nextNode = this.lpConsumerNode().lvNext();
        if (nextNode == null) return (E)null;
        return (E)nextNode.lpValue();
    }

    @Override
    public boolean relaxedOffer(E e) {
        return this.offer(e);
    }

    @Override
    public int drain(MessagePassingQueue.Consumer<E> c) {
        int drained;
        long result = 0L;
        do {
            drained = this.drain(c, (int)4096);
            if (drained != 4096) return (int)result;
        } while ((result += (long)drained) <= 2147479551L);
        return (int)result;
    }

    @Override
    public int drain(MessagePassingQueue.Consumer<E> c, int limit) {
        LinkedQueueAtomicNode<E> chaserNode = this.consumerNode;
        int i = 0;
        while (i < limit) {
            LinkedQueueAtomicNode<E> nextNode = chaserNode.lvNext();
            if (nextNode == null) {
                return i;
            }
            E nextValue = this.getSingleConsumerNodeValue(chaserNode, nextNode);
            chaserNode = nextNode;
            c.accept(nextValue);
            ++i;
        }
        return limit;
    }

    @Override
    public void drain(MessagePassingQueue.Consumer<E> c, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit) {
        LinkedQueueAtomicNode<E> chaserNode = this.consumerNode;
        int idleCounter = 0;
        block0 : while (exit.keepRunning()) {
            int i = 0;
            do {
                if (i >= 4096) continue block0;
                LinkedQueueAtomicNode<E> nextNode = chaserNode.lvNext();
                if (nextNode == null) {
                    idleCounter = wait.idle((int)idleCounter);
                } else {
                    idleCounter = 0;
                    E nextValue = this.getSingleConsumerNodeValue(chaserNode, nextNode);
                    chaserNode = nextNode;
                    c.accept(nextValue);
                }
                ++i;
            } while (true);
            break;
        }
        return;
    }

    @Override
    public int capacity() {
        return -1;
    }
}

