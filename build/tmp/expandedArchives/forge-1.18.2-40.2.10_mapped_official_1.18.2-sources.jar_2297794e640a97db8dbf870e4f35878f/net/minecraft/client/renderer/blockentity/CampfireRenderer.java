package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CampfireRenderer implements BlockEntityRenderer<CampfireBlockEntity> {
   private static final float SIZE = 0.375F;

   public CampfireRenderer(BlockEntityRendererProvider.Context p_173602_) {
   }

   public void render(CampfireBlockEntity p_112344_, float p_112345_, PoseStack p_112346_, MultiBufferSource p_112347_, int p_112348_, int p_112349_) {
      Direction direction = p_112344_.getBlockState().getValue(CampfireBlock.FACING);
      NonNullList<ItemStack> nonnulllist = p_112344_.getItems();
      int i = (int)p_112344_.getBlockPos().asLong();

      for(int j = 0; j < nonnulllist.size(); ++j) {
         ItemStack itemstack = nonnulllist.get(j);
         if (itemstack != ItemStack.EMPTY) {
            p_112346_.pushPose();
            p_112346_.translate(0.5D, 0.44921875D, 0.5D);
            Direction direction1 = Direction.from2DDataValue((j + direction.get2DDataValue()) % 4);
            float f = -direction1.toYRot();
            p_112346_.mulPose(Vector3f.YP.rotationDegrees(f));
            p_112346_.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            p_112346_.translate(-0.3125D, -0.3125D, 0.0D);
            p_112346_.scale(0.375F, 0.375F, 0.375F);
            Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemTransforms.TransformType.FIXED, p_112348_, p_112349_, p_112346_, p_112347_, i + j);
            p_112346_.popPose();
         }
      }

   }
}