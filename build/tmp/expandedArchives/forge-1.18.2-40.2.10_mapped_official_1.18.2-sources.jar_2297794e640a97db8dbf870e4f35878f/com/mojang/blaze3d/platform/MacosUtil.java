package com.mojang.blaze3d.platform;

import ca.weblite.objc.NSObject;
import com.sun.jna.Pointer;
import java.util.Optional;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFWNativeCocoa;

@OnlyIn(Dist.CLIENT)
public class MacosUtil {
   private static final int NS_FULL_SCREEN_WINDOW_MASK = 16384;

   public static void toggleFullscreen(long p_182518_) {
      getNsWindow(p_182518_).filter(MacosUtil::isInKioskMode).ifPresent(MacosUtil::toggleFullscreen);
   }

   private static Optional<NSObject> getNsWindow(long p_182522_) {
      long i = GLFWNativeCocoa.glfwGetCocoaWindow(p_182522_);
      return i != 0L ? Optional.of(new NSObject(new Pointer(i))) : Optional.empty();
   }

   private static boolean isInKioskMode(NSObject p_182520_) {
      return (((Number)p_182520_.sendRaw("styleMask", new Object[0])).longValue() & 16384L) == 16384L;
   }

   private static void toggleFullscreen(NSObject p_182524_) {
      p_182524_.send("toggleFullScreen:", new Object[0]);
   }
}