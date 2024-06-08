package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class ArrowPiercingEnchantment extends Enchantment {
   public ArrowPiercingEnchantment(Enchantment.Rarity p_44602_, EquipmentSlot... p_44603_) {
      super(p_44602_, EnchantmentCategory.CROSSBOW, p_44603_);
   }

   public int getMinCost(int p_44606_) {
      return 1 + (p_44606_ - 1) * 10;
   }

   public int getMaxCost(int p_44610_) {
      return 50;
   }

   public int getMaxLevel() {
      return 4;
   }

   public boolean checkCompatibility(Enchantment p_44608_) {
      return super.checkCompatibility(p_44608_) && p_44608_ != Enchantments.MULTISHOT;
   }
}