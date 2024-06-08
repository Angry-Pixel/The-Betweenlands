package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class BindingCurseEnchantment extends Enchantment {
   public BindingCurseEnchantment(Enchantment.Rarity p_44612_, EquipmentSlot... p_44613_) {
      super(p_44612_, EnchantmentCategory.WEARABLE, p_44613_);
   }

   public int getMinCost(int p_44616_) {
      return 25;
   }

   public int getMaxCost(int p_44619_) {
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