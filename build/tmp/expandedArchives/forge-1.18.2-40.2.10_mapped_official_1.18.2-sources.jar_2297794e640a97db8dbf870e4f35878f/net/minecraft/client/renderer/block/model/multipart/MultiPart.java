package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MultiPart implements UnbakedModel {
   private final StateDefinition<Block, BlockState> definition;
   private final List<Selector> selectors;

   public MultiPart(StateDefinition<Block, BlockState> p_111965_, List<Selector> p_111966_) {
      this.definition = p_111965_;
      this.selectors = p_111966_;
   }

   public List<Selector> getSelectors() {
      return this.selectors;
   }

   public Set<MultiVariant> getMultiVariants() {
      Set<MultiVariant> set = Sets.newHashSet();

      for(Selector selector : this.selectors) {
         set.add(selector.getVariant());
      }

      return set;
   }

   public boolean equals(Object p_111984_) {
      if (this == p_111984_) {
         return true;
      } else if (!(p_111984_ instanceof MultiPart)) {
         return false;
      } else {
         MultiPart multipart = (MultiPart)p_111984_;
         return Objects.equals(this.definition, multipart.definition) && Objects.equals(this.selectors, multipart.selectors);
      }
   }

   public int hashCode() {
      return Objects.hash(this.definition, this.selectors);
   }

   public Collection<ResourceLocation> getDependencies() {
      return this.getSelectors().stream().flatMap((p_111969_) -> {
         return p_111969_.getVariant().getDependencies().stream();
      }).collect(Collectors.toSet());
   }

   public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> p_111976_, Set<Pair<String, String>> p_111977_) {
      return this.getSelectors().stream().flatMap((p_111981_) -> {
         return p_111981_.getVariant().getMaterials(p_111976_, p_111977_).stream();
      }).collect(Collectors.toSet());
   }

   @Nullable
   public BakedModel bake(ModelBakery p_111971_, Function<Material, TextureAtlasSprite> p_111972_, ModelState p_111973_, ResourceLocation p_111974_) {
      MultiPartBakedModel.Builder multipartbakedmodel$builder = new MultiPartBakedModel.Builder();

      for(Selector selector : this.getSelectors()) {
         BakedModel bakedmodel = selector.getVariant().bake(p_111971_, p_111972_, p_111973_, p_111974_);
         if (bakedmodel != null) {
            multipartbakedmodel$builder.add(selector.getPredicate(this.definition), bakedmodel);
         }
      }

      return multipartbakedmodel$builder.build();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<MultiPart> {
      private final BlockModelDefinition.Context context;

      public Deserializer(BlockModelDefinition.Context p_111989_) {
         this.context = p_111989_;
      }

      public MultiPart deserialize(JsonElement p_111994_, Type p_111995_, JsonDeserializationContext p_111996_) throws JsonParseException {
         return new MultiPart(this.context.getDefinition(), this.getSelectors(p_111996_, p_111994_.getAsJsonArray()));
      }

      private List<Selector> getSelectors(JsonDeserializationContext p_111991_, JsonArray p_111992_) {
         List<Selector> list = Lists.newArrayList();

         for(JsonElement jsonelement : p_111992_) {
            list.add(p_111991_.deserialize(jsonelement, Selector.class));
         }

         return list;
      }
   }
}