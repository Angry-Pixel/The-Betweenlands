package thebetweenlands.common.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.networking.packets.ClientboundAmateMapItemDataPacket;

import java.util.List;

// Note to self, this network functionality could also assist in c++ client and server project, TODO: add interoperability with unnamed mod api
// A server with a newer version of the betweenlands install should be able to handel a client using an older version of the mod if applicable & vice versa
public class BetweenlandsPacketHandler {

    // TODO: add betweenlands datapack compatibility
    // TODO: test for security vulnerability and exploits
    // TODO: finalise protocol versions
    public static String ProtocolVersion = "debug-1";                   // Protocol we use (debug) // TODO: remove
    public static List<String> KnownVersions = List.of("debug-1");  // Protocols we except (if not allowed in server or client config, version will be removed from this list)
    public static int protocolID = 0;           // The index of the protocol in our list // TODO: select on connection

    // Server and client will always use the highest mutually known protocol
    // The Betweenlands client & server communication channel (formatted alike to minecraft packet handler)
    // TODO: make version checker
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(TheBetweenlands.ID,"main"), () -> ProtocolVersion, ProtocolVersion::equals, ProtocolVersion::equals);

    public static void init() {
        int index = 0;
        // TODO: add datapack functionality, add datapack packets, make datapack packet handler
        CHANNEL.messageBuilder(ClientboundAmateMapItemDataPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT).
                encoder(ClientboundAmateMapItemDataPacket::encode).
                decoder(ClientboundAmateMapItemDataPacket::new).
                consumer(ClientboundAmateMapItemDataPacket::handle).add();
    }
}
