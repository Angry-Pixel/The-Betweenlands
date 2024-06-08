package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class LootBonusEnchantment extends Enchantment {
   protected LootBonusEnchantment(Enchantment.Rarity p_45087_, EnchantmentCategory p_45088_, EquipmentSlot... p_45089_) {
      super(p_45087_, p_45088_, p_45089_);
   }

   public int getMinCost(int p_45092_) {
      return 15 + (p_45092_ - 1) * 9;
   }

   public int getMaxCost(int p_45096_) {
      return super.getMinCost(p_45096_) + 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean checkCompatibility(Enchantment p_45094_) {
      return super.checkCompatibility(p_45094_) && p_45094_ != Enchantments.SILK_TOUCH;
   }
}