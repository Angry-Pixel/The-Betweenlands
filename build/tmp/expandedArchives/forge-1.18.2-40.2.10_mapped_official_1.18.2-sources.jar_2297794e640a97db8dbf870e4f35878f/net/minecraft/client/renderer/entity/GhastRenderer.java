package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GhastRenderer extends MobRenderer<Ghast, GhastModel<Ghast>> {
   private static final ResourceLocation GHAST_LOCATION = new ResourceLocation("textures/entity/ghast/ghast.png");
   private static final ResourceLocation GHAST_SHOOTING_LOCATION = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

   public GhastRenderer(EntityRendererProvider.Context p_174129_) {
      super(p_174129_, new GhastModel<>(p_174129_.bakeLayer(ModelLayers.GHAST)), 1.5F);
   }

   public ResourceLocation getTextureLocation(Ghast p_114755_) {
      return p_114755_.isCharging() ? GHAST_SHOOTING_LOCATION : GHAST_LOCATION;
   }

   protected void scale(Ghast p_114757_, PoseStack p_114758_, float p_114759_) {
      float f = 1.0F;
      float f1 = 4.5F;
      float f2 = 4.5F;
      p_114758_.scale(4.5F, 4.5F, 4.5F);
   }
}