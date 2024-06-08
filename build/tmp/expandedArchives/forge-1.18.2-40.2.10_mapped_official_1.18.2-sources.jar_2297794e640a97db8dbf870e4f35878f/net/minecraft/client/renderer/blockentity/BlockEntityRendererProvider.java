package net.minecraft.client.renderer.blockentity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@FunctionalInterface
@OnlyIn(Dist.CLIENT)
public interface BlockEntityRendererProvider<T extends BlockEntity> {
   BlockEntityRenderer<T> create(BlockEntityRendererProvider.Context p_173571_);

   @OnlyIn(Dist.CLIENT)
   public static class Context {
      private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
      private final BlockRenderDispatcher blockRenderDispatcher;
      private final EntityModelSet modelSet;
      private final Font font;

      public Context(BlockEntityRenderDispatcher p_173577_, BlockRenderDispatcher p_173578_, EntityModelSet p_173579_, Font p_173580_) {
         this.blockEntityRenderDispatcher = p_173577_;
         this.blockRenderDispatcher = p_173578_;
         this.modelSet = p_173579_;
         this.font = p_173580_;
      }

      public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
         return this.blockEntityRenderDispatcher;
      }

      public BlockRenderDispatcher getBlockRenderDispatcher() {
         return this.blockRenderDispatcher;
      }

      public EntityModelSet getModelSet() {
         return this.modelSet;
      }

      public ModelPart bakeLayer(ModelLayerLocation p_173583_) {
         return this.modelSet.bakeLayer(p_173583_);
      }

      public Font getFont() {
         return this.font;
      }
   }
}