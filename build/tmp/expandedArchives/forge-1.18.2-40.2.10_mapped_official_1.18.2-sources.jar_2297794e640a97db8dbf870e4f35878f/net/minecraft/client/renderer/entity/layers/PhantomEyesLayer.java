package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PhantomEyesLayer<T extends Phantom> extends EyesLayer<T, PhantomModel<T>> {
   private static final RenderType PHANTOM_EYES = RenderType.eyes(new ResourceLocation("textures/entity/phantom_eyes.png"));

   public PhantomEyesLayer(RenderLayerParent<T, PhantomModel<T>> p_117342_) {
      super(p_117342_);
   }

   public RenderType renderType() {
      return PHANTOM_EYES;
   }
}