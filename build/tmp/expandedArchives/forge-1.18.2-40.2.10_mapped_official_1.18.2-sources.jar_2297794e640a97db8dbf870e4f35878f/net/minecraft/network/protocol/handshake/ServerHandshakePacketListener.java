package net.minecraft.network.protocol.handshake;

import net.minecraft.network.protocol.game.ServerPacketListener;

public interface ServerHandshakePacketListener extends ServerPacketListener {
   void handleIntention(ClientIntentionPacket p_134739_);
}