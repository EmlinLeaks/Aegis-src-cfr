/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.ssl.PemEncoded;
import io.netty.handler.ssl.SslUtils;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.CharsetUtil;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import java.nio.charset.Charset;
import java.security.PrivateKey;

public final class PemPrivateKey
extends AbstractReferenceCounted
implements PrivateKey,
PemEncoded {
    private static final long serialVersionUID = 7978017465645018936L;
    private static final byte[] BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n".getBytes((Charset)CharsetUtil.US_ASCII);
    private static final byte[] END_PRIVATE_KEY = "\n-----END PRIVATE KEY-----\n".getBytes((Charset)CharsetUtil.US_ASCII);
    private static final String PKCS8_FORMAT = "PKCS#8";
    private final ByteBuf content;

    static PemEncoded toPEM(ByteBufAllocator allocator, boolean useDirect, PrivateKey key) {
        if (key instanceof PemEncoded) {
            return ((PemEncoded)((Object)key)).retain();
        }
        byte[] bytes = key.getEncoded();
        if (bytes != null) return PemPrivateKey.toPEM((ByteBufAllocator)allocator, (boolean)useDirect, (byte[])bytes);
        throw new IllegalArgumentException((String)(key.getClass().getName() + " does not support encoding"));
    }

    /*
     * Exception decompiling
     */
    static PemEncoded toPEM(ByteBufAllocator allocator, boolean useDirect, byte[] bytes) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 3[TRYBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    public static PemPrivateKey valueOf(byte[] key) {
        return PemPrivateKey.valueOf((ByteBuf)Unpooled.wrappedBuffer((byte[])key));
    }

    public static PemPrivateKey valueOf(ByteBuf key) {
        return new PemPrivateKey((ByteBuf)key);
    }

    private PemPrivateKey(ByteBuf content) {
        this.content = ObjectUtil.checkNotNull(content, (String)"content");
    }

    @Override
    public boolean isSensitive() {
        return true;
    }

    @Override
    public ByteBuf content() {
        int count = this.refCnt();
        if (count > 0) return this.content;
        throw new IllegalReferenceCountException((int)count);
    }

    @Override
    public PemPrivateKey copy() {
        return this.replace((ByteBuf)this.content.copy());
    }

    @Override
    public PemPrivateKey duplicate() {
        return this.replace((ByteBuf)this.content.duplicate());
    }

    @Override
    public PemPrivateKey retainedDuplicate() {
        return this.replace((ByteBuf)this.content.retainedDuplicate());
    }

    @Override
    public PemPrivateKey replace(ByteBuf content) {
        return new PemPrivateKey((ByteBuf)content);
    }

    @Override
    public PemPrivateKey touch() {
        this.content.touch();
        return this;
    }

    @Override
    public PemPrivateKey touch(Object hint) {
        this.content.touch((Object)hint);
        return this;
    }

    @Override
    public PemPrivateKey retain() {
        return (PemPrivateKey)super.retain();
    }

    @Override
    public PemPrivateKey retain(int increment) {
        return (PemPrivateKey)super.retain((int)increment);
    }

    @Override
    protected void deallocate() {
        SslUtils.zerooutAndRelease((ByteBuf)this.content);
    }

    @Override
    public byte[] getEncoded() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAlgorithm() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFormat() {
        return PKCS8_FORMAT;
    }

    @Override
    public void destroy() {
        this.release((int)this.refCnt());
    }

    @Override
    public boolean isDestroyed() {
        if (this.refCnt() != 0) return false;
        return true;
    }
}

