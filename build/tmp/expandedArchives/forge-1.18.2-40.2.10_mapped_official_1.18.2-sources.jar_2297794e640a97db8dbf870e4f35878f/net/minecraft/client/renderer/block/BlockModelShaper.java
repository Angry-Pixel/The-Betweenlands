package net.minecraft.client.renderer.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockModelShaper {
   private final Map<BlockState, BakedModel> modelByStateCache = Maps.newIdentityHashMap();
   private final ModelManager modelManager;

   public BlockModelShaper(ModelManager p_110880_) {
      this.modelManager = p_110880_;
   }

   @Deprecated
   public TextureAtlasSprite getParticleIcon(BlockState p_110883_) {
      return this.getBlockModel(p_110883_).getParticleIcon(net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }

   public TextureAtlasSprite getTexture(BlockState p_110883_, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
      net.minecraftforge.client.model.data.IModelData data = net.minecraftforge.client.model.ModelDataManager.getModelData(level, pos);
      BakedModel model = this.getBlockModel(p_110883_);
      return model.getParticleIcon(model.getModelData(level, pos, p_110883_, data == null ? net.minecraftforge.client.model.data.EmptyModelData.INSTANCE : data));
   }

   public BakedModel getBlockModel(BlockState p_110894_) {
      BakedModel bakedmodel = this.modelByStateCache.get(p_110894_);
      if (bakedmodel == null) {
         bakedmodel = this.modelManager.getMissingModel();
      }

      return bakedmodel;
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public void rebuildCache() {
      this.modelByStateCache.clear();

      for(Block block : Registry.BLOCK) {
         block.getStateDefinition().getPossibleStates().forEach((p_110898_) -> {
            this.modelByStateCache.put(p_110898_, this.modelManager.getModel(stateToModelLocation(p_110898_)));
         });
      }

   }

   public static ModelResourceLocation stateToModelLocation(BlockState p_110896_) {
      return stateToModelLocation(Registry.BLOCK.getKey(p_110896_.getBlock()), p_110896_);
   }

   public static ModelResourceLocation stateToModelLocation(ResourceLocation p_110890_, BlockState p_110891_) {
      return new ModelResourceLocation(p_110890_, statePropertiesToString(p_110891_.getValues()));
   }

   public static String statePropertiesToString(Map<Property<?>, Comparable<?>> p_110888_) {
      StringBuilder stringbuilder = new StringBuilder();

      for(Entry<Property<?>, Comparable<?>> entry : p_110888_.entrySet()) {
         if (stringbuilder.length() != 0) {
            stringbuilder.append(',');
         }

         Property<?> property = entry.getKey();
         stringbuilder.append(property.getName());
         stringbuilder.append('=');
         stringbuilder.append(getValue(property, entry.getValue()));
      }

      return stringbuilder.toString();
   }

   private static <T extends Comparable<T>> String getValue(Property<T> p_110885_, Comparable<?> p_110886_) {
      return p_110885_.getName((T)p_110886_);
   }
}
