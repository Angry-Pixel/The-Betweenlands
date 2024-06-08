package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.FoxHeldItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Fox;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoxRenderer extends MobRenderer<Fox, FoxModel<Fox>> {
   private static final ResourceLocation RED_FOX_TEXTURE = new ResourceLocation("textures/entity/fox/fox.png");
   private static final ResourceLocation RED_FOX_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/fox_sleep.png");
   private static final ResourceLocation SNOW_FOX_TEXTURE = new ResourceLocation("textures/entity/fox/snow_fox.png");
   private static final ResourceLocation SNOW_FOX_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/snow_fox_sleep.png");

   public FoxRenderer(EntityRendererProvider.Context p_174127_) {
      super(p_174127_, new FoxModel<>(p_174127_.bakeLayer(ModelLayers.FOX)), 0.4F);
      this.addLayer(new FoxHeldItemLayer(this));
   }

   protected void setupRotations(Fox p_114738_, PoseStack p_114739_, float p_114740_, float p_114741_, float p_114742_) {
      super.setupRotations(p_114738_, p_114739_, p_114740_, p_114741_, p_114742_);
      if (p_114738_.isPouncing() || p_114738_.isFaceplanted()) {
         float f = -Mth.lerp(p_114742_, p_114738_.xRotO, p_114738_.getXRot());
         p_114739_.mulPose(Vector3f.XP.rotationDegrees(f));
      }

   }

   public ResourceLocation getTextureLocation(Fox p_114736_) {
      if (p_114736_.getFoxType() == Fox.Type.RED) {
         return p_114736_.isSleeping() ? RED_FOX_SLEEP_TEXTURE : RED_FOX_TEXTURE;
      } else {
         return p_114736_.isSleeping() ? SNOW_FOX_SLEEP_TEXTURE : SNOW_FOX_TEXTURE;
      }
   }
}