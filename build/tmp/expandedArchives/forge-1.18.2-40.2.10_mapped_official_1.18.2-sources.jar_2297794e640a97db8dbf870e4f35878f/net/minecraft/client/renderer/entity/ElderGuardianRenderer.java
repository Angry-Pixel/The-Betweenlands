package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElderGuardianRenderer extends GuardianRenderer {
   public static final ResourceLocation GUARDIAN_ELDER_LOCATION = new ResourceLocation("textures/entity/guardian_elder.png");

   public ElderGuardianRenderer(EntityRendererProvider.Context p_173966_) {
      super(p_173966_, 1.2F, ModelLayers.ELDER_GUARDIAN);
   }

   protected void scale(Guardian p_114129_, PoseStack p_114130_, float p_114131_) {
      p_114130_.scale(ElderGuardian.ELDER_SIZE_SCALE, ElderGuardian.ELDER_SIZE_SCALE, ElderGuardian.ELDER_SIZE_SCALE);
   }

   public ResourceLocation getTextureLocation(Guardian p_114127_) {
      return GUARDIAN_ELDER_LOCATION;
   }
}