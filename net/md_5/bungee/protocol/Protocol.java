/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.protocol;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;

public enum Protocol {
    HANDSHAKE{
        {
            super((String)string, (int)n);
            DirectionData.access$200((DirectionData)this.TO_SERVER, net.md_5.bungee.protocol.packet.Handshake.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)0)});
        }
    }
    ,
    GAME{
        {
            super((String)string, (int)n);
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.KeepAlive.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)0), Protocol.access$100((int)107, (int)31), Protocol.access$100((int)393, (int)33), Protocol.access$100((int)477, (int)32), Protocol.access$100((int)573, (int)33)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.Login.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)1), Protocol.access$100((int)107, (int)35), Protocol.access$100((int)393, (int)37), Protocol.access$100((int)573, (int)38)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.Chat.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)2), Protocol.access$100((int)107, (int)15), Protocol.access$100((int)393, (int)14), Protocol.access$100((int)573, (int)15)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.Respawn.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)7), Protocol.access$100((int)107, (int)51), Protocol.access$100((int)335, (int)52), Protocol.access$100((int)338, (int)53), Protocol.access$100((int)393, (int)56), Protocol.access$100((int)477, (int)58), Protocol.access$100((int)573, (int)59)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.BossBar.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)107, (int)12), Protocol.access$100((int)573, (int)13)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.PlayerListItem.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)56), Protocol.access$100((int)107, (int)45), Protocol.access$100((int)338, (int)46), Protocol.access$100((int)393, (int)48), Protocol.access$100((int)477, (int)51), Protocol.access$100((int)573, (int)52)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.TabCompleteResponse.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)58), Protocol.access$100((int)107, (int)14), Protocol.access$100((int)393, (int)16), Protocol.access$100((int)573, (int)17)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.ScoreboardObjective.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)59), Protocol.access$100((int)107, (int)63), Protocol.access$100((int)335, (int)65), Protocol.access$100((int)338, (int)66), Protocol.access$100((int)393, (int)69), Protocol.access$100((int)477, (int)73), Protocol.access$100((int)573, (int)74)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.ScoreboardScore.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)60), Protocol.access$100((int)107, (int)66), Protocol.access$100((int)335, (int)68), Protocol.access$100((int)338, (int)69), Protocol.access$100((int)393, (int)72), Protocol.access$100((int)477, (int)76), Protocol.access$100((int)573, (int)77)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.ScoreboardDisplay.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)61), Protocol.access$100((int)107, (int)56), Protocol.access$100((int)335, (int)58), Protocol.access$100((int)338, (int)59), Protocol.access$100((int)393, (int)62), Protocol.access$100((int)477, (int)66), Protocol.access$100((int)573, (int)67)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.Team.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)62), Protocol.access$100((int)107, (int)65), Protocol.access$100((int)335, (int)67), Protocol.access$100((int)338, (int)68), Protocol.access$100((int)393, (int)71), Protocol.access$100((int)477, (int)75), Protocol.access$100((int)573, (int)76)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.PluginMessage.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)63), Protocol.access$100((int)107, (int)24), Protocol.access$100((int)393, (int)25), Protocol.access$100((int)477, (int)24), Protocol.access$100((int)573, (int)25)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.Kick.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)64), Protocol.access$100((int)107, (int)26), Protocol.access$100((int)393, (int)27), Protocol.access$100((int)477, (int)26), Protocol.access$100((int)573, (int)27)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.Title.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)69), Protocol.access$100((int)335, (int)71), Protocol.access$100((int)338, (int)72), Protocol.access$100((int)393, (int)75), Protocol.access$100((int)477, (int)79), Protocol.access$100((int)573, (int)80)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.PlayerListHeaderFooter.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)71), Protocol.access$100((int)107, (int)72), Protocol.access$100((int)110, (int)71), Protocol.access$100((int)335, (int)73), Protocol.access$100((int)338, (int)74), Protocol.access$100((int)393, (int)78), Protocol.access$100((int)477, (int)83), Protocol.access$100((int)573, (int)84)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.EntityStatus.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)26), Protocol.access$100((int)107, (int)27), Protocol.access$100((int)393, (int)28), Protocol.access$100((int)477, (int)27), Protocol.access$100((int)573, (int)28)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.Commands.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)393, (int)17), Protocol.access$100((int)573, (int)18)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.ViewDistance.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)477, (int)65), Protocol.access$100((int)573, (int)66)});
            DirectionData.access$200((DirectionData)this.TO_SERVER, net.md_5.bungee.protocol.packet.KeepAlive.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)0), Protocol.access$100((int)107, (int)11), Protocol.access$100((int)335, (int)12), Protocol.access$100((int)338, (int)11), Protocol.access$100((int)393, (int)14), Protocol.access$100((int)477, (int)15)});
            DirectionData.access$200((DirectionData)this.TO_SERVER, net.md_5.bungee.protocol.packet.Chat.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)1), Protocol.access$100((int)107, (int)2), Protocol.access$100((int)335, (int)3), Protocol.access$100((int)338, (int)2), Protocol.access$100((int)477, (int)3)});
            DirectionData.access$200((DirectionData)this.TO_SERVER, net.md_5.bungee.protocol.packet.TabCompleteRequest.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)20), Protocol.access$100((int)107, (int)1), Protocol.access$100((int)335, (int)2), Protocol.access$100((int)338, (int)1), Protocol.access$100((int)393, (int)5), Protocol.access$100((int)477, (int)6)});
            DirectionData.access$200((DirectionData)this.TO_SERVER, net.md_5.bungee.protocol.packet.ClientSettings.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)21), Protocol.access$100((int)107, (int)4), Protocol.access$100((int)335, (int)5), Protocol.access$100((int)338, (int)4), Protocol.access$100((int)477, (int)5)});
            DirectionData.access$200((DirectionData)this.TO_SERVER, net.md_5.bungee.protocol.packet.PluginMessage.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)23), Protocol.access$100((int)107, (int)9), Protocol.access$100((int)335, (int)10), Protocol.access$100((int)338, (int)9), Protocol.access$100((int)393, (int)10), Protocol.access$100((int)477, (int)11)});
        }
    }
    ,
    STATUS{
        {
            super((String)string, (int)n);
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.StatusResponse.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)0)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.PingPacket.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)1)});
            DirectionData.access$200((DirectionData)this.TO_SERVER, net.md_5.bungee.protocol.packet.StatusRequest.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)0)});
            DirectionData.access$200((DirectionData)this.TO_SERVER, net.md_5.bungee.protocol.packet.PingPacket.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)1)});
        }
    }
    ,
    LOGIN{
        {
            super((String)string, (int)n);
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.Kick.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)0)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.EncryptionRequest.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)1)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.LoginSuccess.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)2)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.SetCompression.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)3)});
            DirectionData.access$200((DirectionData)this.TO_CLIENT, net.md_5.bungee.protocol.packet.LoginPayloadRequest.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)393, (int)4)});
            DirectionData.access$200((DirectionData)this.TO_SERVER, net.md_5.bungee.protocol.packet.LoginRequest.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)0)});
            DirectionData.access$200((DirectionData)this.TO_SERVER, net.md_5.bungee.protocol.packet.EncryptionResponse.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)47, (int)1)});
            DirectionData.access$200((DirectionData)this.TO_SERVER, net.md_5.bungee.protocol.packet.LoginPayloadResponse.class, (ProtocolMapping[])new ProtocolMapping[]{Protocol.access$100((int)393, (int)2)});
        }
    };
    
    public static final int MAX_PACKET_ID = 255;
    final DirectionData TO_SERVER = new DirectionData((Protocol)this, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_SERVER);
    final DirectionData TO_CLIENT = new DirectionData((Protocol)this, (ProtocolConstants.Direction)ProtocolConstants.Direction.TO_CLIENT);

    public static void main(String[] args) {
        Iterator<Integer> iterator = ProtocolConstants.SUPPORTED_VERSION_IDS.iterator();
        while (iterator.hasNext()) {
            int version = iterator.next().intValue();
            Protocol.dump((int)version);
        }
    }

    private static void dump(int version) {
        Protocol[] arrprotocol = Protocol.values();
        int n = arrprotocol.length;
        int n2 = 0;
        while (n2 < n) {
            Protocol protocol = arrprotocol[n2];
            Protocol.dump((int)version, (Protocol)protocol);
            ++n2;
        }
    }

    private static void dump(int version, Protocol protocol) {
        Protocol.dump((int)version, (DirectionData)protocol.TO_CLIENT);
        Protocol.dump((int)version, (DirectionData)protocol.TO_SERVER);
    }

    private static void dump(int version, DirectionData data) {
        int id = 0;
        while (id < 255) {
            DefinedPacket packet = data.createPacket((int)id, (int)version);
            if (packet != null) {
                System.out.println((String)(version + " " + (Object)((Object)((DirectionData)data).protocolPhase) + " " + (Object)((Object)((DirectionData)data).direction) + " " + id + " " + packet.getClass().getSimpleName()));
            }
            ++id;
        }
    }

    private static ProtocolMapping map(int protocol, int id) {
        return new ProtocolMapping((int)protocol, (int)id);
    }

    static /* synthetic */ ProtocolMapping access$100(int x0, int x1) {
        return Protocol.map((int)x0, (int)x1);
    }
}

