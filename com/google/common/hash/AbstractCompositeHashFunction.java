/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.hash.AbstractCompositeHashFunction;
import com.google.common.hash.AbstractStreamingHashFunction;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;

abstract class AbstractCompositeHashFunction
extends AbstractStreamingHashFunction {
    final HashFunction[] functions;
    private static final long serialVersionUID = 0L;

    AbstractCompositeHashFunction(HashFunction ... functions) {
        HashFunction[] arr$ = functions;
        int len$ = arr$.length;
        int i$ = 0;
        do {
            if (i$ >= len$) {
                this.functions = functions;
                return;
            }
            HashFunction function = arr$[i$];
            Preconditions.checkNotNull(function);
            ++i$;
        } while (true);
    }

    abstract HashCode makeHash(Hasher[] var1);

    @Override
    public Hasher newHasher() {
        Hasher[] hashers = new Hasher[this.functions.length];
        int i = 0;
        while (i < hashers.length) {
            hashers[i] = this.functions[i].newHasher();
            ++i;
        }
        return new Hasher((AbstractCompositeHashFunction)this, (Hasher[])hashers){
            final /* synthetic */ Hasher[] val$hashers;
            final /* synthetic */ AbstractCompositeHashFunction this$0;
            {
                this.this$0 = abstractCompositeHashFunction;
                this.val$hashers = arrhasher;
            }

            public Hasher putByte(byte b) {
                Hasher[] arr$ = this.val$hashers;
                int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    Hasher hasher = arr$[i$];
                    hasher.putByte((byte)b);
                    ++i$;
                }
                return this;
            }

            public Hasher putBytes(byte[] bytes) {
                Hasher[] arr$ = this.val$hashers;
                int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    Hasher hasher = arr$[i$];
                    hasher.putBytes((byte[])bytes);
                    ++i$;
                }
                return this;
            }

            public Hasher putBytes(byte[] bytes, int off, int len) {
                Hasher[] arr$ = this.val$hashers;
                int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    Hasher hasher = arr$[i$];
                    hasher.putBytes((byte[])bytes, (int)off, (int)len);
                    ++i$;
                }
                return this;
            }

            public Hasher putShort(short s) {
                Hasher[] arr$ = this.val$hashers;
                int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    Hasher hasher = arr$[i$];
                    hasher.putShort((short)s);
                    ++i$;
                }
                return this;
            }

            public Hasher putInt(int i) {
                Hasher[] arr$ = this.val$hashers;
                int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    Hasher hasher = arr$[i$];
                    hasher.putInt((int)i);
                    ++i$;
                }
                return this;
            }

            public Hasher putLong(long l) {
                Hasher[] arr$ = this.val$hashers;
                int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    Hasher hasher = arr$[i$];
                    hasher.putLong((long)l);
                    ++i$;
                }
                return this;
            }

            public Hasher putFloat(float f) {
                Hasher[] arr$ = this.val$hashers;
                int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    Hasher hasher = arr$[i$];
                    hasher.putFloat((float)f);
                    ++i$;
                }
                return this;
            }

            public Hasher putDouble(double d) {
                Hasher[] arr$ = this.val$hashers;
                int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    Hasher hasher = arr$[i$];
                    hasher.putDouble((double)d);
                    ++i$;
                }
                return this;
            }

            public Hasher putBoolean(boolean b) {
                Hasher[] arr$ = this.val$hashers;
                int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    Hasher hasher = arr$[i$];
                    hasher.putBoolean((boolean)b);
                    ++i$;
                }
                return this;
            }

            public Hasher putChar(char c) {
                Hasher[] arr$ = this.val$hashers;
                int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    Hasher hasher = arr$[i$];
                    hasher.putChar((char)c);
                    ++i$;
                }
                return this;
            }

            public Hasher putUnencodedChars(java.lang.CharSequence chars) {
                Hasher[] arr$ = this.val$hashers;
                int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    Hasher hasher = arr$[i$];
                    hasher.putUnencodedChars((java.lang.CharSequence)chars);
                    ++i$;
                }
                return this;
            }

            public Hasher putString(java.lang.CharSequence chars, java.nio.charset.Charset charset) {
                Hasher[] arr$ = this.val$hashers;
                int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    Hasher hasher = arr$[i$];
                    hasher.putString((java.lang.CharSequence)chars, (java.nio.charset.Charset)charset);
                    ++i$;
                }
                return this;
            }

            public <T> Hasher putObject(T instance, com.google.common.hash.Funnel<? super T> funnel) {
                Hasher[] arr$ = this.val$hashers;
                int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    Hasher hasher = arr$[i$];
                    hasher.putObject(instance, funnel);
                    ++i$;
                }
                return this;
            }

            public HashCode hash() {
                return this.this$0.makeHash((Hasher[])this.val$hashers);
            }
        };
    }
}

