package net.minecraft.network.protocol;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;

public interface Packet<T extends PacketListener> {
   void write(FriendlyByteBuf p_131343_);

   void handle(T p_131342_);

   default boolean isSkippable() {
      return false;
   }
}