package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderWarningDelayPacket implements Packet<ClientGamePacketListener> {
   private final int warningDelay;

   public ClientboundSetBorderWarningDelayPacket(WorldBorder p_179255_) {
      this.warningDelay = p_179255_.getWarningTime();
   }

   public ClientboundSetBorderWarningDelayPacket(FriendlyByteBuf p_179257_) {
      this.warningDelay = p_179257_.readVarInt();
   }

   public void write(FriendlyByteBuf p_179259_) {
      p_179259_.writeVarInt(this.warningDelay);
   }

   public void handle(ClientGamePacketListener p_179263_) {
      p_179263_.handleSetBorderWarningDelay(this);
   }

   public int getWarningDelay() {
      return this.warningDelay;
   }
}