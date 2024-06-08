package net.minecraft.world.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;

public class ThrowablePotionItem extends PotionItem {
   public ThrowablePotionItem(Item.Properties p_43301_) {
      super(p_43301_);
   }

   public InteractionResultHolder<ItemStack> use(Level p_43303_, Player p_43304_, InteractionHand p_43305_) {
      ItemStack itemstack = p_43304_.getItemInHand(p_43305_);
      if (!p_43303_.isClientSide) {
         ThrownPotion thrownpotion = new ThrownPotion(p_43303_, p_43304_);
         thrownpotion.setItem(itemstack);
         thrownpotion.shootFromRotation(p_43304_, p_43304_.getXRot(), p_43304_.getYRot(), -20.0F, 0.5F, 1.0F);
         p_43303_.addFreshEntity(thrownpotion);
      }

      p_43304_.awardStat(Stats.ITEM_USED.get(this));
      if (!p_43304_.getAbilities().instabuild) {
         itemstack.shrink(1);
      }

      return InteractionResultHolder.sidedSuccess(itemstack, p_43303_.isClientSide());
   }
}