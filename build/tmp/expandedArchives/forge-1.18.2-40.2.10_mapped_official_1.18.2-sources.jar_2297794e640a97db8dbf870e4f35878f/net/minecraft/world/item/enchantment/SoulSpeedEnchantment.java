package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class SoulSpeedEnchantment extends Enchantment {
   public SoulSpeedEnchantment(Enchantment.Rarity p_45175_, EquipmentSlot... p_45176_) {
      super(p_45175_, EnchantmentCategory.ARMOR_FEET, p_45176_);
   }

   public int getMinCost(int p_45179_) {
      return p_45179_ * 10;
   }

   public int getMaxCost(int p_45182_) {
      return this.getMinCost(p_45182_) + 15;
   }

   public boolean isTreasureOnly() {
      return true;
   }

   public boolean isTradeable() {
      return false;
   }

   public boolean isDiscoverable() {
      return false;
   }

   public int getMaxLevel() {
      return 3;
   }
}