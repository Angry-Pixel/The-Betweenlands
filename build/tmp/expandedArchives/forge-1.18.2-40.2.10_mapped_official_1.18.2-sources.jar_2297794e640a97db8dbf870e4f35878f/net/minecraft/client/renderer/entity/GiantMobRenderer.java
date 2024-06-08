package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.GiantZombieModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Giant;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GiantMobRenderer extends MobRenderer<Giant, HumanoidModel<Giant>> {
   private static final ResourceLocation ZOMBIE_LOCATION = new ResourceLocation("textures/entity/zombie/zombie.png");
   private final float scale;

   public GiantMobRenderer(EntityRendererProvider.Context p_174131_, float p_174132_) {
      super(p_174131_, new GiantZombieModel(p_174131_.bakeLayer(ModelLayers.GIANT)), 0.5F * p_174132_);
      this.scale = p_174132_;
      this.addLayer(new ItemInHandLayer<>(this));
      this.addLayer(new HumanoidArmorLayer<>(this, new GiantZombieModel(p_174131_.bakeLayer(ModelLayers.GIANT_INNER_ARMOR)), new GiantZombieModel(p_174131_.bakeLayer(ModelLayers.GIANT_OUTER_ARMOR))));
   }

   protected void scale(Giant p_114775_, PoseStack p_114776_, float p_114777_) {
      p_114776_.scale(this.scale, this.scale, this.scale);
   }

   public ResourceLocation getTextureLocation(Giant p_114773_) {
      return ZOMBIE_LOCATION;
   }
}