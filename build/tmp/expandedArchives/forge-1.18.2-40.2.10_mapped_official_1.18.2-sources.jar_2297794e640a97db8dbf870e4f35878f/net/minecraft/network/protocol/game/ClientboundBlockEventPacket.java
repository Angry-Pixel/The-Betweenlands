package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.Block;

public class ClientboundBlockEventPacket implements Packet<ClientGamePacketListener> {
   private final BlockPos pos;
   private final int b0;
   private final int b1;
   private final Block block;

   public ClientboundBlockEventPacket(BlockPos p_131715_, Block p_131716_, int p_131717_, int p_131718_) {
      this.pos = p_131715_;
      this.block = p_131716_;
      this.b0 = p_131717_;
      this.b1 = p_131718_;
   }

   public ClientboundBlockEventPacket(FriendlyByteBuf p_178623_) {
      this.pos = p_178623_.readBlockPos();
      this.b0 = p_178623_.readUnsignedByte();
      this.b1 = p_178623_.readUnsignedByte();
      this.block = Registry.BLOCK.byId(p_178623_.readVarInt());
   }

   public void write(FriendlyByteBuf p_131727_) {
      p_131727_.writeBlockPos(this.pos);
      p_131727_.writeByte(this.b0);
      p_131727_.writeByte(this.b1);
      p_131727_.writeVarInt(Registry.BLOCK.getId(this.block));
   }

   public void handle(ClientGamePacketListener p_131724_) {
      p_131724_.handleBlockEvent(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getB0() {
      return this.b0;
   }

   public int getB1() {
      return this.b1;
   }

   public Block getBlock() {
      return this.block;
   }
}