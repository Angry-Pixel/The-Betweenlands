package net.minecraft.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public interface ContainerSynchronizer {
   void sendInitialData(AbstractContainerMenu p_150535_, NonNullList<ItemStack> p_150536_, ItemStack p_150537_, int[] p_150538_);

   void sendSlotChange(AbstractContainerMenu p_150530_, int p_150531_, ItemStack p_150532_);

   void sendCarriedChange(AbstractContainerMenu p_150533_, ItemStack p_150534_);

   void sendDataChange(AbstractContainerMenu p_150527_, int p_150528_, int p_150529_);
}