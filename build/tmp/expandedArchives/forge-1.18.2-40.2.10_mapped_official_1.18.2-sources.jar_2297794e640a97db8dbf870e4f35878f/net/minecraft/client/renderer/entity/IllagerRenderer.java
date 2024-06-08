package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class IllagerRenderer<T extends AbstractIllager> extends MobRenderer<T, IllagerModel<T>> {
   protected IllagerRenderer(EntityRendererProvider.Context p_174182_, IllagerModel<T> p_174183_, float p_174184_) {
      super(p_174182_, p_174183_, p_174184_);
      this.addLayer(new CustomHeadLayer<>(this, p_174182_.getModelSet()));
   }

   protected void scale(T p_114919_, PoseStack p_114920_, float p_114921_) {
      float f = 0.9375F;
      p_114920_.scale(0.9375F, 0.9375F, 0.9375F);
   }
}