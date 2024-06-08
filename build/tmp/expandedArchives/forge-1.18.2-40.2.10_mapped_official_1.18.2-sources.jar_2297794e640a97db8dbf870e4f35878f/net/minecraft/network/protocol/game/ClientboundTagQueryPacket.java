package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundTagQueryPacket implements Packet<ClientGamePacketListener> {
   private final int transactionId;
   @Nullable
   private final CompoundTag tag;

   public ClientboundTagQueryPacket(int p_133497_, @Nullable CompoundTag p_133498_) {
      this.transactionId = p_133497_;
      this.tag = p_133498_;
   }

   public ClientboundTagQueryPacket(FriendlyByteBuf p_179433_) {
      this.transactionId = p_179433_.readVarInt();
      this.tag = p_179433_.readNbt();
   }

   public void write(FriendlyByteBuf p_133508_) {
      p_133508_.writeVarInt(this.transactionId);
      p_133508_.writeNbt(this.tag);
   }

   public void handle(ClientGamePacketListener p_133505_) {
      p_133505_.handleTagQueryPacket(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   @Nullable
   public CompoundTag getTag() {
      return this.tag;
   }

   public boolean isSkippable() {
      return true;
   }
}