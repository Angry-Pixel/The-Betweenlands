package net.minecraft.client.gui.narration;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface NarrationSupplier {
   void updateNarration(NarrationElementOutput p_169152_);
}