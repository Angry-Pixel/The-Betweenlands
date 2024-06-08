package com.mojang.blaze3d;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class Blaze3D {
   public static void process(RenderPipeline p_166119_, float p_166120_) {
      ConcurrentLinkedQueue<RenderCall> concurrentlinkedqueue = p_166119_.getRecordingQueue();
   }

   public static void render(RenderPipeline p_166122_, float p_166123_) {
      ConcurrentLinkedQueue<RenderCall> concurrentlinkedqueue = p_166122_.getProcessedQueue();
   }

   public static void youJustLostTheGame() {
      MemoryUtil.memSet(0L, 0, 1L);
   }

   public static double getTime() {
      return GLFW.glfwGetTime();
   }
}