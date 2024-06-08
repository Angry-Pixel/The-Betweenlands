package net.minecraft.world;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;

public final class SimpleMenuProvider implements MenuProvider {
   private final Component title;
   private final MenuConstructor menuConstructor;

   public SimpleMenuProvider(MenuConstructor p_19202_, Component p_19203_) {
      this.menuConstructor = p_19202_;
      this.title = p_19203_;
   }

   public Component getDisplayName() {
      return this.title;
   }

   public AbstractContainerMenu createMenu(int p_19205_, Inventory p_19206_, Player p_19207_) {
      return this.menuConstructor.createMenu(p_19205_, p_19206_, p_19207_);
   }
}