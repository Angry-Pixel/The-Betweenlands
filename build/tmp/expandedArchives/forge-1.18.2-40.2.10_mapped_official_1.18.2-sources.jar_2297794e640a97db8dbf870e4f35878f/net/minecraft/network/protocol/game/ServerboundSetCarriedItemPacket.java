package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundSetCarriedItemPacket implements Packet<ServerGamePacketListener> {
   private final int slot;

   public ServerboundSetCarriedItemPacket(int p_134491_) {
      this.slot = p_134491_;
   }

   public ServerboundSetCarriedItemPacket(FriendlyByteBuf p_179751_) {
      this.slot = p_179751_.readShort();
   }

   public void write(FriendlyByteBuf p_134500_) {
      p_134500_.writeShort(this.slot);
   }

   public void handle(ServerGamePacketListener p_134497_) {
      p_134497_.handleSetCarriedItem(this);
   }

   public int getSlot() {
      return this.slot;
   }
}