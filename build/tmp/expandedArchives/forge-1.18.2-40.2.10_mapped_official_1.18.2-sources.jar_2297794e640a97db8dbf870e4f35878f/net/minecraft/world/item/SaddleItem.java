package net.minecraft.world.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.player.Player;

public class SaddleItem extends Item {
   public SaddleItem(Item.Properties p_43053_) {
      super(p_43053_);
   }

   public InteractionResult interactLivingEntity(ItemStack p_43055_, Player p_43056_, LivingEntity p_43057_, InteractionHand p_43058_) {
      if (p_43057_ instanceof Saddleable && p_43057_.isAlive()) {
         Saddleable saddleable = (Saddleable)p_43057_;
         if (!saddleable.isSaddled() && saddleable.isSaddleable()) {
            if (!p_43056_.level.isClientSide) {
               saddleable.equipSaddle(SoundSource.NEUTRAL);
               p_43055_.shrink(1);
            }

            return InteractionResult.sidedSuccess(p_43056_.level.isClientSide);
         }
      }

      return InteractionResult.PASS;
   }
}