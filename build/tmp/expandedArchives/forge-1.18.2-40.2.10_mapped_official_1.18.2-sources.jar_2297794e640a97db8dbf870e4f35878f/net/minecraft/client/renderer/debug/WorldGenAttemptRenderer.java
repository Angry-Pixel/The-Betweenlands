package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldGenAttemptRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final List<BlockPos> toRender = Lists.newArrayList();
   private final List<Float> scales = Lists.newArrayList();
   private final List<Float> alphas = Lists.newArrayList();
   private final List<Float> reds = Lists.newArrayList();
   private final List<Float> greens = Lists.newArrayList();
   private final List<Float> blues = Lists.newArrayList();

   public void addPos(BlockPos p_113738_, float p_113739_, float p_113740_, float p_113741_, float p_113742_, float p_113743_) {
      this.toRender.add(p_113738_);
      this.scales.add(p_113739_);
      this.alphas.add(p_113743_);
      this.reds.add(p_113740_);
      this.greens.add(p_113741_);
      this.blues.add(p_113742_);
   }

   public void render(PoseStack p_113732_, MultiBufferSource p_113733_, double p_113734_, double p_113735_, double p_113736_) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      bufferbuilder.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

      for(int i = 0; i < this.toRender.size(); ++i) {
         BlockPos blockpos = this.toRender.get(i);
         Float f = this.scales.get(i);
         float f1 = f / 2.0F;
         LevelRenderer.addChainedFilledBoxVertices(bufferbuilder, (double)((float)blockpos.getX() + 0.5F - f1) - p_113734_, (double)((float)blockpos.getY() + 0.5F - f1) - p_113735_, (double)((float)blockpos.getZ() + 0.5F - f1) - p_113736_, (double)((float)blockpos.getX() + 0.5F + f1) - p_113734_, (double)((float)blockpos.getY() + 0.5F + f1) - p_113735_, (double)((float)blockpos.getZ() + 0.5F + f1) - p_113736_, this.reds.get(i), this.greens.get(i), this.blues.get(i), this.alphas.get(i));
      }

      tesselator.end();
      RenderSystem.enableTexture();
   }
}