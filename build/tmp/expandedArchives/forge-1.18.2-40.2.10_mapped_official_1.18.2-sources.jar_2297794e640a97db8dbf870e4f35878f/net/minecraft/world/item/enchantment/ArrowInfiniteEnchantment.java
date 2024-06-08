package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class ArrowInfiniteEnchantment extends Enchantment {
   public ArrowInfiniteEnchantment(Enchantment.Rarity p_44584_, EquipmentSlot... p_44585_) {
      super(p_44584_, EnchantmentCategory.BOW, p_44585_);
   }

   public int getMinCost(int p_44588_) {
      return 20;
   }

   public int getMaxCost(int p_44592_) {
      return 50;
   }

   public int getMaxLevel() {
      return 1;
   }

   public boolean checkCompatibility(Enchantment p_44590_) {
      return p_44590_ instanceof MendingEnchantment ? false : super.checkCompatibility(p_44590_);
   }
}