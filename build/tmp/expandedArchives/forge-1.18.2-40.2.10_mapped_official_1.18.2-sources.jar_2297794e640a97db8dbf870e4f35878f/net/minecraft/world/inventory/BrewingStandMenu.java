package net.minecraft.world.inventory;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;

public class BrewingStandMenu extends AbstractContainerMenu {
   private static final int BOTTLE_SLOT_START = 0;
   private static final int BOTTLE_SLOT_END = 2;
   private static final int INGREDIENT_SLOT = 3;
   private static final int FUEL_SLOT = 4;
   private static final int SLOT_COUNT = 5;
   private static final int DATA_COUNT = 2;
   private static final int INV_SLOT_START = 5;
   private static final int INV_SLOT_END = 32;
   private static final int USE_ROW_SLOT_START = 32;
   private static final int USE_ROW_SLOT_END = 41;
   private final Container brewingStand;
   private final ContainerData brewingStandData;
   private final Slot ingredientSlot;

   public BrewingStandMenu(int p_39090_, Inventory p_39091_) {
      this(p_39090_, p_39091_, new SimpleContainer(5), new SimpleContainerData(2));
   }

   public BrewingStandMenu(int p_39093_, Inventory p_39094_, Container p_39095_, ContainerData p_39096_) {
      super(MenuType.BREWING_STAND, p_39093_);
      checkContainerSize(p_39095_, 5);
      checkContainerDataCount(p_39096_, 2);
      this.brewingStand = p_39095_;
      this.brewingStandData = p_39096_;
      this.addSlot(new BrewingStandMenu.PotionSlot(p_39095_, 0, 56, 51));
      this.addSlot(new BrewingStandMenu.PotionSlot(p_39095_, 1, 79, 58));
      this.addSlot(new BrewingStandMenu.PotionSlot(p_39095_, 2, 102, 51));
      this.ingredientSlot = this.addSlot(new BrewingStandMenu.IngredientsSlot(p_39095_, 3, 79, 17));
      this.addSlot(new BrewingStandMenu.FuelSlot(p_39095_, 4, 17, 17));
      this.addDataSlots(p_39096_);

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_39094_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_39094_, k, 8 + k * 18, 142));
      }

   }

   public boolean stillValid(Player p_39098_) {
      return this.brewingStand.stillValid(p_39098_);
   }

   public ItemStack quickMoveStack(Player p_39100_, int p_39101_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_39101_);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if ((p_39101_ < 0 || p_39101_ > 2) && p_39101_ != 3 && p_39101_ != 4) {
            if (BrewingStandMenu.FuelSlot.mayPlaceItem(itemstack)) {
               if (this.moveItemStackTo(itemstack1, 4, 5, false) || this.ingredientSlot.mayPlace(itemstack1) && !this.moveItemStackTo(itemstack1, 3, 4, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (this.ingredientSlot.mayPlace(itemstack1)) {
               if (!this.moveItemStackTo(itemstack1, 3, 4, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (BrewingStandMenu.PotionSlot.mayPlaceItem(itemstack)) {
               if (!this.moveItemStackTo(itemstack1, 0, 3, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_39101_ >= 5 && p_39101_ < 32) {
               if (!this.moveItemStackTo(itemstack1, 32, 41, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_39101_ >= 32 && p_39101_ < 41) {
               if (!this.moveItemStackTo(itemstack1, 5, 32, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo(itemstack1, 5, 41, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (!this.moveItemStackTo(itemstack1, 5, 41, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemstack1, itemstack);
         }

         if (itemstack1.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(p_39100_, itemstack1);
      }

      return itemstack;
   }

   public int getFuel() {
      return this.brewingStandData.get(1);
   }

   public int getBrewingTicks() {
      return this.brewingStandData.get(0);
   }

   static class FuelSlot extends Slot {
      public FuelSlot(Container p_39105_, int p_39106_, int p_39107_, int p_39108_) {
         super(p_39105_, p_39106_, p_39107_, p_39108_);
      }

      public boolean mayPlace(ItemStack p_39111_) {
         return mayPlaceItem(p_39111_);
      }

      public static boolean mayPlaceItem(ItemStack p_39113_) {
         return p_39113_.is(Items.BLAZE_POWDER);
      }

      public int getMaxStackSize() {
         return 64;
      }
   }

   static class IngredientsSlot extends Slot {
      public IngredientsSlot(Container p_39115_, int p_39116_, int p_39117_, int p_39118_) {
         super(p_39115_, p_39116_, p_39117_, p_39118_);
      }

      public boolean mayPlace(ItemStack p_39121_) {
         return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidIngredient(p_39121_);
      }

      public int getMaxStackSize() {
         return 64;
      }
   }

   static class PotionSlot extends Slot {
      public PotionSlot(Container p_39123_, int p_39124_, int p_39125_, int p_39126_) {
         super(p_39123_, p_39124_, p_39125_, p_39126_);
      }

      public boolean mayPlace(ItemStack p_39132_) {
         return mayPlaceItem(p_39132_);
      }

      public int getMaxStackSize() {
         return 1;
      }

      public void onTake(Player p_150499_, ItemStack p_150500_) {
         Potion potion = PotionUtils.getPotion(p_150500_);
         if (p_150499_ instanceof ServerPlayer) {
            net.minecraftforge.event.ForgeEventFactory.onPlayerBrewedPotion(p_150499_, p_150500_);
            CriteriaTriggers.BREWED_POTION.trigger((ServerPlayer)p_150499_, potion);
         }

         super.onTake(p_150499_, p_150500_);
      }

      public static boolean mayPlaceItem(ItemStack p_39134_) {
         return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidInput(p_39134_);
      }
   }
}
