/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.jcraft.jzlib.Inflater
 *  com.jcraft.jzlib.JZlib
 *  com.jcraft.jzlib.JZlib$WrapperType
 */
package io.netty.handler.codec.compression;

import com.jcraft.jzlib.Inflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.compression.ZlibDecoder;
import io.netty.handler.codec.compression.ZlibUtil;
import io.netty.handler.codec.compression.ZlibWrapper;
import java.util.List;

public class JZlibDecoder
extends ZlibDecoder {
    private final Inflater z = new Inflater();
    private byte[] dictionary;
    private volatile boolean finished;

    public JZlibDecoder() {
        this((ZlibWrapper)ZlibWrapper.ZLIB);
    }

    public JZlibDecoder(ZlibWrapper wrapper) {
        if (wrapper == null) {
            throw new NullPointerException((String)"wrapper");
        }
        int resultCode = this.z.init((JZlib.WrapperType)ZlibUtil.convertWrapperType((ZlibWrapper)wrapper));
        if (resultCode == 0) return;
        ZlibUtil.fail((Inflater)this.z, (String)"initialization failure", (int)resultCode);
    }

    public JZlibDecoder(byte[] dictionary) {
        if (dictionary == null) {
            throw new NullPointerException((String)"dictionary");
        }
        this.dictionary = dictionary;
        int resultCode = this.z.inflateInit((JZlib.WrapperType)JZlib.W_ZLIB);
        if (resultCode == 0) return;
        ZlibUtil.fail((Inflater)this.z, (String)"initialization failure", (int)resultCode);
    }

    @Override
    public boolean isClosed() {
        return this.finished;
    }

    /*
     * Exception decompiling
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Extractable last case doesn't follow previous
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.examineSwitchContiguity(SwitchReplacer.java:478)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.rebuildSwitches(SwitchReplacer.java:328)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:466)
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
}

