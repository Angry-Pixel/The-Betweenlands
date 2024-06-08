package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class WaterWorkerEnchantment extends Enchantment {
   public WaterWorkerEnchantment(Enchantment.Rarity p_45290_, EquipmentSlot... p_45291_) {
      super(p_45290_, EnchantmentCategory.ARMOR_HEAD, p_45291_);
   }

   public int getMinCost(int p_45294_) {
      return 1;
   }

   public int getMaxCost(int p_45296_) {
      return this.getMinCost(p_45296_) + 40;
   }

   public int getMaxLevel() {
      return 1;
   }
}