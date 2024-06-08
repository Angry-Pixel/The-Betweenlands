package net.minecraft.world.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;

public class ArrowItem extends Item {
   public ArrowItem(Item.Properties p_40512_) {
      super(p_40512_);
   }

   public AbstractArrow createArrow(Level p_40513_, ItemStack p_40514_, LivingEntity p_40515_) {
      Arrow arrow = new Arrow(p_40513_, p_40515_);
      arrow.setEffectsFromItem(p_40514_);
      return arrow;
   }

   public boolean isInfinite(ItemStack stack, ItemStack bow, net.minecraft.world.entity.player.Player player) {
      int enchant = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.INFINITY_ARROWS, bow);
      return enchant <= 0 ? false : this.getClass() == ArrowItem.class;
   }
}
