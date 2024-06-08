package net.minecraft.world;

import java.util.Set;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface Container extends Clearable {
   int LARGE_MAX_STACK_SIZE = 64;

   int getContainerSize();

   boolean isEmpty();

   ItemStack getItem(int p_18941_);

   ItemStack removeItem(int p_18942_, int p_18943_);

   ItemStack removeItemNoUpdate(int p_18951_);

   void setItem(int p_18944_, ItemStack p_18945_);

   default int getMaxStackSize() {
      return 64;
   }

   void setChanged();

   boolean stillValid(Player p_18946_);

   default void startOpen(Player p_18955_) {
   }

   default void stopOpen(Player p_18954_) {
   }

   default boolean canPlaceItem(int p_18952_, ItemStack p_18953_) {
      return true;
   }

   default int countItem(Item p_18948_) {
      int i = 0;

      for(int j = 0; j < this.getContainerSize(); ++j) {
         ItemStack itemstack = this.getItem(j);
         if (itemstack.getItem().equals(p_18948_)) {
            i += itemstack.getCount();
         }
      }

      return i;
   }

   default boolean hasAnyOf(Set<Item> p_18950_) {
      for(int i = 0; i < this.getContainerSize(); ++i) {
         ItemStack itemstack = this.getItem(i);
         if (p_18950_.contains(itemstack.getItem()) && itemstack.getCount() > 0) {
            return true;
         }
      }

      return false;
   }
}