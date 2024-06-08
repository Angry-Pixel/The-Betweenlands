package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Cod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CodRenderer extends MobRenderer<Cod, CodModel<Cod>> {
   private static final ResourceLocation COD_LOCATION = new ResourceLocation("textures/entity/fish/cod.png");

   public CodRenderer(EntityRendererProvider.Context p_173954_) {
      super(p_173954_, new CodModel<>(p_173954_.bakeLayer(ModelLayers.COD)), 0.3F);
   }

   public ResourceLocation getTextureLocation(Cod p_114015_) {
      return COD_LOCATION;
   }

   protected void setupRotations(Cod p_114017_, PoseStack p_114018_, float p_114019_, float p_114020_, float p_114021_) {
      super.setupRotations(p_114017_, p_114018_, p_114019_, p_114020_, p_114021_);
      float f = 4.3F * Mth.sin(0.6F * p_114019_);
      p_114018_.mulPose(Vector3f.YP.rotationDegrees(f));
      if (!p_114017_.isInWater()) {
         p_114018_.translate((double)0.1F, (double)0.1F, (double)-0.1F);
         p_114018_.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
      }

   }
}