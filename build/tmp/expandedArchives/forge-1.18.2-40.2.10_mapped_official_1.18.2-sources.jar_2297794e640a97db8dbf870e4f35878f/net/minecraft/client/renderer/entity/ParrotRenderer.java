package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParrotRenderer extends MobRenderer<Parrot, ParrotModel> {
   public static final ResourceLocation[] PARROT_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/parrot/parrot_red_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_green.png"), new ResourceLocation("textures/entity/parrot/parrot_yellow_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_grey.png")};

   public ParrotRenderer(EntityRendererProvider.Context p_174336_) {
      super(p_174336_, new ParrotModel(p_174336_.bakeLayer(ModelLayers.PARROT)), 0.3F);
   }

   public ResourceLocation getTextureLocation(Parrot p_115658_) {
      return PARROT_LOCATIONS[p_115658_.getVariant()];
   }

   public float getBob(Parrot p_115660_, float p_115661_) {
      float f = Mth.lerp(p_115661_, p_115660_.oFlap, p_115660_.flap);
      float f1 = Mth.lerp(p_115661_, p_115660_.oFlapSpeed, p_115660_.flapSpeed);
      return (Mth.sin(f) + 1.0F) * f1;
   }
}