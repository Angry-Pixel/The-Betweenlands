package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.BatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BatRenderer extends MobRenderer<Bat, BatModel> {
   private static final ResourceLocation BAT_LOCATION = new ResourceLocation("textures/entity/bat.png");

   public BatRenderer(EntityRendererProvider.Context p_173929_) {
      super(p_173929_, new BatModel(p_173929_.bakeLayer(ModelLayers.BAT)), 0.25F);
   }

   public ResourceLocation getTextureLocation(Bat p_113876_) {
      return BAT_LOCATION;
   }

   protected void scale(Bat p_113878_, PoseStack p_113879_, float p_113880_) {
      p_113879_.scale(0.35F, 0.35F, 0.35F);
   }

   protected void setupRotations(Bat p_113882_, PoseStack p_113883_, float p_113884_, float p_113885_, float p_113886_) {
      if (p_113882_.isResting()) {
         p_113883_.translate(0.0D, (double)-0.1F, 0.0D);
      } else {
         p_113883_.translate(0.0D, (double)(Mth.cos(p_113884_ * 0.3F) * 0.1F), 0.0D);
      }

      super.setupRotations(p_113882_, p_113883_, p_113884_, p_113885_, p_113886_);
   }
}