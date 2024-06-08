package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.PandaHoldsItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Panda;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PandaRenderer extends MobRenderer<Panda, PandaModel<Panda>> {
   private static final Map<Panda.Gene, ResourceLocation> TEXTURES = Util.make(Maps.newEnumMap(Panda.Gene.class), (p_115647_) -> {
      p_115647_.put(Panda.Gene.NORMAL, new ResourceLocation("textures/entity/panda/panda.png"));
      p_115647_.put(Panda.Gene.LAZY, new ResourceLocation("textures/entity/panda/lazy_panda.png"));
      p_115647_.put(Panda.Gene.WORRIED, new ResourceLocation("textures/entity/panda/worried_panda.png"));
      p_115647_.put(Panda.Gene.PLAYFUL, new ResourceLocation("textures/entity/panda/playful_panda.png"));
      p_115647_.put(Panda.Gene.BROWN, new ResourceLocation("textures/entity/panda/brown_panda.png"));
      p_115647_.put(Panda.Gene.WEAK, new ResourceLocation("textures/entity/panda/weak_panda.png"));
      p_115647_.put(Panda.Gene.AGGRESSIVE, new ResourceLocation("textures/entity/panda/aggressive_panda.png"));
   });

   public PandaRenderer(EntityRendererProvider.Context p_174334_) {
      super(p_174334_, new PandaModel<>(p_174334_.bakeLayer(ModelLayers.PANDA)), 0.9F);
      this.addLayer(new PandaHoldsItemLayer(this));
   }

   public ResourceLocation getTextureLocation(Panda p_115639_) {
      return TEXTURES.getOrDefault(p_115639_.getVariant(), TEXTURES.get(Panda.Gene.NORMAL));
   }

   protected void setupRotations(Panda p_115641_, PoseStack p_115642_, float p_115643_, float p_115644_, float p_115645_) {
      super.setupRotations(p_115641_, p_115642_, p_115643_, p_115644_, p_115645_);
      if (p_115641_.rollCounter > 0) {
         int i = p_115641_.rollCounter;
         int j = i + 1;
         float f = 7.0F;
         float f1 = p_115641_.isBaby() ? 0.3F : 0.8F;
         if (i < 8) {
            float f3 = (float)(90 * i) / 7.0F;
            float f4 = (float)(90 * j) / 7.0F;
            float f2 = this.getAngle(f3, f4, j, p_115645_, 8.0F);
            p_115642_.translate(0.0D, (double)((f1 + 0.2F) * (f2 / 90.0F)), 0.0D);
            p_115642_.mulPose(Vector3f.XP.rotationDegrees(-f2));
         } else if (i < 16) {
            float f13 = ((float)i - 8.0F) / 7.0F;
            float f16 = 90.0F + 90.0F * f13;
            float f5 = 90.0F + 90.0F * ((float)j - 8.0F) / 7.0F;
            float f10 = this.getAngle(f16, f5, j, p_115645_, 16.0F);
            p_115642_.translate(0.0D, (double)(f1 + 0.2F + (f1 - 0.2F) * (f10 - 90.0F) / 90.0F), 0.0D);
            p_115642_.mulPose(Vector3f.XP.rotationDegrees(-f10));
         } else if ((float)i < 24.0F) {
            float f14 = ((float)i - 16.0F) / 7.0F;
            float f17 = 180.0F + 90.0F * f14;
            float f19 = 180.0F + 90.0F * ((float)j - 16.0F) / 7.0F;
            float f11 = this.getAngle(f17, f19, j, p_115645_, 24.0F);
            p_115642_.translate(0.0D, (double)(f1 + f1 * (270.0F - f11) / 90.0F), 0.0D);
            p_115642_.mulPose(Vector3f.XP.rotationDegrees(-f11));
         } else if (i < 32) {
            float f15 = ((float)i - 24.0F) / 7.0F;
            float f18 = 270.0F + 90.0F * f15;
            float f20 = 270.0F + 90.0F * ((float)j - 24.0F) / 7.0F;
            float f12 = this.getAngle(f18, f20, j, p_115645_, 32.0F);
            p_115642_.translate(0.0D, (double)(f1 * ((360.0F - f12) / 90.0F)), 0.0D);
            p_115642_.mulPose(Vector3f.XP.rotationDegrees(-f12));
         }
      }

      float f6 = p_115641_.getSitAmount(p_115645_);
      if (f6 > 0.0F) {
         p_115642_.translate(0.0D, (double)(0.8F * f6), 0.0D);
         p_115642_.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(f6, p_115641_.getXRot(), p_115641_.getXRot() + 90.0F)));
         p_115642_.translate(0.0D, (double)(-1.0F * f6), 0.0D);
         if (p_115641_.isScared()) {
            float f7 = (float)(Math.cos((double)p_115641_.tickCount * 1.25D) * Math.PI * (double)0.05F);
            p_115642_.mulPose(Vector3f.YP.rotationDegrees(f7));
            if (p_115641_.isBaby()) {
               p_115642_.translate(0.0D, (double)0.8F, (double)0.55F);
            }
         }
      }

      float f8 = p_115641_.getLieOnBackAmount(p_115645_);
      if (f8 > 0.0F) {
         float f9 = p_115641_.isBaby() ? 0.5F : 1.3F;
         p_115642_.translate(0.0D, (double)(f9 * f8), 0.0D);
         p_115642_.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(f8, p_115641_.getXRot(), p_115641_.getXRot() + 180.0F)));
      }

   }

   private float getAngle(float p_115625_, float p_115626_, int p_115627_, float p_115628_, float p_115629_) {
      return (float)p_115627_ < p_115629_ ? Mth.lerp(p_115628_, p_115625_, p_115626_) : p_115625_;
   }
}