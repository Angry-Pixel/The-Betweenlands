package net.minecraft.client.gui.screens.inventory;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface MenuAccess<T extends AbstractContainerMenu> {
   T getMenu();
}