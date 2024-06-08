package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundTakeItemEntityPacket implements Packet<ClientGamePacketListener> {
   private final int itemId;
   private final int playerId;
   private final int amount;

   public ClientboundTakeItemEntityPacket(int p_133515_, int p_133516_, int p_133517_) {
      this.itemId = p_133515_;
      this.playerId = p_133516_;
      this.amount = p_133517_;
   }

   public ClientboundTakeItemEntityPacket(FriendlyByteBuf p_179435_) {
      this.itemId = p_179435_.readVarInt();
      this.playerId = p_179435_.readVarInt();
      this.amount = p_179435_.readVarInt();
   }

   public void write(FriendlyByteBuf p_133526_) {
      p_133526_.writeVarInt(this.itemId);
      p_133526_.writeVarInt(this.playerId);
      p_133526_.writeVarInt(this.amount);
   }

   public void handle(ClientGamePacketListener p_133523_) {
      p_133523_.handleTakeItemEntity(this);
   }

   public int getItemId() {
      return this.itemId;
   }

   public int getPlayerId() {
      return this.playerId;
   }

   public int getAmount() {
      return this.amount;
   }
}