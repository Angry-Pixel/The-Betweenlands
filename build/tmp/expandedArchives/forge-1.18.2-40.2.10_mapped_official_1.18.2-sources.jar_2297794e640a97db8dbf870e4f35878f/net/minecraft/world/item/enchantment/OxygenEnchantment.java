package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class OxygenEnchantment extends Enchantment {
   public OxygenEnchantment(Enchantment.Rarity p_45117_, EquipmentSlot... p_45118_) {
      super(p_45117_, EnchantmentCategory.ARMOR_HEAD, p_45118_);
   }

   public int getMinCost(int p_45121_) {
      return 10 * p_45121_;
   }

   public int getMaxCost(int p_45123_) {
      return this.getMinCost(p_45123_) + 30;
   }

   public int getMaxLevel() {
      return 3;
   }
}