/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyHeaderBlockDecoder;
import io.netty.handler.codec.spdy.SpdyHeaderBlockRawDecoder;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyVersion;

public class SpdyHeaderBlockRawDecoder
extends SpdyHeaderBlockDecoder {
    private static final int LENGTH_FIELD_SIZE = 4;
    private final int maxHeaderSize;
    private State state;
    private ByteBuf cumulation;
    private int headerSize;
    private int numHeaders;
    private int length;
    private String name;

    public SpdyHeaderBlockRawDecoder(SpdyVersion spdyVersion, int maxHeaderSize) {
        if (spdyVersion == null) {
            throw new NullPointerException((String)"spdyVersion");
        }
        this.maxHeaderSize = maxHeaderSize;
        this.state = State.READ_NUM_HEADERS;
    }

    private static int readLengthField(ByteBuf buffer) {
        int length = SpdyCodecUtil.getSignedInt((ByteBuf)buffer, (int)buffer.readerIndex());
        buffer.skipBytes((int)4);
        return length;
    }

    @Override
    void decode(ByteBufAllocator alloc, ByteBuf headerBlock, SpdyHeadersFrame frame) throws Exception {
        if (headerBlock == null) {
            throw new NullPointerException((String)"headerBlock");
        }
        if (frame == null) {
            throw new NullPointerException((String)"frame");
        }
        if (this.cumulation == null) {
            this.decodeHeaderBlock((ByteBuf)headerBlock, (SpdyHeadersFrame)frame);
            if (!headerBlock.isReadable()) return;
            this.cumulation = alloc.buffer((int)headerBlock.readableBytes());
            this.cumulation.writeBytes((ByteBuf)headerBlock);
            return;
        }
        this.cumulation.writeBytes((ByteBuf)headerBlock);
        this.decodeHeaderBlock((ByteBuf)this.cumulation, (SpdyHeadersFrame)frame);
        if (this.cumulation.isReadable()) {
            this.cumulation.discardReadBytes();
            return;
        }
        this.releaseBuffer();
    }

    /*
     * Exception decompiling
     */
    protected void decodeHeaderBlock(ByteBuf headerBlock, SpdyHeadersFrame frame) throws Exception {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:404)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:482)
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

    @Override
    void endHeaderBlock(SpdyHeadersFrame frame) throws Exception {
        if (this.state != State.END_HEADER_BLOCK) {
            frame.setInvalid();
        }
        this.releaseBuffer();
        this.headerSize = 0;
        this.name = null;
        this.state = State.READ_NUM_HEADERS;
    }

    @Override
    void end() {
        this.releaseBuffer();
    }

    private void releaseBuffer() {
        if (this.cumulation == null) return;
        this.cumulation.release();
        this.cumulation = null;
    }
}

