package net.minecraft.world.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class NameTagItem extends Item {
   public NameTagItem(Item.Properties p_42952_) {
      super(p_42952_);
   }

   public InteractionResult interactLivingEntity(ItemStack p_42954_, Player p_42955_, LivingEntity p_42956_, InteractionHand p_42957_) {
      if (p_42954_.hasCustomHoverName() && !(p_42956_ instanceof Player)) {
         if (!p_42955_.level.isClientSide && p_42956_.isAlive()) {
            p_42956_.setCustomName(p_42954_.getHoverName());
            if (p_42956_ instanceof Mob) {
               ((Mob)p_42956_).setPersistenceRequired();
            }

            p_42954_.shrink(1);
         }

         return InteractionResult.sidedSuccess(p_42955_.level.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }
}