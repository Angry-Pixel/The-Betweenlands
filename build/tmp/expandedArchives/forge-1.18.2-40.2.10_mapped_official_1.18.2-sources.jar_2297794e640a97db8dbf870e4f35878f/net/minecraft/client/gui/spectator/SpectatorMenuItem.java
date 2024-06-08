package net.minecraft.client.gui.spectator;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface SpectatorMenuItem {
   void selectItem(SpectatorMenu p_101842_);

   Component getName();

   void renderIcon(PoseStack p_101839_, float p_101840_, int p_101841_);

   boolean isEnabled();
}