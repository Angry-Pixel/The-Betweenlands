package com.mojang.blaze3d.pipeline;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface RenderCall {
   void execute();
}