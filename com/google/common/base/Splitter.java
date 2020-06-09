/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.CharMatcher;
import com.google.common.base.CommonMatcher;
import com.google.common.base.CommonPattern;
import com.google.common.base.JdkPattern;
import com.google.common.base.Platform;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@GwtCompatible(emulated=true)
public final class Splitter {
    private final CharMatcher trimmer;
    private final boolean omitEmptyStrings;
    private final Strategy strategy;
    private final int limit;

    private Splitter(Strategy strategy) {
        this((Strategy)strategy, (boolean)false, (CharMatcher)CharMatcher.none(), (int)Integer.MAX_VALUE);
    }

    private Splitter(Strategy strategy, boolean omitEmptyStrings, CharMatcher trimmer, int limit) {
        this.strategy = strategy;
        this.omitEmptyStrings = omitEmptyStrings;
        this.trimmer = trimmer;
        this.limit = limit;
    }

    public static Splitter on(char separator) {
        return Splitter.on((CharMatcher)CharMatcher.is((char)separator));
    }

    public static Splitter on(CharMatcher separatorMatcher) {
        Preconditions.checkNotNull(separatorMatcher);
        return new Splitter((Strategy)new Strategy((CharMatcher)separatorMatcher){
            final /* synthetic */ CharMatcher val$separatorMatcher;
            {
                this.val$separatorMatcher = charMatcher;
            }

            public com.google.common.base.Splitter$SplittingIterator iterator(Splitter splitter, CharSequence toSplit) {
                return new com.google.common.base.Splitter$SplittingIterator(this, (Splitter)splitter, (CharSequence)toSplit){
                    final /* synthetic */ 1 this$0;
                    {
                        this.this$0 = var1_1;
                        super((Splitter)x0, (CharSequence)x1);
                    }

                    int separatorStart(int start) {
                        return this.this$0.val$separatorMatcher.indexIn((CharSequence)this.toSplit, (int)start);
                    }

                    int separatorEnd(int separatorPosition) {
                        return separatorPosition + 1;
                    }
                };
            }
        });
    }

    public static Splitter on(String separator) {
        Preconditions.checkArgument((boolean)(separator.length() != 0), (Object)"The separator may not be the empty string.");
        if (separator.length() != 1) return new Splitter((Strategy)new Strategy((String)separator){
            final /* synthetic */ String val$separator;
            {
                this.val$separator = string;
            }

            public com.google.common.base.Splitter$SplittingIterator iterator(Splitter splitter, CharSequence toSplit) {
                return new com.google.common.base.Splitter$SplittingIterator(this, (Splitter)splitter, (CharSequence)toSplit){
                    final /* synthetic */ 2 this$0;
                    {
                        this.this$0 = var1_1;
                        super((Splitter)x0, (CharSequence)x1);
                    }

                    public int separatorStart(int start) {
                        int separatorLength = this.this$0.val$separator.length();
                        int p = start;
                        int last = this.toSplit.length() - separatorLength;
                        block0 : while (p <= last) {
                            int i = 0;
                            while (i < separatorLength) {
                                if (this.toSplit.charAt((int)(i + p)) != this.this$0.val$separator.charAt((int)i)) {
                                    ++p;
                                    continue block0;
                                }
                                ++i;
                            }
                            return p;
                            break;
                        }
                        return -1;
                    }

                    public int separatorEnd(int separatorPosition) {
                        return separatorPosition + this.this$0.val$separator.length();
                    }
                };
            }
        });
        return Splitter.on((char)separator.charAt((int)0));
    }

    @GwtIncompatible
    public static Splitter on(Pattern separatorPattern) {
        return Splitter.on((CommonPattern)new JdkPattern((Pattern)separatorPattern));
    }

    private static Splitter on(CommonPattern separatorPattern) {
        Preconditions.checkArgument((boolean)(!separatorPattern.matcher((CharSequence)"").matches()), (String)"The pattern may not match the empty string: %s", (Object)separatorPattern);
        return new Splitter((Strategy)new Strategy((CommonPattern)separatorPattern){
            final /* synthetic */ CommonPattern val$separatorPattern;
            {
                this.val$separatorPattern = commonPattern;
            }

            public com.google.common.base.Splitter$SplittingIterator iterator(Splitter splitter, CharSequence toSplit) {
                CommonMatcher matcher = this.val$separatorPattern.matcher((CharSequence)toSplit);
                return new com.google.common.base.Splitter$SplittingIterator(this, (Splitter)splitter, (CharSequence)toSplit, (CommonMatcher)matcher){
                    final /* synthetic */ CommonMatcher val$matcher;
                    final /* synthetic */ 3 this$0;
                    {
                        this.this$0 = var1_1;
                        this.val$matcher = commonMatcher;
                        super((Splitter)x0, (CharSequence)x1);
                    }

                    public int separatorStart(int start) {
                        if (!this.val$matcher.find((int)start)) return -1;
                        int n = this.val$matcher.start();
                        return n;
                    }

                    public int separatorEnd(int separatorPosition) {
                        return this.val$matcher.end();
                    }
                };
            }
        });
    }

    @GwtIncompatible
    public static Splitter onPattern(String separatorPattern) {
        return Splitter.on((CommonPattern)Platform.compilePattern((String)separatorPattern));
    }

    public static Splitter fixedLength(int length) {
        Preconditions.checkArgument((boolean)(length > 0), (Object)"The length may not be less than 1");
        return new Splitter((Strategy)new Strategy((int)length){
            final /* synthetic */ int val$length;
            {
                this.val$length = n;
            }

            public com.google.common.base.Splitter$SplittingIterator iterator(Splitter splitter, CharSequence toSplit) {
                return new com.google.common.base.Splitter$SplittingIterator(this, (Splitter)splitter, (CharSequence)toSplit){
                    final /* synthetic */ 4 this$0;
                    {
                        this.this$0 = var1_1;
                        super((Splitter)x0, (CharSequence)x1);
                    }

                    public int separatorStart(int start) {
                        int nextChunkStart = start + this.this$0.val$length;
                        if (nextChunkStart >= this.toSplit.length()) return -1;
                        int n = nextChunkStart;
                        return n;
                    }

                    public int separatorEnd(int separatorPosition) {
                        return separatorPosition;
                    }
                };
            }
        });
    }

    public Splitter omitEmptyStrings() {
        return new Splitter((Strategy)this.strategy, (boolean)true, (CharMatcher)this.trimmer, (int)this.limit);
    }

    public Splitter limit(int limit) {
        Preconditions.checkArgument((boolean)(limit > 0), (String)"must be greater than zero: %s", (int)limit);
        return new Splitter((Strategy)this.strategy, (boolean)this.omitEmptyStrings, (CharMatcher)this.trimmer, (int)limit);
    }

    public Splitter trimResults() {
        return this.trimResults((CharMatcher)CharMatcher.whitespace());
    }

    public Splitter trimResults(CharMatcher trimmer) {
        Preconditions.checkNotNull(trimmer);
        return new Splitter((Strategy)this.strategy, (boolean)this.omitEmptyStrings, (CharMatcher)trimmer, (int)this.limit);
    }

    public Iterable<String> split(CharSequence sequence) {
        Preconditions.checkNotNull(sequence);
        return new Iterable<String>((Splitter)this, (CharSequence)sequence){
            final /* synthetic */ CharSequence val$sequence;
            final /* synthetic */ Splitter this$0;
            {
                this.this$0 = splitter;
                this.val$sequence = charSequence;
            }

            public Iterator<String> iterator() {
                return Splitter.access$000((Splitter)this.this$0, (CharSequence)this.val$sequence);
            }

            public String toString() {
                return com.google.common.base.Joiner.on((String)", ").appendTo((java.lang.StringBuilder)new java.lang.StringBuilder().append((char)'['), this).append((char)']').toString();
            }
        };
    }

    private Iterator<String> splittingIterator(CharSequence sequence) {
        return this.strategy.iterator((Splitter)this, (CharSequence)sequence);
    }

    @Beta
    public List<String> splitToList(CharSequence sequence) {
        Preconditions.checkNotNull(sequence);
        Iterator<String> iterator = this.splittingIterator((CharSequence)sequence);
        ArrayList<String> result = new ArrayList<String>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return Collections.unmodifiableList(result);
    }

    @Beta
    public MapSplitter withKeyValueSeparator(String separator) {
        return this.withKeyValueSeparator((Splitter)Splitter.on((String)separator));
    }

    @Beta
    public MapSplitter withKeyValueSeparator(char separator) {
        return this.withKeyValueSeparator((Splitter)Splitter.on((char)separator));
    }

    @Beta
    public MapSplitter withKeyValueSeparator(Splitter keyValueSplitter) {
        return new MapSplitter((Splitter)this, (Splitter)keyValueSplitter, null);
    }

    static /* synthetic */ Iterator access$000(Splitter x0, CharSequence x1) {
        return x0.splittingIterator((CharSequence)x1);
    }

    static /* synthetic */ CharMatcher access$200(Splitter x0) {
        return x0.trimmer;
    }

    static /* synthetic */ boolean access$300(Splitter x0) {
        return x0.omitEmptyStrings;
    }

    static /* synthetic */ int access$400(Splitter x0) {
        return x0.limit;
    }
}

