package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ProgramManager {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static void glUseProgram(int p_85579_) {
      RenderSystem.assertOnRenderThread();
      GlStateManager._glUseProgram(p_85579_);
   }

   public static void releaseProgram(Shader p_166622_) {
      RenderSystem.assertOnRenderThread();
      p_166622_.getFragmentProgram().close();
      p_166622_.getVertexProgram().close();
      GlStateManager.glDeleteProgram(p_166622_.getId());
   }

   public static int createProgram() throws IOException {
      RenderSystem.assertOnRenderThread();
      int i = GlStateManager.glCreateProgram();
      if (i <= 0) {
         throw new IOException("Could not create shader program (returned program ID " + i + ")");
      } else {
         return i;
      }
   }

   public static void linkShader(Shader p_166624_) {
      RenderSystem.assertOnRenderThread();
      p_166624_.attachToProgram();
      GlStateManager.glLinkProgram(p_166624_.getId());
      int i = GlStateManager.glGetProgrami(p_166624_.getId(), 35714);
      if (i == 0) {
         LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", p_166624_.getVertexProgram().getName(), p_166624_.getFragmentProgram().getName());
         LOGGER.warn(GlStateManager.glGetProgramInfoLog(p_166624_.getId(), 32768));
      }

   }
}