package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureTarget extends RenderTarget {
   public TextureTarget(int p_166213_, int p_166214_, boolean p_166215_, boolean p_166216_) {
      super(p_166215_);
      RenderSystem.assertOnRenderThreadOrInit();
      this.resize(p_166213_, p_166214_, p_166216_);
   }
}