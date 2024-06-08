package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LlamaDecorLayer extends RenderLayer<Llama, LlamaModel<Llama>> {
   private static final ResourceLocation[] TEXTURE_LOCATION = new ResourceLocation[]{new ResourceLocation("textures/entity/llama/decor/white.png"), new ResourceLocation("textures/entity/llama/decor/orange.png"), new ResourceLocation("textures/entity/llama/decor/magenta.png"), new ResourceLocation("textures/entity/llama/decor/light_blue.png"), new ResourceLocation("textures/entity/llama/decor/yellow.png"), new ResourceLocation("textures/entity/llama/decor/lime.png"), new ResourceLocation("textures/entity/llama/decor/pink.png"), new ResourceLocation("textures/entity/llama/decor/gray.png"), new ResourceLocation("textures/entity/llama/decor/light_gray.png"), new ResourceLocation("textures/entity/llama/decor/cyan.png"), new ResourceLocation("textures/entity/llama/decor/purple.png"), new ResourceLocation("textures/entity/llama/decor/blue.png"), new ResourceLocation("textures/entity/llama/decor/brown.png"), new ResourceLocation("textures/entity/llama/decor/green.png"), new ResourceLocation("textures/entity/llama/decor/red.png"), new ResourceLocation("textures/entity/llama/decor/black.png")};
   private static final ResourceLocation TRADER_LLAMA = new ResourceLocation("textures/entity/llama/decor/trader_llama.png");
   private final LlamaModel<Llama> model;

   public LlamaDecorLayer(RenderLayerParent<Llama, LlamaModel<Llama>> p_174499_, EntityModelSet p_174500_) {
      super(p_174499_);
      this.model = new LlamaModel<>(p_174500_.bakeLayer(ModelLayers.LLAMA_DECOR));
   }

   public void render(PoseStack p_117232_, MultiBufferSource p_117233_, int p_117234_, Llama p_117235_, float p_117236_, float p_117237_, float p_117238_, float p_117239_, float p_117240_, float p_117241_) {
      DyeColor dyecolor = p_117235_.getSwag();
      ResourceLocation resourcelocation;
      if (dyecolor != null) {
         resourcelocation = TEXTURE_LOCATION[dyecolor.getId()];
      } else {
         if (!p_117235_.isTraderLlama()) {
            return;
         }

         resourcelocation = TRADER_LLAMA;
      }

      this.getParentModel().copyPropertiesTo(this.model);
      this.model.setupAnim(p_117235_, p_117236_, p_117237_, p_117239_, p_117240_, p_117241_);
      VertexConsumer vertexconsumer = p_117233_.getBuffer(RenderType.entityCutoutNoCull(resourcelocation));
      this.model.renderToBuffer(p_117232_, vertexconsumer, p_117234_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
   }
}