package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface MenuConstructor {
   @Nullable
   AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_);
}