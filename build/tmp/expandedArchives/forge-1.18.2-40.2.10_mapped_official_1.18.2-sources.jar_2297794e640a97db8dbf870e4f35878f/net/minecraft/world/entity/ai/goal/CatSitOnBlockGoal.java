package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class CatSitOnBlockGoal extends MoveToBlockGoal {
   private final Cat cat;

   public CatSitOnBlockGoal(Cat p_25149_, double p_25150_) {
      super(p_25149_, p_25150_, 8);
      this.cat = p_25149_;
   }

   public boolean canUse() {
      return this.cat.isTame() && !this.cat.isOrderedToSit() && super.canUse();
   }

   public void start() {
      super.start();
      this.cat.setInSittingPose(false);
   }

   public void stop() {
      super.stop();
      this.cat.setInSittingPose(false);
   }

   public void tick() {
      super.tick();
      this.cat.setInSittingPose(this.isReachedTarget());
   }

   protected boolean isValidTarget(LevelReader p_25153_, BlockPos p_25154_) {
      if (!p_25153_.isEmptyBlock(p_25154_.above())) {
         return false;
      } else {
         BlockState blockstate = p_25153_.getBlockState(p_25154_);
         if (blockstate.is(Blocks.CHEST)) {
            return ChestBlockEntity.getOpenCount(p_25153_, p_25154_) < 1;
         } else {
            return blockstate.is(Blocks.FURNACE) && blockstate.getValue(FurnaceBlock.LIT) ? true : blockstate.is(BlockTags.BEDS, (p_25156_) -> {
               return p_25156_.<BedPart>getOptionalValue(BedBlock.PART).map((p_148084_) -> {
                  return p_148084_ != BedPart.HEAD;
               }).orElse(true);
            });
         }
      }
   }
}