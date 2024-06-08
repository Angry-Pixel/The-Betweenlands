package net.minecraft.world.inventory;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class CartographyTableMenu extends AbstractContainerMenu {
   public static final int MAP_SLOT = 0;
   public static final int ADDITIONAL_SLOT = 1;
   public static final int RESULT_SLOT = 2;
   private static final int INV_SLOT_START = 3;
   private static final int INV_SLOT_END = 30;
   private static final int USE_ROW_SLOT_START = 30;
   private static final int USE_ROW_SLOT_END = 39;
   private final ContainerLevelAccess access;
   long lastSoundTime;
   public final Container container = new SimpleContainer(2) {
      public void setChanged() {
         CartographyTableMenu.this.slotsChanged(this);
         super.setChanged();
      }
   };
   private final ResultContainer resultContainer = new ResultContainer() {
      public void setChanged() {
         CartographyTableMenu.this.slotsChanged(this);
         super.setChanged();
      }
   };

   public CartographyTableMenu(int p_39140_, Inventory p_39141_) {
      this(p_39140_, p_39141_, ContainerLevelAccess.NULL);
   }

   public CartographyTableMenu(int p_39143_, Inventory p_39144_, final ContainerLevelAccess p_39145_) {
      super(MenuType.CARTOGRAPHY_TABLE, p_39143_);
      this.access = p_39145_;
      this.addSlot(new Slot(this.container, 0, 15, 15) {
         public boolean mayPlace(ItemStack p_39194_) {
            return p_39194_.is(Items.FILLED_MAP);
         }
      });
      this.addSlot(new Slot(this.container, 1, 15, 52) {
         public boolean mayPlace(ItemStack p_39203_) {
            return p_39203_.is(Items.PAPER) || p_39203_.is(Items.MAP) || p_39203_.is(Items.GLASS_PANE);
         }
      });
      this.addSlot(new Slot(this.resultContainer, 2, 145, 39) {
         public boolean mayPlace(ItemStack p_39217_) {
            return false;
         }

         public void onTake(Player p_150509_, ItemStack p_150510_) {
            CartographyTableMenu.this.slots.get(0).remove(1);
            CartographyTableMenu.this.slots.get(1).remove(1);
            p_150510_.getItem().onCraftedBy(p_150510_, p_150509_.level, p_150509_);
            p_39145_.execute((p_39219_, p_39220_) -> {
               long l = p_39219_.getGameTime();
               if (CartographyTableMenu.this.lastSoundTime != l) {
                  p_39219_.playSound((Player)null, p_39220_, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                  CartographyTableMenu.this.lastSoundTime = l;
               }

            });
            super.onTake(p_150509_, p_150510_);
         }
      });

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_39144_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_39144_, k, 8 + k * 18, 142));
      }

   }

   public boolean stillValid(Player p_39149_) {
      return stillValid(this.access, p_39149_, Blocks.CARTOGRAPHY_TABLE);
   }

   public void slotsChanged(Container p_39147_) {
      ItemStack itemstack = this.container.getItem(0);
      ItemStack itemstack1 = this.container.getItem(1);
      ItemStack itemstack2 = this.resultContainer.getItem(2);
      if (itemstack2.isEmpty() || !itemstack.isEmpty() && !itemstack1.isEmpty()) {
         if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
            this.setupResultSlot(itemstack, itemstack1, itemstack2);
         }
      } else {
         this.resultContainer.removeItemNoUpdate(2);
      }

   }

   private void setupResultSlot(ItemStack p_39163_, ItemStack p_39164_, ItemStack p_39165_) {
      this.access.execute((p_39170_, p_39171_) -> {
         MapItemSavedData mapitemsaveddata = MapItem.getSavedData(p_39163_, p_39170_);
         if (mapitemsaveddata != null) {
            ItemStack itemstack;
            if (p_39164_.is(Items.PAPER) && !mapitemsaveddata.locked && mapitemsaveddata.scale < 4) {
               itemstack = p_39163_.copy();
               itemstack.setCount(1);
               itemstack.getOrCreateTag().putInt("map_scale_direction", 1);
               this.broadcastChanges();
            } else if (p_39164_.is(Items.GLASS_PANE) && !mapitemsaveddata.locked) {
               itemstack = p_39163_.copy();
               itemstack.setCount(1);
               itemstack.getOrCreateTag().putBoolean("map_to_lock", true);
               this.broadcastChanges();
            } else {
               if (!p_39164_.is(Items.MAP)) {
                  this.resultContainer.removeItemNoUpdate(2);
                  this.broadcastChanges();
                  return;
               }

               itemstack = p_39163_.copy();
               itemstack.setCount(2);
               this.broadcastChanges();
            }

            if (!ItemStack.matches(itemstack, p_39165_)) {
               this.resultContainer.setItem(2, itemstack);
               this.broadcastChanges();
            }

         }
      });
   }

   public boolean canTakeItemForPickAll(ItemStack p_39160_, Slot p_39161_) {
      return p_39161_.container != this.resultContainer && super.canTakeItemForPickAll(p_39160_, p_39161_);
   }

   public ItemStack quickMoveStack(Player p_39175_, int p_39176_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_39176_);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (p_39176_ == 2) {
            itemstack1.getItem().onCraftedBy(itemstack1, p_39175_.level, p_39175_);
            if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemstack1, itemstack);
         } else if (p_39176_ != 1 && p_39176_ != 0) {
            if (itemstack1.is(Items.FILLED_MAP)) {
               if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!itemstack1.is(Items.PAPER) && !itemstack1.is(Items.MAP) && !itemstack1.is(Items.GLASS_PANE)) {
               if (p_39176_ >= 3 && p_39176_ < 30) {
                  if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                     return ItemStack.EMPTY;
                  }
               } else if (p_39176_ >= 30 && p_39176_ < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         }

         slot.setChanged();
         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(p_39175_, itemstack1);
         this.broadcastChanges();
      }

      return itemstack;
   }

   public void removed(Player p_39173_) {
      super.removed(p_39173_);
      this.resultContainer.removeItemNoUpdate(2);
      this.access.execute((p_39152_, p_39153_) -> {
         this.clearContainer(p_39173_, this.container);
      });
   }
}