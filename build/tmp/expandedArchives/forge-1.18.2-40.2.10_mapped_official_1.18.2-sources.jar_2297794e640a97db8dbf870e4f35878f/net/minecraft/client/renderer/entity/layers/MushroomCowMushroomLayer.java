package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MushroomCowMushroomLayer<T extends MushroomCow> extends RenderLayer<T, CowModel<T>> {
   public MushroomCowMushroomLayer(RenderLayerParent<T, CowModel<T>> p_117243_) {
      super(p_117243_);
   }

   public void render(PoseStack p_117256_, MultiBufferSource p_117257_, int p_117258_, T p_117259_, float p_117260_, float p_117261_, float p_117262_, float p_117263_, float p_117264_, float p_117265_) {
      if (!p_117259_.isBaby()) {
         Minecraft minecraft = Minecraft.getInstance();
         boolean flag = minecraft.shouldEntityAppearGlowing(p_117259_) && p_117259_.isInvisible();
         if (!p_117259_.isInvisible() || flag) {
            BlockRenderDispatcher blockrenderdispatcher = minecraft.getBlockRenderer();
            BlockState blockstate = p_117259_.getMushroomType().getBlockState();
            int i = LivingEntityRenderer.getOverlayCoords(p_117259_, 0.0F);
            BakedModel bakedmodel = blockrenderdispatcher.getBlockModel(blockstate);
            p_117256_.pushPose();
            p_117256_.translate((double)0.2F, (double)-0.35F, 0.5D);
            p_117256_.mulPose(Vector3f.YP.rotationDegrees(-48.0F));
            p_117256_.scale(-1.0F, -1.0F, 1.0F);
            p_117256_.translate(-0.5D, -0.5D, -0.5D);
            this.renderMushroomBlock(p_117256_, p_117257_, p_117258_, flag, blockrenderdispatcher, blockstate, i, bakedmodel);
            p_117256_.popPose();
            p_117256_.pushPose();
            p_117256_.translate((double)0.2F, (double)-0.35F, 0.5D);
            p_117256_.mulPose(Vector3f.YP.rotationDegrees(42.0F));
            p_117256_.translate((double)0.1F, 0.0D, (double)-0.6F);
            p_117256_.mulPose(Vector3f.YP.rotationDegrees(-48.0F));
            p_117256_.scale(-1.0F, -1.0F, 1.0F);
            p_117256_.translate(-0.5D, -0.5D, -0.5D);
            this.renderMushroomBlock(p_117256_, p_117257_, p_117258_, flag, blockrenderdispatcher, blockstate, i, bakedmodel);
            p_117256_.popPose();
            p_117256_.pushPose();
            this.getParentModel().getHead().translateAndRotate(p_117256_);
            p_117256_.translate(0.0D, (double)-0.7F, (double)-0.2F);
            p_117256_.mulPose(Vector3f.YP.rotationDegrees(-78.0F));
            p_117256_.scale(-1.0F, -1.0F, 1.0F);
            p_117256_.translate(-0.5D, -0.5D, -0.5D);
            this.renderMushroomBlock(p_117256_, p_117257_, p_117258_, flag, blockrenderdispatcher, blockstate, i, bakedmodel);
            p_117256_.popPose();
         }
      }
   }

   private void renderMushroomBlock(PoseStack p_174502_, MultiBufferSource p_174503_, int p_174504_, boolean p_174505_, BlockRenderDispatcher p_174506_, BlockState p_174507_, int p_174508_, BakedModel p_174509_) {
      if (p_174505_) {
         p_174506_.getModelRenderer().renderModel(p_174502_.last(), p_174503_.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)), p_174507_, p_174509_, 0.0F, 0.0F, 0.0F, p_174504_, p_174508_);
      } else {
         p_174506_.renderSingleBlock(p_174507_, p_174502_, p_174503_, p_174504_, p_174508_);
      }

   }
}