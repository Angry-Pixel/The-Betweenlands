package net.minecraft.world.inventory;

import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Slot {
   private final int slot;
   public final Container container;
   public int index;
   public final int x;
   public final int y;

   public Slot(Container p_40223_, int p_40224_, int p_40225_, int p_40226_) {
      this.container = p_40223_;
      this.slot = p_40224_;
      this.x = p_40225_;
      this.y = p_40226_;
   }

   public void onQuickCraft(ItemStack p_40235_, ItemStack p_40236_) {
      int i = p_40236_.getCount() - p_40235_.getCount();
      if (i > 0) {
         this.onQuickCraft(p_40236_, i);
      }

   }

   protected void onQuickCraft(ItemStack p_40232_, int p_40233_) {
   }

   protected void onSwapCraft(int p_40237_) {
   }

   protected void checkTakeAchievements(ItemStack p_40239_) {
   }

   public void onTake(Player p_150645_, ItemStack p_150646_) {
      this.setChanged();
   }

   public boolean mayPlace(ItemStack p_40231_) {
      return true;
   }

   public ItemStack getItem() {
      return this.container.getItem(this.slot);
   }

   public boolean hasItem() {
      return !this.getItem().isEmpty();
   }

   public void set(ItemStack p_40240_) {
      this.container.setItem(this.slot, p_40240_);
      this.setChanged();
   }

   public void setChanged() {
      this.container.setChanged();
   }

   public int getMaxStackSize() {
      return this.container.getMaxStackSize();
   }

   public int getMaxStackSize(ItemStack p_40238_) {
      return Math.min(this.getMaxStackSize(), p_40238_.getMaxStackSize());
   }

   @Nullable
   public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
      return backgroundPair;
   }

   public ItemStack remove(int p_40227_) {
      return this.container.removeItem(this.slot, p_40227_);
   }

   public boolean mayPickup(Player p_40228_) {
      return true;
   }

   public boolean isActive() {
      return true;
   }

   /**
    * Retrieves the index in the inventory for this slot, this value should typically not
    * be used, but can be useful for some occasions.
    *
    * @return Index in associated inventory for this slot.
    */
   public int getSlotIndex() {
      return slot;
   }

   /**
    * Checks if the other slot is in the same inventory, by comparing the inventory reference.
    * @param other
    * @return true if the other slot is in the same inventory
    */
   public boolean isSameInventory(Slot other) {
      return this.container == other.container;
   }

   private Pair<ResourceLocation, ResourceLocation> backgroundPair;
   /**
    * Sets the background atlas and sprite location.
    *
    * @param atlas The atlas name
    * @param sprite The sprite located on that atlas.
    * @return this, to allow chaining.
    */
   public Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
       this.backgroundPair = Pair.of(atlas, sprite);
       return this;
   }

   public Optional<ItemStack> tryRemove(int p_150642_, int p_150643_, Player p_150644_) {
      if (!this.mayPickup(p_150644_)) {
         return Optional.empty();
      } else if (!this.allowModification(p_150644_) && p_150643_ < this.getItem().getCount()) {
         return Optional.empty();
      } else {
         p_150642_ = Math.min(p_150642_, p_150643_);
         ItemStack itemstack = this.remove(p_150642_);
         if (itemstack.isEmpty()) {
            return Optional.empty();
         } else {
            if (this.getItem().isEmpty()) {
               this.set(ItemStack.EMPTY);
            }

            return Optional.of(itemstack);
         }
      }
   }

   public ItemStack safeTake(int p_150648_, int p_150649_, Player p_150650_) {
      Optional<ItemStack> optional = this.tryRemove(p_150648_, p_150649_, p_150650_);
      optional.ifPresent((p_150655_) -> {
         this.onTake(p_150650_, p_150655_);
      });
      return optional.orElse(ItemStack.EMPTY);
   }

   public ItemStack safeInsert(ItemStack p_150660_) {
      return this.safeInsert(p_150660_, p_150660_.getCount());
   }

   public ItemStack safeInsert(ItemStack p_150657_, int p_150658_) {
      if (!p_150657_.isEmpty() && this.mayPlace(p_150657_)) {
         ItemStack itemstack = this.getItem();
         int i = Math.min(Math.min(p_150658_, p_150657_.getCount()), this.getMaxStackSize(p_150657_) - itemstack.getCount());
         if (itemstack.isEmpty()) {
            this.set(p_150657_.split(i));
         } else if (ItemStack.isSameItemSameTags(itemstack, p_150657_)) {
            p_150657_.shrink(i);
            itemstack.grow(i);
            this.set(itemstack);
         }

         return p_150657_;
      } else {
         return p_150657_;
      }
   }

   public boolean allowModification(Player p_150652_) {
      return this.mayPickup(p_150652_) && this.mayPlace(this.getItem());
   }

   public int getContainerSlot() {
      return this.slot;
   }
}
