package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderWarningDistancePacket implements Packet<ClientGamePacketListener> {
   private final int warningBlocks;

   public ClientboundSetBorderWarningDistancePacket(WorldBorder p_179267_) {
      this.warningBlocks = p_179267_.getWarningBlocks();
   }

   public ClientboundSetBorderWarningDistancePacket(FriendlyByteBuf p_179269_) {
      this.warningBlocks = p_179269_.readVarInt();
   }

   public void write(FriendlyByteBuf p_179271_) {
      p_179271_.writeVarInt(this.warningBlocks);
   }

   public void handle(ClientGamePacketListener p_179275_) {
      p_179275_.handleSetBorderWarningDistance(this);
   }

   public int getWarningBlocks() {
      return this.warningBlocks;
   }
}