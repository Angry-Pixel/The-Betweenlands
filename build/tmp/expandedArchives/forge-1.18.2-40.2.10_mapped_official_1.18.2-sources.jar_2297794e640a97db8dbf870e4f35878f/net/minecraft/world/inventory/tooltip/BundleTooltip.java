package net.minecraft.world.inventory.tooltip;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class BundleTooltip implements TooltipComponent {
   private final NonNullList<ItemStack> items;
   private final int weight;

   public BundleTooltip(NonNullList<ItemStack> p_150677_, int p_150678_) {
      this.items = p_150677_;
      this.weight = p_150678_;
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   public int getWeight() {
      return this.weight;
   }
}