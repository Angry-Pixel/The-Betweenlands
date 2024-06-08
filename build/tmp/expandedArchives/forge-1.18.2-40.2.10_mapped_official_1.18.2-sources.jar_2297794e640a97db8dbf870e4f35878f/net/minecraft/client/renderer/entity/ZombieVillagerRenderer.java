package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZombieVillagerRenderer extends HumanoidMobRenderer<ZombieVillager, ZombieVillagerModel<ZombieVillager>> {
   private static final ResourceLocation ZOMBIE_VILLAGER_LOCATION = new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png");

   public ZombieVillagerRenderer(EntityRendererProvider.Context p_174463_) {
      super(p_174463_, new ZombieVillagerModel<>(p_174463_.bakeLayer(ModelLayers.ZOMBIE_VILLAGER)), 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, new ZombieVillagerModel(p_174463_.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)), new ZombieVillagerModel(p_174463_.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR))));
      this.addLayer(new VillagerProfessionLayer<>(this, p_174463_.getResourceManager(), "zombie_villager"));
   }

   public ResourceLocation getTextureLocation(ZombieVillager p_116559_) {
      return ZOMBIE_VILLAGER_LOCATION;
   }

   protected boolean isShaking(ZombieVillager p_116561_) {
      return super.isShaking(p_116561_) || p_116561_.isConverting();
   }
}