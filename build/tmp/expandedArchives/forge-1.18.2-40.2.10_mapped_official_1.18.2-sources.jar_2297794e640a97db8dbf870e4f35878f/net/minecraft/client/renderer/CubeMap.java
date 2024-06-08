package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CubeMap {
   private static final int SIDES = 6;
   private final ResourceLocation[] images = new ResourceLocation[6];

   public CubeMap(ResourceLocation p_108848_) {
      for(int i = 0; i < 6; ++i) {
         this.images[i] = new ResourceLocation(p_108848_.getNamespace(), p_108848_.getPath() + "_" + i + ".png");
      }

   }

   public void render(Minecraft p_108850_, float p_108851_, float p_108852_, float p_108853_) {
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      Matrix4f matrix4f = Matrix4f.perspective(85.0D, (float)p_108850_.getWindow().getWidth() / (float)p_108850_.getWindow().getHeight(), 0.05F, 10.0F);
      RenderSystem.backupProjectionMatrix();
      RenderSystem.setProjectionMatrix(matrix4f);
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.pushPose();
      posestack.setIdentity();
      posestack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
      RenderSystem.applyModelViewMatrix();
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      RenderSystem.disableCull();
      RenderSystem.depthMask(false);
      RenderSystem.defaultBlendFunc();
      int i = 2;

      for(int j = 0; j < 4; ++j) {
         posestack.pushPose();
         float f = ((float)(j % 2) / 2.0F - 0.5F) / 256.0F;
         float f1 = ((float)(j / 2) / 2.0F - 0.5F) / 256.0F;
         float f2 = 0.0F;
         posestack.translate((double)f, (double)f1, 0.0D);
         posestack.mulPose(Vector3f.XP.rotationDegrees(p_108851_));
         posestack.mulPose(Vector3f.YP.rotationDegrees(p_108852_));
         RenderSystem.applyModelViewMatrix();

         for(int k = 0; k < 6; ++k) {
            RenderSystem.setShaderTexture(0, this.images[k]);
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            int l = Math.round(255.0F * p_108853_) / (j + 1);
            if (k == 0) {
               bufferbuilder.vertex(-1.0D, -1.0D, 1.0D).uv(0.0F, 0.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(-1.0D, 1.0D, 1.0D).uv(0.0F, 1.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(1.0D, 1.0D, 1.0D).uv(1.0F, 1.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(1.0D, -1.0D, 1.0D).uv(1.0F, 0.0F).color(255, 255, 255, l).endVertex();
            }

            if (k == 1) {
               bufferbuilder.vertex(1.0D, -1.0D, 1.0D).uv(0.0F, 0.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(1.0D, 1.0D, 1.0D).uv(0.0F, 1.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(1.0D, 1.0D, -1.0D).uv(1.0F, 1.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(1.0D, -1.0D, -1.0D).uv(1.0F, 0.0F).color(255, 255, 255, l).endVertex();
            }

            if (k == 2) {
               bufferbuilder.vertex(1.0D, -1.0D, -1.0D).uv(0.0F, 0.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(1.0D, 1.0D, -1.0D).uv(0.0F, 1.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(-1.0D, 1.0D, -1.0D).uv(1.0F, 1.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(-1.0D, -1.0D, -1.0D).uv(1.0F, 0.0F).color(255, 255, 255, l).endVertex();
            }

            if (k == 3) {
               bufferbuilder.vertex(-1.0D, -1.0D, -1.0D).uv(0.0F, 0.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(-1.0D, 1.0D, -1.0D).uv(0.0F, 1.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(-1.0D, 1.0D, 1.0D).uv(1.0F, 1.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(-1.0D, -1.0D, 1.0D).uv(1.0F, 0.0F).color(255, 255, 255, l).endVertex();
            }

            if (k == 4) {
               bufferbuilder.vertex(-1.0D, -1.0D, -1.0D).uv(0.0F, 0.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(-1.0D, -1.0D, 1.0D).uv(0.0F, 1.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(1.0D, -1.0D, 1.0D).uv(1.0F, 1.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(1.0D, -1.0D, -1.0D).uv(1.0F, 0.0F).color(255, 255, 255, l).endVertex();
            }

            if (k == 5) {
               bufferbuilder.vertex(-1.0D, 1.0D, 1.0D).uv(0.0F, 0.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(-1.0D, 1.0D, -1.0D).uv(0.0F, 1.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(1.0D, 1.0D, -1.0D).uv(1.0F, 1.0F).color(255, 255, 255, l).endVertex();
               bufferbuilder.vertex(1.0D, 1.0D, 1.0D).uv(1.0F, 0.0F).color(255, 255, 255, l).endVertex();
            }

            tesselator.end();
         }

         posestack.popPose();
         RenderSystem.applyModelViewMatrix();
         RenderSystem.colorMask(true, true, true, false);
      }

      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.restoreProjectionMatrix();
      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
      RenderSystem.depthMask(true);
      RenderSystem.enableCull();
      RenderSystem.enableDepthTest();
   }

   public CompletableFuture<Void> preload(TextureManager p_108855_, Executor p_108856_) {
      CompletableFuture<?>[] completablefuture = new CompletableFuture[6];

      for(int i = 0; i < completablefuture.length; ++i) {
         completablefuture[i] = p_108855_.preload(this.images[i], p_108856_);
      }

      return CompletableFuture.allOf(completablefuture);
   }
}