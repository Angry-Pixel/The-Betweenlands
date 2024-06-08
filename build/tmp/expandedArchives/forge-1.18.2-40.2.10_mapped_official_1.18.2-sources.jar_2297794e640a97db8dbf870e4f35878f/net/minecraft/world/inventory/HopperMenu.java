package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HopperMenu extends AbstractContainerMenu {
   public static final int CONTAINER_SIZE = 5;
   private final Container hopper;

   public HopperMenu(int p_39640_, Inventory p_39641_) {
      this(p_39640_, p_39641_, new SimpleContainer(5));
   }

   public HopperMenu(int p_39643_, Inventory p_39644_, Container p_39645_) {
      super(MenuType.HOPPER, p_39643_);
      this.hopper = p_39645_;
      checkContainerSize(p_39645_, 5);
      p_39645_.startOpen(p_39644_.player);
      int i = 51;

      for(int j = 0; j < 5; ++j) {
         this.addSlot(new Slot(p_39645_, j, 44 + j * 18, 20));
      }

      for(int l = 0; l < 3; ++l) {
         for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(p_39644_, k + l * 9 + 9, 8 + k * 18, l * 18 + 51));
         }
      }

      for(int i1 = 0; i1 < 9; ++i1) {
         this.addSlot(new Slot(p_39644_, i1, 8 + i1 * 18, 109));
      }

   }

   public boolean stillValid(Player p_39647_) {
      return this.hopper.stillValid(p_39647_);
   }

   public ItemStack quickMoveStack(Player p_39651_, int p_39652_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_39652_);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (p_39652_ < this.hopper.getContainerSize()) {
            if (!this.moveItemStackTo(itemstack1, this.hopper.getContainerSize(), this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(itemstack1, 0, this.hopper.getContainerSize(), false)) {
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

   public void removed(Player p_39649_) {
      super.removed(p_39649_);
      this.hopper.stopOpen(p_39649_);
   }
}