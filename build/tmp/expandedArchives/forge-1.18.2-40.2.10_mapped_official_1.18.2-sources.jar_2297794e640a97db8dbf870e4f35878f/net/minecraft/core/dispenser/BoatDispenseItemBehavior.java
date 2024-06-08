package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class BoatDispenseItemBehavior extends DefaultDispenseItemBehavior {
   private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
   private final Boat.Type type;

   public BoatDispenseItemBehavior(Boat.Type p_123371_) {
      this.type = p_123371_;
   }

   public ItemStack execute(BlockSource p_123375_, ItemStack p_123376_) {
      Direction direction = p_123375_.getBlockState().getValue(DispenserBlock.FACING);
      Level level = p_123375_.getLevel();
      double d0 = p_123375_.x() + (double)((float)direction.getStepX() * 1.125F);
      double d1 = p_123375_.y() + (double)((float)direction.getStepY() * 1.125F);
      double d2 = p_123375_.z() + (double)((float)direction.getStepZ() * 1.125F);
      BlockPos blockpos = p_123375_.getPos().relative(direction);
      double d3;
      if (level.getFluidState(blockpos).is(FluidTags.WATER)) {
         d3 = 1.0D;
      } else {
         if (!level.getBlockState(blockpos).isAir() || !level.getFluidState(blockpos.below()).is(FluidTags.WATER)) {
            return this.defaultDispenseItemBehavior.dispense(p_123375_, p_123376_);
         }

         d3 = 0.0D;
      }

      Boat boat = new Boat(level, d0, d1 + d3, d2);
      boat.setType(this.type);
      boat.setYRot(direction.toYRot());
      level.addFreshEntity(boat);
      p_123376_.shrink(1);
      return p_123376_;
   }

   protected void playSound(BlockSource p_123373_) {
      p_123373_.getLevel().levelEvent(1000, p_123373_.getPos(), 0);
   }
}