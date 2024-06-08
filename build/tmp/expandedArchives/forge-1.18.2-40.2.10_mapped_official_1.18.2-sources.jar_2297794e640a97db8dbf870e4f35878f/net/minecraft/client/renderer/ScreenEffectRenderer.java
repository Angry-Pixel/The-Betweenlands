package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenEffectRenderer {
   private static final ResourceLocation UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png");

   public static void renderScreenEffect(Minecraft p_110719_, PoseStack p_110720_) {
      Player player = p_110719_.player;
      if (!player.noPhysics) {
         org.apache.commons.lang3.tuple.Pair<BlockState, BlockPos> overlay = getOverlayBlock(player);
         if (overlay != null) {
            if (!net.minecraftforge.event.ForgeEventFactory.renderBlockOverlay(player, p_110720_, net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType.BLOCK, overlay.getLeft(), overlay.getRight()))
                renderTex(p_110719_.getBlockRenderer().getBlockModelShaper().getTexture(overlay.getLeft(), p_110719_.level, overlay.getRight()), p_110720_);
         }
      }

      if (!p_110719_.player.isSpectator()) {
         if (p_110719_.player.isEyeInFluid(FluidTags.WATER)) {
            if (!net.minecraftforge.event.ForgeEventFactory.renderWaterOverlay(player, p_110720_))
            renderWater(p_110719_, p_110720_);
         }

         if (p_110719_.player.isOnFire()) {
            if (!net.minecraftforge.event.ForgeEventFactory.renderFireOverlay(player, p_110720_))
            renderFire(p_110719_, p_110720_);
         }
      }

   }

   @Nullable
   private static BlockState getViewBlockingState(Player p_110717_) {
      return getOverlayBlock(p_110717_).getLeft();
   }

   @Nullable
   private static org.apache.commons.lang3.tuple.Pair<BlockState, BlockPos> getOverlayBlock(Player p_110717_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i = 0; i < 8; ++i) {
         double d0 = p_110717_.getX() + (double)(((float)((i >> 0) % 2) - 0.5F) * p_110717_.getBbWidth() * 0.8F);
         double d1 = p_110717_.getEyeY() + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
         double d2 = p_110717_.getZ() + (double)(((float)((i >> 2) % 2) - 0.5F) * p_110717_.getBbWidth() * 0.8F);
         blockpos$mutableblockpos.set(d0, d1, d2);
         BlockState blockstate = p_110717_.level.getBlockState(blockpos$mutableblockpos);
         if (blockstate.getRenderShape() != RenderShape.INVISIBLE && blockstate.isViewBlocking(p_110717_.level, blockpos$mutableblockpos)) {
            return org.apache.commons.lang3.tuple.Pair.of(blockstate, blockpos$mutableblockpos.immutable());
         }
      }

      return null;
   }

   private static void renderTex(TextureAtlasSprite p_173297_, PoseStack p_173298_) {
      RenderSystem.setShaderTexture(0, p_173297_.atlas().location());
      RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
      BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
      float f = 0.1F;
      float f1 = -1.0F;
      float f2 = 1.0F;
      float f3 = -1.0F;
      float f4 = 1.0F;
      float f5 = -0.5F;
      float f6 = p_173297_.getU0();
      float f7 = p_173297_.getU1();
      float f8 = p_173297_.getV0();
      float f9 = p_173297_.getV1();
      Matrix4f matrix4f = p_173298_.last().pose();
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
      bufferbuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).uv(f7, f9).endVertex();
      bufferbuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).uv(f6, f9).endVertex();
      bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).uv(f6, f8).endVertex();
      bufferbuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).uv(f7, f8).endVertex();
      bufferbuilder.end();
      BufferUploader.end(bufferbuilder);
   }

   private static void renderWater(Minecraft p_110726_, PoseStack p_110727_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.enableTexture();
      RenderSystem.setShaderTexture(0, UNDERWATER_LOCATION);
      BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
      float f = p_110726_.player.getBrightness();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(f, f, f, 0.1F);
      float f1 = 4.0F;
      float f2 = -1.0F;
      float f3 = 1.0F;
      float f4 = -1.0F;
      float f5 = 1.0F;
      float f6 = -0.5F;
      float f7 = -p_110726_.player.getYRot() / 64.0F;
      float f8 = p_110726_.player.getXRot() / 64.0F;
      Matrix4f matrix4f = p_110727_.last().pose();
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferbuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).uv(4.0F + f7, 4.0F + f8).endVertex();
      bufferbuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).uv(0.0F + f7, 4.0F + f8).endVertex();
      bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).uv(0.0F + f7, 0.0F + f8).endVertex();
      bufferbuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).uv(4.0F + f7, 0.0F + f8).endVertex();
      bufferbuilder.end();
      BufferUploader.end(bufferbuilder);
      RenderSystem.disableBlend();
   }

   private static void renderFire(Minecraft p_110729_, PoseStack p_110730_) {
      BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
      RenderSystem.depthFunc(519);
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.enableTexture();
      TextureAtlasSprite textureatlassprite = ModelBakery.FIRE_1.sprite();
      RenderSystem.setShaderTexture(0, textureatlassprite.atlas().location());
      float f = textureatlassprite.getU0();
      float f1 = textureatlassprite.getU1();
      float f2 = (f + f1) / 2.0F;
      float f3 = textureatlassprite.getV0();
      float f4 = textureatlassprite.getV1();
      float f5 = (f3 + f4) / 2.0F;
      float f6 = textureatlassprite.uvShrinkRatio();
      float f7 = Mth.lerp(f6, f, f2);
      float f8 = Mth.lerp(f6, f1, f2);
      float f9 = Mth.lerp(f6, f3, f5);
      float f10 = Mth.lerp(f6, f4, f5);
      float f11 = 1.0F;

      for(int i = 0; i < 2; ++i) {
         p_110730_.pushPose();
         float f12 = -0.5F;
         float f13 = 0.5F;
         float f14 = -0.5F;
         float f15 = 0.5F;
         float f16 = -0.5F;
         p_110730_.translate((double)((float)(-(i * 2 - 1)) * 0.24F), (double)-0.3F, 0.0D);
         p_110730_.mulPose(Vector3f.YP.rotationDegrees((float)(i * 2 - 1) * 10.0F));
         Matrix4f matrix4f = p_110730_.last().pose();
         bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
         bufferbuilder.vertex(matrix4f, -0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(f8, f10).endVertex();
         bufferbuilder.vertex(matrix4f, 0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(f7, f10).endVertex();
         bufferbuilder.vertex(matrix4f, 0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(f7, f9).endVertex();
         bufferbuilder.vertex(matrix4f, -0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).uv(f8, f9).endVertex();
         bufferbuilder.end();
         BufferUploader.end(bufferbuilder);
         p_110730_.popPose();
      }

      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
      RenderSystem.depthFunc(515);
   }
}
