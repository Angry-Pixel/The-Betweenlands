package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class VanishingCurseEnchantment extends Enchantment {
   public VanishingCurseEnchantment(Enchantment.Rarity p_45270_, EquipmentSlot... p_45271_) {
      super(p_45270_, EnchantmentCategory.VANISHABLE, p_45271_);
   }

   public int getMinCost(int p_45274_) {
      return 25;
   }

   public int getMaxCost(int p_45277_) {
      return 50;
   }

   public int getMaxLevel() {
      return 1;
   }

   public boolean isTreasureOnly() {
      return true;
   }

   public boolean isCurse() {
      return true;
   }
}