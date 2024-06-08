package net.minecraft.world.inventory;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;

public abstract class AbstractContainerMenu {
   private static Logger LOGGER = LogUtils.getLogger();
   public static final int SLOT_CLICKED_OUTSIDE = -999;
   public static final int QUICKCRAFT_TYPE_CHARITABLE = 0;
   public static final int QUICKCRAFT_TYPE_GREEDY = 1;
   public static final int QUICKCRAFT_TYPE_CLONE = 2;
   public static final int QUICKCRAFT_HEADER_START = 0;
   public static final int QUICKCRAFT_HEADER_CONTINUE = 1;
   public static final int QUICKCRAFT_HEADER_END = 2;
   public static final int CARRIED_SLOT_SIZE = Integer.MAX_VALUE;
   private final NonNullList<ItemStack> lastSlots = NonNullList.create();
   public final NonNullList<Slot> slots = NonNullList.create();
   private final List<DataSlot> dataSlots = Lists.newArrayList();
   private ItemStack carried = ItemStack.EMPTY;
   private final NonNullList<ItemStack> remoteSlots = NonNullList.create();
   private final IntList remoteDataSlots = new IntArrayList();
   private ItemStack remoteCarried = ItemStack.EMPTY;
   private int stateId;
   @Nullable
   private final MenuType<?> menuType;
   public final int containerId;
   private int quickcraftType = -1;
   private int quickcraftStatus;
   private final Set<Slot> quickcraftSlots = Sets.newHashSet();
   private final List<ContainerListener> containerListeners = Lists.newArrayList();
   @Nullable
   private ContainerSynchronizer synchronizer;
   private boolean suppressRemoteUpdates;

   protected AbstractContainerMenu(@Nullable MenuType<?> p_38851_, int p_38852_) {
      this.menuType = p_38851_;
      this.containerId = p_38852_;
   }

   protected static boolean stillValid(ContainerLevelAccess p_38890_, Player p_38891_, Block p_38892_) {
      return p_38890_.evaluate((p_38916_, p_38917_) -> {
         return !p_38916_.getBlockState(p_38917_).is(p_38892_) ? false : p_38891_.distanceToSqr((double)p_38917_.getX() + 0.5D, (double)p_38917_.getY() + 0.5D, (double)p_38917_.getZ() + 0.5D) <= 64.0D;
      }, true);
   }

   public MenuType<?> getType() {
      if (this.menuType == null) {
         throw new UnsupportedOperationException("Unable to construct this menu by type");
      } else {
         return this.menuType;
      }
   }

   protected static void checkContainerSize(Container p_38870_, int p_38871_) {
      int i = p_38870_.getContainerSize();
      if (i < p_38871_) {
         throw new IllegalArgumentException("Container size " + i + " is smaller than expected " + p_38871_);
      }
   }

   protected static void checkContainerDataCount(ContainerData p_38887_, int p_38888_) {
      int i = p_38887_.getCount();
      if (i < p_38888_) {
         throw new IllegalArgumentException("Container data count " + i + " is smaller than expected " + p_38888_);
      }
   }

   public boolean isValidSlotIndex(int p_207776_) {
      return p_207776_ == -1 || p_207776_ == -999 || p_207776_ < this.slots.size();
   }

   protected Slot addSlot(Slot p_38898_) {
      p_38898_.index = this.slots.size();
      this.slots.add(p_38898_);
      this.lastSlots.add(ItemStack.EMPTY);
      this.remoteSlots.add(ItemStack.EMPTY);
      return p_38898_;
   }

   protected DataSlot addDataSlot(DataSlot p_38896_) {
      this.dataSlots.add(p_38896_);
      this.remoteDataSlots.add(0);
      return p_38896_;
   }

   protected void addDataSlots(ContainerData p_38885_) {
      for(int i = 0; i < p_38885_.getCount(); ++i) {
         this.addDataSlot(DataSlot.forContainer(p_38885_, i));
      }

   }

   public void addSlotListener(ContainerListener p_38894_) {
      if (!this.containerListeners.contains(p_38894_)) {
         this.containerListeners.add(p_38894_);
         this.broadcastChanges();
      }
   }

   public void setSynchronizer(ContainerSynchronizer p_150417_) {
      this.synchronizer = p_150417_;
      this.sendAllDataToRemote();
   }

   public void sendAllDataToRemote() {
      int i = 0;

      for(int j = this.slots.size(); i < j; ++i) {
         this.remoteSlots.set(i, this.slots.get(i).getItem().copy());
      }

      this.remoteCarried = this.getCarried().copy();
      i = 0;

      for(int k = this.dataSlots.size(); i < k; ++i) {
         this.remoteDataSlots.set(i, this.dataSlots.get(i).get());
      }

      if (this.synchronizer != null) {
         this.synchronizer.sendInitialData(this, this.remoteSlots, this.remoteCarried, this.remoteDataSlots.toIntArray());
      }

   }

   public void removeSlotListener(ContainerListener p_38944_) {
      this.containerListeners.remove(p_38944_);
   }

   public NonNullList<ItemStack> getItems() {
      NonNullList<ItemStack> nonnulllist = NonNullList.create();

      for(Slot slot : this.slots) {
         nonnulllist.add(slot.getItem());
      }

      return nonnulllist;
   }

   public void broadcastChanges() {
      for(int i = 0; i < this.slots.size(); ++i) {
         ItemStack itemstack = this.slots.get(i).getItem();
         Supplier<ItemStack> supplier = Suppliers.memoize(itemstack::copy);
         this.triggerSlotListeners(i, itemstack, supplier);
         this.synchronizeSlotToRemote(i, itemstack, supplier);
      }

      this.synchronizeCarriedToRemote();

      for(int j = 0; j < this.dataSlots.size(); ++j) {
         DataSlot dataslot = this.dataSlots.get(j);
         int k = dataslot.get();
         if (dataslot.checkAndClearUpdateFlag()) {
            this.updateDataSlotListeners(j, k);
         }

         this.synchronizeDataSlotToRemote(j, k);
      }

   }

   public void broadcastFullState() {
      for(int i = 0; i < this.slots.size(); ++i) {
         ItemStack itemstack = this.slots.get(i).getItem();
         this.triggerSlotListeners(i, itemstack, itemstack::copy);
      }

      for(int j = 0; j < this.dataSlots.size(); ++j) {
         DataSlot dataslot = this.dataSlots.get(j);
         if (dataslot.checkAndClearUpdateFlag()) {
            this.updateDataSlotListeners(j, dataslot.get());
         }
      }

      this.sendAllDataToRemote();
   }

   private void updateDataSlotListeners(int p_182421_, int p_182422_) {
      for(ContainerListener containerlistener : this.containerListeners) {
         containerlistener.dataChanged(this, p_182421_, p_182422_);
      }

   }

   private void triggerSlotListeners(int p_150408_, ItemStack p_150409_, Supplier<ItemStack> p_150410_) {
      ItemStack itemstack = this.lastSlots.get(p_150408_);
      if (!ItemStack.matches(itemstack, p_150409_)) {
         boolean clientStackChanged = !p_150409_.equals(itemstack, true);
         ItemStack itemstack1 = p_150410_.get();
         this.lastSlots.set(p_150408_, itemstack1);

         if(clientStackChanged)
         for(ContainerListener containerlistener : this.containerListeners) {
            containerlistener.slotChanged(this, p_150408_, itemstack1);
         }
      }

   }

   private void synchronizeSlotToRemote(int p_150436_, ItemStack p_150437_, Supplier<ItemStack> p_150438_) {
      if (!this.suppressRemoteUpdates) {
         ItemStack itemstack = this.remoteSlots.get(p_150436_);
         if (!ItemStack.matches(itemstack, p_150437_)) {
            ItemStack itemstack1 = p_150438_.get();
            this.remoteSlots.set(p_150436_, itemstack1);
            if (this.synchronizer != null) {
               this.synchronizer.sendSlotChange(this, p_150436_, itemstack1);
            }
         }

      }
   }

   private void synchronizeDataSlotToRemote(int p_150441_, int p_150442_) {
      if (!this.suppressRemoteUpdates) {
         int i = this.remoteDataSlots.getInt(p_150441_);
         if (i != p_150442_) {
            this.remoteDataSlots.set(p_150441_, p_150442_);
            if (this.synchronizer != null) {
               this.synchronizer.sendDataChange(this, p_150441_, p_150442_);
            }
         }

      }
   }

   private void synchronizeCarriedToRemote() {
      if (!this.suppressRemoteUpdates) {
         if (!ItemStack.matches(this.getCarried(), this.remoteCarried)) {
            this.remoteCarried = this.getCarried().copy();
            if (this.synchronizer != null) {
               this.synchronizer.sendCarriedChange(this, this.remoteCarried);
            }
         }

      }
   }

   public void setRemoteSlot(int p_150405_, ItemStack p_150406_) {
      this.remoteSlots.set(p_150405_, p_150406_.copy());
   }

   public void setRemoteSlotNoCopy(int p_182415_, ItemStack p_182416_) {
      if (p_182415_ >= 0 && p_182415_ < this.remoteSlots.size()) {
         this.remoteSlots.set(p_182415_, p_182416_);
      } else {
         LOGGER.debug("Incorrect slot index: {} available slots: {}", p_182415_, this.remoteSlots.size());
      }
   }

   public void setRemoteCarried(ItemStack p_150423_) {
      this.remoteCarried = p_150423_.copy();
   }

   public boolean clickMenuButton(Player p_38875_, int p_38876_) {
      return false;
   }

   public Slot getSlot(int p_38854_) {
      return this.slots.get(p_38854_);
   }

   public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
      return this.slots.get(p_38942_).getItem();
   }

   public void clicked(int p_150400_, int p_150401_, ClickType p_150402_, Player p_150403_) {
      try {
         this.doClick(p_150400_, p_150401_, p_150402_, p_150403_);
      } catch (Exception exception) {
         CrashReport crashreport = CrashReport.forThrowable(exception, "Container click");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Click info");
         crashreportcategory.setDetail("Menu Type", () -> {
            return this.menuType != null ? Registry.MENU.getKey(this.menuType).toString() : "<no type>";
         });
         crashreportcategory.setDetail("Menu Class", () -> {
            return this.getClass().getCanonicalName();
         });
         crashreportcategory.setDetail("Slot Count", this.slots.size());
         crashreportcategory.setDetail("Slot", p_150400_);
         crashreportcategory.setDetail("Button", p_150401_);
         crashreportcategory.setDetail("Type", p_150402_);
         throw new ReportedException(crashreport);
      }
   }

   private void doClick(int p_150431_, int p_150432_, ClickType p_150433_, Player p_150434_) {
      Inventory inventory = p_150434_.getInventory();
      if (p_150433_ == ClickType.QUICK_CRAFT) {
         int i = this.quickcraftStatus;
         this.quickcraftStatus = getQuickcraftHeader(p_150432_);
         if ((i != 1 || this.quickcraftStatus != 2) && i != this.quickcraftStatus) {
            this.resetQuickCraft();
         } else if (this.getCarried().isEmpty()) {
            this.resetQuickCraft();
         } else if (this.quickcraftStatus == 0) {
            this.quickcraftType = getQuickcraftType(p_150432_);
            if (isValidQuickcraftType(this.quickcraftType, p_150434_)) {
               this.quickcraftStatus = 1;
               this.quickcraftSlots.clear();
            } else {
               this.resetQuickCraft();
            }
         } else if (this.quickcraftStatus == 1) {
            Slot slot = this.slots.get(p_150431_);
            ItemStack itemstack = this.getCarried();
            if (canItemQuickReplace(slot, itemstack, true) && slot.mayPlace(itemstack) && (this.quickcraftType == 2 || itemstack.getCount() > this.quickcraftSlots.size()) && this.canDragTo(slot)) {
               this.quickcraftSlots.add(slot);
            }
         } else if (this.quickcraftStatus == 2) {
            if (!this.quickcraftSlots.isEmpty()) {
               if (this.quickcraftSlots.size() == 1) {
                  int l = (this.quickcraftSlots.iterator().next()).index;
                  this.resetQuickCraft();
                  this.doClick(l, this.quickcraftType, ClickType.PICKUP, p_150434_);
                  return;
               }

               ItemStack itemstack3 = this.getCarried().copy();
               int j1 = this.getCarried().getCount();

               for(Slot slot1 : this.quickcraftSlots) {
                  ItemStack itemstack1 = this.getCarried();
                  if (slot1 != null && canItemQuickReplace(slot1, itemstack1, true) && slot1.mayPlace(itemstack1) && (this.quickcraftType == 2 || itemstack1.getCount() >= this.quickcraftSlots.size()) && this.canDragTo(slot1)) {
                     ItemStack itemstack2 = itemstack3.copy();
                     int j = slot1.hasItem() ? slot1.getItem().getCount() : 0;
                     getQuickCraftSlotCount(this.quickcraftSlots, this.quickcraftType, itemstack2, j);
                     int k = Math.min(itemstack2.getMaxStackSize(), slot1.getMaxStackSize(itemstack2));
                     if (itemstack2.getCount() > k) {
                        itemstack2.setCount(k);
                     }

                     j1 -= itemstack2.getCount() - j;
                     slot1.set(itemstack2);
                  }
               }

               itemstack3.setCount(j1);
               this.setCarried(itemstack3);
            }

            this.resetQuickCraft();
         } else {
            this.resetQuickCraft();
         }
      } else if (this.quickcraftStatus != 0) {
         this.resetQuickCraft();
      } else if ((p_150433_ == ClickType.PICKUP || p_150433_ == ClickType.QUICK_MOVE) && (p_150432_ == 0 || p_150432_ == 1)) {
         ClickAction clickaction = p_150432_ == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;
         if (p_150431_ == -999) {
            if (!this.getCarried().isEmpty()) {
               if (clickaction == ClickAction.PRIMARY) {
                  p_150434_.drop(this.getCarried(), true);
                  this.setCarried(ItemStack.EMPTY);
               } else {
                  p_150434_.drop(this.getCarried().split(1), true);
               }
            }
         } else if (p_150433_ == ClickType.QUICK_MOVE) {
            if (p_150431_ < 0) {
               return;
            }

            Slot slot6 = this.slots.get(p_150431_);
            if (!slot6.mayPickup(p_150434_)) {
               return;
            }

            for(ItemStack itemstack9 = this.quickMoveStack(p_150434_, p_150431_); !itemstack9.isEmpty() && ItemStack.isSame(slot6.getItem(), itemstack9); itemstack9 = this.quickMoveStack(p_150434_, p_150431_)) {
            }
         } else {
            if (p_150431_ < 0) {
               return;
            }

            Slot slot7 = this.slots.get(p_150431_);
            ItemStack itemstack10 = slot7.getItem();
            ItemStack itemstack11 = this.getCarried();
            p_150434_.updateTutorialInventoryAction(itemstack11, slot7.getItem(), clickaction);
            if (!itemstack11.overrideStackedOnOther(slot7, clickaction, p_150434_) && !itemstack10.overrideOtherStackedOnMe(itemstack11, slot7, clickaction, p_150434_, this.createCarriedSlotAccess())) {
               if (itemstack10.isEmpty()) {
                  if (!itemstack11.isEmpty()) {
                     int l2 = clickaction == ClickAction.PRIMARY ? itemstack11.getCount() : 1;
                     this.setCarried(slot7.safeInsert(itemstack11, l2));
                  }
               } else if (slot7.mayPickup(p_150434_)) {
                  if (itemstack11.isEmpty()) {
                     int i3 = clickaction == ClickAction.PRIMARY ? itemstack10.getCount() : (itemstack10.getCount() + 1) / 2;
                     Optional<ItemStack> optional1 = slot7.tryRemove(i3, Integer.MAX_VALUE, p_150434_);
                     optional1.ifPresent((p_150421_) -> {
                        this.setCarried(p_150421_);
                        slot7.onTake(p_150434_, p_150421_);
                     });
                  } else if (slot7.mayPlace(itemstack11)) {
                     if (ItemStack.isSameItemSameTags(itemstack10, itemstack11)) {
                        int j3 = clickaction == ClickAction.PRIMARY ? itemstack11.getCount() : 1;
                        this.setCarried(slot7.safeInsert(itemstack11, j3));
                     } else if (itemstack11.getCount() <= slot7.getMaxStackSize(itemstack11)) {
                        slot7.set(itemstack11);
                        this.setCarried(itemstack10);
                     }
                  } else if (ItemStack.isSameItemSameTags(itemstack10, itemstack11)) {
                     Optional<ItemStack> optional = slot7.tryRemove(itemstack10.getCount(), itemstack11.getMaxStackSize() - itemstack11.getCount(), p_150434_);
                     optional.ifPresent((p_150428_) -> {
                        itemstack11.grow(p_150428_.getCount());
                        slot7.onTake(p_150434_, p_150428_);
                     });
                  }
               }
            }

            slot7.setChanged();
         }
      } else if (p_150433_ == ClickType.SWAP) {
         Slot slot2 = this.slots.get(p_150431_);
         ItemStack itemstack4 = inventory.getItem(p_150432_);
         ItemStack itemstack7 = slot2.getItem();
         if (!itemstack4.isEmpty() || !itemstack7.isEmpty()) {
            if (itemstack4.isEmpty()) {
               if (slot2.mayPickup(p_150434_)) {
                  inventory.setItem(p_150432_, itemstack7);
                  slot2.onSwapCraft(itemstack7.getCount());
                  slot2.set(ItemStack.EMPTY);
                  slot2.onTake(p_150434_, itemstack7);
               }
            } else if (itemstack7.isEmpty()) {
               if (slot2.mayPlace(itemstack4)) {
                  int l1 = slot2.getMaxStackSize(itemstack4);
                  if (itemstack4.getCount() > l1) {
                     slot2.set(itemstack4.split(l1));
                  } else {
                     inventory.setItem(p_150432_, ItemStack.EMPTY);
                     slot2.set(itemstack4);
                  }
               }
            } else if (slot2.mayPickup(p_150434_) && slot2.mayPlace(itemstack4)) {
               int i2 = slot2.getMaxStackSize(itemstack4);
               if (itemstack4.getCount() > i2) {
                  slot2.set(itemstack4.split(i2));
                  slot2.onTake(p_150434_, itemstack7);
                  if (!inventory.add(itemstack7)) {
                     p_150434_.drop(itemstack7, true);
                  }
               } else {
                  inventory.setItem(p_150432_, itemstack7);
                  slot2.set(itemstack4);
                  slot2.onTake(p_150434_, itemstack7);
               }
            }
         }
      } else if (p_150433_ == ClickType.CLONE && p_150434_.getAbilities().instabuild && this.getCarried().isEmpty() && p_150431_ >= 0) {
         Slot slot5 = this.slots.get(p_150431_);
         if (slot5.hasItem()) {
            ItemStack itemstack6 = slot5.getItem().copy();
            itemstack6.setCount(itemstack6.getMaxStackSize());
            this.setCarried(itemstack6);
         }
      } else if (p_150433_ == ClickType.THROW && this.getCarried().isEmpty() && p_150431_ >= 0) {
         Slot slot4 = this.slots.get(p_150431_);
         int i1 = p_150432_ == 0 ? 1 : slot4.getItem().getCount();
         ItemStack itemstack8 = slot4.safeTake(i1, Integer.MAX_VALUE, p_150434_);
         p_150434_.drop(itemstack8, true);
      } else if (p_150433_ == ClickType.PICKUP_ALL && p_150431_ >= 0) {
         Slot slot3 = this.slots.get(p_150431_);
         ItemStack itemstack5 = this.getCarried();
         if (!itemstack5.isEmpty() && (!slot3.hasItem() || !slot3.mayPickup(p_150434_))) {
            int k1 = p_150432_ == 0 ? 0 : this.slots.size() - 1;
            int j2 = p_150432_ == 0 ? 1 : -1;

            for(int k2 = 0; k2 < 2; ++k2) {
               for(int k3 = k1; k3 >= 0 && k3 < this.slots.size() && itemstack5.getCount() < itemstack5.getMaxStackSize(); k3 += j2) {
                  Slot slot8 = this.slots.get(k3);
                  if (slot8.hasItem() && canItemQuickReplace(slot8, itemstack5, true) && slot8.mayPickup(p_150434_) && this.canTakeItemForPickAll(itemstack5, slot8)) {
                     ItemStack itemstack12 = slot8.getItem();
                     if (k2 != 0 || itemstack12.getCount() != itemstack12.getMaxStackSize()) {
                        ItemStack itemstack13 = slot8.safeTake(itemstack12.getCount(), itemstack5.getMaxStackSize() - itemstack5.getCount(), p_150434_);
                        itemstack5.grow(itemstack13.getCount());
                     }
                  }
               }
            }
         }
      }

   }

   private SlotAccess createCarriedSlotAccess() {
      return new SlotAccess() {
         public ItemStack get() {
            return AbstractContainerMenu.this.getCarried();
         }

         public boolean set(ItemStack p_150452_) {
            AbstractContainerMenu.this.setCarried(p_150452_);
            return true;
         }
      };
   }

   public boolean canTakeItemForPickAll(ItemStack p_38908_, Slot p_38909_) {
      return true;
   }

   public void removed(Player p_38940_) {
      if (p_38940_ instanceof ServerPlayer) {
         ItemStack itemstack = this.getCarried();
         if (!itemstack.isEmpty()) {
            if (p_38940_.isAlive() && !((ServerPlayer)p_38940_).hasDisconnected()) {
               p_38940_.getInventory().placeItemBackInInventory(itemstack);
            } else {
               p_38940_.drop(itemstack, false);
            }

            this.setCarried(ItemStack.EMPTY);
         }
      }

   }

   protected void clearContainer(Player p_150412_, Container p_150413_) {
      if (!p_150412_.isAlive() || p_150412_ instanceof ServerPlayer && ((ServerPlayer)p_150412_).hasDisconnected()) {
         for(int j = 0; j < p_150413_.getContainerSize(); ++j) {
            p_150412_.drop(p_150413_.removeItemNoUpdate(j), false);
         }

      } else {
         for(int i = 0; i < p_150413_.getContainerSize(); ++i) {
            Inventory inventory = p_150412_.getInventory();
            if (inventory.player instanceof ServerPlayer) {
               inventory.placeItemBackInInventory(p_150413_.removeItemNoUpdate(i));
            }
         }

      }
   }

   public void slotsChanged(Container p_38868_) {
      this.broadcastChanges();
   }

   public void setItem(int p_182407_, int p_182408_, ItemStack p_182409_) {
      this.getSlot(p_182407_).set(p_182409_);
      this.stateId = p_182408_;
   }

   public void initializeContents(int p_182411_, List<ItemStack> p_182412_, ItemStack p_182413_) {
      for(int i = 0; i < p_182412_.size(); ++i) {
         this.getSlot(i).set(p_182412_.get(i));
      }

      this.carried = p_182413_;
      this.stateId = p_182411_;
   }

   public void setData(int p_38855_, int p_38856_) {
      this.dataSlots.get(p_38855_).set(p_38856_);
   }

   public abstract boolean stillValid(Player p_38874_);

   protected boolean moveItemStackTo(ItemStack p_38904_, int p_38905_, int p_38906_, boolean p_38907_) {
      boolean flag = false;
      int i = p_38905_;
      if (p_38907_) {
         i = p_38906_ - 1;
      }

      if (p_38904_.isStackable()) {
         while(!p_38904_.isEmpty()) {
            if (p_38907_) {
               if (i < p_38905_) {
                  break;
               }
            } else if (i >= p_38906_) {
               break;
            }

            Slot slot = this.slots.get(i);
            ItemStack itemstack = slot.getItem();
            if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(p_38904_, itemstack)) {
               int j = itemstack.getCount() + p_38904_.getCount();
               int maxSize = Math.min(slot.getMaxStackSize(), p_38904_.getMaxStackSize());
               if (j <= maxSize) {
                  p_38904_.setCount(0);
                  itemstack.setCount(j);
                  slot.setChanged();
                  flag = true;
               } else if (itemstack.getCount() < maxSize) {
                  p_38904_.shrink(maxSize - itemstack.getCount());
                  itemstack.setCount(maxSize);
                  slot.setChanged();
                  flag = true;
               }
            }

            if (p_38907_) {
               --i;
            } else {
               ++i;
            }
         }
      }

      if (!p_38904_.isEmpty()) {
         if (p_38907_) {
            i = p_38906_ - 1;
         } else {
            i = p_38905_;
         }

         while(true) {
            if (p_38907_) {
               if (i < p_38905_) {
                  break;
               }
            } else if (i >= p_38906_) {
               break;
            }

            Slot slot1 = this.slots.get(i);
            ItemStack itemstack1 = slot1.getItem();
            if (itemstack1.isEmpty() && slot1.mayPlace(p_38904_)) {
               if (p_38904_.getCount() > slot1.getMaxStackSize()) {
                  slot1.set(p_38904_.split(slot1.getMaxStackSize()));
               } else {
                  slot1.set(p_38904_.split(p_38904_.getCount()));
               }

               slot1.setChanged();
               flag = true;
               break;
            }

            if (p_38907_) {
               --i;
            } else {
               ++i;
            }
         }
      }

      return flag;
   }

   public static int getQuickcraftType(int p_38929_) {
      return p_38929_ >> 2 & 3;
   }

   public static int getQuickcraftHeader(int p_38948_) {
      return p_38948_ & 3;
   }

   public static int getQuickcraftMask(int p_38931_, int p_38932_) {
      return p_38931_ & 3 | (p_38932_ & 3) << 2;
   }

   public static boolean isValidQuickcraftType(int p_38863_, Player p_38864_) {
      if (p_38863_ == 0) {
         return true;
      } else if (p_38863_ == 1) {
         return true;
      } else {
         return p_38863_ == 2 && p_38864_.getAbilities().instabuild;
      }
   }

   protected void resetQuickCraft() {
      this.quickcraftStatus = 0;
      this.quickcraftSlots.clear();
   }

   public static boolean canItemQuickReplace(@Nullable Slot p_38900_, ItemStack p_38901_, boolean p_38902_) {
      boolean flag = p_38900_ == null || !p_38900_.hasItem();
      if (!flag && ItemStack.isSameItemSameTags(p_38901_, p_38900_.getItem())) {
         return p_38900_.getItem().getCount() + (p_38902_ ? 0 : p_38901_.getCount()) <= p_38901_.getMaxStackSize();
      } else {
         return flag;
      }
   }

   public static void getQuickCraftSlotCount(Set<Slot> p_38923_, int p_38924_, ItemStack p_38925_, int p_38926_) {
      switch(p_38924_) {
      case 0:
         p_38925_.setCount(Mth.floor((float)p_38925_.getCount() / (float)p_38923_.size()));
         break;
      case 1:
         p_38925_.setCount(1);
         break;
      case 2:
         p_38925_.setCount(p_38925_.getMaxStackSize());
      }

      p_38925_.grow(p_38926_);
   }

   public boolean canDragTo(Slot p_38945_) {
      return true;
   }

   public static int getRedstoneSignalFromBlockEntity(@Nullable BlockEntity p_38919_) {
      return p_38919_ instanceof Container ? getRedstoneSignalFromContainer((Container)p_38919_) : 0;
   }

   public static int getRedstoneSignalFromContainer(@Nullable Container p_38939_) {
      if (p_38939_ == null) {
         return 0;
      } else {
         int i = 0;
         float f = 0.0F;

         for(int j = 0; j < p_38939_.getContainerSize(); ++j) {
            ItemStack itemstack = p_38939_.getItem(j);
            if (!itemstack.isEmpty()) {
               f += (float)itemstack.getCount() / (float)Math.min(p_38939_.getMaxStackSize(), itemstack.getMaxStackSize());
               ++i;
            }
         }

         f /= (float)p_38939_.getContainerSize();
         return Mth.floor(f * 14.0F) + (i > 0 ? 1 : 0);
      }
   }

   public void setCarried(ItemStack p_150439_) {
      this.carried = p_150439_;
   }

   public ItemStack getCarried() {
      return this.carried;
   }

   public void suppressRemoteUpdates() {
      this.suppressRemoteUpdates = true;
   }

   public void resumeRemoteUpdates() {
      this.suppressRemoteUpdates = false;
   }

   public void transferState(AbstractContainerMenu p_150415_) {
      Table<Container, Integer, Integer> table = HashBasedTable.create();

      for(int i = 0; i < p_150415_.slots.size(); ++i) {
         Slot slot = p_150415_.slots.get(i);
         table.put(slot.container, slot.getContainerSlot(), i);
      }

      for(int j = 0; j < this.slots.size(); ++j) {
         Slot slot1 = this.slots.get(j);
         Integer integer = table.get(slot1.container, slot1.getContainerSlot());
         if (integer != null) {
            this.lastSlots.set(j, p_150415_.lastSlots.get(integer));
            this.remoteSlots.set(j, p_150415_.remoteSlots.get(integer));
         }
      }

   }

   public OptionalInt findSlot(Container p_182418_, int p_182419_) {
      for(int i = 0; i < this.slots.size(); ++i) {
         Slot slot = this.slots.get(i);
         if (slot.container == p_182418_ && p_182419_ == slot.getContainerSlot()) {
            return OptionalInt.of(i);
         }
      }

      return OptionalInt.empty();
   }

   public int getStateId() {
      return this.stateId;
   }

   public int incrementStateId() {
      this.stateId = this.stateId + 1 & 32767;
      return this.stateId;
   }
}
