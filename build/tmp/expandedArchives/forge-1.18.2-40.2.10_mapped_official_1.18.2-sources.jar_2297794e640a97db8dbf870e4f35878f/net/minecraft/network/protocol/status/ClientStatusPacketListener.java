package net.minecraft.network.protocol.status;

import net.minecraft.network.PacketListener;

public interface ClientStatusPacketListener extends PacketListener {
   void handleStatusResponse(ClientboundStatusResponsePacket p_134872_);

   void handlePongResponse(ClientboundPongResponsePacket p_134871_);
}