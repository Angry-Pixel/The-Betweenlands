package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundBlockEntityTagQuery implements Packet<ServerGamePacketListener> {
   private final int transactionId;
   private final BlockPos pos;

   public ServerboundBlockEntityTagQuery(int p_133802_, BlockPos p_133803_) {
      this.transactionId = p_133802_;
      this.pos = p_133803_;
   }

   public ServerboundBlockEntityTagQuery(FriendlyByteBuf p_179540_) {
      this.transactionId = p_179540_.readVarInt();
      this.pos = p_179540_.readBlockPos();
   }

   public void write(FriendlyByteBuf p_133812_) {
      p_133812_.writeVarInt(this.transactionId);
      p_133812_.writeBlockPos(this.pos);
   }

   public void handle(ServerGamePacketListener p_133809_) {
      p_133809_.handleBlockEntityTagQuery(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public BlockPos getPos() {
      return this.pos;
   }
}