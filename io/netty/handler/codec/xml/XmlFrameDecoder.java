/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.xml;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.TooLongFrameException;
import java.util.List;

public class XmlFrameDecoder
extends ByteToMessageDecoder {
    private final int maxFrameLength;

    public XmlFrameDecoder(int maxFrameLength) {
        if (maxFrameLength < 1) {
            throw new IllegalArgumentException((String)"maxFrameLength must be a positive int");
        }
        this.maxFrameLength = maxFrameLength;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        openingBracketFound = false;
        atLeastOneXmlElementFound = false;
        inCDATASection = false;
        openBracketsCount = 0L;
        length = 0;
        leadingWhiteSpaceCount = 0;
        bufferLength = in.writerIndex();
        if (bufferLength > this.maxFrameLength) {
            in.skipBytes((int)in.readableBytes());
            this.fail((long)((long)bufferLength));
            return;
        }
        block0 : for (i = in.readerIndex(); i < bufferLength; ++i) {
            readByte = in.getByte((int)i);
            if (!openingBracketFound && Character.isWhitespace((int)readByte)) {
                ++leadingWhiteSpaceCount;
                continue;
            }
            if (!openingBracketFound && readByte != 60) {
                XmlFrameDecoder.fail((ChannelHandlerContext)ctx);
                in.skipBytes((int)in.readableBytes());
                return;
            }
            if (inCDATASection || readByte != 60) ** GOTO lbl44
            openingBracketFound = true;
            if (i >= bufferLength - 1) continue;
            peekAheadByte = in.getByte((int)(i + 1));
            if (peekAheadByte == 47) {
            } else {
                if (XmlFrameDecoder.isValidStartCharForXmlElement((byte)peekAheadByte)) {
                    atLeastOneXmlElementFound = true;
                    ++openBracketsCount;
                    continue;
                }
                if (peekAheadByte == 33) {
                    if (XmlFrameDecoder.isCommentBlockStart((ByteBuf)in, (int)i)) {
                        ++openBracketsCount;
                        continue;
                    }
                    if (!XmlFrameDecoder.isCDATABlockStart((ByteBuf)in, (int)i)) continue;
                    ++openBracketsCount;
                    inCDATASection = true;
                    continue;
                }
                if (peekAheadByte != 63) continue;
                ++openBracketsCount;
                continue;
lbl44: // 1 sources:
                if (!inCDATASection && readByte == 47) {
                    if (i >= bufferLength - 1 || in.getByte((int)(i + 1)) != 62) continue;
                    --openBracketsCount;
                    continue;
                }
                if (readByte != 62) continue;
                length = i + 1;
                if (i - 1 > -1) {
                    peekBehindByte = in.getByte((int)(i - 1));
                    if (!inCDATASection) {
                        if (peekBehindByte == 63) {
                            --openBracketsCount;
                        } else if (peekBehindByte == 45 && i - 2 > -1 && in.getByte((int)(i - 2)) == 45) {
                            --openBracketsCount;
                        }
                    } else if (peekBehindByte == 93 && i - 2 > -1 && in.getByte((int)(i - 2)) == 93) {
                        --openBracketsCount;
                        inCDATASection = false;
                    }
                }
                if (!atLeastOneXmlElementFound || openBracketsCount != 0L) continue;
                break;
            }
            for (peekFurtherAheadIndex = i + 2; peekFurtherAheadIndex <= bufferLength - 1; ++peekFurtherAheadIndex) {
                if (in.getByte((int)peekFurtherAheadIndex) != 62) continue;
                --openBracketsCount;
                continue block0;
            }
        }
        readerIndex = in.readerIndex();
        xmlElementLength = length - readerIndex;
        if (openBracketsCount != 0L) return;
        if (xmlElementLength <= 0) return;
        if (readerIndex + xmlElementLength >= bufferLength) {
            xmlElementLength = in.readableBytes();
        }
        frame = XmlFrameDecoder.extractFrame((ByteBuf)in, (int)(readerIndex + leadingWhiteSpaceCount), (int)(xmlElementLength - leadingWhiteSpaceCount));
        in.skipBytes((int)xmlElementLength);
        out.add((Object)frame);
    }

    private void fail(long frameLength) {
        if (frameLength <= 0L) throw new TooLongFrameException((String)("frame length exceeds " + this.maxFrameLength + " - discarding"));
        throw new TooLongFrameException((String)("frame length exceeds " + this.maxFrameLength + ": " + frameLength + " - discarded"));
    }

    private static void fail(ChannelHandlerContext ctx) {
        ctx.fireExceptionCaught((Throwable)new CorruptedFrameException((String)"frame contains content before the xml starts"));
    }

    private static ByteBuf extractFrame(ByteBuf buffer, int index, int length) {
        return buffer.copy((int)index, (int)length);
    }

    private static boolean isValidStartCharForXmlElement(byte b) {
        if (b >= 97) {
            if (b <= 122) return true;
        }
        if (b >= 65) {
            if (b <= 90) return true;
        }
        if (b == 58) return true;
        if (b == 95) return true;
        return false;
    }

    private static boolean isCommentBlockStart(ByteBuf in, int i) {
        if (i >= in.writerIndex() - 3) return false;
        if (in.getByte((int)(i + 2)) != 45) return false;
        if (in.getByte((int)(i + 3)) != 45) return false;
        return true;
    }

    private static boolean isCDATABlockStart(ByteBuf in, int i) {
        if (i >= in.writerIndex() - 8) return false;
        if (in.getByte((int)(i + 2)) != 91) return false;
        if (in.getByte((int)(i + 3)) != 67) return false;
        if (in.getByte((int)(i + 4)) != 68) return false;
        if (in.getByte((int)(i + 5)) != 65) return false;
        if (in.getByte((int)(i + 6)) != 84) return false;
        if (in.getByte((int)(i + 7)) != 65) return false;
        if (in.getByte((int)(i + 8)) != 91) return false;
        return true;
    }
}

