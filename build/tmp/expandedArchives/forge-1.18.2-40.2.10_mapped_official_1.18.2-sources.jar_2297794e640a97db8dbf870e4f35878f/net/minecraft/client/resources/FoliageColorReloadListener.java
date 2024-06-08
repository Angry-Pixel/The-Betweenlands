package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.FoliageColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoliageColorReloadListener extends SimplePreparableReloadListener<int[]> {
   private static final ResourceLocation LOCATION = new ResourceLocation("textures/colormap/foliage.png");

   protected int[] prepare(ResourceManager p_118660_, ProfilerFiller p_118661_) {
      try {
         return LegacyStuffWrapper.getPixels(p_118660_, LOCATION);
      } catch (IOException ioexception) {
         throw new IllegalStateException("Failed to load foliage color texture", ioexception);
      }
   }

   protected void apply(int[] p_118667_, ResourceManager p_118668_, ProfilerFiller p_118669_) {
      FoliageColor.init(p_118667_);
   }
}