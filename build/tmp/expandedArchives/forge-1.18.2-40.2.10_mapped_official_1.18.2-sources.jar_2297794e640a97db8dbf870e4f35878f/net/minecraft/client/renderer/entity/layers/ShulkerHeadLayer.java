package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerHeadLayer extends RenderLayer<Shulker, ShulkerModel<Shulker>> {
   public ShulkerHeadLayer(RenderLayerParent<Shulker, ShulkerModel<Shulker>> p_117432_) {
      super(p_117432_);
   }

   public void render(PoseStack p_117445_, MultiBufferSource p_117446_, int p_117447_, Shulker p_117448_, float p_117449_, float p_117450_, float p_117451_, float p_117452_, float p_117453_, float p_117454_) {
      ResourceLocation resourcelocation = ShulkerRenderer.getTextureLocation(p_117448_.getColor());
      VertexConsumer vertexconsumer = p_117446_.getBuffer(RenderType.entitySolid(resourcelocation));
      this.getParentModel().getHead().render(p_117445_, vertexconsumer, p_117447_, LivingEntityRenderer.getOverlayCoords(p_117448_, 0.0F));
   }
}