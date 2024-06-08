package net.minecraft.recipebook;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.slf4j.Logger;

public class ServerPlaceRecipe<C extends Container> implements PlaceRecipe<Integer> {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final StackedContents stackedContents = new StackedContents();
   protected Inventory inventory;
   protected RecipeBookMenu<C> menu;

   public ServerPlaceRecipe(RecipeBookMenu<C> p_135431_) {
      this.menu = p_135431_;
   }

   public void recipeClicked(ServerPlayer p_135435_, @Nullable Recipe<C> p_135436_, boolean p_135437_) {
      if (p_135436_ != null && p_135435_.getRecipeBook().contains(p_135436_)) {
         this.inventory = p_135435_.getInventory();
         if (this.testClearGrid() || p_135435_.isCreative()) {
            this.stackedContents.clear();
            p_135435_.getInventory().fillStackedContents(this.stackedContents);
            this.menu.fillCraftSlotsStackedContents(this.stackedContents);
            if (this.stackedContents.canCraft(p_135436_, (IntList)null)) {
               this.handleRecipeClicked(p_135436_, p_135437_);
            } else {
               this.clearGrid(true);
               p_135435_.connection.send(new ClientboundPlaceGhostRecipePacket(p_135435_.containerMenu.containerId, p_135436_));
            }

            p_135435_.getInventory().setChanged();
         }
      }
   }

   protected void clearGrid(boolean p_179845_) {
      for(int i = 0; i < this.menu.getSize(); ++i) {
         if (this.menu.shouldMoveToInventory(i)) {
            ItemStack itemstack = this.menu.getSlot(i).getItem().copy();
            this.inventory.placeItemBackInInventory(itemstack, false);
            this.menu.getSlot(i).set(itemstack);
         }
      }

      this.menu.clearCraftingContent();
   }

   protected void handleRecipeClicked(Recipe<C> p_135441_, boolean p_135442_) {
      boolean flag = this.menu.recipeMatches(p_135441_);
      int i = this.stackedContents.getBiggestCraftableStack(p_135441_, (IntList)null);
      if (flag) {
         for(int j = 0; j < this.menu.getGridHeight() * this.menu.getGridWidth() + 1; ++j) {
            if (j != this.menu.getResultSlotIndex()) {
               ItemStack itemstack = this.menu.getSlot(j).getItem();
               if (!itemstack.isEmpty() && Math.min(i, itemstack.getMaxStackSize()) < itemstack.getCount() + 1) {
                  return;
               }
            }
         }
      }

      int j1 = this.getStackSize(p_135442_, i, flag);
      IntList intlist = new IntArrayList();
      if (this.stackedContents.canCraft(p_135441_, intlist, j1)) {
         int k = j1;

         for(int l : intlist) {
            int i1 = StackedContents.fromStackingIndex(l).getMaxStackSize();
            if (i1 < k) {
               k = i1;
            }
         }

         if (this.stackedContents.canCraft(p_135441_, intlist, k)) {
            this.clearGrid(false);
            this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), p_135441_, intlist.iterator(), k);
         }
      }

   }

   public void addItemToSlot(Iterator<Integer> p_135444_, int p_135445_, int p_135446_, int p_135447_, int p_135448_) {
      Slot slot = this.menu.getSlot(p_135445_);
      ItemStack itemstack = StackedContents.fromStackingIndex(p_135444_.next());
      if (!itemstack.isEmpty()) {
         for(int i = 0; i < p_135446_; ++i) {
            this.moveItemToGrid(slot, itemstack);
         }
      }

   }

   protected int getStackSize(boolean p_135450_, int p_135451_, boolean p_135452_) {
      int i = 1;
      if (p_135450_) {
         i = p_135451_;
      } else if (p_135452_) {
         i = 64;

         for(int j = 0; j < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++j) {
            if (j != this.menu.getResultSlotIndex()) {
               ItemStack itemstack = this.menu.getSlot(j).getItem();
               if (!itemstack.isEmpty() && i > itemstack.getCount()) {
                  i = itemstack.getCount();
               }
            }
         }

         if (i < 64) {
            ++i;
         }
      }

      return i;
   }

   protected void moveItemToGrid(Slot p_135439_, ItemStack p_135440_) {
      int i = this.inventory.findSlotMatchingUnusedItem(p_135440_);
      if (i != -1) {
         ItemStack itemstack = this.inventory.getItem(i).copy();
         if (!itemstack.isEmpty()) {
            if (itemstack.getCount() > 1) {
               this.inventory.removeItem(i, 1);
            } else {
               this.inventory.removeItemNoUpdate(i);
            }

            itemstack.setCount(1);
            if (p_135439_.getItem().isEmpty()) {
               p_135439_.set(itemstack);
            } else {
               p_135439_.getItem().grow(1);
            }

         }
      }
   }

   private boolean testClearGrid() {
      List<ItemStack> list = Lists.newArrayList();
      int i = this.getAmountOfFreeSlotsInInventory();

      for(int j = 0; j < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++j) {
         if (j != this.menu.getResultSlotIndex()) {
            ItemStack itemstack = this.menu.getSlot(j).getItem().copy();
            if (!itemstack.isEmpty()) {
               int k = this.inventory.getSlotWithRemainingSpace(itemstack);
               if (k == -1 && list.size() <= i) {
                  for(ItemStack itemstack1 : list) {
                     if (itemstack1.sameItem(itemstack) && itemstack1.getCount() != itemstack1.getMaxStackSize() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) {
                        itemstack1.grow(itemstack.getCount());
                        itemstack.setCount(0);
                        break;
                     }
                  }

                  if (!itemstack.isEmpty()) {
                     if (list.size() >= i) {
                        return false;
                     }

                     list.add(itemstack);
                  }
               } else if (k == -1) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private int getAmountOfFreeSlotsInInventory() {
      int i = 0;

      for(ItemStack itemstack : this.inventory.items) {
         if (itemstack.isEmpty()) {
            ++i;
         }
      }

      return i;
   }
}