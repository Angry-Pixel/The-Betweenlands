package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public interface WorldlyContainer extends Container {
   int[] getSlotsForFace(Direction p_19238_);

   boolean canPlaceItemThroughFace(int p_19235_, ItemStack p_19236_, @Nullable Direction p_19237_);

   boolean canTakeItemThroughFace(int p_19239_, ItemStack p_19240_, Direction p_19241_);
}