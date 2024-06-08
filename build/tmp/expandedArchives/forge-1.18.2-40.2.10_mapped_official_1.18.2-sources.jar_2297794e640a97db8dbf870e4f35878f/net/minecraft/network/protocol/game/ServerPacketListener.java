package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;

public interface ServerPacketListener extends PacketListener {
   default boolean shouldPropagateHandlingExceptions() {
      return false;
   }
}