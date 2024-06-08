package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class TridentLoyaltyEnchantment extends Enchantment {
   public TridentLoyaltyEnchantment(Enchantment.Rarity p_45240_, EquipmentSlot... p_45241_) {
      super(p_45240_, EnchantmentCategory.TRIDENT, p_45241_);
   }

   public int getMinCost(int p_45244_) {
      return 5 + p_45244_ * 7;
   }

   public int getMaxCost(int p_45248_) {
      return 50;
   }

   public int getMaxLevel() {
      return 3;
   }
}