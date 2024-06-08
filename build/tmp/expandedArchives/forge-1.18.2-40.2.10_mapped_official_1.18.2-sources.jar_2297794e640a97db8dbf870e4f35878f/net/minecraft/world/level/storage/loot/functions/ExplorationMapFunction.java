package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import java.util.Locale;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ConfiguredStructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ExplorationMapFunction extends LootItemConditionalFunction {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final TagKey<ConfiguredStructureFeature<?, ?>> DEFAULT_FEATURE = ConfiguredStructureTags.ON_TREASURE_MAPS;
   public static final String DEFAULT_DECORATION_NAME = "mansion";
   public static final MapDecoration.Type DEFAULT_DECORATION = MapDecoration.Type.MANSION;
   public static final byte DEFAULT_ZOOM = 2;
   public static final int DEFAULT_SEARCH_RADIUS = 50;
   public static final boolean DEFAULT_SKIP_EXISTING = true;
   final TagKey<ConfiguredStructureFeature<?, ?>> destination;
   final MapDecoration.Type mapDecoration;
   final byte zoom;
   final int searchRadius;
   final boolean skipKnownStructures;

   ExplorationMapFunction(LootItemCondition[] p_210652_, TagKey<ConfiguredStructureFeature<?, ?>> p_210653_, MapDecoration.Type p_210654_, byte p_210655_, int p_210656_, boolean p_210657_) {
      super(p_210652_);
      this.destination = p_210653_;
      this.mapDecoration = p_210654_;
      this.zoom = p_210655_;
      this.searchRadius = p_210656_;
      this.skipKnownStructures = p_210657_;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.EXPLORATION_MAP;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.ORIGIN);
   }

   public ItemStack run(ItemStack p_80547_, LootContext p_80548_) {
      if (!p_80547_.is(Items.MAP)) {
         return p_80547_;
      } else {
         Vec3 vec3 = p_80548_.getParamOrNull(LootContextParams.ORIGIN);
         if (vec3 != null) {
            ServerLevel serverlevel = p_80548_.getLevel();
            BlockPos blockpos = serverlevel.findNearestMapFeature(this.destination, new BlockPos(vec3), this.searchRadius, this.skipKnownStructures);
            if (blockpos != null) {
               ItemStack itemstack = MapItem.create(serverlevel, blockpos.getX(), blockpos.getZ(), this.zoom, true, true);
               MapItem.renderBiomePreviewMap(serverlevel, itemstack);
               MapItemSavedData.addTargetDecoration(itemstack, blockpos, "+", this.mapDecoration);
               return itemstack;
            }
         }

         return p_80547_;
      }
   }

   public static ExplorationMapFunction.Builder makeExplorationMap() {
      return new ExplorationMapFunction.Builder();
   }

   public static class Builder extends LootItemConditionalFunction.Builder<ExplorationMapFunction.Builder> {
      private TagKey<ConfiguredStructureFeature<?, ?>> destination = ExplorationMapFunction.DEFAULT_FEATURE;
      private MapDecoration.Type mapDecoration = ExplorationMapFunction.DEFAULT_DECORATION;
      private byte zoom = 2;
      private int searchRadius = 50;
      private boolean skipKnownStructures = true;

      protected ExplorationMapFunction.Builder getThis() {
         return this;
      }

      public ExplorationMapFunction.Builder setDestination(TagKey<ConfiguredStructureFeature<?, ?>> p_210659_) {
         this.destination = p_210659_;
         return this;
      }

      public ExplorationMapFunction.Builder setMapDecoration(MapDecoration.Type p_80574_) {
         this.mapDecoration = p_80574_;
         return this;
      }

      public ExplorationMapFunction.Builder setZoom(byte p_80570_) {
         this.zoom = p_80570_;
         return this;
      }

      public ExplorationMapFunction.Builder setSearchRadius(int p_165206_) {
         this.searchRadius = p_165206_;
         return this;
      }

      public ExplorationMapFunction.Builder setSkipKnownStructures(boolean p_80576_) {
         this.skipKnownStructures = p_80576_;
         return this;
      }

      public LootItemFunction build() {
         return new ExplorationMapFunction(this.getConditions(), this.destination, this.mapDecoration, this.zoom, this.searchRadius, this.skipKnownStructures);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<ExplorationMapFunction> {
      public void serialize(JsonObject p_80587_, ExplorationMapFunction p_80588_, JsonSerializationContext p_80589_) {
         super.serialize(p_80587_, p_80588_, p_80589_);
         if (!p_80588_.destination.equals(ExplorationMapFunction.DEFAULT_FEATURE)) {
            p_80587_.addProperty("destination", p_80588_.destination.location().toString());
         }

         if (p_80588_.mapDecoration != ExplorationMapFunction.DEFAULT_DECORATION) {
            p_80587_.add("decoration", p_80589_.serialize(p_80588_.mapDecoration.toString().toLowerCase(Locale.ROOT)));
         }

         if (p_80588_.zoom != 2) {
            p_80587_.addProperty("zoom", p_80588_.zoom);
         }

         if (p_80588_.searchRadius != 50) {
            p_80587_.addProperty("search_radius", p_80588_.searchRadius);
         }

         if (!p_80588_.skipKnownStructures) {
            p_80587_.addProperty("skip_existing_chunks", p_80588_.skipKnownStructures);
         }

      }

      public ExplorationMapFunction deserialize(JsonObject p_80583_, JsonDeserializationContext p_80584_, LootItemCondition[] p_80585_) {
         TagKey<ConfiguredStructureFeature<?, ?>> tagkey = readStructure(p_80583_);
         String s = p_80583_.has("decoration") ? GsonHelper.getAsString(p_80583_, "decoration") : "mansion";
         MapDecoration.Type mapdecoration$type = ExplorationMapFunction.DEFAULT_DECORATION;

         try {
            mapdecoration$type = MapDecoration.Type.valueOf(s.toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException illegalargumentexception) {
            ExplorationMapFunction.LOGGER.error("Error while parsing loot table decoration entry. Found {}. Defaulting to {}", s, ExplorationMapFunction.DEFAULT_DECORATION);
         }

         byte b0 = GsonHelper.getAsByte(p_80583_, "zoom", (byte)2);
         int i = GsonHelper.getAsInt(p_80583_, "search_radius", 50);
         boolean flag = GsonHelper.getAsBoolean(p_80583_, "skip_existing_chunks", true);
         return new ExplorationMapFunction(p_80585_, tagkey, mapdecoration$type, b0, i, flag);
      }

      private static TagKey<ConfiguredStructureFeature<?, ?>> readStructure(JsonObject p_210661_) {
         if (p_210661_.has("destination")) {
            String s = GsonHelper.getAsString(p_210661_, "destination");
            return TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation(s));
         } else {
            return ExplorationMapFunction.DEFAULT_FEATURE;
         }
      }
   }
}