package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class FireAspectEnchantment extends Enchantment {
   protected FireAspectEnchantment(Enchantment.Rarity p_44996_, EquipmentSlot... p_44997_) {
      super(p_44996_, EnchantmentCategory.WEAPON, p_44997_);
   }

   public int getMinCost(int p_45000_) {
      return 10 + 20 * (p_45000_ - 1);
   }

   public int getMaxCost(int p_45002_) {
      return super.getMinCost(p_45002_) + 50;
   }

   public int getMaxLevel() {
      return 2;
   }
}