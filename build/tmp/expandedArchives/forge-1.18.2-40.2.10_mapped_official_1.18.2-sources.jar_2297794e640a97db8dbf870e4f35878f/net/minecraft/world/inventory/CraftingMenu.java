package net.minecraft.world.inventory;

import java.util.Optional;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class CraftingMenu extends RecipeBookMenu<CraftingContainer> {
   public static final int RESULT_SLOT = 0;
   private static final int CRAFT_SLOT_START = 1;
   private static final int CRAFT_SLOT_END = 10;
   private static final int INV_SLOT_START = 10;
   private static final int INV_SLOT_END = 37;
   private static final int USE_ROW_SLOT_START = 37;
   private static final int USE_ROW_SLOT_END = 46;
   private final CraftingContainer craftSlots = new CraftingContainer(this, 3, 3);
   private final ResultContainer resultSlots = new ResultContainer();
   private final ContainerLevelAccess access;
   private final Player player;

   public CraftingMenu(int p_39353_, Inventory p_39354_) {
      this(p_39353_, p_39354_, ContainerLevelAccess.NULL);
   }

   public CraftingMenu(int p_39356_, Inventory p_39357_, ContainerLevelAccess p_39358_) {
      super(MenuType.CRAFTING, p_39356_);
      this.access = p_39358_;
      this.player = p_39357_.player;
      this.addSlot(new ResultSlot(p_39357_.player, this.craftSlots, this.resultSlots, 0, 124, 35));

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 3; ++j) {
            this.addSlot(new Slot(this.craftSlots, j + i * 3, 30 + j * 18, 17 + i * 18));
         }
      }

      for(int k = 0; k < 3; ++k) {
         for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(p_39357_, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
         }
      }

      for(int l = 0; l < 9; ++l) {
         this.addSlot(new Slot(p_39357_, l, 8 + l * 18, 142));
      }

   }

   protected static void slotChangedCraftingGrid(AbstractContainerMenu p_150547_, Level p_150548_, Player p_150549_, CraftingContainer p_150550_, ResultContainer p_150551_) {
      if (!p_150548_.isClientSide) {
         ServerPlayer serverplayer = (ServerPlayer)p_150549_;
         ItemStack itemstack = ItemStack.EMPTY;
         Optional<CraftingRecipe> optional = p_150548_.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, p_150550_, p_150548_);
         if (optional.isPresent()) {
            CraftingRecipe craftingrecipe = optional.get();
            if (p_150551_.setRecipeUsed(p_150548_, serverplayer, craftingrecipe)) {
               itemstack = craftingrecipe.assemble(p_150550_);
            }
         }

         p_150551_.setItem(0, itemstack);
         p_150547_.setRemoteSlot(0, itemstack);
         serverplayer.connection.send(new ClientboundContainerSetSlotPacket(p_150547_.containerId, p_150547_.incrementStateId(), 0, itemstack));
      }
   }

   public void slotsChanged(Container p_39366_) {
      this.access.execute((p_39386_, p_39387_) -> {
         slotChangedCraftingGrid(this, p_39386_, this.player, this.craftSlots, this.resultSlots);
      });
   }

   public void fillCraftSlotsStackedContents(StackedContents p_39374_) {
      this.craftSlots.fillStackedContents(p_39374_);
   }

   public void clearCraftingContent() {
      this.craftSlots.clearContent();
      this.resultSlots.clearContent();
   }

   public boolean recipeMatches(Recipe<? super CraftingContainer> p_39384_) {
      return p_39384_.matches(this.craftSlots, this.player.level);
   }

   public void removed(Player p_39389_) {
      super.removed(p_39389_);
      this.access.execute((p_39371_, p_39372_) -> {
         this.clearContainer(p_39389_, this.craftSlots);
      });
   }

   public boolean stillValid(Player p_39368_) {
      return stillValid(this.access, p_39368_, Blocks.CRAFTING_TABLE);
   }

   public ItemStack quickMoveStack(Player p_39391_, int p_39392_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_39392_);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (p_39392_ == 0) {
            this.access.execute((p_39378_, p_39379_) -> {
               itemstack1.getItem().onCraftedBy(itemstack1, p_39378_, p_39391_);
            });
            if (!this.moveItemStackTo(itemstack1, 10, 46, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemstack1, itemstack);
         } else if (p_39392_ >= 10 && p_39392_ < 46) {
            if (!this.moveItemStackTo(itemstack1, 1, 10, false)) {
               if (p_39392_ < 37) {
                  if (!this.moveItemStackTo(itemstack1, 37, 46, false)) {
                     return ItemStack.EMPTY;
                  }
               } else if (!this.moveItemStackTo(itemstack1, 10, 37, false)) {
                  return ItemStack.EMPTY;
               }
            }
         } else if (!this.moveItemStackTo(itemstack1, 10, 46, false)) {
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

         slot.onTake(p_39391_, itemstack1);
         if (p_39392_ == 0) {
            p_39391_.drop(itemstack1, false);
         }
      }

      return itemstack;
   }

   public boolean canTakeItemForPickAll(ItemStack p_39381_, Slot p_39382_) {
      return p_39382_.container != this.resultSlots && super.canTakeItemForPickAll(p_39381_, p_39382_);
   }

   public int getResultSlotIndex() {
      return 0;
   }

   public int getGridWidth() {
      return this.craftSlots.getWidth();
   }

   public int getGridHeight() {
      return this.craftSlots.getHeight();
   }

   public int getSize() {
      return 10;
   }

   public RecipeBookType getRecipeBookType() {
      return RecipeBookType.CRAFTING;
   }

   public boolean shouldMoveToInventory(int p_150553_) {
      return p_150553_ != this.getResultSlotIndex();
   }
}