package net.minecraft.network.protocol.status;

import net.minecraft.network.protocol.game.ServerPacketListener;

public interface ServerStatusPacketListener extends ServerPacketListener {
   void handlePingRequest(ServerboundPingRequestPacket p_134986_);

   void handleStatusRequest(ServerboundStatusRequestPacket p_134987_);
}