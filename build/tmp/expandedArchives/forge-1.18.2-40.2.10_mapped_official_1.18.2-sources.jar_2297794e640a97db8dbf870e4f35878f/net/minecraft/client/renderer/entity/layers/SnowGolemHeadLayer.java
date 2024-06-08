package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnowGolemHeadLayer extends RenderLayer<SnowGolem, SnowGolemModel<SnowGolem>> {
   public SnowGolemHeadLayer(RenderLayerParent<SnowGolem, SnowGolemModel<SnowGolem>> p_117481_) {
      super(p_117481_);
   }

   public void render(PoseStack p_117494_, MultiBufferSource p_117495_, int p_117496_, SnowGolem p_117497_, float p_117498_, float p_117499_, float p_117500_, float p_117501_, float p_117502_, float p_117503_) {
      if (p_117497_.hasPumpkin()) {
         Minecraft minecraft = Minecraft.getInstance();
         boolean flag = minecraft.shouldEntityAppearGlowing(p_117497_) && p_117497_.isInvisible();
         if (!p_117497_.isInvisible() || flag) {
            p_117494_.pushPose();
            this.getParentModel().getHead().translateAndRotate(p_117494_);
            float f = 0.625F;
            p_117494_.translate(0.0D, -0.34375D, 0.0D);
            p_117494_.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            p_117494_.scale(0.625F, -0.625F, -0.625F);
            ItemStack itemstack = new ItemStack(Blocks.CARVED_PUMPKIN);
            if (flag) {
               BlockState blockstate = Blocks.CARVED_PUMPKIN.defaultBlockState();
               BlockRenderDispatcher blockrenderdispatcher = minecraft.getBlockRenderer();
               BakedModel bakedmodel = blockrenderdispatcher.getBlockModel(blockstate);
               int i = LivingEntityRenderer.getOverlayCoords(p_117497_, 0.0F);
               p_117494_.translate(-0.5D, -0.5D, -0.5D);
               blockrenderdispatcher.getModelRenderer().renderModel(p_117494_.last(), p_117495_.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)), blockstate, bakedmodel, 0.0F, 0.0F, 0.0F, p_117496_, i);
            } else {
               minecraft.getItemRenderer().renderStatic(p_117497_, itemstack, ItemTransforms.TransformType.HEAD, false, p_117494_, p_117495_, p_117497_.level, p_117496_, LivingEntityRenderer.getOverlayCoords(p_117497_, 0.0F), p_117497_.getId());
            }

            p_117494_.popPose();
         }
      }
   }
}