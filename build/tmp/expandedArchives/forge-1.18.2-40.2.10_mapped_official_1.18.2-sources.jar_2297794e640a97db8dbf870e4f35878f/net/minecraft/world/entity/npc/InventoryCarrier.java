package net.minecraft.world.entity.npc;

import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.Container;

public interface InventoryCarrier {
   @VisibleForDebug
   Container getInventory();
}