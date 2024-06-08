package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;

public interface ResourceManager extends ResourceProvider {
   Set<String> getNamespaces();

   boolean hasResource(ResourceLocation p_10729_);

   List<Resource> getResources(ResourceLocation p_10730_) throws IOException;

   Collection<ResourceLocation> listResources(String p_10726_, Predicate<String> p_10727_);

   Stream<PackResources> listPacks();

   public static enum Empty implements ResourceManager {
      INSTANCE;

      public Set<String> getNamespaces() {
         return ImmutableSet.of();
      }

      public Resource getResource(ResourceLocation p_10742_) throws IOException {
         throw new FileNotFoundException(p_10742_.toString());
      }

      public boolean hasResource(ResourceLocation p_10745_) {
         return false;
      }

      public List<Resource> getResources(ResourceLocation p_10747_) {
         return ImmutableList.of();
      }

      public Collection<ResourceLocation> listResources(String p_10739_, Predicate<String> p_10740_) {
         return ImmutableSet.of();
      }

      public Stream<PackResources> listPacks() {
         return Stream.of();
      }
   }
}