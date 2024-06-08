package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConduitRenderer implements BlockEntityRenderer<ConduitBlockEntity> {
   public static final Material SHELL_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/base"));
   public static final Material ACTIVE_SHELL_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/cage"));
   public static final Material WIND_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/wind"));
   public static final Material VERTICAL_WIND_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/wind_vertical"));
   public static final Material OPEN_EYE_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/open_eye"));
   public static final Material CLOSED_EYE_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/closed_eye"));
   private final ModelPart eye;
   private final ModelPart wind;
   private final ModelPart shell;
   private final ModelPart cage;
   private final BlockEntityRenderDispatcher renderer;

   public ConduitRenderer(BlockEntityRendererProvider.Context p_173613_) {
      this.renderer = p_173613_.getBlockEntityRenderDispatcher();
      this.eye = p_173613_.bakeLayer(ModelLayers.CONDUIT_EYE);
      this.wind = p_173613_.bakeLayer(ModelLayers.CONDUIT_WIND);
      this.shell = p_173613_.bakeLayer(ModelLayers.CONDUIT_SHELL);
      this.cage = p_173613_.bakeLayer(ModelLayers.CONDUIT_CAGE);
   }

   public static LayerDefinition createEyeLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 16, 16);
   }

   public static LayerDefinition createWindLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("wind", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   public static LayerDefinition createShellLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 32, 16);
   }

   public static LayerDefinition createCageLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 32, 16);
   }

   public void render(ConduitBlockEntity p_112399_, float p_112400_, PoseStack p_112401_, MultiBufferSource p_112402_, int p_112403_, int p_112404_) {
      float f = (float)p_112399_.tickCount + p_112400_;
      if (!p_112399_.isActive()) {
         float f5 = p_112399_.getActiveRotation(0.0F);
         VertexConsumer vertexconsumer1 = SHELL_TEXTURE.buffer(p_112402_, RenderType::entitySolid);
         p_112401_.pushPose();
         p_112401_.translate(0.5D, 0.5D, 0.5D);
         p_112401_.mulPose(Vector3f.YP.rotationDegrees(f5));
         this.shell.render(p_112401_, vertexconsumer1, p_112403_, p_112404_);
         p_112401_.popPose();
      } else {
         float f1 = p_112399_.getActiveRotation(p_112400_) * (180F / (float)Math.PI);
         float f2 = Mth.sin(f * 0.1F) / 2.0F + 0.5F;
         f2 = f2 * f2 + f2;
         p_112401_.pushPose();
         p_112401_.translate(0.5D, (double)(0.3F + f2 * 0.2F), 0.5D);
         Vector3f vector3f = new Vector3f(0.5F, 1.0F, 0.5F);
         vector3f.normalize();
         p_112401_.mulPose(vector3f.rotationDegrees(f1));
         this.cage.render(p_112401_, ACTIVE_SHELL_TEXTURE.buffer(p_112402_, RenderType::entityCutoutNoCull), p_112403_, p_112404_);
         p_112401_.popPose();
         int i = p_112399_.tickCount / 66 % 3;
         p_112401_.pushPose();
         p_112401_.translate(0.5D, 0.5D, 0.5D);
         if (i == 1) {
            p_112401_.mulPose(Vector3f.XP.rotationDegrees(90.0F));
         } else if (i == 2) {
            p_112401_.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
         }

         VertexConsumer vertexconsumer = (i == 1 ? VERTICAL_WIND_TEXTURE : WIND_TEXTURE).buffer(p_112402_, RenderType::entityCutoutNoCull);
         this.wind.render(p_112401_, vertexconsumer, p_112403_, p_112404_);
         p_112401_.popPose();
         p_112401_.pushPose();
         p_112401_.translate(0.5D, 0.5D, 0.5D);
         p_112401_.scale(0.875F, 0.875F, 0.875F);
         p_112401_.mulPose(Vector3f.XP.rotationDegrees(180.0F));
         p_112401_.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
         this.wind.render(p_112401_, vertexconsumer, p_112403_, p_112404_);
         p_112401_.popPose();
         Camera camera = this.renderer.camera;
         p_112401_.pushPose();
         p_112401_.translate(0.5D, (double)(0.3F + f2 * 0.2F), 0.5D);
         p_112401_.scale(0.5F, 0.5F, 0.5F);
         float f3 = -camera.getYRot();
         p_112401_.mulPose(Vector3f.YP.rotationDegrees(f3));
         p_112401_.mulPose(Vector3f.XP.rotationDegrees(camera.getXRot()));
         p_112401_.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
         float f4 = 1.3333334F;
         p_112401_.scale(1.3333334F, 1.3333334F, 1.3333334F);
         this.eye.render(p_112401_, (p_112399_.isHunting() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE).buffer(p_112402_, RenderType::entityCutoutNoCull), p_112403_, p_112404_);
         p_112401_.popPose();
      }
   }
}