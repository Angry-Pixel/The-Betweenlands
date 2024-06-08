package net.minecraft.server.packs.resources;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.Unit;
import org.slf4j.Logger;

public class ReloadableResourceManager implements ResourceManager, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private CloseableResourceManager resources;
   private final List<PreparableReloadListener> listeners = Lists.newArrayList();
   private final PackType type;

   public ReloadableResourceManager(PackType p_203820_) {
      this.type = p_203820_;
      this.resources = new MultiPackResourceManager(p_203820_, List.of());
   }

   public void close() {
      this.resources.close();
   }

   public void registerReloadListener(PreparableReloadListener p_10714_) {
      this.listeners.add(p_10714_);
   }

   public ReloadInstance createReload(Executor p_143930_, Executor p_143931_, CompletableFuture<Unit> p_143932_, List<PackResources> p_143933_) {
      LOGGER.info("Reloading ResourceManager: {}", LogUtils.defer(() -> {
         return p_143933_.stream().map(PackResources::getName).collect(Collectors.joining(", "));
      }));
      this.resources.close();
      this.resources = new MultiPackResourceManager(this.type, p_143933_);
      return SimpleReloadInstance.create(this.resources, this.listeners, p_143930_, p_143931_, p_143932_, LOGGER.isDebugEnabled());
   }

   public Resource getResource(ResourceLocation p_203833_) throws IOException {
      return this.resources.getResource(p_203833_);
   }

   public Set<String> getNamespaces() {
      return this.resources.getNamespaces();
   }

   public boolean hasResource(ResourceLocation p_203828_) {
      return this.resources.hasResource(p_203828_);
   }

   public List<Resource> getResources(ResourceLocation p_203831_) throws IOException {
      return this.resources.getResources(p_203831_);
   }

   public Collection<ResourceLocation> listResources(String p_203823_, Predicate<String> p_203824_) {
      return this.resources.listResources(p_203823_, p_203824_);
   }

   public Stream<PackResources> listPacks() {
      return this.resources.listPacks();
   }

   public void registerReloadListenerIfNotPresent(PreparableReloadListener listener) {
      if (!this.listeners.contains(listener)) {
         this.registerReloadListener(listener);
      }
   }
}
