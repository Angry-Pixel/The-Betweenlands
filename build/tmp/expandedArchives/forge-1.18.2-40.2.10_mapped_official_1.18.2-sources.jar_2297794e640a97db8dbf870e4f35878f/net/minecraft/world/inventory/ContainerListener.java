package net.minecraft.world.inventory;

import net.minecraft.world.item.ItemStack;

public interface ContainerListener {
   void slotChanged(AbstractContainerMenu p_39315_, int p_39316_, ItemStack p_39317_);

   void dataChanged(AbstractContainerMenu p_150524_, int p_150525_, int p_150526_);
}