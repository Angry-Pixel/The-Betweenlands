package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockRenderDispatcher implements ResourceManagerReloadListener {
   private final BlockModelShaper blockModelShaper;
   private final ModelBlockRenderer modelRenderer;
   private final BlockEntityWithoutLevelRenderer blockEntityRenderer;
   private final LiquidBlockRenderer liquidBlockRenderer;
   private final Random random = new Random();
   private final BlockColors blockColors;

   public BlockRenderDispatcher(BlockModelShaper p_173399_, BlockEntityWithoutLevelRenderer p_173400_, BlockColors p_173401_) {
      this.blockModelShaper = p_173399_;
      this.blockEntityRenderer = p_173400_;
      this.blockColors = p_173401_;
      this.modelRenderer = new net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer(this.blockColors);
      this.liquidBlockRenderer = new LiquidBlockRenderer();
   }

   public BlockModelShaper getBlockModelShaper() {
      return this.blockModelShaper;
   }

   @Deprecated //Forge: Model parameter
   public void renderBreakingTexture(BlockState p_110919_, BlockPos p_110920_, BlockAndTintGetter p_110921_, PoseStack p_110922_, VertexConsumer p_110923_) {
       renderBreakingTexture(p_110919_,p_110920_, p_110921_, p_110922_, p_110923_, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }
   public void renderBreakingTexture(BlockState p_110919_, BlockPos p_110920_, BlockAndTintGetter p_110921_, PoseStack p_110922_, VertexConsumer p_110923_, net.minecraftforge.client.model.data.IModelData modelData) {
      if (p_110919_.getRenderShape() == RenderShape.MODEL) {
         BakedModel bakedmodel = this.blockModelShaper.getBlockModel(p_110919_);
         long i = p_110919_.getSeed(p_110920_);
         this.modelRenderer.tesselateBlock(p_110921_, bakedmodel, p_110919_, p_110920_, p_110922_, p_110923_, true, this.random, i, OverlayTexture.NO_OVERLAY, modelData);
      }
   }

   @Deprecated //Forge: Model parameter
   public boolean renderBatched(BlockState p_110925_, BlockPos p_110926_, BlockAndTintGetter p_110927_, PoseStack p_110928_, VertexConsumer p_110929_, boolean p_110930_, Random p_110931_) {
       return renderBatched(p_110925_, p_110926_, p_110927_, p_110928_, p_110929_, p_110930_, p_110931_, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }
   public boolean renderBatched(BlockState p_110925_, BlockPos p_110926_, BlockAndTintGetter p_110927_, PoseStack p_110928_, VertexConsumer p_110929_, boolean p_110930_, Random p_110931_, net.minecraftforge.client.model.data.IModelData modelData) {
      try {
         RenderShape rendershape = p_110925_.getRenderShape();
         return rendershape != RenderShape.MODEL ? false : this.modelRenderer.tesselateBlock(p_110927_, this.getBlockModel(p_110925_), p_110925_, p_110926_, p_110928_, p_110929_, p_110930_, p_110931_, p_110925_.getSeed(p_110926_), OverlayTexture.NO_OVERLAY, modelData);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Tesselating block in world");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Block being tesselated");
         CrashReportCategory.populateBlockDetails(crashreportcategory, p_110927_, p_110926_, p_110925_);
         throw new ReportedException(crashreport);
      }
   }

   public boolean renderLiquid(BlockPos p_203144_, BlockAndTintGetter p_203145_, VertexConsumer p_203146_, BlockState p_203147_, FluidState p_203148_) {
      try {
         return this.liquidBlockRenderer.tesselate(p_203145_, p_203144_, p_203146_, p_203147_, p_203148_);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Tesselating liquid in world");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Block being tesselated");
         CrashReportCategory.populateBlockDetails(crashreportcategory, p_203145_, p_203144_, (BlockState)null);
         throw new ReportedException(crashreport);
      }
   }

   public ModelBlockRenderer getModelRenderer() {
      return this.modelRenderer;
   }

   public BakedModel getBlockModel(BlockState p_110911_) {
      return this.blockModelShaper.getBlockModel(p_110911_);
   }

   @Deprecated //Forge: Model parameter
   public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_) {
      renderSingleBlock(p_110913_, p_110914_, p_110915_, p_110916_, p_110917_, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }
   public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, net.minecraftforge.client.model.data.IModelData modelData) {
      RenderShape rendershape = p_110913_.getRenderShape();
      if (rendershape != RenderShape.INVISIBLE) {
         switch(rendershape) {
         case MODEL:
            BakedModel bakedmodel = this.getBlockModel(p_110913_);
            int i = this.blockColors.getColor(p_110913_, (BlockAndTintGetter)null, (BlockPos)null, 0);
            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;
            this.modelRenderer.renderModel(p_110914_.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData);
            break;
         case ENTITYBLOCK_ANIMATED:
            ItemStack stack = new ItemStack(p_110913_.getBlock());
            net.minecraftforge.client.RenderProperties.get(stack).getItemStackRenderer().renderByItem(stack, ItemTransforms.TransformType.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
         }

      }
   }

   public void onResourceManagerReload(ResourceManager p_110909_) {
      this.liquidBlockRenderer.setupSprites();
   }
}
