package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Overlay extends GuiComponent implements Widget {
   public boolean isPauseScreen() {
      return true;
   }
}