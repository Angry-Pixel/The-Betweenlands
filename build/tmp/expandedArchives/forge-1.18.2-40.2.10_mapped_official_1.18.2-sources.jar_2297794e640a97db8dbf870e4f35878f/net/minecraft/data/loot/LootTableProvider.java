package net.minecraft.data.loot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;

public class LootTableProvider implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final DataGenerator generator;
   private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> subProviders = ImmutableList.of(Pair.of(FishingLoot::new, LootContextParamSets.FISHING), Pair.of(ChestLoot::new, LootContextParamSets.CHEST), Pair.of(EntityLoot::new, LootContextParamSets.ENTITY), Pair.of(BlockLoot::new, LootContextParamSets.BLOCK), Pair.of(PiglinBarterLoot::new, LootContextParamSets.PIGLIN_BARTER), Pair.of(GiftLoot::new, LootContextParamSets.GIFT));

   public LootTableProvider(DataGenerator p_124437_) {
      this.generator = p_124437_;
   }

   public void run(HashCache p_124444_) {
      Path path = this.generator.getOutputFolder();
      Map<ResourceLocation, LootTable> map = Maps.newHashMap();
      this.getTables().forEach((p_124458_) -> {
         p_124458_.getFirst().get().accept((p_176077_, p_176078_) -> {
            if (map.put(p_176077_, p_176078_.setParamSet(p_124458_.getSecond()).build()) != null) {
               throw new IllegalStateException("Duplicate loot table " + p_176077_);
            }
         });
      });
      ValidationContext validationcontext = new ValidationContext(LootContextParamSets.ALL_PARAMS, (p_124465_) -> {
         return null;
      }, map::get);

      validate(map, validationcontext);

      Multimap<String, String> multimap = validationcontext.getProblems();
      if (!multimap.isEmpty()) {
         multimap.forEach((p_124446_, p_124447_) -> {
            LOGGER.warn("Found validation problem in {}: {}", p_124446_, p_124447_);
         });
         throw new IllegalStateException("Failed to validate loot tables, see logs");
      } else {
         map.forEach((p_124451_, p_124452_) -> {
            Path path1 = createPath(path, p_124451_);

            try {
               DataProvider.save(GSON, p_124444_, LootTables.serialize(p_124452_), path1);
            } catch (IOException ioexception) {
               LOGGER.error("Couldn't save loot table {}", path1, ioexception);
            }

         });
      }
   }

   protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
      return subProviders;
   }

   protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
      for(ResourceLocation resourcelocation : Sets.difference(BuiltInLootTables.all(), map.keySet())) {
         validationtracker.reportProblem("Missing built-in table: " + resourcelocation);
      }

      map.forEach((p_218436_2_, p_218436_3_) -> {
         LootTables.validate(validationtracker, p_218436_2_, p_218436_3_);
      });
   }

   private static Path createPath(Path p_124454_, ResourceLocation p_124455_) {
      return p_124454_.resolve("data/" + p_124455_.getNamespace() + "/loot_tables/" + p_124455_.getPath() + ".json");
   }

   public String getName() {
      return "LootTables";
   }
}
