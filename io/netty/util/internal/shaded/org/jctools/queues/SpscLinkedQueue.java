/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.BaseLinkedQueue;
import io.netty.util.internal.shaded.org.jctools.queues.LinkedQueueNode;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;

public class SpscLinkedQueue<E>
extends BaseLinkedQueue<E> {
    public SpscLinkedQueue() {
        LinkedQueueNode<E> node = this.newNode();
        this.spProducerNode(node);
        this.spConsumerNode(node);
        node.soNext(null);
    }

    @Override
    public boolean offer(E e) {
        if (null == e) {
            throw new NullPointerException();
        }
        LinkedQueueNode<E> nextNode = this.newNode(e);
        this.lpProducerNode().soNext(nextNode);
        this.spProducerNode(nextNode);
        return true;
    }

    @Override
    public E poll() {
        return (E)this.relaxedPoll();
    }

    @Override
    public E peek() {
        return (E)this.relaxedPeek();
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s) {
        long result = 0L;
        do {
            this.fill(s, (int)4096);
        } while ((result += 4096L) <= 2147479551L);
        return (int)result;
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s, int limit) {
        LinkedQueueNode<E> tail;
        if (limit == 0) {
            return 0;
        }
        LinkedQueueNode<E> head = tail = this.newNode(s.get());
        int i = 1;
        do {
            if (i >= limit) {
                LinkedQueueNode<E> oldPNode = this.lpProducerNode();
                oldPNode.soNext(head);
                this.spProducerNode(tail);
                return limit;
            }
            LinkedQueueNode<E> temp = this.newNode(s.get());
            tail.soNext(temp);
            tail = temp;
            ++i;
        } while (true);
    }

    @Override
    public void fill(MessagePassingQueue.Supplier<E> s, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit) {
        LinkedQueueNode<E> chaserNode = this.producerNode;
        block0 : while (exit.keepRunning()) {
            int i = 0;
            do {
                if (i >= 4096) continue block0;
                LinkedQueueNode<E> nextNode = this.newNode(s.get());
                chaserNode.soNext(nextNode);
                this.producerNode = chaserNode = nextNode;
                ++i;
            } while (true);
            break;
        }
        return;
    }
}

