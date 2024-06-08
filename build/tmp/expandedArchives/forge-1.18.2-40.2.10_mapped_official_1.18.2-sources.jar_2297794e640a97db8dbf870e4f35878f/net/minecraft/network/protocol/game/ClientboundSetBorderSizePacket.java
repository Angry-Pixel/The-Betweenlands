package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderSizePacket implements Packet<ClientGamePacketListener> {
   private final double size;

   public ClientboundSetBorderSizePacket(WorldBorder p_179243_) {
      this.size = p_179243_.getLerpTarget();
   }

   public ClientboundSetBorderSizePacket(FriendlyByteBuf p_179245_) {
      this.size = p_179245_.readDouble();
   }

   public void write(FriendlyByteBuf p_179247_) {
      p_179247_.writeDouble(this.size);
   }

   public void handle(ClientGamePacketListener p_179251_) {
      p_179251_.handleSetBorderSize(this);
   }

   public double getSize() {
      return this.size;
   }
}