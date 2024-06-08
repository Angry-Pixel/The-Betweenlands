package net.minecraft.server.packs.repository;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

public class PackRepository implements AutoCloseable {
   private final Set<RepositorySource> sources;
   private Map<String, Pack> available = ImmutableMap.of();
   private List<Pack> selected = ImmutableList.of();
   private final Pack.PackConstructor constructor;

   public PackRepository(Pack.PackConstructor p_10502_, RepositorySource... p_10503_) {
      this.constructor = p_10502_;
      this.sources = new java.util.HashSet<>(java.util.Arrays.asList(p_10503_));
   }

   public PackRepository(PackType p_143890_, RepositorySource... p_143891_) {
      this((p_143894_, p_143895_, p_143896_, p_143897_, p_143898_, p_143899_, p_143900_, hidden) -> {
         return new Pack(p_143894_, p_143895_, p_143896_, p_143897_, p_143898_, p_143890_, p_143899_, p_143900_, hidden);
      }, p_143891_);
      net.minecraftforge.fml.ModLoader.get().postEvent(new net.minecraftforge.event.AddPackFindersEvent(p_143890_, sources::add));
   }

   public void reload() {
      List<String> list = this.selected.stream().map(Pack::getId).collect(ImmutableList.toImmutableList());
      this.close();
      this.available = this.discoverAvailable();
      this.selected = this.rebuildSelected(list);
   }

   private Map<String, Pack> discoverAvailable() {
      Map<String, Pack> map = Maps.newTreeMap();

      for(RepositorySource repositorysource : this.sources) {
         repositorysource.loadPacks((p_143903_) -> {
            map.put(p_143903_.getId(), p_143903_);
         }, this.constructor);
      }

      return ImmutableMap.copyOf(map);
   }

   public void setSelected(Collection<String> p_10510_) {
      this.selected = this.rebuildSelected(p_10510_);
   }

   private List<Pack> rebuildSelected(Collection<String> p_10518_) {
      List<Pack> list = this.getAvailablePacks(p_10518_).collect(Collectors.toList());

      for(Pack pack : this.available.values()) {
         if (pack.isRequired() && !list.contains(pack)) {
            pack.getDefaultPosition().insert(list, pack, Functions.identity(), false);
         }
      }

      return ImmutableList.copyOf(list);
   }

   private Stream<Pack> getAvailablePacks(Collection<String> p_10521_) {
      return p_10521_.stream().map(this.available::get).filter(Objects::nonNull);
   }

   public Collection<String> getAvailableIds() {
      return this.available.keySet();
   }

   public Collection<Pack> getAvailablePacks() {
      return this.available.values();
   }

   public Collection<String> getSelectedIds() {
      return this.selected.stream().map(Pack::getId).collect(ImmutableSet.toImmutableSet());
   }

   public Collection<Pack> getSelectedPacks() {
      return this.selected;
   }

   @Nullable
   public Pack getPack(String p_10508_) {
      return this.available.get(p_10508_);
   }

   public synchronized void addPackFinder(RepositorySource packFinder) {
      this.sources.add(packFinder);
   }

   public void close() {
      this.available.values().forEach(Pack::close);
   }

   public boolean isAvailable(String p_10516_) {
      return this.available.containsKey(p_10516_);
   }

   public List<PackResources> openAllSelected() {
      return this.selected.stream().map(Pack::open).collect(ImmutableList.toImmutableList());
   }
}
