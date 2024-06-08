package net.minecraft.world.level.block;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WoodButtonBlock extends ButtonBlock {
   public WoodButtonBlock(BlockBehaviour.Properties p_58284_) {
      super(true, p_58284_);
   }

   protected SoundEvent getSound(boolean p_58286_) {
      return p_58286_ ? SoundEvents.WOODEN_BUTTON_CLICK_ON : SoundEvents.WOODEN_BUTTON_CLICK_OFF;
   }
}