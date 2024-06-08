package net.minecraft.world;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CompoundContainer implements Container {
   private final Container container1;
   private final Container container2;

   public CompoundContainer(Container p_18913_, Container p_18914_) {
      this.container1 = p_18913_;
      this.container2 = p_18914_;
   }

   public int getContainerSize() {
      return this.container1.getContainerSize() + this.container2.getContainerSize();
   }

   public boolean isEmpty() {
      return this.container1.isEmpty() && this.container2.isEmpty();
   }

   public boolean contains(Container p_18928_) {
      return this.container1 == p_18928_ || this.container2 == p_18928_;
   }

   public ItemStack getItem(int p_18920_) {
      return p_18920_ >= this.container1.getContainerSize() ? this.container2.getItem(p_18920_ - this.container1.getContainerSize()) : this.container1.getItem(p_18920_);
   }

   public ItemStack removeItem(int p_18922_, int p_18923_) {
      return p_18922_ >= this.container1.getContainerSize() ? this.container2.removeItem(p_18922_ - this.container1.getContainerSize(), p_18923_) : this.container1.removeItem(p_18922_, p_18923_);
   }

   public ItemStack removeItemNoUpdate(int p_18932_) {
      return p_18932_ >= this.container1.getContainerSize() ? this.container2.removeItemNoUpdate(p_18932_ - this.container1.getContainerSize()) : this.container1.removeItemNoUpdate(p_18932_);
   }

   public void setItem(int p_18925_, ItemStack p_18926_) {
      if (p_18925_ >= this.container1.getContainerSize()) {
         this.container2.setItem(p_18925_ - this.container1.getContainerSize(), p_18926_);
      } else {
         this.container1.setItem(p_18925_, p_18926_);
      }

   }

   public int getMaxStackSize() {
      return this.container1.getMaxStackSize();
   }

   public void setChanged() {
      this.container1.setChanged();
      this.container2.setChanged();
   }

   public boolean stillValid(Player p_18930_) {
      return this.container1.stillValid(p_18930_) && this.container2.stillValid(p_18930_);
   }

   public void startOpen(Player p_18940_) {
      this.container1.startOpen(p_18940_);
      this.container2.startOpen(p_18940_);
   }

   public void stopOpen(Player p_18937_) {
      this.container1.stopOpen(p_18937_);
      this.container2.stopOpen(p_18937_);
   }

   public boolean canPlaceItem(int p_18934_, ItemStack p_18935_) {
      return p_18934_ >= this.container1.getContainerSize() ? this.container2.canPlaceItem(p_18934_ - this.container1.getContainerSize(), p_18935_) : this.container1.canPlaceItem(p_18934_, p_18935_);
   }

   public void clearContent() {
      this.container1.clearContent();
      this.container2.clearContent();
   }
}