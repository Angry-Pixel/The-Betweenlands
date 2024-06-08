package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpiderEyesLayer<T extends Entity, M extends SpiderModel<T>> extends EyesLayer<T, M> {
   private static final RenderType SPIDER_EYES = RenderType.eyes(new ResourceLocation("textures/entity/spider_eyes.png"));

   public SpiderEyesLayer(RenderLayerParent<T, M> p_117507_) {
      super(p_117507_);
   }

   public RenderType renderType() {
      return SPIDER_EYES;
   }
}