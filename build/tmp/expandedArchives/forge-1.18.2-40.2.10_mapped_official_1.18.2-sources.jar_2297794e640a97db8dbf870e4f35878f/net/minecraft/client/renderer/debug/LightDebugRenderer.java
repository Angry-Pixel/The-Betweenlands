package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LightDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private static final int MAX_RENDER_DIST = 10;

   public LightDebugRenderer(Minecraft p_113585_) {
      this.minecraft = p_113585_;
   }

   public void render(PoseStack p_113587_, MultiBufferSource p_113588_, double p_113589_, double p_113590_, double p_113591_) {
      Level level = this.minecraft.level;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      BlockPos blockpos = new BlockPos(p_113589_, p_113590_, p_113591_);
      LongSet longset = new LongOpenHashSet();

      for(BlockPos blockpos1 : BlockPos.betweenClosed(blockpos.offset(-10, -10, -10), blockpos.offset(10, 10, 10))) {
         int i = level.getBrightness(LightLayer.SKY, blockpos1);
         float f = (float)(15 - i) / 15.0F * 0.5F + 0.16F;
         int j = Mth.hsvToRgb(f, 0.9F, 0.9F);
         long k = SectionPos.blockToSection(blockpos1.asLong());
         if (longset.add(k)) {
            DebugRenderer.renderFloatingText(level.getChunkSource().getLightEngine().getDebugData(LightLayer.SKY, SectionPos.of(k)), (double)SectionPos.sectionToBlockCoord(SectionPos.x(k), 8), (double)SectionPos.sectionToBlockCoord(SectionPos.y(k), 8), (double)SectionPos.sectionToBlockCoord(SectionPos.z(k), 8), 16711680, 0.3F);
         }

         if (i != 15) {
            DebugRenderer.renderFloatingText(String.valueOf(i), (double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.25D, (double)blockpos1.getZ() + 0.5D, j);
         }
      }

      RenderSystem.enableTexture();
   }
}