package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MultiVariant implements UnbakedModel {
   private final List<Variant> variants;

   public MultiVariant(List<Variant> p_111847_) {
      this.variants = p_111847_;
   }

   public List<Variant> getVariants() {
      return this.variants;
   }

   public boolean equals(Object p_111862_) {
      if (this == p_111862_) {
         return true;
      } else if (p_111862_ instanceof MultiVariant) {
         MultiVariant multivariant = (MultiVariant)p_111862_;
         return this.variants.equals(multivariant.variants);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.variants.hashCode();
   }

   public Collection<ResourceLocation> getDependencies() {
      return this.getVariants().stream().map(Variant::getModelLocation).collect(Collectors.toSet());
   }

   public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> p_111855_, Set<Pair<String, String>> p_111856_) {
      return this.getVariants().stream().map(Variant::getModelLocation).distinct().flatMap((p_111860_) -> {
         return p_111855_.apply(p_111860_).getMaterials(p_111855_, p_111856_).stream();
      }).collect(Collectors.toSet());
   }

   @Nullable
   public BakedModel bake(ModelBakery p_111850_, Function<Material, TextureAtlasSprite> p_111851_, ModelState p_111852_, ResourceLocation p_111853_) {
      if (this.getVariants().isEmpty()) {
         return null;
      } else {
         WeightedBakedModel.Builder weightedbakedmodel$builder = new WeightedBakedModel.Builder();

         for(Variant variant : this.getVariants()) {
            BakedModel bakedmodel = p_111850_.bake(variant.getModelLocation(), variant, p_111851_);
            weightedbakedmodel$builder.add(bakedmodel, variant.getWeight());
         }

         return weightedbakedmodel$builder.build();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<MultiVariant> {
      public MultiVariant deserialize(JsonElement p_111867_, Type p_111868_, JsonDeserializationContext p_111869_) throws JsonParseException {
         List<Variant> list = Lists.newArrayList();
         if (p_111867_.isJsonArray()) {
            JsonArray jsonarray = p_111867_.getAsJsonArray();
            if (jsonarray.size() == 0) {
               throw new JsonParseException("Empty variant array");
            }

            for(JsonElement jsonelement : jsonarray) {
               list.add(p_111869_.deserialize(jsonelement, Variant.class));
            }
         } else {
            list.add(p_111869_.deserialize(p_111867_, Variant.class));
         }

         return new MultiVariant(list);
      }
   }
}
