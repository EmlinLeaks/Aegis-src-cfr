/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyFrameDecoder;
import io.netty.handler.codec.spdy.SpdyFrameDecoderDelegate;
import io.netty.handler.codec.spdy.SpdyVersion;
import io.netty.util.internal.ObjectUtil;

public class SpdyFrameDecoder {
    private final int spdyVersion;
    private final int maxChunkSize;
    private final SpdyFrameDecoderDelegate delegate;
    private State state;
    private byte flags;
    private int length;
    private int streamId;
    private int numSettings;

    public SpdyFrameDecoder(SpdyVersion spdyVersion, SpdyFrameDecoderDelegate delegate) {
        this((SpdyVersion)spdyVersion, (SpdyFrameDecoderDelegate)delegate, (int)8192);
    }

    public SpdyFrameDecoder(SpdyVersion spdyVersion, SpdyFrameDecoderDelegate delegate, int maxChunkSize) {
        if (spdyVersion == null) {
            throw new NullPointerException((String)"spdyVersion");
        }
        if (delegate == null) {
            throw new NullPointerException((String)"delegate");
        }
        ObjectUtil.checkPositive((int)maxChunkSize, (String)"maxChunkSize");
        this.spdyVersion = spdyVersion.getVersion();
        this.delegate = delegate;
        this.maxChunkSize = maxChunkSize;
        this.state = State.READ_COMMON_HEADER;
    }

    public void decode(ByteBuf buffer) {
        block16 : do {
            switch (1.$SwitchMap$io$netty$handler$codec$spdy$SpdyFrameDecoder$State[this.state.ordinal()]) {
                case 1: {
                    boolean control;
                    int type;
                    int version;
                    if (buffer.readableBytes() < 8) {
                        return;
                    }
                    int frameOffset = buffer.readerIndex();
                    int flagsOffset = frameOffset + 4;
                    int lengthOffset = frameOffset + 5;
                    buffer.skipBytes((int)8);
                    boolean bl = control = (buffer.getByte((int)frameOffset) & 128) != 0;
                    if (control) {
                        version = SpdyCodecUtil.getUnsignedShort((ByteBuf)buffer, (int)frameOffset) & 32767;
                        type = SpdyCodecUtil.getUnsignedShort((ByteBuf)buffer, (int)(frameOffset + 2));
                        this.streamId = 0;
                    } else {
                        version = this.spdyVersion;
                        type = 0;
                        this.streamId = SpdyCodecUtil.getUnsignedInt((ByteBuf)buffer, (int)frameOffset);
                    }
                    this.flags = buffer.getByte((int)flagsOffset);
                    this.length = SpdyCodecUtil.getUnsignedMedium((ByteBuf)buffer, (int)lengthOffset);
                    if (version != this.spdyVersion) {
                        this.state = State.FRAME_ERROR;
                        this.delegate.readFrameError((String)"Invalid SPDY Version");
                        continue block16;
                    }
                    if (!SpdyFrameDecoder.isValidFrameHeader((int)this.streamId, (int)type, (byte)this.flags, (int)this.length)) {
                        this.state = State.FRAME_ERROR;
                        this.delegate.readFrameError((String)"Invalid Frame Error");
                        continue block16;
                    }
                    this.state = SpdyFrameDecoder.getNextState((int)type, (int)this.length);
                    continue block16;
                }
                case 2: {
                    if (this.length == 0) {
                        this.state = State.READ_COMMON_HEADER;
                        this.delegate.readDataFrame((int)this.streamId, (boolean)SpdyFrameDecoder.hasFlag((byte)this.flags, (byte)1), (ByteBuf)Unpooled.buffer((int)0));
                        continue block16;
                    }
                    int dataLength = Math.min((int)this.maxChunkSize, (int)this.length);
                    if (buffer.readableBytes() < dataLength) {
                        return;
                    }
                    ByteBuf data = buffer.alloc().buffer((int)dataLength);
                    data.writeBytes((ByteBuf)buffer, (int)dataLength);
                    this.length -= dataLength;
                    if (this.length == 0) {
                        this.state = State.READ_COMMON_HEADER;
                    }
                    boolean last = this.length == 0 && SpdyFrameDecoder.hasFlag((byte)this.flags, (byte)1);
                    this.delegate.readDataFrame((int)this.streamId, (boolean)last, (ByteBuf)data);
                    continue block16;
                }
                case 3: {
                    if (buffer.readableBytes() < 10) {
                        return;
                    }
                    int offset = buffer.readerIndex();
                    this.streamId = SpdyCodecUtil.getUnsignedInt((ByteBuf)buffer, (int)offset);
                    int associatedToStreamId = SpdyCodecUtil.getUnsignedInt((ByteBuf)buffer, (int)(offset + 4));
                    byte priority = (byte)(buffer.getByte((int)(offset + 8)) >> 5 & 7);
                    boolean last = SpdyFrameDecoder.hasFlag((byte)this.flags, (byte)1);
                    boolean unidirectional = SpdyFrameDecoder.hasFlag((byte)this.flags, (byte)2);
                    buffer.skipBytes((int)10);
                    this.length -= 10;
                    if (this.streamId == 0) {
                        this.state = State.FRAME_ERROR;
                        this.delegate.readFrameError((String)"Invalid SYN_STREAM Frame");
                        continue block16;
                    }
                    this.state = State.READ_HEADER_BLOCK;
                    this.delegate.readSynStreamFrame((int)this.streamId, (int)associatedToStreamId, (byte)priority, (boolean)last, (boolean)unidirectional);
                    continue block16;
                }
                case 4: {
                    if (buffer.readableBytes() < 4) {
                        return;
                    }
                    this.streamId = SpdyCodecUtil.getUnsignedInt((ByteBuf)buffer, (int)buffer.readerIndex());
                    boolean last = SpdyFrameDecoder.hasFlag((byte)this.flags, (byte)1);
                    buffer.skipBytes((int)4);
                    this.length -= 4;
                    if (this.streamId == 0) {
                        this.state = State.FRAME_ERROR;
                        this.delegate.readFrameError((String)"Invalid SYN_REPLY Frame");
                        continue block16;
                    }
                    this.state = State.READ_HEADER_BLOCK;
                    this.delegate.readSynReplyFrame((int)this.streamId, (boolean)last);
                    continue block16;
                }
                case 5: {
                    if (buffer.readableBytes() < 8) {
                        return;
                    }
                    this.streamId = SpdyCodecUtil.getUnsignedInt((ByteBuf)buffer, (int)buffer.readerIndex());
                    int statusCode = SpdyCodecUtil.getSignedInt((ByteBuf)buffer, (int)(buffer.readerIndex() + 4));
                    buffer.skipBytes((int)8);
                    if (this.streamId == 0 || statusCode == 0) {
                        this.state = State.FRAME_ERROR;
                        this.delegate.readFrameError((String)"Invalid RST_STREAM Frame");
                        continue block16;
                    }
                    this.state = State.READ_COMMON_HEADER;
                    this.delegate.readRstStreamFrame((int)this.streamId, (int)statusCode);
                    continue block16;
                }
                case 6: {
                    if (buffer.readableBytes() < 4) {
                        return;
                    }
                    boolean clear = SpdyFrameDecoder.hasFlag((byte)this.flags, (byte)1);
                    this.numSettings = SpdyCodecUtil.getUnsignedInt((ByteBuf)buffer, (int)buffer.readerIndex());
                    buffer.skipBytes((int)4);
                    this.length -= 4;
                    if ((this.length & 7) != 0 || this.length >> 3 != this.numSettings) {
                        this.state = State.FRAME_ERROR;
                        this.delegate.readFrameError((String)"Invalid SETTINGS Frame");
                        continue block16;
                    }
                    this.state = State.READ_SETTING;
                    this.delegate.readSettingsFrame((boolean)clear);
                    continue block16;
                }
                case 7: {
                    if (this.numSettings == 0) {
                        this.state = State.READ_COMMON_HEADER;
                        this.delegate.readSettingsEnd();
                        continue block16;
                    }
                    if (buffer.readableBytes() < 8) {
                        return;
                    }
                    byte settingsFlags = buffer.getByte((int)buffer.readerIndex());
                    int id = SpdyCodecUtil.getUnsignedMedium((ByteBuf)buffer, (int)(buffer.readerIndex() + 1));
                    int value = SpdyCodecUtil.getSignedInt((ByteBuf)buffer, (int)(buffer.readerIndex() + 4));
                    boolean persistValue = SpdyFrameDecoder.hasFlag((byte)settingsFlags, (byte)1);
                    boolean persisted = SpdyFrameDecoder.hasFlag((byte)settingsFlags, (byte)2);
                    buffer.skipBytes((int)8);
                    --this.numSettings;
                    this.delegate.readSetting((int)id, (int)value, (boolean)persistValue, (boolean)persisted);
                    continue block16;
                }
                case 8: {
                    if (buffer.readableBytes() < 4) {
                        return;
                    }
                    int pingId = SpdyCodecUtil.getSignedInt((ByteBuf)buffer, (int)buffer.readerIndex());
                    buffer.skipBytes((int)4);
                    this.state = State.READ_COMMON_HEADER;
                    this.delegate.readPingFrame((int)pingId);
                    continue block16;
                }
                case 9: {
                    if (buffer.readableBytes() < 8) {
                        return;
                    }
                    int lastGoodStreamId = SpdyCodecUtil.getUnsignedInt((ByteBuf)buffer, (int)buffer.readerIndex());
                    int statusCode = SpdyCodecUtil.getSignedInt((ByteBuf)buffer, (int)(buffer.readerIndex() + 4));
                    buffer.skipBytes((int)8);
                    this.state = State.READ_COMMON_HEADER;
                    this.delegate.readGoAwayFrame((int)lastGoodStreamId, (int)statusCode);
                    continue block16;
                }
                case 10: {
                    if (buffer.readableBytes() < 4) {
                        return;
                    }
                    this.streamId = SpdyCodecUtil.getUnsignedInt((ByteBuf)buffer, (int)buffer.readerIndex());
                    boolean last = SpdyFrameDecoder.hasFlag((byte)this.flags, (byte)1);
                    buffer.skipBytes((int)4);
                    this.length -= 4;
                    if (this.streamId == 0) {
                        this.state = State.FRAME_ERROR;
                        this.delegate.readFrameError((String)"Invalid HEADERS Frame");
                        continue block16;
                    }
                    this.state = State.READ_HEADER_BLOCK;
                    this.delegate.readHeadersFrame((int)this.streamId, (boolean)last);
                    continue block16;
                }
                case 11: {
                    if (buffer.readableBytes() < 8) {
                        return;
                    }
                    this.streamId = SpdyCodecUtil.getUnsignedInt((ByteBuf)buffer, (int)buffer.readerIndex());
                    int deltaWindowSize = SpdyCodecUtil.getUnsignedInt((ByteBuf)buffer, (int)(buffer.readerIndex() + 4));
                    buffer.skipBytes((int)8);
                    if (deltaWindowSize == 0) {
                        this.state = State.FRAME_ERROR;
                        this.delegate.readFrameError((String)"Invalid WINDOW_UPDATE Frame");
                        continue block16;
                    }
                    this.state = State.READ_COMMON_HEADER;
                    this.delegate.readWindowUpdateFrame((int)this.streamId, (int)deltaWindowSize);
                    continue block16;
                }
                case 12: {
                    if (this.length == 0) {
                        this.state = State.READ_COMMON_HEADER;
                        this.delegate.readHeaderBlockEnd();
                        continue block16;
                    }
                    if (!buffer.isReadable()) {
                        return;
                    }
                    int compressedBytes = Math.min((int)buffer.readableBytes(), (int)this.length);
                    ByteBuf headerBlock = buffer.alloc().buffer((int)compressedBytes);
                    headerBlock.writeBytes((ByteBuf)buffer, (int)compressedBytes);
                    this.length -= compressedBytes;
                    this.delegate.readHeaderBlock((ByteBuf)headerBlock);
                    continue block16;
                }
                case 13: {
                    int numBytes = Math.min((int)buffer.readableBytes(), (int)this.length);
                    buffer.skipBytes((int)numBytes);
                    this.length -= numBytes;
                    if (this.length != 0) return;
                    this.state = State.READ_COMMON_HEADER;
                    continue block16;
                }
                case 14: {
                    buffer.skipBytes((int)buffer.readableBytes());
                    return;
                }
            }
            break;
        } while (true);
        throw new Error((String)"Shouldn't reach here.");
    }

    private static boolean hasFlag(byte flags, byte flag) {
        if ((flags & flag) == 0) return false;
        return true;
    }

    private static State getNextState(int type, int length) {
        switch (type) {
            case 0: {
                return State.READ_DATA_FRAME;
            }
            case 1: {
                return State.READ_SYN_STREAM_FRAME;
            }
            case 2: {
                return State.READ_SYN_REPLY_FRAME;
            }
            case 3: {
                return State.READ_RST_STREAM_FRAME;
            }
            case 4: {
                return State.READ_SETTINGS_FRAME;
            }
            case 6: {
                return State.READ_PING_FRAME;
            }
            case 7: {
                return State.READ_GOAWAY_FRAME;
            }
            case 8: {
                return State.READ_HEADERS_FRAME;
            }
            case 9: {
                return State.READ_WINDOW_UPDATE_FRAME;
            }
        }
        if (length == 0) return State.READ_COMMON_HEADER;
        return State.DISCARD_FRAME;
    }

    private static boolean isValidFrameHeader(int streamId, int type, byte flags, int length) {
        switch (type) {
            case 0: {
                if (streamId == 0) return false;
                return true;
            }
            case 1: {
                if (length < 10) return false;
                return true;
            }
            case 2: {
                if (length < 4) return false;
                return true;
            }
            case 3: {
                if (flags != 0) return false;
                if (length != 8) return false;
                return true;
            }
            case 4: {
                if (length < 4) return false;
                return true;
            }
            case 6: {
                if (length != 4) return false;
                return true;
            }
            case 7: {
                if (length != 8) return false;
                return true;
            }
            case 8: {
                if (length < 4) return false;
                return true;
            }
            case 9: {
                if (length != 8) return false;
                return true;
            }
        }
        return true;
    }
}

