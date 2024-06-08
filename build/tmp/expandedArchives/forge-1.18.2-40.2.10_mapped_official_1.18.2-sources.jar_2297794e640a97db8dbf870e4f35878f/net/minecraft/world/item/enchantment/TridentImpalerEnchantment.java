package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;

public class TridentImpalerEnchantment extends Enchantment {
   public TridentImpalerEnchantment(Enchantment.Rarity p_45229_, EquipmentSlot... p_45230_) {
      super(p_45229_, EnchantmentCategory.TRIDENT, p_45230_);
   }

   public int getMinCost(int p_45233_) {
      return 1 + (p_45233_ - 1) * 8;
   }

   public int getMaxCost(int p_45238_) {
      return this.getMinCost(p_45238_) + 20;
   }

   public int getMaxLevel() {
      return 5;
   }

   public float getDamageBonus(int p_45235_, MobType p_45236_) {
      return p_45236_ == MobType.WATER ? (float)p_45235_ * 2.5F : 0.0F;
   }
}