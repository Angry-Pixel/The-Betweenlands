package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class VirtualScreen implements AutoCloseable {
   private final Minecraft minecraft;
   private final ScreenManager screenManager;

   public VirtualScreen(Minecraft p_110871_) {
      this.minecraft = p_110871_;
      this.screenManager = new ScreenManager(Monitor::new);
   }

   public Window newWindow(DisplayData p_110873_, @Nullable String p_110874_, String p_110875_) {
      return new Window(this.minecraft, this.screenManager, p_110873_, p_110874_, p_110875_);
   }

   public void close() {
      this.screenManager.shutdown();
   }
}