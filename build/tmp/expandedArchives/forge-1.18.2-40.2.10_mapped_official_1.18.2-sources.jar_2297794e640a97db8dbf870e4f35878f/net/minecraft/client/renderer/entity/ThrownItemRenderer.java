package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThrownItemRenderer<T extends Entity & ItemSupplier> extends EntityRenderer<T> {
   private static final float MIN_CAMERA_DISTANCE_SQUARED = 12.25F;
   private final ItemRenderer itemRenderer;
   private final float scale;
   private final boolean fullBright;

   public ThrownItemRenderer(EntityRendererProvider.Context p_174416_, float p_174417_, boolean p_174418_) {
      super(p_174416_);
      this.itemRenderer = p_174416_.getItemRenderer();
      this.scale = p_174417_;
      this.fullBright = p_174418_;
   }

   public ThrownItemRenderer(EntityRendererProvider.Context p_174414_) {
      this(p_174414_, 1.0F, false);
   }

   protected int getBlockLightLevel(T p_116092_, BlockPos p_116093_) {
      return this.fullBright ? 15 : super.getBlockLightLevel(p_116092_, p_116093_);
   }

   public void render(T p_116085_, float p_116086_, float p_116087_, PoseStack p_116088_, MultiBufferSource p_116089_, int p_116090_) {
      if (p_116085_.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(p_116085_) < 12.25D)) {
         p_116088_.pushPose();
         p_116088_.scale(this.scale, this.scale, this.scale);
         p_116088_.mulPose(this.entityRenderDispatcher.cameraOrientation());
         p_116088_.mulPose(Vector3f.YP.rotationDegrees(180.0F));
         this.itemRenderer.renderStatic(p_116085_.getItem(), ItemTransforms.TransformType.GROUND, p_116090_, OverlayTexture.NO_OVERLAY, p_116088_, p_116089_, p_116085_.getId());
         p_116088_.popPose();
         super.render(p_116085_, p_116086_, p_116087_, p_116088_, p_116089_, p_116090_);
      }
   }

   public ResourceLocation getTextureLocation(Entity p_116083_) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}