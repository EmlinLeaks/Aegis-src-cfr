/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.haproxy;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.haproxy.HAProxyCommand;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.codec.haproxy.HAProxyProtocolException;
import io.netty.handler.codec.haproxy.HAProxyProtocolVersion;
import io.netty.handler.codec.haproxy.HAProxyProxiedProtocol;
import io.netty.handler.codec.haproxy.HAProxySSLTLV;
import io.netty.handler.codec.haproxy.HAProxyTLV;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class HAProxyMessage
extends AbstractReferenceCounted {
    private static final ResourceLeakDetector<HAProxyMessage> leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(HAProxyMessage.class);
    private final ResourceLeakTracker<HAProxyMessage> leak;
    private final HAProxyProtocolVersion protocolVersion;
    private final HAProxyCommand command;
    private final HAProxyProxiedProtocol proxiedProtocol;
    private final String sourceAddress;
    private final String destinationAddress;
    private final int sourcePort;
    private final int destinationPort;
    private final List<HAProxyTLV> tlvs;

    private HAProxyMessage(HAProxyProtocolVersion protocolVersion, HAProxyCommand command, HAProxyProxiedProtocol proxiedProtocol, String sourceAddress, String destinationAddress, String sourcePort, String destinationPort) {
        this((HAProxyProtocolVersion)protocolVersion, (HAProxyCommand)command, (HAProxyProxiedProtocol)proxiedProtocol, (String)sourceAddress, (String)destinationAddress, (int)HAProxyMessage.portStringToInt((String)sourcePort), (int)HAProxyMessage.portStringToInt((String)destinationPort));
    }

    private HAProxyMessage(HAProxyProtocolVersion protocolVersion, HAProxyCommand command, HAProxyProxiedProtocol proxiedProtocol, String sourceAddress, String destinationAddress, int sourcePort, int destinationPort) {
        this((HAProxyProtocolVersion)protocolVersion, (HAProxyCommand)command, (HAProxyProxiedProtocol)proxiedProtocol, (String)sourceAddress, (String)destinationAddress, (int)sourcePort, (int)destinationPort, Collections.emptyList());
    }

    private HAProxyMessage(HAProxyProtocolVersion protocolVersion, HAProxyCommand command, HAProxyProxiedProtocol proxiedProtocol, String sourceAddress, String destinationAddress, int sourcePort, int destinationPort, List<HAProxyTLV> tlvs) {
        if (proxiedProtocol == null) {
            throw new NullPointerException((String)"proxiedProtocol");
        }
        HAProxyProxiedProtocol.AddressFamily addrFamily = proxiedProtocol.addressFamily();
        HAProxyMessage.checkAddress((String)sourceAddress, (HAProxyProxiedProtocol.AddressFamily)addrFamily);
        HAProxyMessage.checkAddress((String)destinationAddress, (HAProxyProxiedProtocol.AddressFamily)addrFamily);
        HAProxyMessage.checkPort((int)sourcePort);
        HAProxyMessage.checkPort((int)destinationPort);
        this.protocolVersion = protocolVersion;
        this.command = command;
        this.proxiedProtocol = proxiedProtocol;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
        this.tlvs = Collections.unmodifiableList(tlvs);
        this.leak = leakDetector.track((HAProxyMessage)this);
    }

    static HAProxyMessage decodeHeader(ByteBuf header) {
        HAProxyProtocolVersion ver;
        String srcAddress;
        HAProxyCommand cmd;
        HAProxyProxiedProtocol protAndFam;
        String dstAddress;
        if (header == null) {
            throw new NullPointerException((String)"header");
        }
        if (header.readableBytes() < 16) {
            throw new HAProxyProtocolException((String)("incomplete header: " + header.readableBytes() + " bytes (expected: 16+ bytes)"));
        }
        header.skipBytes((int)12);
        byte verCmdByte = header.readByte();
        try {
            ver = HAProxyProtocolVersion.valueOf((byte)verCmdByte);
        }
        catch (IllegalArgumentException e) {
            throw new HAProxyProtocolException((Throwable)e);
        }
        if (ver != HAProxyProtocolVersion.V2) {
            throw new HAProxyProtocolException((String)("version 1 unsupported: 0x" + Integer.toHexString((int)verCmdByte)));
        }
        try {
            cmd = HAProxyCommand.valueOf((byte)verCmdByte);
        }
        catch (IllegalArgumentException e) {
            throw new HAProxyProtocolException((Throwable)e);
        }
        if (cmd == HAProxyCommand.LOCAL) {
            return HAProxyMessage.unknownMsg((HAProxyProtocolVersion)HAProxyProtocolVersion.V2, (HAProxyCommand)HAProxyCommand.LOCAL);
        }
        try {
            protAndFam = HAProxyProxiedProtocol.valueOf((byte)header.readByte());
        }
        catch (IllegalArgumentException e) {
            throw new HAProxyProtocolException((Throwable)e);
        }
        if (protAndFam == HAProxyProxiedProtocol.UNKNOWN) {
            return HAProxyMessage.unknownMsg((HAProxyProtocolVersion)HAProxyProtocolVersion.V2, (HAProxyCommand)HAProxyCommand.PROXY);
        }
        int addressInfoLen = header.readUnsignedShort();
        int srcPort = 0;
        int dstPort = 0;
        HAProxyProxiedProtocol.AddressFamily addressFamily = protAndFam.addressFamily();
        if (addressFamily == HAProxyProxiedProtocol.AddressFamily.AF_UNIX) {
            if (addressInfoLen < 216) throw new HAProxyProtocolException((String)("incomplete UNIX socket address information: " + Math.min((int)addressInfoLen, (int)header.readableBytes()) + " bytes (expected: 216+ bytes)"));
            if (header.readableBytes() < 216) {
                throw new HAProxyProtocolException((String)("incomplete UNIX socket address information: " + Math.min((int)addressInfoLen, (int)header.readableBytes()) + " bytes (expected: 216+ bytes)"));
            }
            int startIdx = header.readerIndex();
            int addressEnd = header.forEachByte((int)startIdx, (int)108, (ByteProcessor)ByteProcessor.FIND_NUL);
            int addressLen = addressEnd == -1 ? 108 : addressEnd - startIdx;
            srcAddress = header.toString((int)startIdx, (int)addressLen, (Charset)CharsetUtil.US_ASCII);
            addressEnd = header.forEachByte((int)(startIdx += 108), (int)108, (ByteProcessor)ByteProcessor.FIND_NUL);
            addressLen = addressEnd == -1 ? 108 : addressEnd - startIdx;
            dstAddress = header.toString((int)startIdx, (int)addressLen, (Charset)CharsetUtil.US_ASCII);
            header.readerIndex((int)(startIdx + 108));
        } else {
            int addressLen;
            if (addressFamily == HAProxyProxiedProtocol.AddressFamily.AF_IPv4) {
                if (addressInfoLen < 12) throw new HAProxyProtocolException((String)("incomplete IPv4 address information: " + Math.min((int)addressInfoLen, (int)header.readableBytes()) + " bytes (expected: 12+ bytes)"));
                if (header.readableBytes() < 12) {
                    throw new HAProxyProtocolException((String)("incomplete IPv4 address information: " + Math.min((int)addressInfoLen, (int)header.readableBytes()) + " bytes (expected: 12+ bytes)"));
                }
                addressLen = 4;
            } else {
                if (addressFamily != HAProxyProxiedProtocol.AddressFamily.AF_IPv6) throw new HAProxyProtocolException((String)("unable to parse address information (unknown address family: " + (Object)((Object)addressFamily) + ')'));
                if (addressInfoLen < 36) throw new HAProxyProtocolException((String)("incomplete IPv6 address information: " + Math.min((int)addressInfoLen, (int)header.readableBytes()) + " bytes (expected: 36+ bytes)"));
                if (header.readableBytes() < 36) {
                    throw new HAProxyProtocolException((String)("incomplete IPv6 address information: " + Math.min((int)addressInfoLen, (int)header.readableBytes()) + " bytes (expected: 36+ bytes)"));
                }
                addressLen = 16;
            }
            srcAddress = HAProxyMessage.ipBytesToString((ByteBuf)header, (int)addressLen);
            dstAddress = HAProxyMessage.ipBytesToString((ByteBuf)header, (int)addressLen);
            srcPort = header.readUnsignedShort();
            dstPort = header.readUnsignedShort();
        }
        List<HAProxyTLV> tlvs = HAProxyMessage.readTlvs((ByteBuf)header);
        return new HAProxyMessage((HAProxyProtocolVersion)ver, (HAProxyCommand)cmd, (HAProxyProxiedProtocol)protAndFam, (String)srcAddress, (String)dstAddress, (int)srcPort, (int)dstPort, tlvs);
    }

    private static List<HAProxyTLV> readTlvs(ByteBuf header) {
        HAProxyTLV haProxyTLV = HAProxyMessage.readNextTLV((ByteBuf)header);
        if (haProxyTLV == null) {
            return Collections.emptyList();
        }
        ArrayList<HAProxyTLV> haProxyTLVs = new ArrayList<HAProxyTLV>((int)4);
        do {
            haProxyTLVs.add(haProxyTLV);
            if (!(haProxyTLV instanceof HAProxySSLTLV)) continue;
            haProxyTLVs.addAll(((HAProxySSLTLV)haProxyTLV).encapsulatedTLVs());
        } while ((haProxyTLV = HAProxyMessage.readNextTLV((ByteBuf)header)) != null);
        return haProxyTLVs;
    }

    private static HAProxyTLV readNextTLV(ByteBuf header) {
        if (header.readableBytes() < 4) {
            return null;
        }
        byte typeAsByte = header.readByte();
        HAProxyTLV.Type type = HAProxyTLV.Type.typeForByteValue((byte)typeAsByte);
        int length = header.readUnsignedShort();
        switch (1.$SwitchMap$io$netty$handler$codec$haproxy$HAProxyTLV$Type[type.ordinal()]) {
            case 1: {
                ByteBuf rawContent = header.retainedSlice((int)header.readerIndex(), (int)length);
                ByteBuf byteBuf = header.readSlice((int)length);
                byte client = byteBuf.readByte();
                int verify = byteBuf.readInt();
                if (byteBuf.readableBytes() < 4) return new HAProxySSLTLV((int)verify, (byte)client, Collections.<HAProxyTLV>emptyList(), (ByteBuf)rawContent);
                ArrayList<HAProxyTLV> encapsulatedTlvs = new ArrayList<HAProxyTLV>((int)4);
                do {
                    HAProxyTLV haProxyTLV;
                    if ((haProxyTLV = HAProxyMessage.readNextTLV((ByteBuf)byteBuf)) == null) {
                        return new HAProxySSLTLV((int)verify, (byte)client, encapsulatedTlvs, (ByteBuf)rawContent);
                    }
                    encapsulatedTlvs.add(haProxyTLV);
                } while (byteBuf.readableBytes() >= 4);
                return new HAProxySSLTLV((int)verify, (byte)client, encapsulatedTlvs, (ByteBuf)rawContent);
            }
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: {
                return new HAProxyTLV((HAProxyTLV.Type)type, (byte)typeAsByte, (ByteBuf)header.readRetainedSlice((int)length));
            }
        }
        return null;
    }

    static HAProxyMessage decodeHeader(String header) {
        HAProxyProxiedProtocol protAndFam;
        if (header == null) {
            throw new HAProxyProtocolException((String)"header");
        }
        String[] parts = header.split((String)" ");
        int numParts = parts.length;
        if (numParts < 2) {
            throw new HAProxyProtocolException((String)("invalid header: " + header + " (expected: 'PROXY' and proxied protocol values)"));
        }
        if (!"PROXY".equals((Object)parts[0])) {
            throw new HAProxyProtocolException((String)("unknown identifier: " + parts[0]));
        }
        try {
            protAndFam = HAProxyProxiedProtocol.valueOf((String)parts[1]);
        }
        catch (IllegalArgumentException e) {
            throw new HAProxyProtocolException((Throwable)e);
        }
        if (protAndFam != HAProxyProxiedProtocol.TCP4 && protAndFam != HAProxyProxiedProtocol.TCP6 && protAndFam != HAProxyProxiedProtocol.UNKNOWN) {
            throw new HAProxyProtocolException((String)("unsupported v1 proxied protocol: " + parts[1]));
        }
        if (protAndFam == HAProxyProxiedProtocol.UNKNOWN) {
            return HAProxyMessage.unknownMsg((HAProxyProtocolVersion)HAProxyProtocolVersion.V1, (HAProxyCommand)HAProxyCommand.PROXY);
        }
        if (numParts == 6) return new HAProxyMessage((HAProxyProtocolVersion)HAProxyProtocolVersion.V1, (HAProxyCommand)HAProxyCommand.PROXY, (HAProxyProxiedProtocol)protAndFam, (String)parts[2], (String)parts[3], (String)parts[4], (String)parts[5]);
        throw new HAProxyProtocolException((String)("invalid TCP4/6 header: " + header + " (expected: 6 parts)"));
    }

    private static HAProxyMessage unknownMsg(HAProxyProtocolVersion version, HAProxyCommand command) {
        return new HAProxyMessage((HAProxyProtocolVersion)version, (HAProxyCommand)command, (HAProxyProxiedProtocol)HAProxyProxiedProtocol.UNKNOWN, null, null, (int)0, (int)0);
    }

    private static String ipBytesToString(ByteBuf header, int addressLen) {
        StringBuilder sb = new StringBuilder();
        int ipv4Len = 4;
        int ipv6Len = 8;
        if (addressLen == 4) {
            for (int i = 0; i < 4; ++i) {
                sb.append((int)(header.readByte() & 255));
                sb.append((char)'.');
            }
        } else {
            for (int i = 0; i < 8; ++i) {
                sb.append((String)Integer.toHexString((int)header.readUnsignedShort()));
                sb.append((char)':');
            }
        }
        sb.setLength((int)(sb.length() - 1));
        return sb.toString();
    }

    private static int portStringToInt(String value) {
        int port;
        try {
            port = Integer.parseInt((String)value);
        }
        catch (NumberFormatException e) {
            throw new HAProxyProtocolException((String)("invalid port: " + value), (Throwable)e);
        }
        if (port <= 0) throw new HAProxyProtocolException((String)("invalid port: " + value + " (expected: 1 ~ 65535)"));
        if (port <= 65535) return port;
        throw new HAProxyProtocolException((String)("invalid port: " + value + " (expected: 1 ~ 65535)"));
    }

    private static void checkAddress(String address, HAProxyProxiedProtocol.AddressFamily addrFamily) {
        if (addrFamily == null) {
            throw new NullPointerException((String)"addrFamily");
        }
        switch (addrFamily) {
            case AF_UNSPEC: {
                if (address == null) return;
                throw new HAProxyProtocolException((String)("unable to validate an AF_UNSPEC address: " + address));
            }
            case AF_UNIX: {
                return;
            }
        }
        if (address == null) {
            throw new NullPointerException((String)"address");
        }
        switch (addrFamily) {
            case AF_IPv4: {
                if (NetUtil.isValidIpV4Address((String)address)) return;
                throw new HAProxyProtocolException((String)("invalid IPv4 address: " + address));
            }
            case AF_IPv6: {
                if (NetUtil.isValidIpV6Address((String)address)) return;
                throw new HAProxyProtocolException((String)("invalid IPv6 address: " + address));
            }
        }
        throw new Error();
    }

    private static void checkPort(int port) {
        if (port < 0) throw new HAProxyProtocolException((String)("invalid port: " + port + " (expected: 1 ~ 65535)"));
        if (port <= 65535) return;
        throw new HAProxyProtocolException((String)("invalid port: " + port + " (expected: 1 ~ 65535)"));
    }

    public HAProxyProtocolVersion protocolVersion() {
        return this.protocolVersion;
    }

    public HAProxyCommand command() {
        return this.command;
    }

    public HAProxyProxiedProtocol proxiedProtocol() {
        return this.proxiedProtocol;
    }

    public String sourceAddress() {
        return this.sourceAddress;
    }

    public String destinationAddress() {
        return this.destinationAddress;
    }

    public int sourcePort() {
        return this.sourcePort;
    }

    public int destinationPort() {
        return this.destinationPort;
    }

    public List<HAProxyTLV> tlvs() {
        return this.tlvs;
    }

    @Override
    public HAProxyMessage touch() {
        this.tryRecord();
        return (HAProxyMessage)super.touch();
    }

    @Override
    public HAProxyMessage touch(Object hint) {
        if (this.leak == null) return this;
        this.leak.record((Object)hint);
        return this;
    }

    @Override
    public HAProxyMessage retain() {
        this.tryRecord();
        return (HAProxyMessage)super.retain();
    }

    @Override
    public HAProxyMessage retain(int increment) {
        this.tryRecord();
        return (HAProxyMessage)super.retain((int)increment);
    }

    @Override
    public boolean release() {
        this.tryRecord();
        return super.release();
    }

    @Override
    public boolean release(int decrement) {
        this.tryRecord();
        return super.release((int)decrement);
    }

    private void tryRecord() {
        if (this.leak == null) return;
        this.leak.record();
    }

    @Override
    protected void deallocate() {
        try {
            Iterator<HAProxyTLV> iterator = this.tlvs.iterator();
            while (iterator.hasNext()) {
                HAProxyTLV tlv = iterator.next();
                tlv.release();
            }
            return;
        }
        finally {
            ResourceLeakTracker<HAProxyMessage> leak = this.leak;
            if (leak != null) {
                boolean closed = leak.close((HAProxyMessage)this);
                assert (closed);
            }
        }
    }
}

