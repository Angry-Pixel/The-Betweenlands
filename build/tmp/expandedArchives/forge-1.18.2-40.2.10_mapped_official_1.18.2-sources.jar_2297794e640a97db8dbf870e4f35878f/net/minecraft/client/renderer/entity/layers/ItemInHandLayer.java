package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemInHandLayer<T extends LivingEntity, M extends EntityModel<T> & ArmedModel> extends RenderLayer<T, M> {
   public ItemInHandLayer(RenderLayerParent<T, M> p_117183_) {
      super(p_117183_);
   }

   public void render(PoseStack p_117204_, MultiBufferSource p_117205_, int p_117206_, T p_117207_, float p_117208_, float p_117209_, float p_117210_, float p_117211_, float p_117212_, float p_117213_) {
      boolean flag = p_117207_.getMainArm() == HumanoidArm.RIGHT;
      ItemStack itemstack = flag ? p_117207_.getOffhandItem() : p_117207_.getMainHandItem();
      ItemStack itemstack1 = flag ? p_117207_.getMainHandItem() : p_117207_.getOffhandItem();
      if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
         p_117204_.pushPose();
         if (this.getParentModel().young) {
            float f = 0.5F;
            p_117204_.translate(0.0D, 0.75D, 0.0D);
            p_117204_.scale(0.5F, 0.5F, 0.5F);
         }

         this.renderArmWithItem(p_117207_, itemstack1, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, p_117204_, p_117205_, p_117206_);
         this.renderArmWithItem(p_117207_, itemstack, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, p_117204_, p_117205_, p_117206_);
         p_117204_.popPose();
      }
   }

   protected void renderArmWithItem(LivingEntity p_117185_, ItemStack p_117186_, ItemTransforms.TransformType p_117187_, HumanoidArm p_117188_, PoseStack p_117189_, MultiBufferSource p_117190_, int p_117191_) {
      if (!p_117186_.isEmpty()) {
         p_117189_.pushPose();
         this.getParentModel().translateToHand(p_117188_, p_117189_);
         p_117189_.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
         p_117189_.mulPose(Vector3f.YP.rotationDegrees(180.0F));
         boolean flag = p_117188_ == HumanoidArm.LEFT;
         p_117189_.translate((double)((float)(flag ? -1 : 1) / 16.0F), 0.125D, -0.625D);
         Minecraft.getInstance().getItemInHandRenderer().renderItem(p_117185_, p_117186_, p_117187_, flag, p_117189_, p_117190_, p_117191_);
         p_117189_.popPose();
      }
   }
}