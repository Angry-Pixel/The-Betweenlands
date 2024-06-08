package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WeightedBakedModel implements net.minecraftforge.client.model.data.IDynamicBakedModel {
   private final int totalWeight;
   private final List<WeightedEntry.Wrapper<BakedModel>> list;
   private final BakedModel wrapped;

   public WeightedBakedModel(List<WeightedEntry.Wrapper<BakedModel>> p_119544_) {
      this.list = p_119544_;
      this.totalWeight = WeightedRandom.getTotalWeight(p_119544_);
      this.wrapped = p_119544_.get(0).getData();
   }

   // FORGE: Implement our overloads (here and below) so child models can have custom logic 
   public List<BakedQuad> getQuads(@Nullable BlockState p_119547_, @Nullable Direction p_119548_, Random p_119549_, net.minecraftforge.client.model.data.IModelData modelData) {
       return WeightedRandom.getWeightedItem(this.list, Math.abs((int)p_119549_.nextLong()) % this.totalWeight).map((p_174916_) -> {
           return p_174916_.getData().getQuads(p_119547_, p_119548_, p_119549_, modelData);
       }).orElse(Collections.emptyList());
   }

   public boolean useAmbientOcclusion() {
      return this.wrapped.useAmbientOcclusion();
   }

   @Override
   public boolean useAmbientOcclusion(BlockState state) {
      return this.wrapped.useAmbientOcclusion(state);
   }

   public boolean isGui3d() {
      return this.wrapped.isGui3d();
   }

   public boolean usesBlockLight() {
      return this.wrapped.usesBlockLight();
   }

   public boolean isCustomRenderer() {
      return this.wrapped.isCustomRenderer();
   }

   public TextureAtlasSprite getParticleIcon() {
      return this.wrapped.getParticleIcon();
   }

   public TextureAtlasSprite getParticleIcon(net.minecraftforge.client.model.data.IModelData modelData) {
      return this.wrapped.getParticleIcon(modelData);
   }

   public ItemTransforms getTransforms() {
      return this.wrapped.getTransforms();
   }

   public BakedModel handlePerspective(net.minecraft.client.renderer.block.model.ItemTransforms.TransformType transformType, com.mojang.blaze3d.vertex.PoseStack poseStack) {
      return this.wrapped.handlePerspective(transformType, poseStack);
   }

   public ItemOverrides getOverrides() {
      return this.wrapped.getOverrides();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<WeightedEntry.Wrapper<BakedModel>> list = Lists.newArrayList();

      public WeightedBakedModel.Builder add(@Nullable BakedModel p_119560_, int p_119561_) {
         if (p_119560_ != null) {
            this.list.add(WeightedEntry.wrap(p_119560_, p_119561_));
         }

         return this;
      }

      @Nullable
      public BakedModel build() {
         if (this.list.isEmpty()) {
            return null;
         } else {
            return (BakedModel)(this.list.size() == 1 ? this.list.get(0).getData() : new WeightedBakedModel(this.list));
         }
      }
   }
}
