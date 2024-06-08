package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;

public class LootTables extends SimpleJsonResourceReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = Deserializers.createLootTableSerializer().create();
   private Map<ResourceLocation, LootTable> tables = ImmutableMap.of();
   private final PredicateManager predicateManager;

   public LootTables(PredicateManager p_79194_) {
      super(GSON, "loot_tables");
      this.predicateManager = p_79194_;
   }

   public LootTable get(ResourceLocation p_79218_) {
      return this.tables.getOrDefault(p_79218_, LootTable.EMPTY);
   }

   protected void apply(Map<ResourceLocation, JsonElement> p_79214_, ResourceManager p_79215_, ProfilerFiller p_79216_) {
      Builder<ResourceLocation, LootTable> builder = ImmutableMap.builder();
      JsonElement jsonelement = p_79214_.remove(BuiltInLootTables.EMPTY);
      if (jsonelement != null) {
         LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", (Object)BuiltInLootTables.EMPTY);
      }

      p_79214_.forEach((p_79198_, p_79199_) -> {
         try (net.minecraft.server.packs.resources.Resource res = p_79215_.getResource(getPreparedPath(p_79198_));){
            LootTable loottable = net.minecraftforge.common.ForgeHooks.loadLootTable(GSON, p_79198_, p_79199_, res == null || !res.getSourceName().equals("Default"), this);
            builder.put(p_79198_, loottable);
         } catch (Exception exception) {
            LOGGER.error("Couldn't parse loot table {}", p_79198_, exception);
         }

      });
      builder.put(BuiltInLootTables.EMPTY, LootTable.EMPTY);
      ImmutableMap<ResourceLocation, LootTable> immutablemap = builder.build();
      ValidationContext validationcontext = new ValidationContext(LootContextParamSets.ALL_PARAMS, this.predicateManager::get, immutablemap::get);
      immutablemap.forEach((p_79221_, p_79222_) -> {
         validate(validationcontext, p_79221_, p_79222_);
      });
      validationcontext.getProblems().forEach((p_79211_, p_79212_) -> {
         LOGGER.warn("Found validation problem in {}: {}", p_79211_, p_79212_);
      });
      this.tables = immutablemap;
   }

   public static void validate(ValidationContext p_79203_, ResourceLocation p_79204_, LootTable p_79205_) {
      p_79205_.validate(p_79203_.setParams(p_79205_.getParamSet()).enterTable("{" + p_79204_ + "}", p_79204_));
   }

   public static JsonElement serialize(LootTable p_79201_) {
      return GSON.toJsonTree(p_79201_);
   }

   public Set<ResourceLocation> getIds() {
      return this.tables.keySet();
   }
}
