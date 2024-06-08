package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

public class PlayerEnderChestContainer extends SimpleContainer {
   @Nullable
   private EnderChestBlockEntity activeChest;

   public PlayerEnderChestContainer() {
      super(27);
   }

   public void setActiveChest(EnderChestBlockEntity p_40106_) {
      this.activeChest = p_40106_;
   }

   public boolean isActiveChest(EnderChestBlockEntity p_150634_) {
      return this.activeChest == p_150634_;
   }

   public void fromTag(ListTag p_40108_) {
      for(int i = 0; i < this.getContainerSize(); ++i) {
         this.setItem(i, ItemStack.EMPTY);
      }

      for(int k = 0; k < p_40108_.size(); ++k) {
         CompoundTag compoundtag = p_40108_.getCompound(k);
         int j = compoundtag.getByte("Slot") & 255;
         if (j >= 0 && j < this.getContainerSize()) {
            this.setItem(j, ItemStack.of(compoundtag));
         }
      }

   }

   public ListTag createTag() {
      ListTag listtag = new ListTag();

      for(int i = 0; i < this.getContainerSize(); ++i) {
         ItemStack itemstack = this.getItem(i);
         if (!itemstack.isEmpty()) {
            CompoundTag compoundtag = new CompoundTag();
            compoundtag.putByte("Slot", (byte)i);
            itemstack.save(compoundtag);
            listtag.add(compoundtag);
         }
      }

      return listtag;
   }

   public boolean stillValid(Player p_40104_) {
      return this.activeChest != null && !this.activeChest.stillValid(p_40104_) ? false : super.stillValid(p_40104_);
   }

   public void startOpen(Player p_40112_) {
      if (this.activeChest != null) {
         this.activeChest.startOpen(p_40112_);
      }

      super.startOpen(p_40112_);
   }

   public void stopOpen(Player p_40110_) {
      if (this.activeChest != null) {
         this.activeChest.stopOpen(p_40110_);
      }

      super.stopOpen(p_40110_);
      this.activeChest = null;
   }
}