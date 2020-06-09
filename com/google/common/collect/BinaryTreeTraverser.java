/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.BinaryTreeTraverser;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.TreeTraverser;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Deque;

@Beta
@GwtCompatible
public abstract class BinaryTreeTraverser<T>
extends TreeTraverser<T> {
    public abstract Optional<T> leftChild(T var1);

    public abstract Optional<T> rightChild(T var1);

    @Override
    public final Iterable<T> children(T root) {
        Preconditions.checkNotNull(root);
        return new FluentIterable<T>((BinaryTreeTraverser)this, root){
            final /* synthetic */ Object val$root;
            final /* synthetic */ BinaryTreeTraverser this$0;
            {
                this.this$0 = binaryTreeTraverser;
                this.val$root = object;
            }

            public java.util.Iterator<T> iterator() {
                return new com.google.common.collect.AbstractIterator<T>(this){
                    boolean doneLeft;
                    boolean doneRight;
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = var1_1;
                    }

                    protected T computeNext() {
                        if (!this.doneLeft) {
                            this.doneLeft = true;
                            Optional<Object> left = this.this$1.this$0.leftChild(this.this$1.val$root);
                            if (left.isPresent()) {
                                return (T)left.get();
                            }
                        }
                        if (this.doneRight) return (T)this.endOfData();
                        this.doneRight = true;
                        Optional<Object> right = this.this$1.this$0.rightChild(this.this$1.val$root);
                        if (!right.isPresent()) return (T)this.endOfData();
                        return (T)right.get();
                    }
                };
            }
        };
    }

    @Override
    UnmodifiableIterator<T> preOrderIterator(T root) {
        return new PreOrderIterator((BinaryTreeTraverser)this, root);
    }

    @Override
    UnmodifiableIterator<T> postOrderIterator(T root) {
        return new PostOrderIterator((BinaryTreeTraverser)this, root);
    }

    public final FluentIterable<T> inOrderTraversal(T root) {
        Preconditions.checkNotNull(root);
        return new FluentIterable<T>((BinaryTreeTraverser)this, root){
            final /* synthetic */ Object val$root;
            final /* synthetic */ BinaryTreeTraverser this$0;
            {
                this.this$0 = binaryTreeTraverser;
                this.val$root = object;
            }

            public UnmodifiableIterator<T> iterator() {
                return new com.google.common.collect.BinaryTreeTraverser$InOrderIterator((BinaryTreeTraverser)this.this$0, this.val$root);
            }
        };
    }

    private static <T> void pushIfPresent(Deque<T> stack, Optional<T> node) {
        if (!node.isPresent()) return;
        stack.addLast(node.get());
    }

    static /* synthetic */ void access$000(Deque x0, Optional x1) {
        BinaryTreeTraverser.pushIfPresent(x0, x1);
    }
}

