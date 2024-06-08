package net.minecraft.world.entity.ai.gossip;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.SerializableUUID;
import net.minecraft.util.VisibleForDebug;

public class GossipContainer {
   public static final int DISCARD_THRESHOLD = 2;
   private final Map<UUID, GossipContainer.EntityGossips> gossips = Maps.newHashMap();

   @VisibleForDebug
   public Map<UUID, Object2IntMap<GossipType>> getGossipEntries() {
      Map<UUID, Object2IntMap<GossipType>> map = Maps.newHashMap();
      this.gossips.keySet().forEach((p_148167_) -> {
         GossipContainer.EntityGossips gossipcontainer$entitygossips = this.gossips.get(p_148167_);
         map.put(p_148167_, gossipcontainer$entitygossips.entries);
      });
      return map;
   }

   public void decay() {
      Iterator<GossipContainer.EntityGossips> iterator = this.gossips.values().iterator();

      while(iterator.hasNext()) {
         GossipContainer.EntityGossips gossipcontainer$entitygossips = iterator.next();
         gossipcontainer$entitygossips.decay();
         if (gossipcontainer$entitygossips.isEmpty()) {
            iterator.remove();
         }
      }

   }

   private Stream<GossipContainer.GossipEntry> unpack() {
      return this.gossips.entrySet().stream().flatMap((p_26185_) -> {
         return p_26185_.getValue().unpack(p_26185_.getKey());
      });
   }

   private Collection<GossipContainer.GossipEntry> selectGossipsForTransfer(Random p_26187_, int p_26188_) {
      List<GossipContainer.GossipEntry> list = this.unpack().collect(Collectors.toList());
      if (list.isEmpty()) {
         return Collections.emptyList();
      } else {
         int[] aint = new int[list.size()];
         int i = 0;

         for(int j = 0; j < list.size(); ++j) {
            GossipContainer.GossipEntry gossipcontainer$gossipentry = list.get(j);
            i += Math.abs(gossipcontainer$gossipentry.weightedValue());
            aint[j] = i - 1;
         }

         Set<GossipContainer.GossipEntry> set = Sets.newIdentityHashSet();

         for(int i1 = 0; i1 < p_26188_; ++i1) {
            int k = p_26187_.nextInt(i);
            int l = Arrays.binarySearch(aint, k);
            set.add(list.get(l < 0 ? -l - 1 : l));
         }

         return set;
      }
   }

   private GossipContainer.EntityGossips getOrCreate(UUID p_26190_) {
      return this.gossips.computeIfAbsent(p_26190_, (p_26202_) -> {
         return new GossipContainer.EntityGossips();
      });
   }

   public void transferFrom(GossipContainer p_26164_, Random p_26165_, int p_26166_) {
      Collection<GossipContainer.GossipEntry> collection = p_26164_.selectGossipsForTransfer(p_26165_, p_26166_);
      collection.forEach((p_26200_) -> {
         int i = p_26200_.value - p_26200_.type.decayPerTransfer;
         if (i >= 2) {
            this.getOrCreate(p_26200_.target).entries.mergeInt(p_26200_.type, i, GossipContainer::mergeValuesForTransfer);
         }

      });
   }

   public int getReputation(UUID p_26196_, Predicate<GossipType> p_26197_) {
      GossipContainer.EntityGossips gossipcontainer$entitygossips = this.gossips.get(p_26196_);
      return gossipcontainer$entitygossips != null ? gossipcontainer$entitygossips.weightedValue(p_26197_) : 0;
   }

   public long getCountForType(GossipType p_148163_, DoublePredicate p_148164_) {
      return this.gossips.values().stream().filter((p_148174_) -> {
         return p_148164_.test((double)(p_148174_.entries.getOrDefault(p_148163_, 0) * p_148163_.weight));
      }).count();
   }

   public void add(UUID p_26192_, GossipType p_26193_, int p_26194_) {
      GossipContainer.EntityGossips gossipcontainer$entitygossips = this.getOrCreate(p_26192_);
      gossipcontainer$entitygossips.entries.mergeInt(p_26193_, p_26194_, (p_186096_, p_186097_) -> {
         return this.mergeValuesForAddition(p_26193_, p_186096_, p_186097_);
      });
      gossipcontainer$entitygossips.makeSureValueIsntTooLowOrTooHigh(p_26193_);
      if (gossipcontainer$entitygossips.isEmpty()) {
         this.gossips.remove(p_26192_);
      }

   }

   public void remove(UUID p_148176_, GossipType p_148177_, int p_148178_) {
      this.add(p_148176_, p_148177_, -p_148178_);
   }

   public void remove(UUID p_148169_, GossipType p_148170_) {
      GossipContainer.EntityGossips gossipcontainer$entitygossips = this.gossips.get(p_148169_);
      if (gossipcontainer$entitygossips != null) {
         gossipcontainer$entitygossips.remove(p_148170_);
         if (gossipcontainer$entitygossips.isEmpty()) {
            this.gossips.remove(p_148169_);
         }
      }

   }

   public void remove(GossipType p_148161_) {
      Iterator<GossipContainer.EntityGossips> iterator = this.gossips.values().iterator();

      while(iterator.hasNext()) {
         GossipContainer.EntityGossips gossipcontainer$entitygossips = iterator.next();
         gossipcontainer$entitygossips.remove(p_148161_);
         if (gossipcontainer$entitygossips.isEmpty()) {
            iterator.remove();
         }
      }

   }

   public <T> Dynamic<T> store(DynamicOps<T> p_26180_) {
      return new Dynamic<>(p_26180_, p_26180_.createList(this.unpack().map((p_26183_) -> {
         return p_26183_.store(p_26180_);
      }).map(Dynamic::getValue)));
   }

   public void update(Dynamic<?> p_26178_) {
      p_26178_.asStream().map(GossipContainer.GossipEntry::load).flatMap((p_26176_) -> {
         return p_26176_.result().stream();
      }).forEach((p_26162_) -> {
         this.getOrCreate(p_26162_.target).entries.put(p_26162_.type, p_26162_.value);
      });
   }

   private static int mergeValuesForTransfer(int p_26159_, int p_26160_) {
      return Math.max(p_26159_, p_26160_);
   }

   private int mergeValuesForAddition(GossipType p_26168_, int p_26169_, int p_26170_) {
      int i = p_26169_ + p_26170_;
      return i > p_26168_.max ? Math.max(p_26168_.max, p_26169_) : i;
   }

   static class EntityGossips {
      final Object2IntMap<GossipType> entries = new Object2IntOpenHashMap<>();

      public int weightedValue(Predicate<GossipType> p_26221_) {
         return this.entries.object2IntEntrySet().stream().filter((p_26224_) -> {
            return p_26221_.test(p_26224_.getKey());
         }).mapToInt((p_26214_) -> {
            return p_26214_.getIntValue() * (p_26214_.getKey()).weight;
         }).sum();
      }

      public Stream<GossipContainer.GossipEntry> unpack(UUID p_26216_) {
         return this.entries.object2IntEntrySet().stream().map((p_26219_) -> {
            return new GossipContainer.GossipEntry(p_26216_, p_26219_.getKey(), p_26219_.getIntValue());
         });
      }

      public void decay() {
         ObjectIterator<Entry<GossipType>> objectiterator = this.entries.object2IntEntrySet().iterator();

         while(objectiterator.hasNext()) {
            Entry<GossipType> entry = objectiterator.next();
            int i = entry.getIntValue() - (entry.getKey()).decayPerDay;
            if (i < 2) {
               objectiterator.remove();
            } else {
               entry.setValue(i);
            }
         }

      }

      public boolean isEmpty() {
         return this.entries.isEmpty();
      }

      public void makeSureValueIsntTooLowOrTooHigh(GossipType p_26212_) {
         int i = this.entries.getInt(p_26212_);
         if (i > p_26212_.max) {
            this.entries.put(p_26212_, p_26212_.max);
         }

         if (i < 2) {
            this.remove(p_26212_);
         }

      }

      public void remove(GossipType p_26227_) {
         this.entries.removeInt(p_26227_);
      }
   }

   static class GossipEntry {
      public static final String TAG_TARGET = "Target";
      public static final String TAG_TYPE = "Type";
      public static final String TAG_VALUE = "Value";
      public final UUID target;
      public final GossipType type;
      public final int value;

      public GossipEntry(UUID p_26232_, GossipType p_26233_, int p_26234_) {
         this.target = p_26232_;
         this.type = p_26233_;
         this.value = p_26234_;
      }

      public int weightedValue() {
         return this.value * this.type.weight;
      }

      public String toString() {
         return "GossipEntry{target=" + this.target + ", type=" + this.type + ", value=" + this.value + "}";
      }

      public <T> Dynamic<T> store(DynamicOps<T> p_26239_) {
         return new Dynamic<>(p_26239_, p_26239_.createMap(ImmutableMap.of(p_26239_.createString("Target"), SerializableUUID.CODEC.encodeStart(p_26239_, this.target).result().orElseThrow(RuntimeException::new), p_26239_.createString("Type"), p_26239_.createString(this.type.id), p_26239_.createString("Value"), p_26239_.createInt(this.value))));
      }

      public static DataResult<GossipContainer.GossipEntry> load(Dynamic<?> p_26237_) {
         return DataResult.unbox(DataResult.instance().group(p_26237_.get("Target").read(SerializableUUID.CODEC), p_26237_.get("Type").asString().map(GossipType::byId), p_26237_.get("Value").asNumber().map(Number::intValue)).apply(DataResult.instance(), GossipContainer.GossipEntry::new));
      }
   }
}