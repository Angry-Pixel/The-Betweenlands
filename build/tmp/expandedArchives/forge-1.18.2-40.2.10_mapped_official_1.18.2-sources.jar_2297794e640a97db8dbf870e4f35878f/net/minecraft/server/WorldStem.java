package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;

public record WorldStem(CloseableResourceManager resourceManager, ReloadableServerResources dataPackResources, RegistryAccess.Frozen registryAccess, WorldData worldData) implements AutoCloseable {
   public static CompletableFuture<WorldStem> load(WorldStem.InitConfig p_206912_, WorldStem.DataPackConfigSupplier p_206913_, WorldStem.WorldDataSupplier p_206914_, Executor p_206915_, Executor p_206916_) {
      try {
         DataPackConfig datapackconfig = p_206913_.get();
         DataPackConfig datapackconfig1 = MinecraftServer.configurePackRepository(p_206912_.packRepository(), datapackconfig, p_206912_.safeMode());
         List<PackResources> list = p_206912_.packRepository().openAllSelected();
         CloseableResourceManager closeableresourcemanager = new MultiPackResourceManager(PackType.SERVER_DATA, list);
         Pair<WorldData, RegistryAccess.Frozen> pair = p_206914_.get(closeableresourcemanager, datapackconfig1);
         WorldData worlddata = pair.getFirst();
         RegistryAccess.Frozen registryaccess$frozen = pair.getSecond();
         return ReloadableServerResources.loadResources(closeableresourcemanager, registryaccess$frozen, p_206912_.commandSelection(), p_206912_.functionCompilationLevel(), p_206915_, p_206916_).whenComplete((p_206909_, p_206910_) -> {
            if (p_206910_ != null) {
               closeableresourcemanager.close();
            }

         }).thenApply((p_206906_) -> {
            return new WorldStem(closeableresourcemanager, p_206906_, registryaccess$frozen, worlddata);
         });
      } catch (Exception exception) {
         return CompletableFuture.failedFuture(exception);
      }
   }

   public void close() {
      this.resourceManager.close();
   }

   public void updateGlobals() {
      this.dataPackResources.updateRegistryTags(this.registryAccess);
   }

   @FunctionalInterface
   public interface DataPackConfigSupplier extends Supplier<DataPackConfig> {
      static WorldStem.DataPackConfigSupplier loadFromWorld(LevelStorageSource.LevelStorageAccess p_206929_) {
         return () -> {
            DataPackConfig datapackconfig = p_206929_.getDataPacks();
            if (datapackconfig == null) {
               throw new IllegalStateException("Failed to load data pack config");
            } else {
               return datapackconfig;
            }
         };
      }
   }

   public static record InitConfig(PackRepository packRepository, Commands.CommandSelection commandSelection, int functionCompilationLevel, boolean safeMode) {
   }

   @FunctionalInterface
   public interface WorldDataSupplier {
      Pair<WorldData, RegistryAccess.Frozen> get(ResourceManager p_206952_, DataPackConfig p_206953_);

      static WorldStem.WorldDataSupplier loadFromWorld(LevelStorageSource.LevelStorageAccess p_206955_) {
         return (p_206949_, p_206950_) -> {
            RegistryAccess.Writable registryaccess$writable = RegistryAccess.builtinCopy();
            DynamicOps<Tag> dynamicops = RegistryOps.createAndLoad(NbtOps.INSTANCE, registryaccess$writable, p_206949_);
            WorldData worlddata = p_206955_.getDataTag(dynamicops, p_206950_, registryaccess$writable.allElementsLifecycle());
            if (worlddata == null) {
               throw new IllegalStateException("Failed to load world");
            } else {
               return Pair.of(worlddata, registryaccess$writable.freeze());
            }
         };
      }
   }
}