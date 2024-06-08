package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

@OnlyIn(Dist.CLIENT)
public class MultiPartBakedModel implements net.minecraftforge.client.model.data.IDynamicBakedModel {
   private final List<Pair<Predicate<BlockState>, BakedModel>> selectors;
   protected final boolean hasAmbientOcclusion;
   protected final boolean isGui3d;
   protected final boolean usesBlockLight;
   protected final TextureAtlasSprite particleIcon;
   protected final ItemTransforms transforms;
   protected final ItemOverrides overrides;
   private final Map<BlockState, BitSet> selectorCache = new Object2ObjectOpenCustomHashMap<>(Util.identityStrategy());
   private final BakedModel defaultModel;

   public MultiPartBakedModel(List<Pair<Predicate<BlockState>, BakedModel>> p_119462_) {
      this.selectors = p_119462_;
      BakedModel bakedmodel = p_119462_.iterator().next().getRight();
      this.defaultModel = bakedmodel;
      this.hasAmbientOcclusion = bakedmodel.useAmbientOcclusion();
      this.isGui3d = bakedmodel.isGui3d();
      this.usesBlockLight = bakedmodel.usesBlockLight();
      this.particleIcon = bakedmodel.getParticleIcon();
      this.transforms = bakedmodel.getTransforms();
      this.overrides = bakedmodel.getOverrides();
   }

   // FORGE: Implement our overloads (here and below) so child models can have custom logic 
   public List<BakedQuad> getQuads(@Nullable BlockState p_119465_, @Nullable Direction p_119466_, Random p_119467_, net.minecraftforge.client.model.data.IModelData modelData) {
      if (p_119465_ == null) {
         return Collections.emptyList();
      } else {
         BitSet bitset = this.selectorCache.get(p_119465_);
         if (bitset == null) {
            bitset = new BitSet();

            for(int i = 0; i < this.selectors.size(); ++i) {
               Pair<Predicate<BlockState>, BakedModel> pair = this.selectors.get(i);
               if (pair.getLeft().test(p_119465_)) {
                  bitset.set(i);
               }
            }

            this.selectorCache.put(p_119465_, bitset);
         }

         List<BakedQuad> list = Lists.newArrayList();
         long k = p_119467_.nextLong();

         for(int j = 0; j < bitset.length(); ++j) {
            if (bitset.get(j)) {
               list.addAll(this.selectors.get(j).getRight().getQuads(p_119465_, p_119466_, new Random(k), net.minecraftforge.client.model.data.MultipartModelData.resolve(this.selectors.get(j).getRight(), modelData)));
            }
         }

         return list;
      }
   }

   public boolean useAmbientOcclusion() {
      return this.hasAmbientOcclusion;
   }

   public boolean useAmbientOcclusion(BlockState state) {
      return this.defaultModel.useAmbientOcclusion(state);
   }

   public boolean isGui3d() {
      return this.isGui3d;
   }

   public boolean usesBlockLight() {
      return this.usesBlockLight;
   }

   public boolean isCustomRenderer() {
      return false;
   }

   @Deprecated
   public TextureAtlasSprite getParticleIcon() {
      return this.particleIcon;
   }

   public TextureAtlasSprite getParticleIcon(net.minecraftforge.client.model.data.IModelData modelData) {
      return this.defaultModel.getParticleIcon(modelData);
   }

   @Deprecated
   public ItemTransforms getTransforms() {
      return this.transforms;
   }

   public BakedModel handlePerspective(net.minecraft.client.renderer.block.model.ItemTransforms.TransformType transformType, com.mojang.blaze3d.vertex.PoseStack poseStack) {
      return this.defaultModel.handlePerspective(transformType, poseStack);
   }

   public ItemOverrides getOverrides() {
      return this.overrides;
   }

   @Override
   public net.minecraftforge.client.model.data.IModelData getModelData(net.minecraft.world.level.BlockAndTintGetter world, net.minecraft.core.BlockPos pos, BlockState state, net.minecraftforge.client.model.data.IModelData tileData) {
      return net.minecraftforge.client.model.data.MultipartModelData.create(selectors, world, pos, state, tileData);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<Pair<Predicate<BlockState>, BakedModel>> selectors = Lists.newArrayList();

      public void add(Predicate<BlockState> p_119478_, BakedModel p_119479_) {
         this.selectors.add(Pair.of(p_119478_, p_119479_));
      }

      public BakedModel build() {
         return new MultiPartBakedModel(this.selectors);
      }
   }
}
