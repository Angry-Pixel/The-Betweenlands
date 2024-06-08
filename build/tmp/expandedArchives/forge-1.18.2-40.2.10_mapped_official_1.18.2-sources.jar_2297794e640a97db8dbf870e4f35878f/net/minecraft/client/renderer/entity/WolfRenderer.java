package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WolfRenderer extends MobRenderer<Wolf, WolfModel<Wolf>> {
   private static final ResourceLocation WOLF_LOCATION = new ResourceLocation("textures/entity/wolf/wolf.png");
   private static final ResourceLocation WOLF_TAME_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_tame.png");
   private static final ResourceLocation WOLF_ANGRY_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_angry.png");

   public WolfRenderer(EntityRendererProvider.Context p_174452_) {
      super(p_174452_, new WolfModel<>(p_174452_.bakeLayer(ModelLayers.WOLF)), 0.5F);
      this.addLayer(new WolfCollarLayer(this));
   }

   protected float getBob(Wolf p_116528_, float p_116529_) {
      return p_116528_.getTailAngle();
   }

   public void render(Wolf p_116531_, float p_116532_, float p_116533_, PoseStack p_116534_, MultiBufferSource p_116535_, int p_116536_) {
      if (p_116531_.isWet()) {
         float f = p_116531_.getWetShade(p_116533_);
         this.model.setColor(f, f, f);
      }

      super.render(p_116531_, p_116532_, p_116533_, p_116534_, p_116535_, p_116536_);
      if (p_116531_.isWet()) {
         this.model.setColor(1.0F, 1.0F, 1.0F);
      }

   }

   public ResourceLocation getTextureLocation(Wolf p_116526_) {
      if (p_116526_.isTame()) {
         return WOLF_TAME_LOCATION;
      } else {
         return p_116526_.isAngry() ? WOLF_ANGRY_LOCATION : WOLF_LOCATION;
      }
   }
}