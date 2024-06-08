package net.minecraft.resources;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;

public class RegistryOps<T> extends DelegatingOps<T> {
   private final Optional<RegistryLoader.Bound> loader;
   private final RegistryAccess registryAccess;
   private final DynamicOps<JsonElement> asJson;

   public static <T> RegistryOps<T> create(DynamicOps<T> p_206822_, RegistryAccess p_206823_) {
      return new RegistryOps<>(p_206822_, p_206823_, Optional.empty());
   }

   public static <T> RegistryOps<T> createAndLoad(DynamicOps<T> p_206814_, RegistryAccess.Writable p_206815_, ResourceManager p_206816_) {
      return createAndLoad(p_206814_, p_206815_, RegistryResourceAccess.forResourceManager(p_206816_));
   }

   public static <T> RegistryOps<T> createAndLoad(DynamicOps<T> p_206818_, RegistryAccess.Writable p_206819_, RegistryResourceAccess p_206820_) {
      RegistryLoader registryloader = new RegistryLoader(p_206820_);
      RegistryOps<T> registryops = new RegistryOps<>(p_206818_, p_206819_, Optional.of(registryloader.bind(p_206819_)));
      RegistryAccess.load(p_206819_, registryops.getAsJson(), registryloader);
      return registryops;
   }

   private RegistryOps(DynamicOps<T> p_206809_, RegistryAccess p_206810_, Optional<RegistryLoader.Bound> p_206811_) {
      super(p_206809_);
      this.loader = p_206811_;
      this.registryAccess = p_206810_;
      this.asJson = p_206809_ == JsonOps.INSTANCE ? (DynamicOps<JsonElement>) this : new RegistryOps<>(JsonOps.INSTANCE, p_206810_, p_206811_);
   }

   public <E> Optional<? extends Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> p_206827_) {
      return this.registryAccess.registry(p_206827_);
   }

   public Optional<RegistryLoader.Bound> registryLoader() {
      return this.loader;
   }

   public DynamicOps<JsonElement> getAsJson() {
      return this.asJson;
   }

   public static <E> MapCodec<Registry<E>> retrieveRegistry(ResourceKey<? extends Registry<? extends E>> p_206833_) {
      return ExtraCodecs.retrieveContext((p_206830_) -> {
         if (p_206830_ instanceof RegistryOps) {
            RegistryOps<?> registryops = (RegistryOps)p_206830_;
            return registryops.registry(p_206833_).map((p_206825_) -> {
               return DataResult.<Registry<E>>success(p_206825_, p_206825_.elementsLifecycle());
            }).orElseGet(() -> {
               return DataResult.error("Unknown registry: " + p_206833_);
            });
         } else {
            return DataResult.error("Not a registry ops");
         }
      });
   }
}