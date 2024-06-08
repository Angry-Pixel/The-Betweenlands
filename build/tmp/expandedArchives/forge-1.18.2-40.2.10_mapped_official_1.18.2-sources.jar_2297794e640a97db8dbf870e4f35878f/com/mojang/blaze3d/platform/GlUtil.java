package com.mojang.blaze3d.platform;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class GlUtil {
   public static ByteBuffer allocateMemory(int p_166248_) {
      return MemoryUtil.memAlloc(p_166248_);
   }

   public static void freeMemory(Buffer p_166252_) {
      MemoryUtil.memFree(p_166252_);
   }

   public static String getVendor() {
      return GlStateManager._getString(7936);
   }

   public static String getCpuInfo() {
      return GLX._getCpuInfo();
   }

   public static String getRenderer() {
      return GlStateManager._getString(7937);
   }

   public static String getOpenGLVersion() {
      return GlStateManager._getString(7938);
   }
}