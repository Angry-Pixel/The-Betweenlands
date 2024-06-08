package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreeperPowerLayer extends EnergySwirlLayer<Creeper, CreeperModel<Creeper>> {
   private static final ResourceLocation POWER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
   private final CreeperModel<Creeper> model;

   public CreeperPowerLayer(RenderLayerParent<Creeper, CreeperModel<Creeper>> p_174471_, EntityModelSet p_174472_) {
      super(p_174471_);
      this.model = new CreeperModel<>(p_174472_.bakeLayer(ModelLayers.CREEPER_ARMOR));
   }

   protected float xOffset(float p_116683_) {
      return p_116683_ * 0.01F;
   }

   protected ResourceLocation getTextureLocation() {
      return POWER_LOCATION;
   }

   protected EntityModel<Creeper> model() {
      return this.model;
   }
}