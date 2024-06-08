package net.minecraft.client.gui.spectator;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface SpectatorMenuListener {
   void onSpectatorMenuClosed(SpectatorMenu p_101843_);
}