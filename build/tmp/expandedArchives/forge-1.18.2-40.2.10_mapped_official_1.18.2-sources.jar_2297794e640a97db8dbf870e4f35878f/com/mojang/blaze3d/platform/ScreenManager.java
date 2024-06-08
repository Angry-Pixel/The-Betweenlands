package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.lwjgl.glfw.GLFWMonitorCallbackI;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ScreenManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Long2ObjectMap<Monitor> monitors = new Long2ObjectOpenHashMap<>();
   private final MonitorCreator monitorCreator;

   public ScreenManager(MonitorCreator p_85265_) {
      RenderSystem.assertInInitPhase();
      this.monitorCreator = p_85265_;
      GLFW.glfwSetMonitorCallback(this::onMonitorChange);
      PointerBuffer pointerbuffer = GLFW.glfwGetMonitors();
      if (pointerbuffer != null) {
         for(int i = 0; i < pointerbuffer.limit(); ++i) {
            long j = pointerbuffer.get(i);
            this.monitors.put(j, p_85265_.createMonitor(j));
         }
      }

   }

   private void onMonitorChange(long p_85274_, int p_85275_) {
      RenderSystem.assertOnRenderThread();
      if (p_85275_ == 262145) {
         this.monitors.put(p_85274_, this.monitorCreator.createMonitor(p_85274_));
         LOGGER.debug("Monitor {} connected. Current monitors: {}", p_85274_, this.monitors);
      } else if (p_85275_ == 262146) {
         this.monitors.remove(p_85274_);
         LOGGER.debug("Monitor {} disconnected. Current monitors: {}", p_85274_, this.monitors);
      }

   }

   @Nullable
   public Monitor getMonitor(long p_85272_) {
      RenderSystem.assertInInitPhase();
      return this.monitors.get(p_85272_);
   }

   @Nullable
   public Monitor findBestMonitor(Window p_85277_) {
      long i = GLFW.glfwGetWindowMonitor(p_85277_.getWindow());
      if (i != 0L) {
         return this.getMonitor(i);
      } else {
         int j = p_85277_.getX();
         int k = j + p_85277_.getScreenWidth();
         int l = p_85277_.getY();
         int i1 = l + p_85277_.getScreenHeight();
         int j1 = -1;
         Monitor monitor = null;
         long k1 = GLFW.glfwGetPrimaryMonitor();
         LOGGER.debug("Selecting monitor - primary: {}, current monitors: {}", k1, this.monitors);

         for(Monitor monitor1 : this.monitors.values()) {
            int l1 = monitor1.getX();
            int i2 = l1 + monitor1.getCurrentMode().getWidth();
            int j2 = monitor1.getY();
            int k2 = j2 + monitor1.getCurrentMode().getHeight();
            int l2 = clamp(j, l1, i2);
            int i3 = clamp(k, l1, i2);
            int j3 = clamp(l, j2, k2);
            int k3 = clamp(i1, j2, k2);
            int l3 = Math.max(0, i3 - l2);
            int i4 = Math.max(0, k3 - j3);
            int j4 = l3 * i4;
            if (j4 > j1) {
               monitor = monitor1;
               j1 = j4;
            } else if (j4 == j1 && k1 == monitor1.getMonitor()) {
               LOGGER.debug("Primary monitor {} is preferred to monitor {}", monitor1, monitor);
               monitor = monitor1;
            }
         }

         LOGGER.debug("Selected monitor: {}", (Object)monitor);
         return monitor;
      }
   }

   public static int clamp(int p_85268_, int p_85269_, int p_85270_) {
      if (p_85268_ < p_85269_) {
         return p_85269_;
      } else {
         return p_85268_ > p_85270_ ? p_85270_ : p_85268_;
      }
   }

   public void shutdown() {
      RenderSystem.assertOnRenderThread();
      GLFWMonitorCallback glfwmonitorcallback = GLFW.glfwSetMonitorCallback((GLFWMonitorCallbackI)null);
      if (glfwmonitorcallback != null) {
         glfwmonitorcallback.free();
      }

   }
}