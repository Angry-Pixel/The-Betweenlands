package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PanoramaRenderer {
   private final Minecraft minecraft;
   private final CubeMap cubeMap;
   private float time;

   public PanoramaRenderer(CubeMap p_110002_) {
      this.cubeMap = p_110002_;
      this.minecraft = Minecraft.getInstance();
   }

   public void render(float p_110004_, float p_110005_) {
      this.time += p_110004_;
      this.cubeMap.render(this.minecraft, Mth.sin(this.time * 0.001F) * 5.0F + 25.0F, -this.time * 0.1F, p_110005_);
   }
}