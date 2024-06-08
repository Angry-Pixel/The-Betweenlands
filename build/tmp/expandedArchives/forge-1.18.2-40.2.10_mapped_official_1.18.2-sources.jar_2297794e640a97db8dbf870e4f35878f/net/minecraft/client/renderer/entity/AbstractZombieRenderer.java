package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractZombieRenderer<T extends Zombie, M extends ZombieModel<T>> extends HumanoidMobRenderer<T, M> {
   private static final ResourceLocation ZOMBIE_LOCATION = new ResourceLocation("textures/entity/zombie/zombie.png");

   protected AbstractZombieRenderer(EntityRendererProvider.Context p_173910_, M p_173911_, M p_173912_, M p_173913_) {
      super(p_173910_, p_173911_, 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, p_173912_, p_173913_));
   }

   public ResourceLocation getTextureLocation(Zombie p_113771_) {
      return ZOMBIE_LOCATION;
   }

   protected boolean isShaking(T p_113773_) {
      return super.isShaking(p_113773_) || p_113773_.isUnderWaterConverting();
   }
}