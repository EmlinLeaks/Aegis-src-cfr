/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.compression.ByteBufChecksum;
import io.netty.handler.codec.compression.DecompressionException;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.ZlibDecoder;
import io.netty.handler.codec.compression.ZlibWrapper;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class JdkZlibDecoder
extends ZlibDecoder {
    private static final int FHCRC = 2;
    private static final int FEXTRA = 4;
    private static final int FNAME = 8;
    private static final int FCOMMENT = 16;
    private static final int FRESERVED = 224;
    private Inflater inflater;
    private final byte[] dictionary;
    private final ByteBufChecksum crc;
    private final boolean decompressConcatenated;
    private GzipState gzipState = GzipState.HEADER_START;
    private int flags = -1;
    private int xlen = -1;
    private volatile boolean finished;
    private boolean decideZlibOrNone;

    public JdkZlibDecoder() {
        this((ZlibWrapper)ZlibWrapper.ZLIB, null, (boolean)false);
    }

    public JdkZlibDecoder(byte[] dictionary) {
        this((ZlibWrapper)ZlibWrapper.ZLIB, (byte[])dictionary, (boolean)false);
    }

    public JdkZlibDecoder(ZlibWrapper wrapper) {
        this((ZlibWrapper)wrapper, null, (boolean)false);
    }

    public JdkZlibDecoder(ZlibWrapper wrapper, boolean decompressConcatenated) {
        this((ZlibWrapper)wrapper, null, (boolean)decompressConcatenated);
    }

    public JdkZlibDecoder(boolean decompressConcatenated) {
        this((ZlibWrapper)ZlibWrapper.GZIP, null, (boolean)decompressConcatenated);
    }

    /*
     * Unable to fully structure code
     */
    private JdkZlibDecoder(ZlibWrapper wrapper, byte[] dictionary, boolean decompressConcatenated) {
        super();
        if (wrapper == null) {
            throw new NullPointerException((String)"wrapper");
        }
        this.decompressConcatenated = decompressConcatenated;
        switch (1.$SwitchMap$io$netty$handler$codec$compression$ZlibWrapper[wrapper.ordinal()]) {
            case 1: {
                this.inflater = new Inflater((boolean)true);
                this.crc = ByteBufChecksum.wrapChecksum((Checksum)new CRC32());
                ** break;
            }
            case 2: {
                this.inflater = new Inflater((boolean)true);
                this.crc = null;
                ** break;
            }
            case 3: {
                this.inflater = new Inflater();
                this.crc = null;
                ** break;
            }
            case 4: {
                this.decideZlibOrNone = true;
                this.crc = null;
                ** break;
            }
        }
        throw new IllegalArgumentException((String)("Only GZIP or ZLIB is supported, but you used " + (Object)wrapper));
lbl26: // 4 sources:
        this.dictionary = dictionary;
    }

    @Override
    public boolean isClosed() {
        return this.finished;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (this.finished) {
            in.skipBytes((int)in.readableBytes());
            return;
        }
        int readableBytes = in.readableBytes();
        if (readableBytes == 0) {
            return;
        }
        if (this.decideZlibOrNone) {
            if (readableBytes < 2) {
                return;
            }
            boolean nowrap = !JdkZlibDecoder.looksLikeZlib((short)in.getShort((int)in.readerIndex()));
            this.inflater = new Inflater((boolean)nowrap);
            this.decideZlibOrNone = false;
        }
        if (this.crc != null) {
            switch (this.gzipState) {
                case FOOTER_START: {
                    if (!this.readGZIPFooter((ByteBuf)in)) return;
                    this.finished = true;
                    return;
                }
            }
            if (this.gzipState != GzipState.HEADER_END && !this.readGZIPHeader((ByteBuf)in)) {
                return;
            }
            readableBytes = in.readableBytes();
        }
        if (in.hasArray()) {
            this.inflater.setInput((byte[])in.array(), (int)(in.arrayOffset() + in.readerIndex()), (int)readableBytes);
        } else {
            byte[] array = new byte[readableBytes];
            in.getBytes((int)in.readerIndex(), (byte[])array);
            this.inflater.setInput((byte[])array);
        }
        ByteBuf decompressed = ctx.alloc().heapBuffer((int)(this.inflater.getRemaining() << 1));
        try {
            boolean readFooter = false;
            while (!this.inflater.needsInput()) {
                byte[] outArray = decompressed.array();
                int writerIndex = decompressed.writerIndex();
                int outIndex = decompressed.arrayOffset() + writerIndex;
                int outputLength = this.inflater.inflate((byte[])outArray, (int)outIndex, (int)decompressed.writableBytes());
                if (outputLength > 0) {
                    decompressed.writerIndex((int)(writerIndex + outputLength));
                    if (this.crc != null) {
                        this.crc.update((byte[])outArray, (int)outIndex, (int)outputLength);
                    }
                } else if (this.inflater.needsDictionary()) {
                    if (this.dictionary == null) {
                        throw new DecompressionException((String)"decompression failure, unable to set dictionary as non was specified");
                    }
                    this.inflater.setDictionary((byte[])this.dictionary);
                }
                if (this.inflater.finished()) {
                    if (this.crc == null) {
                        this.finished = true;
                        break;
                    }
                    readFooter = true;
                    break;
                }
                decompressed.ensureWritable((int)(this.inflater.getRemaining() << 1));
            }
            in.skipBytes((int)(readableBytes - this.inflater.getRemaining()));
            if (!readFooter) return;
            this.gzipState = GzipState.FOOTER_START;
            if (!this.readGZIPFooter((ByteBuf)in)) return;
            this.finished = !this.decompressConcatenated;
            if (this.finished) return;
            this.inflater.reset();
            this.crc.reset();
            this.gzipState = GzipState.HEADER_START;
            return;
        }
        catch (DataFormatException e) {
            throw new DecompressionException((String)"decompression failure", (Throwable)e);
        }
        finally {
            if (decompressed.isReadable()) {
                out.add((Object)decompressed);
            } else {
                decompressed.release();
            }
        }
    }

    @Override
    protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved0((ChannelHandlerContext)ctx);
        if (this.inflater == null) return;
        this.inflater.end();
    }

    private boolean readGZIPHeader(ByteBuf in) {
        switch (this.gzipState) {
            case HEADER_START: {
                if (in.readableBytes() < 10) {
                    return false;
                }
                byte magic0 = in.readByte();
                byte magic1 = in.readByte();
                if (magic0 != 31) {
                    throw new DecompressionException((String)"Input is not in the GZIP format");
                }
                this.crc.update((int)magic0);
                this.crc.update((int)magic1);
                short method = in.readUnsignedByte();
                if (method != 8) {
                    throw new DecompressionException((String)("Unsupported compression method " + method + " in the GZIP header"));
                }
                this.crc.update((int)method);
                this.flags = (int)in.readUnsignedByte();
                this.crc.update((int)this.flags);
                if ((this.flags & 224) != 0) {
                    throw new DecompressionException((String)"Reserved flags are set in the GZIP header");
                }
                this.crc.update((ByteBuf)in, (int)in.readerIndex(), (int)4);
                in.skipBytes((int)4);
                this.crc.update((int)in.readUnsignedByte());
                this.crc.update((int)in.readUnsignedByte());
                this.gzipState = GzipState.FLG_READ;
            }
            case FLG_READ: {
                if ((this.flags & 4) != 0) {
                    if (in.readableBytes() < 2) {
                        return false;
                    }
                    short xlen1 = in.readUnsignedByte();
                    short xlen2 = in.readUnsignedByte();
                    this.crc.update((int)xlen1);
                    this.crc.update((int)xlen2);
                    this.xlen |= xlen1 << 8 | xlen2;
                }
                this.gzipState = GzipState.XLEN_READ;
            }
            case XLEN_READ: {
                if (this.xlen != -1) {
                    if (in.readableBytes() < this.xlen) {
                        return false;
                    }
                    this.crc.update((ByteBuf)in, (int)in.readerIndex(), (int)this.xlen);
                    in.skipBytes((int)this.xlen);
                }
                this.gzipState = GzipState.SKIP_FNAME;
            }
            case SKIP_FNAME: {
                short b;
                if ((this.flags & 8) != 0) {
                    if (!in.isReadable()) {
                        return false;
                    }
                    do {
                        b = in.readUnsignedByte();
                        this.crc.update((int)b);
                    } while (b != 0 && in.isReadable());
                }
                this.gzipState = GzipState.SKIP_COMMENT;
            }
            case SKIP_COMMENT: {
                short b;
                if ((this.flags & 16) != 0) {
                    if (!in.isReadable()) {
                        return false;
                    }
                    do {
                        b = in.readUnsignedByte();
                        this.crc.update((int)b);
                    } while (b != 0 && in.isReadable());
                }
                this.gzipState = GzipState.PROCESS_FHCRC;
            }
            case PROCESS_FHCRC: {
                if ((this.flags & 2) != 0) {
                    if (in.readableBytes() < 4) {
                        return false;
                    }
                    this.verifyCrc((ByteBuf)in);
                }
                this.crc.reset();
                this.gzipState = GzipState.HEADER_END;
            }
            case HEADER_END: {
                return true;
            }
        }
        throw new IllegalStateException();
    }

    private boolean readGZIPFooter(ByteBuf buf) {
        if (buf.readableBytes() < 8) {
            return false;
        }
        this.verifyCrc((ByteBuf)buf);
        int dataLength = 0;
        int i = 0;
        do {
            if (i >= 4) {
                int readLength = this.inflater.getTotalOut();
                if (dataLength == readLength) return true;
                throw new DecompressionException((String)("Number of bytes mismatch. Expected: " + dataLength + ", Got: " + readLength));
            }
            dataLength |= buf.readUnsignedByte() << i * 8;
            ++i;
        } while (true);
    }

    private void verifyCrc(ByteBuf in) {
        long crcValue = 0L;
        int i = 0;
        do {
            if (i >= 4) {
                long readCrc = this.crc.getValue();
                if (crcValue == readCrc) return;
                throw new DecompressionException((String)("CRC value mismatch. Expected: " + crcValue + ", Got: " + readCrc));
            }
            crcValue |= (long)in.readUnsignedByte() << i * 8;
            ++i;
        } while (true);
    }

    private static boolean looksLikeZlib(short cmf_flg) {
        if ((cmf_flg & 30720) != 30720) return false;
        if (cmf_flg % 31 != 0) return false;
        return true;
    }
}

