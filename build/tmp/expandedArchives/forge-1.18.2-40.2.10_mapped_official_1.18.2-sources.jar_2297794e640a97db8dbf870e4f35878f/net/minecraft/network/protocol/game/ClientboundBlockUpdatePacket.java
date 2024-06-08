package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ClientboundBlockUpdatePacket implements Packet<ClientGamePacketListener> {
   private final BlockPos pos;
   private final BlockState blockState;

   public ClientboundBlockUpdatePacket(BlockPos p_131738_, BlockState p_131739_) {
      this.pos = p_131738_;
      this.blockState = p_131739_;
   }

   public ClientboundBlockUpdatePacket(BlockGetter p_131735_, BlockPos p_131736_) {
      this(p_131736_, p_131735_.getBlockState(p_131736_));
   }

   public ClientboundBlockUpdatePacket(FriendlyByteBuf p_178628_) {
      this.pos = p_178628_.readBlockPos();
      this.blockState = Block.BLOCK_STATE_REGISTRY.byId(p_178628_.readVarInt());
   }

   public void write(FriendlyByteBuf p_131748_) {
      p_131748_.writeBlockPos(this.pos);
      p_131748_.writeVarInt(Block.getId(this.blockState));
   }

   public void handle(ClientGamePacketListener p_131745_) {
      p_131745_.handleBlockUpdate(this);
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   public BlockPos getPos() {
      return this.pos;
   }
}