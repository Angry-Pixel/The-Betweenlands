package net.minecraft.client.resources.model;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.AtlasSet;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelManager extends SimplePreparableReloadListener<ModelBakery> implements AutoCloseable {
   private Map<ResourceLocation, BakedModel> bakedRegistry = new java.util.HashMap<>();
   @Nullable
   private AtlasSet atlases;
   private final BlockModelShaper blockModelShaper;
   private final TextureManager textureManager;
   private final BlockColors blockColors;
   private int maxMipmapLevels;
   private BakedModel missingModel;
   private Object2IntMap<BlockState> modelGroups;

   public ModelManager(TextureManager p_119406_, BlockColors p_119407_, int p_119408_) {
      this.textureManager = p_119406_;
      this.blockColors = p_119407_;
      this.maxMipmapLevels = p_119408_;
      this.blockModelShaper = new BlockModelShaper(this);
   }

   public BakedModel getModel(ResourceLocation modelLocation) {
      return this.bakedRegistry.getOrDefault(modelLocation, this.missingModel);
   }

   public BakedModel getModel(ModelResourceLocation p_119423_) {
      return this.bakedRegistry.getOrDefault(p_119423_, this.missingModel);
   }

   public BakedModel getMissingModel() {
      return this.missingModel;
   }

   public BlockModelShaper getBlockModelShaper() {
      return this.blockModelShaper;
   }

   protected ModelBakery prepare(ResourceManager p_119413_, ProfilerFiller p_119414_) {
      p_119414_.startTick();
      net.minecraftforge.client.model.ForgeModelBakery modelbakery = new net.minecraftforge.client.model.ForgeModelBakery(p_119413_, this.blockColors, p_119414_, this.maxMipmapLevels);
      p_119414_.endTick();
      return modelbakery;
   }

   protected void apply(ModelBakery p_119419_, ResourceManager p_119420_, ProfilerFiller p_119421_) {
      p_119421_.startTick();
      p_119421_.push("upload");
      if (this.atlases != null) {
         this.atlases.close();
      }

      this.atlases = p_119419_.uploadTextures(this.textureManager, p_119421_);
      this.bakedRegistry = p_119419_.getBakedTopLevelModels();
      this.modelGroups = p_119419_.getModelGroups();
      this.missingModel = this.bakedRegistry.get(ModelBakery.MISSING_MODEL_LOCATION);
      net.minecraftforge.client.ForgeHooksClient.onModelBake(this, this.bakedRegistry, (net.minecraftforge.client.model.ForgeModelBakery) p_119419_);
      p_119421_.popPush("cache");
      this.blockModelShaper.rebuildCache();
      p_119421_.pop();
      p_119421_.endTick();
   }

   public boolean requiresRender(BlockState p_119416_, BlockState p_119417_) {
      if (p_119416_ == p_119417_) {
         return false;
      } else {
         int i = this.modelGroups.getInt(p_119416_);
         if (i != -1) {
            int j = this.modelGroups.getInt(p_119417_);
            if (i == j) {
               FluidState fluidstate = p_119416_.getFluidState();
               FluidState fluidstate1 = p_119417_.getFluidState();
               return fluidstate != fluidstate1;
            }
         }

         return true;
      }
   }

   public TextureAtlas getAtlas(ResourceLocation p_119429_) {
      if (this.atlases == null) throw new RuntimeException("getAtlasTexture called too early!");
      return this.atlases.getAtlas(p_119429_);
   }

   public void close() {
      if (this.atlases != null) {
         this.atlases.close();
      }

   }

   public void updateMaxMipLevel(int p_119411_) {
      this.maxMipmapLevels = p_119411_;
   }
}
