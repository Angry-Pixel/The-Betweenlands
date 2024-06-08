package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundSelectTradePacket implements Packet<ServerGamePacketListener> {
   private final int item;

   public ServerboundSelectTradePacket(int p_134462_) {
      this.item = p_134462_;
   }

   public ServerboundSelectTradePacket(FriendlyByteBuf p_179747_) {
      this.item = p_179747_.readVarInt();
   }

   public void write(FriendlyByteBuf p_134471_) {
      p_134471_.writeVarInt(this.item);
   }

   public void handle(ServerGamePacketListener p_134468_) {
      p_134468_.handleSelectTrade(this);
   }

   public int getItem() {
      return this.item;
   }
}