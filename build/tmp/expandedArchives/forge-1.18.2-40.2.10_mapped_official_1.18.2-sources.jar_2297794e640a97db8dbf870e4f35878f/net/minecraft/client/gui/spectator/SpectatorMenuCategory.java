package net.minecraft.client.gui.spectator;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface SpectatorMenuCategory {
   List<SpectatorMenuItem> getItems();

   Component getPrompt();
}