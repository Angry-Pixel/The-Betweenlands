package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class SweepingEdgeEnchantment extends Enchantment {
   public SweepingEdgeEnchantment(Enchantment.Rarity p_45186_, EquipmentSlot... p_45187_) {
      super(p_45186_, EnchantmentCategory.WEAPON, p_45187_);
   }

   public int getMinCost(int p_45190_) {
      return 5 + (p_45190_ - 1) * 9;
   }

   public int getMaxCost(int p_45192_) {
      return this.getMinCost(p_45192_) + 15;
   }

   public int getMaxLevel() {
      return 3;
   }

   public static float getSweepingDamageRatio(int p_45194_) {
      return 1.0F - 1.0F / (float)(p_45194_ + 1);
   }
}