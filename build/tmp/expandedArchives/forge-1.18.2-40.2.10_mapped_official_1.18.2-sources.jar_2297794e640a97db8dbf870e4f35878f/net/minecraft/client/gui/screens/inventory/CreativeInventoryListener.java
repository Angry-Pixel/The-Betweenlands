package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreativeInventoryListener implements ContainerListener {
   private final Minecraft minecraft;

   public CreativeInventoryListener(Minecraft p_98492_) {
      this.minecraft = p_98492_;
   }

   public void slotChanged(AbstractContainerMenu p_98498_, int p_98499_, ItemStack p_98500_) {
      this.minecraft.gameMode.handleCreativeModeItemAdd(p_98500_, p_98499_);
   }

   public void dataChanged(AbstractContainerMenu p_169732_, int p_169733_, int p_169734_) {
   }
}