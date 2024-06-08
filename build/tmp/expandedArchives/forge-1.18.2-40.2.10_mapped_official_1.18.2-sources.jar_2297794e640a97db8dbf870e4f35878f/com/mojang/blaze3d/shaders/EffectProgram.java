package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EffectProgram extends Program {
   private static final GlslPreprocessor PREPROCESSOR = new GlslPreprocessor() {
      public String applyImport(boolean p_166595_, String p_166596_) {
         return "#error Import statement not supported";
      }
   };
   private int references;

   private EffectProgram(Program.Type p_166582_, int p_166583_, String p_166584_) {
      super(p_166582_, p_166583_, p_166584_);
   }

   public void attachToEffect(Effect p_166587_) {
      RenderSystem.assertOnRenderThread();
      ++this.references;
      this.attachToShader(p_166587_);
   }

   public void close() {
      RenderSystem.assertOnRenderThread();
      --this.references;
      if (this.references <= 0) {
         super.close();
      }

   }

   public static EffectProgram compileShader(Program.Type p_166589_, String p_166590_, InputStream p_166591_, String p_166592_) throws IOException {
      RenderSystem.assertOnRenderThread();
      int i = compileShaderInternal(p_166589_, p_166590_, p_166591_, p_166592_, PREPROCESSOR);
      EffectProgram effectprogram = new EffectProgram(p_166589_, i, p_166590_);
      p_166589_.getPrograms().put(p_166590_, effectprogram);
      return effectprogram;
   }
}