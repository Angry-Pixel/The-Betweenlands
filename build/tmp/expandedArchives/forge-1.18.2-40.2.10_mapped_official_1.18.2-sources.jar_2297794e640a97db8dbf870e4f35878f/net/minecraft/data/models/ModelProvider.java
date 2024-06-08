package net.minecraft.data.models;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;

public class ModelProvider implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final DataGenerator generator;

   public ModelProvider(DataGenerator p_125100_) {
      this.generator = p_125100_;
   }

   public void run(HashCache p_125103_) {
      Path path = this.generator.getOutputFolder();
      Map<Block, BlockStateGenerator> map = Maps.newHashMap();
      Consumer<BlockStateGenerator> consumer = (p_125120_) -> {
         Block block = p_125120_.getBlock();
         BlockStateGenerator blockstategenerator = map.put(block, p_125120_);
         if (blockstategenerator != null) {
            throw new IllegalStateException("Duplicate blockstate definition for " + block);
         }
      };
      Map<ResourceLocation, Supplier<JsonElement>> map1 = Maps.newHashMap();
      Set<Item> set = Sets.newHashSet();
      BiConsumer<ResourceLocation, Supplier<JsonElement>> biconsumer = (p_125123_, p_125124_) -> {
         Supplier<JsonElement> supplier = map1.put(p_125123_, p_125124_);
         if (supplier != null) {
            throw new IllegalStateException("Duplicate model definition for " + p_125123_);
         }
      };
      Consumer<Item> consumer1 = set::add;
      (new BlockModelGenerators(consumer, biconsumer, consumer1)).run();
      (new ItemModelGenerators(biconsumer)).run();
      List<Block> list = Registry.BLOCK.stream().filter((p_125117_) -> {
         return !map.containsKey(p_125117_);
      }).collect(Collectors.toList());
      if (!list.isEmpty()) {
         throw new IllegalStateException("Missing blockstate definitions for: " + list);
      } else {
         Registry.BLOCK.forEach((p_125128_) -> {
            Item item = Item.BY_BLOCK.get(p_125128_);
            if (item != null) {
               if (set.contains(item)) {
                  return;
               }

               ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(item);
               if (!map1.containsKey(resourcelocation)) {
                  map1.put(resourcelocation, new DelegatedModel(ModelLocationUtils.getModelLocation(p_125128_)));
               }
            }

         });
         this.saveCollection(p_125103_, path, map, ModelProvider::createBlockStatePath);
         this.saveCollection(p_125103_, path, map1, ModelProvider::createModelPath);
      }
   }

   private <T> void saveCollection(HashCache p_125105_, Path p_125106_, Map<T, ? extends Supplier<JsonElement>> p_125107_, BiFunction<Path, T, Path> p_125108_) {
      p_125107_.forEach((p_125133_, p_125134_) -> {
         Path path = p_125108_.apply(p_125106_, p_125133_);

         try {
            DataProvider.save(GSON, p_125105_, p_125134_.get(), path);
         } catch (Exception exception) {
            LOGGER.error("Couldn't save {}", path, exception);
         }

      });
   }

   private static Path createBlockStatePath(Path p_125110_, Block p_125111_) {
      ResourceLocation resourcelocation = Registry.BLOCK.getKey(p_125111_);
      return p_125110_.resolve("assets/" + resourcelocation.getNamespace() + "/blockstates/" + resourcelocation.getPath() + ".json");
   }

   private static Path createModelPath(Path p_125113_, ResourceLocation p_125114_) {
      return p_125113_.resolve("assets/" + p_125114_.getNamespace() + "/models/" + p_125114_.getPath() + ".json");
   }

   public String getName() {
      return "Block State Definitions";
   }
}