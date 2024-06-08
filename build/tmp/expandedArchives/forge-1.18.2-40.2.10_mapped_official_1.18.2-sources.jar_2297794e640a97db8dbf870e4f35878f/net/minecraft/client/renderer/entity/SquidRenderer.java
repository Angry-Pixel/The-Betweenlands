package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.SquidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Squid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SquidRenderer<T extends Squid> extends MobRenderer<T, SquidModel<T>> {
   private static final ResourceLocation SQUID_LOCATION = new ResourceLocation("textures/entity/squid/squid.png");

   public SquidRenderer(EntityRendererProvider.Context p_174406_, SquidModel<T> p_174407_) {
      super(p_174406_, p_174407_, 0.7F);
   }

   public ResourceLocation getTextureLocation(T p_116030_) {
      return SQUID_LOCATION;
   }

   protected void setupRotations(T p_116035_, PoseStack p_116036_, float p_116037_, float p_116038_, float p_116039_) {
      float f = Mth.lerp(p_116039_, p_116035_.xBodyRotO, p_116035_.xBodyRot);
      float f1 = Mth.lerp(p_116039_, p_116035_.zBodyRotO, p_116035_.zBodyRot);
      p_116036_.translate(0.0D, 0.5D, 0.0D);
      p_116036_.mulPose(Vector3f.YP.rotationDegrees(180.0F - p_116038_));
      p_116036_.mulPose(Vector3f.XP.rotationDegrees(f));
      p_116036_.mulPose(Vector3f.YP.rotationDegrees(f1));
      p_116036_.translate(0.0D, (double)-1.2F, 0.0D);
   }

   protected float getBob(T p_116032_, float p_116033_) {
      return Mth.lerp(p_116033_, p_116032_.oldTentacleAngle, p_116032_.tentacleAngle);
   }
}