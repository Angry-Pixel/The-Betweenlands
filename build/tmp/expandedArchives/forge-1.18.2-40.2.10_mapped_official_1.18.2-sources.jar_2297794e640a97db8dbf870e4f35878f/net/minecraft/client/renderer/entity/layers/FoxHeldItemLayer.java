package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoxHeldItemLayer extends RenderLayer<Fox, FoxModel<Fox>> {
   public FoxHeldItemLayer(RenderLayerParent<Fox, FoxModel<Fox>> p_116994_) {
      super(p_116994_);
   }

   public void render(PoseStack p_117007_, MultiBufferSource p_117008_, int p_117009_, Fox p_117010_, float p_117011_, float p_117012_, float p_117013_, float p_117014_, float p_117015_, float p_117016_) {
      boolean flag = p_117010_.isSleeping();
      boolean flag1 = p_117010_.isBaby();
      p_117007_.pushPose();
      if (flag1) {
         float f = 0.75F;
         p_117007_.scale(0.75F, 0.75F, 0.75F);
         p_117007_.translate(0.0D, 0.5D, (double)0.209375F);
      }

      p_117007_.translate((double)((this.getParentModel()).head.x / 16.0F), (double)((this.getParentModel()).head.y / 16.0F), (double)((this.getParentModel()).head.z / 16.0F));
      float f1 = p_117010_.getHeadRollAngle(p_117013_);
      p_117007_.mulPose(Vector3f.ZP.rotation(f1));
      p_117007_.mulPose(Vector3f.YP.rotationDegrees(p_117015_));
      p_117007_.mulPose(Vector3f.XP.rotationDegrees(p_117016_));
      if (p_117010_.isBaby()) {
         if (flag) {
            p_117007_.translate((double)0.4F, (double)0.26F, (double)0.15F);
         } else {
            p_117007_.translate((double)0.06F, (double)0.26F, -0.5D);
         }
      } else if (flag) {
         p_117007_.translate((double)0.46F, (double)0.26F, (double)0.22F);
      } else {
         p_117007_.translate((double)0.06F, (double)0.27F, -0.5D);
      }

      p_117007_.mulPose(Vector3f.XP.rotationDegrees(90.0F));
      if (flag) {
         p_117007_.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
      }

      ItemStack itemstack = p_117010_.getItemBySlot(EquipmentSlot.MAINHAND);
      Minecraft.getInstance().getItemInHandRenderer().renderItem(p_117010_, itemstack, ItemTransforms.TransformType.GROUND, false, p_117007_, p_117008_, p_117009_);
      p_117007_.popPose();
   }
}