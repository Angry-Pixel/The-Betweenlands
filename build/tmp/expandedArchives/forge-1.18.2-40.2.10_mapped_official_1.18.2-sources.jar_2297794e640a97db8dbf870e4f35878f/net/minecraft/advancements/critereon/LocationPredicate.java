package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import org.slf4j.Logger;

public class LocationPredicate {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final LocationPredicate ANY = new LocationPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, (ResourceKey<Biome>)null, (ResourceKey<ConfiguredStructureFeature<?, ?>>)null, (ResourceKey<Level>)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   private final MinMaxBounds.Doubles x;
   private final MinMaxBounds.Doubles y;
   private final MinMaxBounds.Doubles z;
   @Nullable
   private final ResourceKey<Biome> biome;
   @Nullable
   private final ResourceKey<ConfiguredStructureFeature<?, ?>> feature;
   @Nullable
   private final ResourceKey<Level> dimension;
   @Nullable
   private final Boolean smokey;
   private final LightPredicate light;
   private final BlockPredicate block;
   private final FluidPredicate fluid;

   public LocationPredicate(MinMaxBounds.Doubles p_207916_, MinMaxBounds.Doubles p_207917_, MinMaxBounds.Doubles p_207918_, @Nullable ResourceKey<Biome> p_207919_, @Nullable ResourceKey<ConfiguredStructureFeature<?, ?>> p_207920_, @Nullable ResourceKey<Level> p_207921_, @Nullable Boolean p_207922_, LightPredicate p_207923_, BlockPredicate p_207924_, FluidPredicate p_207925_) {
      this.x = p_207916_;
      this.y = p_207917_;
      this.z = p_207918_;
      this.biome = p_207919_;
      this.feature = p_207920_;
      this.dimension = p_207921_;
      this.smokey = p_207922_;
      this.light = p_207923_;
      this.block = p_207924_;
      this.fluid = p_207925_;
   }

   public static LocationPredicate inBiome(ResourceKey<Biome> p_52635_) {
      return new LocationPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, p_52635_, (ResourceKey<ConfiguredStructureFeature<?, ?>>)null, (ResourceKey<Level>)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public static LocationPredicate inDimension(ResourceKey<Level> p_52639_) {
      return new LocationPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, (ResourceKey<Biome>)null, (ResourceKey<ConfiguredStructureFeature<?, ?>>)null, p_52639_, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public static LocationPredicate inFeature(ResourceKey<ConfiguredStructureFeature<?, ?>> p_207929_) {
      return new LocationPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, (ResourceKey<Biome>)null, p_207929_, (ResourceKey<Level>)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public static LocationPredicate atYLocation(MinMaxBounds.Doubles p_187443_) {
      return new LocationPredicate(MinMaxBounds.Doubles.ANY, p_187443_, MinMaxBounds.Doubles.ANY, (ResourceKey<Biome>)null, (ResourceKey<ConfiguredStructureFeature<?, ?>>)null, (ResourceKey<Level>)null, (Boolean)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public boolean matches(ServerLevel p_52618_, double p_52619_, double p_52620_, double p_52621_) {
      if (!this.x.matches(p_52619_)) {
         return false;
      } else if (!this.y.matches(p_52620_)) {
         return false;
      } else if (!this.z.matches(p_52621_)) {
         return false;
      } else if (this.dimension != null && this.dimension != p_52618_.dimension()) {
         return false;
      } else {
         BlockPos blockpos = new BlockPos(p_52619_, p_52620_, p_52621_);
         boolean flag = p_52618_.isLoaded(blockpos);
         if (this.biome == null || flag && p_52618_.getBiome(blockpos).is(this.biome)) {
            if (this.feature == null || flag && p_52618_.structureFeatureManager().getStructureWithPieceAt(blockpos, this.feature).isValid()) {
               if (this.smokey == null || flag && this.smokey == CampfireBlock.isSmokeyPos(p_52618_, blockpos)) {
                  if (!this.light.matches(p_52618_, blockpos)) {
                     return false;
                  } else if (!this.block.matches(p_52618_, blockpos)) {
                     return false;
                  } else {
                     return this.fluid.matches(p_52618_, blockpos);
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (!this.x.isAny() || !this.y.isAny() || !this.z.isAny()) {
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.add("x", this.x.serializeToJson());
            jsonobject1.add("y", this.y.serializeToJson());
            jsonobject1.add("z", this.z.serializeToJson());
            jsonobject.add("position", jsonobject1);
         }

         if (this.dimension != null) {
            Level.RESOURCE_KEY_CODEC.encodeStart(JsonOps.INSTANCE, this.dimension).resultOrPartial(LOGGER::error).ifPresent((p_52633_) -> {
               jsonobject.add("dimension", p_52633_);
            });
         }

         if (this.feature != null) {
            jsonobject.addProperty("feature", this.feature.location().toString());
         }

         if (this.biome != null) {
            jsonobject.addProperty("biome", this.biome.location().toString());
         }

         if (this.smokey != null) {
            jsonobject.addProperty("smokey", this.smokey);
         }

         jsonobject.add("light", this.light.serializeToJson());
         jsonobject.add("block", this.block.serializeToJson());
         jsonobject.add("fluid", this.fluid.serializeToJson());
         return jsonobject;
      }
   }

   public static LocationPredicate fromJson(@Nullable JsonElement p_52630_) {
      if (p_52630_ != null && !p_52630_.isJsonNull()) {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_52630_, "location");
         JsonObject jsonobject1 = GsonHelper.getAsJsonObject(jsonobject, "position", new JsonObject());
         MinMaxBounds.Doubles minmaxbounds$doubles = MinMaxBounds.Doubles.fromJson(jsonobject1.get("x"));
         MinMaxBounds.Doubles minmaxbounds$doubles1 = MinMaxBounds.Doubles.fromJson(jsonobject1.get("y"));
         MinMaxBounds.Doubles minmaxbounds$doubles2 = MinMaxBounds.Doubles.fromJson(jsonobject1.get("z"));
         ResourceKey<Level> resourcekey = jsonobject.has("dimension") ? ResourceLocation.CODEC.parse(JsonOps.INSTANCE, jsonobject.get("dimension")).resultOrPartial(LOGGER::error).map((p_52637_) -> {
            return ResourceKey.create(Registry.DIMENSION_REGISTRY, p_52637_);
         }).orElse((ResourceKey<Level>)null) : null;
         ResourceKey<ConfiguredStructureFeature<?, ?>> resourcekey1 = jsonobject.has("feature") ? ResourceLocation.CODEC.parse(JsonOps.INSTANCE, jsonobject.get("feature")).resultOrPartial(LOGGER::error).map((p_207927_) -> {
            return ResourceKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, p_207927_);
         }).orElse((ResourceKey<ConfiguredStructureFeature<?, ?>>)null) : null;
         ResourceKey<Biome> resourcekey2 = null;
         if (jsonobject.has("biome")) {
            ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(jsonobject, "biome"));
            resourcekey2 = ResourceKey.create(Registry.BIOME_REGISTRY, resourcelocation);
         }

         Boolean obool = jsonobject.has("smokey") ? jsonobject.get("smokey").getAsBoolean() : null;
         LightPredicate lightpredicate = LightPredicate.fromJson(jsonobject.get("light"));
         BlockPredicate blockpredicate = BlockPredicate.fromJson(jsonobject.get("block"));
         FluidPredicate fluidpredicate = FluidPredicate.fromJson(jsonobject.get("fluid"));
         return new LocationPredicate(minmaxbounds$doubles, minmaxbounds$doubles1, minmaxbounds$doubles2, resourcekey2, resourcekey1, resourcekey, obool, lightpredicate, blockpredicate, fluidpredicate);
      } else {
         return ANY;
      }
   }

   public static class Builder {
      private MinMaxBounds.Doubles x = MinMaxBounds.Doubles.ANY;
      private MinMaxBounds.Doubles y = MinMaxBounds.Doubles.ANY;
      private MinMaxBounds.Doubles z = MinMaxBounds.Doubles.ANY;
      @Nullable
      private ResourceKey<Biome> biome;
      @Nullable
      private ResourceKey<ConfiguredStructureFeature<?, ?>> feature;
      @Nullable
      private ResourceKey<Level> dimension;
      @Nullable
      private Boolean smokey;
      private LightPredicate light = LightPredicate.ANY;
      private BlockPredicate block = BlockPredicate.ANY;
      private FluidPredicate fluid = FluidPredicate.ANY;

      public static LocationPredicate.Builder location() {
         return new LocationPredicate.Builder();
      }

      public LocationPredicate.Builder setX(MinMaxBounds.Doubles p_153971_) {
         this.x = p_153971_;
         return this;
      }

      public LocationPredicate.Builder setY(MinMaxBounds.Doubles p_153975_) {
         this.y = p_153975_;
         return this;
      }

      public LocationPredicate.Builder setZ(MinMaxBounds.Doubles p_153979_) {
         this.z = p_153979_;
         return this;
      }

      public LocationPredicate.Builder setBiome(@Nullable ResourceKey<Biome> p_52657_) {
         this.biome = p_52657_;
         return this;
      }

      public LocationPredicate.Builder setFeature(@Nullable ResourceKey<ConfiguredStructureFeature<?, ?>> p_207931_) {
         this.feature = p_207931_;
         return this;
      }

      public LocationPredicate.Builder setDimension(@Nullable ResourceKey<Level> p_153977_) {
         this.dimension = p_153977_;
         return this;
      }

      public LocationPredicate.Builder setLight(LightPredicate p_153969_) {
         this.light = p_153969_;
         return this;
      }

      public LocationPredicate.Builder setBlock(BlockPredicate p_52653_) {
         this.block = p_52653_;
         return this;
      }

      public LocationPredicate.Builder setFluid(FluidPredicate p_153967_) {
         this.fluid = p_153967_;
         return this;
      }

      public LocationPredicate.Builder setSmokey(Boolean p_52655_) {
         this.smokey = p_52655_;
         return this;
      }

      public LocationPredicate build() {
         return new LocationPredicate(this.x, this.y, this.z, this.biome, this.feature, this.dimension, this.smokey, this.light, this.block, this.fluid);
      }
   }
}