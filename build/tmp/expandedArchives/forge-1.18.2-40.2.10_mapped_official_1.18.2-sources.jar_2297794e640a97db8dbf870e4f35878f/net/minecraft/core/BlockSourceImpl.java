package net.minecraft.core;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockSourceImpl implements BlockSource {
   private final ServerLevel level;
   private final BlockPos pos;

   public BlockSourceImpl(ServerLevel p_122213_, BlockPos p_122214_) {
      this.level = p_122213_;
      this.pos = p_122214_;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   public double x() {
      return (double)this.pos.getX() + 0.5D;
   }

   public double y() {
      return (double)this.pos.getY() + 0.5D;
   }

   public double z() {
      return (double)this.pos.getZ() + 0.5D;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public BlockState getBlockState() {
      return this.level.getBlockState(this.pos);
   }

   public <T extends BlockEntity> T getEntity() {
      return (T)this.level.getBlockEntity(this.pos);
   }
}