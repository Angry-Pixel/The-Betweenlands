package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;

public class ArrowDamageEnchantment extends Enchantment {
   public ArrowDamageEnchantment(Enchantment.Rarity p_44568_, EquipmentSlot... p_44569_) {
      super(p_44568_, EnchantmentCategory.BOW, p_44569_);
   }

   public int getMinCost(int p_44572_) {
      return 1 + (p_44572_ - 1) * 10;
   }

   public int getMaxCost(int p_44574_) {
      return this.getMinCost(p_44574_) + 15;
   }

   public int getMaxLevel() {
      return 5;
   }
}