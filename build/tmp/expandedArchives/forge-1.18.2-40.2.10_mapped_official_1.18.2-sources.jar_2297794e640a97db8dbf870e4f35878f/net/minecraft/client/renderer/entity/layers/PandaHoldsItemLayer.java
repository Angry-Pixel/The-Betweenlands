package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PandaHoldsItemLayer extends RenderLayer<Panda, PandaModel<Panda>> {
   public PandaHoldsItemLayer(RenderLayerParent<Panda, PandaModel<Panda>> p_117267_) {
      super(p_117267_);
   }

   public void render(PoseStack p_117280_, MultiBufferSource p_117281_, int p_117282_, Panda p_117283_, float p_117284_, float p_117285_, float p_117286_, float p_117287_, float p_117288_, float p_117289_) {
      ItemStack itemstack = p_117283_.getItemBySlot(EquipmentSlot.MAINHAND);
      if (p_117283_.isSitting() && !p_117283_.isScared()) {
         float f = -0.6F;
         float f1 = 1.4F;
         if (p_117283_.isEating()) {
            f -= 0.2F * Mth.sin(p_117287_ * 0.6F) + 0.2F;
            f1 -= 0.09F * Mth.sin(p_117287_ * 0.6F);
         }

         p_117280_.pushPose();
         p_117280_.translate((double)0.1F, (double)f1, (double)f);
         Minecraft.getInstance().getItemInHandRenderer().renderItem(p_117283_, itemstack, ItemTransforms.TransformType.GROUND, false, p_117280_, p_117281_, p_117282_);
         p_117280_.popPose();
      }
   }
}