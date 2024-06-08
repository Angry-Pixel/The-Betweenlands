package net.minecraft.server.packs.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

public class MultiPackResourceManager implements CloseableResourceManager {
   private final Map<String, FallbackResourceManager> namespacedManagers;
   private final List<PackResources> packs;

   public MultiPackResourceManager(PackType p_203797_, List<PackResources> p_203798_) {
      this.packs = List.copyOf(p_203798_);
      Map<String, FallbackResourceManager> map = new HashMap<>();

      for(PackResources packresources : p_203798_) {
         for(String s : packresources.getNamespaces(p_203797_)) {
            map.computeIfAbsent(s, (p_203802_) -> {
               return new FallbackResourceManager(p_203797_, p_203802_);
            }).add(packresources);
         }
      }

      this.namespacedManagers = map;
   }

   public Set<String> getNamespaces() {
      return this.namespacedManagers.keySet();
   }

   public Resource getResource(ResourceLocation p_203813_) throws IOException {
      ResourceManager resourcemanager = this.namespacedManagers.get(p_203813_.getNamespace());
      if (resourcemanager != null) {
         return resourcemanager.getResource(p_203813_);
      } else {
         throw new FileNotFoundException(p_203813_.toString());
      }
   }

   public boolean hasResource(ResourceLocation p_203807_) {
      ResourceManager resourcemanager = this.namespacedManagers.get(p_203807_.getNamespace());
      return resourcemanager != null ? resourcemanager.hasResource(p_203807_) : false;
   }

   public List<Resource> getResources(ResourceLocation p_203810_) throws IOException {
      ResourceManager resourcemanager = this.namespacedManagers.get(p_203810_.getNamespace());
      if (resourcemanager != null) {
         return resourcemanager.getResources(p_203810_);
      } else {
         throw new FileNotFoundException(p_203810_.toString());
      }
   }

   public Collection<ResourceLocation> listResources(String p_203804_, Predicate<String> p_203805_) {
      Set<ResourceLocation> set = Sets.newHashSet();

      for(FallbackResourceManager fallbackresourcemanager : this.namespacedManagers.values()) {
         set.addAll(fallbackresourcemanager.listResources(p_203804_, p_203805_));
      }

      List<ResourceLocation> list = Lists.newArrayList(set);
      Collections.sort(list);
      return list;
   }

   public Stream<PackResources> listPacks() {
      return this.packs.stream();
   }

   public void close() {
      this.packs.forEach(PackResources::close);
   }
}