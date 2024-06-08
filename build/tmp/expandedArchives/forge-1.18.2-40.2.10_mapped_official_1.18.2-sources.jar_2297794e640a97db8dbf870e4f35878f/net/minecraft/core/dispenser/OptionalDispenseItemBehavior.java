package net.minecraft.core.dispenser;

import net.minecraft.core.BlockSource;

public abstract class OptionalDispenseItemBehavior extends DefaultDispenseItemBehavior {
   private boolean success = true;

   public boolean isSuccess() {
      return this.success;
   }

   public void setSuccess(boolean p_123574_) {
      this.success = p_123574_;
   }

   protected void playSound(BlockSource p_123572_) {
      p_123572_.getLevel().levelEvent(this.isSuccess() ? 1000 : 1001, p_123572_.getPos(), 0);
   }
}