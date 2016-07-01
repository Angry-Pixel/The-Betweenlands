package thebetweenlands.common.registries;

import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.base.IPacket;
import thebetweenlands.common.network.packet.server.PacketDruidAltarProgress;


public class PacketRegistry {
    private static byte nextPacketId = 0;

    public static void preInit() {
        registerPacket(PacketDruidAltarProgress.class);
    }


    private static void registerPacket(Class<? extends IPacket> packetClass) {
        TheBetweenlands.packetRegistry.registerPacket(packetClass, nextPacketId++);
    }
}
