package net.minecraft.world.item.enchantment;

import java.util.Random;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class DigDurabilityEnchantment extends Enchantment {
   protected DigDurabilityEnchantment(Enchantment.Rarity p_44648_, EquipmentSlot... p_44649_) {
      super(p_44648_, EnchantmentCategory.BREAKABLE, p_44649_);
   }

   public int getMinCost(int p_44652_) {
      return 5 + (p_44652_ - 1) * 8;
   }

   public int getMaxCost(int p_44660_) {
      return super.getMinCost(p_44660_) + 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canEnchant(ItemStack p_44654_) {
      return p_44654_.isDamageableItem() ? true : super.canEnchant(p_44654_);
   }

   public static boolean shouldIgnoreDurabilityDrop(ItemStack p_44656_, int p_44657_, Random p_44658_) {
      if (p_44656_.getItem() instanceof ArmorItem && p_44658_.nextFloat() < 0.6F) {
         return false;
      } else {
         return p_44658_.nextInt(p_44657_ + 1) > 0;
      }
   }
}