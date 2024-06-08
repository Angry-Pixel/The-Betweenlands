package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LecternRenderer implements BlockEntityRenderer<LecternBlockEntity> {
   private final BookModel bookModel;

   public LecternRenderer(BlockEntityRendererProvider.Context p_173621_) {
      this.bookModel = new BookModel(p_173621_.bakeLayer(ModelLayers.BOOK));
   }

   public void render(LecternBlockEntity p_112435_, float p_112436_, PoseStack p_112437_, MultiBufferSource p_112438_, int p_112439_, int p_112440_) {
      BlockState blockstate = p_112435_.getBlockState();
      if (blockstate.getValue(LecternBlock.HAS_BOOK)) {
         p_112437_.pushPose();
         p_112437_.translate(0.5D, 1.0625D, 0.5D);
         float f = blockstate.getValue(LecternBlock.FACING).getClockWise().toYRot();
         p_112437_.mulPose(Vector3f.YP.rotationDegrees(-f));
         p_112437_.mulPose(Vector3f.ZP.rotationDegrees(67.5F));
         p_112437_.translate(0.0D, -0.125D, 0.0D);
         this.bookModel.setupAnim(0.0F, 0.1F, 0.9F, 1.2F);
         VertexConsumer vertexconsumer = EnchantTableRenderer.BOOK_LOCATION.buffer(p_112438_, RenderType::entitySolid);
         this.bookModel.render(p_112437_, vertexconsumer, p_112439_, p_112440_, 1.0F, 1.0F, 1.0F, 1.0F);
         p_112437_.popPose();
      }
   }
}