package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DiggingEnchantment extends Enchantment {
   protected DiggingEnchantment(Enchantment.Rarity p_44662_, EquipmentSlot... p_44663_) {
      super(p_44662_, EnchantmentCategory.DIGGER, p_44663_);
   }

   public int getMinCost(int p_44666_) {
      return 1 + 10 * (p_44666_ - 1);
   }

   public int getMaxCost(int p_44670_) {
      return super.getMinCost(p_44670_) + 50;
   }

   public int getMaxLevel() {
      return 5;
   }

   public boolean canEnchant(ItemStack p_44668_) {
      return p_44668_.getItem() instanceof net.minecraft.world.item.ShearsItem ? true : super.canEnchant(p_44668_);
   }
}
