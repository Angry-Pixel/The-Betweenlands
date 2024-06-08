package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderDispatcher implements ResourceManagerReloadListener {
   private Map<BlockEntityType<?>, BlockEntityRenderer<?>> renderers = ImmutableMap.of();
   public final Font font;
   private final EntityModelSet entityModelSet;
   public Level level;
   public Camera camera;
   public HitResult cameraHitResult;
   private final Supplier<BlockRenderDispatcher> blockRenderDispatcher;

   public BlockEntityRenderDispatcher(Font p_173559_, EntityModelSet p_173560_, Supplier<BlockRenderDispatcher> p_173561_) {
      this.font = p_173559_;
      this.entityModelSet = p_173560_;
      this.blockRenderDispatcher = p_173561_;
   }

   @Nullable
   public <E extends BlockEntity> BlockEntityRenderer<E> getRenderer(E p_112266_) {
      return (BlockEntityRenderer<E>) this.renderers.get(p_112266_.getType());
   }

   public void prepare(Level p_173565_, Camera p_173566_, HitResult p_173567_) {
      if (this.level != p_173565_) {
         this.setLevel(p_173565_);
      }

      this.camera = p_173566_;
      this.cameraHitResult = p_173567_;
   }

   public <E extends BlockEntity> void render(E p_112268_, float p_112269_, PoseStack p_112270_, MultiBufferSource p_112271_) {
      BlockEntityRenderer<E> blockentityrenderer = this.getRenderer(p_112268_);
      if (blockentityrenderer != null) {
         if (p_112268_.hasLevel() && p_112268_.getType().isValid(p_112268_.getBlockState())) {
            if (blockentityrenderer.shouldRender(p_112268_, this.camera.getPosition())) {
               tryRender(p_112268_, () -> {
                  setupAndRender(blockentityrenderer, p_112268_, p_112269_, p_112270_, p_112271_);
               });
            }
         }
      }
   }

   private static <T extends BlockEntity> void setupAndRender(BlockEntityRenderer<T> p_112285_, T p_112286_, float p_112287_, PoseStack p_112288_, MultiBufferSource p_112289_) {
      Level level = p_112286_.getLevel();
      int i;
      if (level != null) {
         i = LevelRenderer.getLightColor(level, p_112286_.getBlockPos());
      } else {
         i = 15728880;
      }

      p_112285_.render(p_112286_, p_112287_, p_112288_, p_112289_, i, OverlayTexture.NO_OVERLAY);
   }

   public <E extends BlockEntity> boolean renderItem(E p_112273_, PoseStack p_112274_, MultiBufferSource p_112275_, int p_112276_, int p_112277_) {
      BlockEntityRenderer<E> blockentityrenderer = this.getRenderer(p_112273_);
      if (blockentityrenderer == null) {
         return true;
      } else {
         tryRender(p_112273_, () -> {
            blockentityrenderer.render(p_112273_, 0.0F, p_112274_, p_112275_, p_112276_, p_112277_);
         });
         return false;
      }
   }

   private static void tryRender(BlockEntity p_112279_, Runnable p_112280_) {
      try {
         p_112280_.run();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering Block Entity");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Block Entity Details");
         p_112279_.fillCrashReportCategory(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   public void setLevel(@Nullable Level p_112258_) {
      this.level = p_112258_;
      if (p_112258_ == null) {
         this.camera = null;
      }

   }

   public void onResourceManagerReload(ResourceManager p_173563_) {
      BlockEntityRendererProvider.Context blockentityrendererprovider$context = new BlockEntityRendererProvider.Context(this, this.blockRenderDispatcher.get(), this.entityModelSet, this.font);
      this.renderers = BlockEntityRenderers.createEntityRenderers(blockentityrendererprovider$context);
   }
}