package net.minecraft.data.info;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.slf4j.Logger;

public class WorldgenRegistryDumpReport implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;

   public WorldgenRegistryDumpReport(DataGenerator p_194679_) {
      this.generator = p_194679_;
   }

   public void run(HashCache p_194682_) {
      Path path = this.generator.getOutputFolder();
      RegistryAccess registryaccess = RegistryAccess.BUILTIN.get();
      int i = 0;
      Registry<LevelStem> registry = DimensionType.defaultDimensions(registryaccess, 0L, false);
      ChunkGenerator chunkgenerator = WorldGenSettings.makeDefaultOverworld(registryaccess, 0L, false);
      Registry<LevelStem> registry1 = WorldGenSettings.withOverworld(registryaccess.ownedRegistryOrThrow(Registry.DIMENSION_TYPE_REGISTRY), registry, chunkgenerator);
      DynamicOps<JsonElement> dynamicops = RegistryOps.create(JsonOps.INSTANCE, registryaccess);
      RegistryAccess.knownRegistries().forEach((p_194713_) -> {
         dumpRegistryCap(p_194682_, path, registryaccess, dynamicops, p_194713_);
      });
      dumpRegistry(path, p_194682_, dynamicops, Registry.LEVEL_STEM_REGISTRY, registry1, LevelStem.CODEC);
   }

   private static <T> void dumpRegistryCap(HashCache p_194684_, Path p_194685_, RegistryAccess p_194686_, DynamicOps<JsonElement> p_194687_, RegistryAccess.RegistryData<T> p_194688_) {
      dumpRegistry(p_194685_, p_194684_, p_194687_, p_194688_.key(), p_194686_.ownedRegistryOrThrow(p_194688_.key()), p_194688_.codec());
   }

   private static <E, T extends Registry<E>> void dumpRegistry(Path p_194698_, HashCache p_194699_, DynamicOps<JsonElement> p_194700_, ResourceKey<? extends T> p_194701_, T p_194702_, Encoder<E> p_194703_) {
      for(Entry<ResourceKey<E>, E> entry : p_194702_.entrySet()) {
         Path path = createPath(p_194698_, p_194701_.location(), entry.getKey().location());
         dumpValue(path, p_194699_, p_194700_, p_194703_, entry.getValue());
      }

   }

   private static <E> void dumpValue(Path p_194692_, HashCache p_194693_, DynamicOps<JsonElement> p_194694_, Encoder<E> p_194695_, E p_194696_) {
      try {
         Optional<JsonElement> optional = p_194695_.encodeStart(p_194694_, p_194696_).resultOrPartial((p_206405_) -> {
            LOGGER.error("Couldn't serialize element {}: {}", p_194692_, p_206405_);
         });
         if (optional.isPresent()) {
            DataProvider.save(GSON, p_194693_, optional.get(), p_194692_);
         }
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't save element {}", p_194692_, ioexception);
      }

   }

   private static Path createPath(Path p_194705_, ResourceLocation p_194706_, ResourceLocation p_194707_) {
      return resolveTopPath(p_194705_).resolve(p_194707_.getNamespace()).resolve(p_194706_.getPath()).resolve(p_194707_.getPath() + ".json");
   }

   private static Path resolveTopPath(Path p_194690_) {
      return p_194690_.resolve("reports").resolve("worldgen");
   }

   public String getName() {
      return "Worldgen";
   }
}