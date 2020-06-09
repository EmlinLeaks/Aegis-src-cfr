/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.fusesource.hawtjni.runtime;

public class Callback {
    Object object;
    String method;
    String signature;
    int argCount;
    long address;
    long errorResult;
    boolean isStatic;
    boolean isArrayBased;
    static final String PTR_SIGNATURE = "J";
    static final String SIGNATURE_0 = Callback.getSignature((int)0);
    static final String SIGNATURE_1 = Callback.getSignature((int)1);
    static final String SIGNATURE_2 = Callback.getSignature((int)2);
    static final String SIGNATURE_3 = Callback.getSignature((int)3);
    static final String SIGNATURE_4 = Callback.getSignature((int)4);
    static final String SIGNATURE_N = "([J)J";

    public Callback(Object object, String method, int argCount) {
        this((Object)object, (String)method, (int)argCount, (boolean)false);
    }

    public Callback(Object object, String method, int argCount, boolean isArrayBased) {
        this((Object)object, (String)method, (int)argCount, (boolean)isArrayBased, (long)0L);
    }

    /*
     * Unable to fully structure code
     */
    public Callback(Object object, String method, int argCount, boolean isArrayBased, long errorResult) {
        super();
        this.object = object;
        this.method = method;
        this.argCount = argCount;
        this.isStatic = object instanceof Class;
        this.isArrayBased = isArrayBased;
        this.errorResult = errorResult;
        if (isArrayBased) {
            this.signature = "([J)J";
        } else {
            switch (argCount) {
                case 0: {
                    this.signature = Callback.SIGNATURE_0;
                    ** break;
                }
                case 1: {
                    this.signature = Callback.SIGNATURE_1;
                    ** break;
                }
                case 2: {
                    this.signature = Callback.SIGNATURE_2;
                    ** break;
                }
                case 3: {
                    this.signature = Callback.SIGNATURE_3;
                    ** break;
                }
                case 4: {
                    this.signature = Callback.SIGNATURE_4;
                    ** break;
                }
            }
            this.signature = Callback.getSignature((int)argCount);
        }
lbl28: // 7 sources:
        this.address = Callback.bind((Callback)this, (Object)object, (String)method, (String)this.signature, (int)argCount, (boolean)this.isStatic, (boolean)isArrayBased, (long)errorResult);
    }

    static synchronized native long bind(Callback var0, Object var1, String var2, String var3, int var4, boolean var5, boolean var6, long var7);

    public void dispose() {
        if (this.object == null) {
            return;
        }
        Callback.unbind((Callback)this);
        this.signature = null;
        this.method = null;
        this.object = null;
        this.address = 0L;
    }

    public long getAddress() {
        return this.address;
    }

    public static native String getPlatform();

    public static native int getEntryCount();

    static String getSignature(int argCount) {
        String signature = "(";
        int i = 0;
        while (i < argCount) {
            signature = signature + "J";
            ++i;
        }
        return signature + ")J";
    }

    public static final synchronized native void setEnabled(boolean var0);

    public static final synchronized native boolean getEnabled();

    public static final synchronized native void reset();

    static final synchronized native void unbind(Callback var0);
}

