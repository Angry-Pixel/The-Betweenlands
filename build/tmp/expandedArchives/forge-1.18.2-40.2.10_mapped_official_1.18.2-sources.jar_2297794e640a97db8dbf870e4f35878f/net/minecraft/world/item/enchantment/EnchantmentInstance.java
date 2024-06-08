package net.minecraft.world.item.enchantment;

import net.minecraft.util.random.WeightedEntry;

public class EnchantmentInstance extends WeightedEntry.IntrusiveBase {
   public final Enchantment enchantment;
   public final int level;

   public EnchantmentInstance(Enchantment p_44950_, int p_44951_) {
      super(p_44950_.getRarity().getWeight());
      this.enchantment = p_44950_;
      this.level = p_44951_;
   }
}