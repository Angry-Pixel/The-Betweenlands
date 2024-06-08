package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantTableRenderer implements BlockEntityRenderer<EnchantmentTableBlockEntity> {
   public static final Material BOOK_LOCATION = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/enchanting_table_book"));
   private final BookModel bookModel;

   public EnchantTableRenderer(BlockEntityRendererProvider.Context p_173619_) {
      this.bookModel = new BookModel(p_173619_.bakeLayer(ModelLayers.BOOK));
   }

   public void render(EnchantmentTableBlockEntity p_112418_, float p_112419_, PoseStack p_112420_, MultiBufferSource p_112421_, int p_112422_, int p_112423_) {
      p_112420_.pushPose();
      p_112420_.translate(0.5D, 0.75D, 0.5D);
      float f = (float)p_112418_.time + p_112419_;
      p_112420_.translate(0.0D, (double)(0.1F + Mth.sin(f * 0.1F) * 0.01F), 0.0D);

      float f1;
      for(f1 = p_112418_.rot - p_112418_.oRot; f1 >= (float)Math.PI; f1 -= ((float)Math.PI * 2F)) {
      }

      while(f1 < -(float)Math.PI) {
         f1 += ((float)Math.PI * 2F);
      }

      float f2 = p_112418_.oRot + f1 * p_112419_;
      p_112420_.mulPose(Vector3f.YP.rotation(-f2));
      p_112420_.mulPose(Vector3f.ZP.rotationDegrees(80.0F));
      float f3 = Mth.lerp(p_112419_, p_112418_.oFlip, p_112418_.flip);
      float f4 = Mth.frac(f3 + 0.25F) * 1.6F - 0.3F;
      float f5 = Mth.frac(f3 + 0.75F) * 1.6F - 0.3F;
      float f6 = Mth.lerp(p_112419_, p_112418_.oOpen, p_112418_.open);
      this.bookModel.setupAnim(f, Mth.clamp(f4, 0.0F, 1.0F), Mth.clamp(f5, 0.0F, 1.0F), f6);
      VertexConsumer vertexconsumer = BOOK_LOCATION.buffer(p_112421_, RenderType::entitySolid);
      this.bookModel.render(p_112420_, vertexconsumer, p_112422_, p_112423_, 1.0F, 1.0F, 1.0F, 1.0F);
      p_112420_.popPose();
   }
}