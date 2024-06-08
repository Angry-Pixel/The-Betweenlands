package com.mojang.blaze3d.platform;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

@OnlyIn(Dist.CLIENT)
public final class Monitor {
   private final long monitor;
   private final List<VideoMode> videoModes;
   private VideoMode currentMode;
   private int x;
   private int y;

   public Monitor(long p_84942_) {
      this.monitor = p_84942_;
      this.videoModes = Lists.newArrayList();
      this.refreshVideoModes();
   }

   public void refreshVideoModes() {
      RenderSystem.assertInInitPhase();
      this.videoModes.clear();
      Buffer buffer = GLFW.glfwGetVideoModes(this.monitor);

      for(int i = buffer.limit() - 1; i >= 0; --i) {
         buffer.position(i);
         VideoMode videomode = new VideoMode(buffer);
         if (videomode.getRedBits() >= 8 && videomode.getGreenBits() >= 8 && videomode.getBlueBits() >= 8) {
            this.videoModes.add(videomode);
         }
      }

      int[] aint = new int[1];
      int[] aint1 = new int[1];
      GLFW.glfwGetMonitorPos(this.monitor, aint, aint1);
      this.x = aint[0];
      this.y = aint1[0];
      GLFWVidMode glfwvidmode = GLFW.glfwGetVideoMode(this.monitor);
      this.currentMode = new VideoMode(glfwvidmode);
   }

   public VideoMode getPreferredVidMode(Optional<VideoMode> p_84949_) {
      RenderSystem.assertInInitPhase();
      if (p_84949_.isPresent()) {
         VideoMode videomode = p_84949_.get();

         for(VideoMode videomode1 : this.videoModes) {
            if (videomode1.equals(videomode)) {
               return videomode1;
            }
         }
      }

      return this.getCurrentMode();
   }

   public int getVideoModeIndex(VideoMode p_84947_) {
      RenderSystem.assertInInitPhase();
      return this.videoModes.indexOf(p_84947_);
   }

   public VideoMode getCurrentMode() {
      return this.currentMode;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public VideoMode getMode(int p_84945_) {
      return this.videoModes.get(p_84945_);
   }

   public int getModeCount() {
      return this.videoModes.size();
   }

   public long getMonitor() {
      return this.monitor;
   }

   public String toString() {
      return String.format("Monitor[%s %sx%s %s]", this.monitor, this.x, this.y, this.currentMode);
   }
}