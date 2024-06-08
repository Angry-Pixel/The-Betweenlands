package net.minecraft.core.dispenser;

import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public abstract class AbstractProjectileDispenseBehavior extends DefaultDispenseItemBehavior {
   public ItemStack execute(BlockSource p_123366_, ItemStack p_123367_) {
      Level level = p_123366_.getLevel();
      Position position = DispenserBlock.getDispensePosition(p_123366_);
      Direction direction = p_123366_.getBlockState().getValue(DispenserBlock.FACING);
      Projectile projectile = this.getProjectile(level, position, p_123367_);
      projectile.shoot((double)direction.getStepX(), (double)((float)direction.getStepY() + 0.1F), (double)direction.getStepZ(), this.getPower(), this.getUncertainty());
      level.addFreshEntity(projectile);
      p_123367_.shrink(1);
      return p_123367_;
   }

   protected void playSound(BlockSource p_123364_) {
      p_123364_.getLevel().levelEvent(1002, p_123364_.getPos(), 0);
   }

   protected abstract Projectile getProjectile(Level p_123360_, Position p_123361_, ItemStack p_123362_);

   protected float getUncertainty() {
      return 6.0F;
   }

   protected float getPower() {
      return 1.1F;
   }
}