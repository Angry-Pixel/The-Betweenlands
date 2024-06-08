package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ShulkerBoxMenu extends AbstractContainerMenu {
   private static final int CONTAINER_SIZE = 27;
   private final Container container;

   public ShulkerBoxMenu(int p_40188_, Inventory p_40189_) {
      this(p_40188_, p_40189_, new SimpleContainer(27));
   }

   public ShulkerBoxMenu(int p_40191_, Inventory p_40192_, Container p_40193_) {
      super(MenuType.SHULKER_BOX, p_40191_);
      checkContainerSize(p_40193_, 27);
      this.container = p_40193_;
      p_40193_.startOpen(p_40192_.player);
      int i = 3;
      int j = 9;

      for(int k = 0; k < 3; ++k) {
         for(int l = 0; l < 9; ++l) {
            this.addSlot(new ShulkerBoxSlot(p_40193_, l + k * 9, 8 + l * 18, 18 + k * 18));
         }
      }

      for(int i1 = 0; i1 < 3; ++i1) {
         for(int k1 = 0; k1 < 9; ++k1) {
            this.addSlot(new Slot(p_40192_, k1 + i1 * 9 + 9, 8 + k1 * 18, 84 + i1 * 18));
         }
      }

      for(int j1 = 0; j1 < 9; ++j1) {
         this.addSlot(new Slot(p_40192_, j1, 8 + j1 * 18, 142));
      }

   }

   public boolean stillValid(Player p_40195_) {
      return this.container.stillValid(p_40195_);
   }

   public ItemStack quickMoveStack(Player p_40199_, int p_40200_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_40200_);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (p_40200_ < this.container.getContainerSize()) {
            if (!this.moveItemStackTo(itemstack1, this.container.getContainerSize(), this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(itemstack1, 0, this.container.getContainerSize(), false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }
      }

      return itemstack;
   }

   public void removed(Player p_40197_) {
      super.removed(p_40197_);
      this.container.stopOpen(p_40197_);
   }
}