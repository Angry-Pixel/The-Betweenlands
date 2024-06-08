package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class LecternMenu extends AbstractContainerMenu {
   private static final int DATA_COUNT = 1;
   private static final int SLOT_COUNT = 1;
   public static final int BUTTON_PREV_PAGE = 1;
   public static final int BUTTON_NEXT_PAGE = 2;
   public static final int BUTTON_TAKE_BOOK = 3;
   public static final int BUTTON_PAGE_JUMP_RANGE_START = 100;
   private final Container lectern;
   private final ContainerData lecternData;

   public LecternMenu(int p_39822_) {
      this(p_39822_, new SimpleContainer(1), new SimpleContainerData(1));
   }

   public LecternMenu(int p_39824_, Container p_39825_, ContainerData p_39826_) {
      super(MenuType.LECTERN, p_39824_);
      checkContainerSize(p_39825_, 1);
      checkContainerDataCount(p_39826_, 1);
      this.lectern = p_39825_;
      this.lecternData = p_39826_;
      this.addSlot(new Slot(p_39825_, 0, 0, 0) {
         public void setChanged() {
            super.setChanged();
            LecternMenu.this.slotsChanged(this.container);
         }
      });
      this.addDataSlots(p_39826_);
   }

   public boolean clickMenuButton(Player p_39833_, int p_39834_) {
      if (p_39834_ >= 100) {
         int k = p_39834_ - 100;
         this.setData(0, k);
         return true;
      } else {
         switch(p_39834_) {
         case 1:
            int j = this.lecternData.get(0);
            this.setData(0, j - 1);
            return true;
         case 2:
            int i = this.lecternData.get(0);
            this.setData(0, i + 1);
            return true;
         case 3:
            if (!p_39833_.mayBuild()) {
               return false;
            }

            ItemStack itemstack = this.lectern.removeItemNoUpdate(0);
            this.lectern.setChanged();
            if (!p_39833_.getInventory().add(itemstack)) {
               p_39833_.drop(itemstack, false);
            }

            return true;
         default:
            return false;
         }
      }
   }

   public void setData(int p_39828_, int p_39829_) {
      super.setData(p_39828_, p_39829_);
      this.broadcastChanges();
   }

   public boolean stillValid(Player p_39831_) {
      return this.lectern.stillValid(p_39831_);
   }

   public ItemStack getBook() {
      return this.lectern.getItem(0);
   }

   public int getPage() {
      return this.lecternData.get(0);
   }
}