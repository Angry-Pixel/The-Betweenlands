package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class WaterWalkerEnchantment extends Enchantment {
   public WaterWalkerEnchantment(Enchantment.Rarity p_45280_, EquipmentSlot... p_45281_) {
      super(p_45280_, EnchantmentCategory.ARMOR_FEET, p_45281_);
   }

   public int getMinCost(int p_45284_) {
      return p_45284_ * 10;
   }

   public int getMaxCost(int p_45288_) {
      return this.getMinCost(p_45288_) + 15;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean checkCompatibility(Enchantment p_45286_) {
      return super.checkCompatibility(p_45286_) && p_45286_ != Enchantments.FROST_WALKER;
   }
}