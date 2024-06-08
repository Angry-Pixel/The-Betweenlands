package com.mojang.blaze3d.platform;

import java.util.OptionalInt;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisplayData {
   public final int width;
   public final int height;
   public final OptionalInt fullscreenWidth;
   public final OptionalInt fullscreenHeight;
   public final boolean isFullscreen;

   public DisplayData(int p_84011_, int p_84012_, OptionalInt p_84013_, OptionalInt p_84014_, boolean p_84015_) {
      this.width = p_84011_;
      this.height = p_84012_;
      this.fullscreenWidth = p_84013_;
      this.fullscreenHeight = p_84014_;
      this.isFullscreen = p_84015_;
   }
}