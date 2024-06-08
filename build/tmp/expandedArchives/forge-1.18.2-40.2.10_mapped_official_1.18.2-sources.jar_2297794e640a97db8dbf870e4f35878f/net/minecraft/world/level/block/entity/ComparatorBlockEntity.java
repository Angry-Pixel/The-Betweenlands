package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class ComparatorBlockEntity extends BlockEntity {
   private int output;

   public ComparatorBlockEntity(BlockPos p_155386_, BlockState p_155387_) {
      super(BlockEntityType.COMPARATOR, p_155386_, p_155387_);
   }

   protected void saveAdditional(CompoundTag p_187493_) {
      super.saveAdditional(p_187493_);
      p_187493_.putInt("OutputSignal", this.output);
   }

   public void load(CompoundTag p_155389_) {
      super.load(p_155389_);
      this.output = p_155389_.getInt("OutputSignal");
   }

   public int getOutputSignal() {
      return this.output;
   }

   public void setOutputSignal(int p_59176_) {
      this.output = p_59176_;
   }
}