package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundClearTitlesPacket implements Packet<ClientGamePacketListener> {
   private final boolean resetTimes;

   public ClientboundClearTitlesPacket(boolean p_178781_) {
      this.resetTimes = p_178781_;
   }

   public ClientboundClearTitlesPacket(FriendlyByteBuf p_178779_) {
      this.resetTimes = p_178779_.readBoolean();
   }

   public void write(FriendlyByteBuf p_178783_) {
      p_178783_.writeBoolean(this.resetTimes);
   }

   public void handle(ClientGamePacketListener p_178787_) {
      p_178787_.handleTitlesClear(this);
   }

   public boolean shouldResetTimes() {
      return this.resetTimes;
   }
}