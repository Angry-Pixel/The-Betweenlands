package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class FurnaceFuelSlot extends Slot {
   private final AbstractFurnaceMenu menu;

   public FurnaceFuelSlot(AbstractFurnaceMenu p_39520_, Container p_39521_, int p_39522_, int p_39523_, int p_39524_) {
      super(p_39521_, p_39522_, p_39523_, p_39524_);
      this.menu = p_39520_;
   }

   public boolean mayPlace(ItemStack p_39526_) {
      return this.menu.isFuel(p_39526_) || isBucket(p_39526_);
   }

   public int getMaxStackSize(ItemStack p_39528_) {
      return isBucket(p_39528_) ? 1 : super.getMaxStackSize(p_39528_);
   }

   public static boolean isBucket(ItemStack p_39530_) {
      return p_39530_.is(Items.BUCKET);
   }
}