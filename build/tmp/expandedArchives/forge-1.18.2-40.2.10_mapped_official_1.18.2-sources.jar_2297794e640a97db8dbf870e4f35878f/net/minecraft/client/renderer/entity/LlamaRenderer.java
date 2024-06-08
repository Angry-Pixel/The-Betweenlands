package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.layers.LlamaDecorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LlamaRenderer extends MobRenderer<Llama, LlamaModel<Llama>> {
   private static final ResourceLocation[] LLAMA_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/llama/creamy.png"), new ResourceLocation("textures/entity/llama/white.png"), new ResourceLocation("textures/entity/llama/brown.png"), new ResourceLocation("textures/entity/llama/gray.png")};

   public LlamaRenderer(EntityRendererProvider.Context p_174293_, ModelLayerLocation p_174294_) {
      super(p_174293_, new LlamaModel<>(p_174293_.bakeLayer(p_174294_)), 0.7F);
      this.addLayer(new LlamaDecorLayer(this, p_174293_.getModelSet()));
   }

   public ResourceLocation getTextureLocation(Llama p_115355_) {
      return LLAMA_LOCATIONS[p_115355_.getVariant()];
   }
}