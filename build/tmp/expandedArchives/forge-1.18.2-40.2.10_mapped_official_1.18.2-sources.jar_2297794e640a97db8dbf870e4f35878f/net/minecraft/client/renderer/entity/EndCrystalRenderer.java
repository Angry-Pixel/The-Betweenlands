package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndCrystalRenderer extends EntityRenderer<EndCrystal> {
   private static final ResourceLocation END_CRYSTAL_LOCATION = new ResourceLocation("textures/entity/end_crystal/end_crystal.png");
   private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(END_CRYSTAL_LOCATION);
   private static final float SIN_45 = (float)Math.sin((Math.PI / 4D));
   private static final String GLASS = "glass";
   private static final String BASE = "base";
   private final ModelPart cube;
   private final ModelPart glass;
   private final ModelPart base;

   public EndCrystalRenderer(EntityRendererProvider.Context p_173970_) {
      super(p_173970_);
      this.shadowRadius = 0.5F;
      ModelPart modelpart = p_173970_.bakeLayer(ModelLayers.END_CRYSTAL);
      this.glass = modelpart.getChild("glass");
      this.cube = modelpart.getChild("cube");
      this.base = modelpart.getChild("base");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("glass", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      partdefinition.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   public void render(EndCrystal p_114162_, float p_114163_, float p_114164_, PoseStack p_114165_, MultiBufferSource p_114166_, int p_114167_) {
      p_114165_.pushPose();
      float f = getY(p_114162_, p_114164_);
      float f1 = ((float)p_114162_.time + p_114164_) * 3.0F;
      VertexConsumer vertexconsumer = p_114166_.getBuffer(RENDER_TYPE);
      p_114165_.pushPose();
      p_114165_.scale(2.0F, 2.0F, 2.0F);
      p_114165_.translate(0.0D, -0.5D, 0.0D);
      int i = OverlayTexture.NO_OVERLAY;
      if (p_114162_.showsBottom()) {
         this.base.render(p_114165_, vertexconsumer, p_114167_, i);
      }

      p_114165_.mulPose(Vector3f.YP.rotationDegrees(f1));
      p_114165_.translate(0.0D, (double)(1.5F + f / 2.0F), 0.0D);
      p_114165_.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
      this.glass.render(p_114165_, vertexconsumer, p_114167_, i);
      float f2 = 0.875F;
      p_114165_.scale(0.875F, 0.875F, 0.875F);
      p_114165_.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
      p_114165_.mulPose(Vector3f.YP.rotationDegrees(f1));
      this.glass.render(p_114165_, vertexconsumer, p_114167_, i);
      p_114165_.scale(0.875F, 0.875F, 0.875F);
      p_114165_.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
      p_114165_.mulPose(Vector3f.YP.rotationDegrees(f1));
      this.cube.render(p_114165_, vertexconsumer, p_114167_, i);
      p_114165_.popPose();
      p_114165_.popPose();
      BlockPos blockpos = p_114162_.getBeamTarget();
      if (blockpos != null) {
         float f3 = (float)blockpos.getX() + 0.5F;
         float f4 = (float)blockpos.getY() + 0.5F;
         float f5 = (float)blockpos.getZ() + 0.5F;
         float f6 = (float)((double)f3 - p_114162_.getX());
         float f7 = (float)((double)f4 - p_114162_.getY());
         float f8 = (float)((double)f5 - p_114162_.getZ());
         p_114165_.translate((double)f6, (double)f7, (double)f8);
         EnderDragonRenderer.renderCrystalBeams(-f6, -f7 + f, -f8, p_114164_, p_114162_.time, p_114165_, p_114166_, p_114167_);
      }

      super.render(p_114162_, p_114163_, p_114164_, p_114165_, p_114166_, p_114167_);
   }

   public static float getY(EndCrystal p_114159_, float p_114160_) {
      float f = (float)p_114159_.time + p_114160_;
      float f1 = Mth.sin(f * 0.2F) / 2.0F + 0.5F;
      f1 = (f1 * f1 + f1) * 0.4F;
      return f1 - 1.4F;
   }

   public ResourceLocation getTextureLocation(EndCrystal p_114157_) {
      return END_CRYSTAL_LOCATION;
   }

   public boolean shouldRender(EndCrystal p_114169_, Frustum p_114170_, double p_114171_, double p_114172_, double p_114173_) {
      return super.shouldRender(p_114169_, p_114170_, p_114171_, p_114172_, p_114173_) || p_114169_.getBeamTarget() != null;
   }
}