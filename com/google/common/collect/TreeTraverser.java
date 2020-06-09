/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.TreeTraverser;
import com.google.common.collect.UnmodifiableIterator;

@Beta
@GwtCompatible
public abstract class TreeTraverser<T> {
    public static <T> TreeTraverser<T> using(Function<T, ? extends Iterable<T>> nodeToChildrenFunction) {
        Preconditions.checkNotNull(nodeToChildrenFunction);
        return new TreeTraverser<T>(nodeToChildrenFunction){
            final /* synthetic */ Function val$nodeToChildrenFunction;
            {
                this.val$nodeToChildrenFunction = function;
            }

            public Iterable<T> children(T root) {
                return (Iterable)this.val$nodeToChildrenFunction.apply(root);
            }
        };
    }

    public abstract Iterable<T> children(T var1);

    public final FluentIterable<T> preOrderTraversal(T root) {
        Preconditions.checkNotNull(root);
        return new FluentIterable<T>((TreeTraverser)this, root){
            final /* synthetic */ Object val$root;
            final /* synthetic */ TreeTraverser this$0;
            {
                this.this$0 = treeTraverser;
                this.val$root = object;
            }

            public UnmodifiableIterator<T> iterator() {
                return this.this$0.preOrderIterator(this.val$root);
            }
        };
    }

    UnmodifiableIterator<T> preOrderIterator(T root) {
        return new PreOrderIterator((TreeTraverser)this, root);
    }

    public final FluentIterable<T> postOrderTraversal(T root) {
        Preconditions.checkNotNull(root);
        return new FluentIterable<T>((TreeTraverser)this, root){
            final /* synthetic */ Object val$root;
            final /* synthetic */ TreeTraverser this$0;
            {
                this.this$0 = treeTraverser;
                this.val$root = object;
            }

            public UnmodifiableIterator<T> iterator() {
                return this.this$0.postOrderIterator(this.val$root);
            }
        };
    }

    UnmodifiableIterator<T> postOrderIterator(T root) {
        return new PostOrderIterator((TreeTraverser)this, root);
    }

    public final FluentIterable<T> breadthFirstTraversal(T root) {
        Preconditions.checkNotNull(root);
        return new FluentIterable<T>((TreeTraverser)this, root){
            final /* synthetic */ Object val$root;
            final /* synthetic */ TreeTraverser this$0;
            {
                this.this$0 = treeTraverser;
                this.val$root = object;
            }

            public UnmodifiableIterator<T> iterator() {
                return new com.google.common.collect.TreeTraverser$BreadthFirstIterator((TreeTraverser)this.this$0, this.val$root);
            }
        };
    }
}

