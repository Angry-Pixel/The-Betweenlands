package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class TridentRiptideEnchantment extends Enchantment {
   public TridentRiptideEnchantment(Enchantment.Rarity p_45250_, EquipmentSlot... p_45251_) {
      super(p_45250_, EnchantmentCategory.TRIDENT, p_45251_);
   }

   public int getMinCost(int p_45254_) {
      return 10 + p_45254_ * 7;
   }

   public int getMaxCost(int p_45258_) {
      return 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean checkCompatibility(Enchantment p_45256_) {
      return super.checkCompatibility(p_45256_) && p_45256_ != Enchantments.LOYALTY && p_45256_ != Enchantments.CHANNELING;
   }
}