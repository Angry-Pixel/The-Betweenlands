package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DispenserMenu extends AbstractContainerMenu {
   private static final int SLOT_COUNT = 9;
   private static final int INV_SLOT_START = 9;
   private static final int INV_SLOT_END = 36;
   private static final int USE_ROW_SLOT_START = 36;
   private static final int USE_ROW_SLOT_END = 45;
   private final Container dispenser;

   public DispenserMenu(int p_39433_, Inventory p_39434_) {
      this(p_39433_, p_39434_, new SimpleContainer(9));
   }

   public DispenserMenu(int p_39436_, Inventory p_39437_, Container p_39438_) {
      super(MenuType.GENERIC_3x3, p_39436_);
      checkContainerSize(p_39438_, 9);
      this.dispenser = p_39438_;
      p_39438_.startOpen(p_39437_.player);

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 3; ++j) {
            this.addSlot(new Slot(p_39438_, j + i * 3, 62 + j * 18, 17 + i * 18));
         }
      }

      for(int k = 0; k < 3; ++k) {
         for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(p_39437_, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
         }
      }

      for(int l = 0; l < 9; ++l) {
         this.addSlot(new Slot(p_39437_, l, 8 + l * 18, 142));
      }

   }

   public boolean stillValid(Player p_39440_) {
      return this.dispenser.stillValid(p_39440_);
   }

   public ItemStack quickMoveStack(Player p_39444_, int p_39445_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_39445_);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (p_39445_ < 9) {
            if (!this.moveItemStackTo(itemstack1, 9, 45, true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(itemstack1, 0, 9, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(p_39444_, itemstack1);
      }

      return itemstack;
   }

   public void removed(Player p_39442_) {
      super.removed(p_39442_);
      this.dispenser.stopOpen(p_39442_);
   }
}