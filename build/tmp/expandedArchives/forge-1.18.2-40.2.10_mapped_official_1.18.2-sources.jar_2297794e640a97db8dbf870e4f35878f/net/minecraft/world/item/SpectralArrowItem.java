package net.minecraft.world.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.level.Level;

public class SpectralArrowItem extends ArrowItem {
   public SpectralArrowItem(Item.Properties p_43235_) {
      super(p_43235_);
   }

   public AbstractArrow createArrow(Level p_43237_, ItemStack p_43238_, LivingEntity p_43239_) {
      return new SpectralArrow(p_43237_, p_43239_);
   }
}