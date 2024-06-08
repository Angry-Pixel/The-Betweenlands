package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class MendingEnchantment extends Enchantment {
   public MendingEnchantment(Enchantment.Rarity p_45098_, EquipmentSlot... p_45099_) {
      super(p_45098_, EnchantmentCategory.BREAKABLE, p_45099_);
   }

   public int getMinCost(int p_45102_) {
      return p_45102_ * 25;
   }

   public int getMaxCost(int p_45105_) {
      return this.getMinCost(p_45105_) + 50;
   }

   public boolean isTreasureOnly() {
      return true;
   }

   public int getMaxLevel() {
      return 1;
   }
}