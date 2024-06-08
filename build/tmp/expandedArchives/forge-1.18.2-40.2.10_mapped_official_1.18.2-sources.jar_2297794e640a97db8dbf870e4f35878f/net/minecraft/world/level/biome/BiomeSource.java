package net.minecraft.world.level.biome;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.util.Graph;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;

public abstract class BiomeSource implements BiomeResolver {
   public static final Codec<BiomeSource> CODEC;
   private final Set<Holder<Biome>> possibleBiomes;
   private final Supplier<List<BiomeSource.StepFeatureData>> featuresPerStep;

   protected BiomeSource(Stream<Holder<Biome>> p_47896_) {
      this(p_47896_.distinct().toList());
   }

   protected BiomeSource(List<Holder<Biome>> p_47894_) {
      this.possibleBiomes = new ObjectLinkedOpenHashSet<>(p_47894_);
      this.featuresPerStep = Suppliers.memoize(() -> {
         return this.buildFeaturesPerStep(p_47894_, true);
      });
   }

   private List<BiomeSource.StepFeatureData> buildFeaturesPerStep(List<Holder<Biome>> p_186728_, boolean p_186729_) {
      Object2IntMap<PlacedFeature> object2intmap = new Object2IntOpenHashMap<>();
      MutableInt mutableint = new MutableInt(0);

      record FeatureData(int featureIndex, int step, PlacedFeature feature) {
      }

      Comparator<FeatureData> comparator = Comparator.comparingInt(FeatureData::step).thenComparingInt(FeatureData::featureIndex);
      Map<FeatureData, Set<FeatureData>> map = new TreeMap<>(comparator);
      int i = 0;

      for(Holder<Biome> holder : p_186728_) {
         Biome biome = holder.value();
         List<FeatureData> list = Lists.newArrayList();
         List<HolderSet<PlacedFeature>> list1 = biome.getGenerationSettings().features();
         i = Math.max(i, list1.size());

         for(int j = 0; j < list1.size(); ++j) {
            for(Holder<PlacedFeature> holder1 : list1.get(j)) {
               PlacedFeature placedfeature = holder1.value();
               list.add(new FeatureData(object2intmap.computeIfAbsent(placedfeature, (p_204235_) -> {
                  return mutableint.getAndIncrement();
               }), j, placedfeature));
            }
         }

         for(int i1 = 0; i1 < list.size(); ++i1) {
            Set<FeatureData> set2 = map.computeIfAbsent(list.get(i1), (p_204230_) -> {
               return new TreeSet<>(comparator);
            });
            if (i1 < list.size() - 1) {
               set2.add(list.get(i1 + 1));
            }
         }
      }

      Set<FeatureData> set = new TreeSet<>(comparator);
      Set<FeatureData> set1 = new TreeSet<>(comparator);
      List<FeatureData> list2 = Lists.newArrayList();

      for(FeatureData biomesource$1featuredata : map.keySet()) {
         if (!set1.isEmpty()) {
            throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
         }

         if (!set.contains(biomesource$1featuredata) && Graph.depthFirstSearch(map, set, set1, list2::add, biomesource$1featuredata)) {
            if (!p_186729_) {
               throw new IllegalStateException("Feature order cycle found");
            }

            List<Holder<Biome>> list3 = new ArrayList<>(p_186728_);

            int j1;
            do {
               j1 = list3.size();
               ListIterator<Holder<Biome>> listiterator = list3.listIterator();

               while(listiterator.hasNext()) {
                  Holder<Biome> holder2 = listiterator.next();
                  listiterator.remove();

                  try {
                     this.buildFeaturesPerStep(list3, false);
                  } catch (IllegalStateException illegalstateexception) {
                     continue;
                  }

                  listiterator.add(holder2);
               }
            } while(j1 != list3.size());

            throw new IllegalStateException("Feature order cycle found, involved biomes: " + list3);
         }
      }

      Collections.reverse(list2);
      Builder<BiomeSource.StepFeatureData> builder = ImmutableList.builder();

      for(int l = 0; l < i; ++l) {
         final int l_f = l;
         List<PlacedFeature> list4 = list2.stream().filter((p_204227_) -> {
            return p_204227_.step() == l_f;
         }).map(FeatureData::feature).collect(Collectors.toList());
         int k1 = list4.size();
         Object2IntMap<PlacedFeature> object2intmap1 = new Object2IntOpenCustomHashMap<>(k1, Util.identityStrategy());

         for(int k = 0; k < k1; ++k) {
            object2intmap1.put(list4.get(k), k);
         }

         builder.add(new BiomeSource.StepFeatureData(list4, object2intmap1));
      }

      return builder.build();
   }

   protected abstract Codec<? extends BiomeSource> codec();

   public abstract BiomeSource withSeed(long p_47916_);

   public Set<Holder<Biome>> possibleBiomes() {
      return this.possibleBiomes;
   }

   public Set<Holder<Biome>> getBiomesWithin(int p_186705_, int p_186706_, int p_186707_, int p_186708_, Climate.Sampler p_186709_) {
      int i = QuartPos.fromBlock(p_186705_ - p_186708_);
      int j = QuartPos.fromBlock(p_186706_ - p_186708_);
      int k = QuartPos.fromBlock(p_186707_ - p_186708_);
      int l = QuartPos.fromBlock(p_186705_ + p_186708_);
      int i1 = QuartPos.fromBlock(p_186706_ + p_186708_);
      int j1 = QuartPos.fromBlock(p_186707_ + p_186708_);
      int k1 = l - i + 1;
      int l1 = i1 - j + 1;
      int i2 = j1 - k + 1;
      Set<Holder<Biome>> set = Sets.newHashSet();

      for(int j2 = 0; j2 < i2; ++j2) {
         for(int k2 = 0; k2 < k1; ++k2) {
            for(int l2 = 0; l2 < l1; ++l2) {
               int i3 = i + k2;
               int j3 = j + l2;
               int k3 = k + j2;
               set.add(this.getNoiseBiome(i3, j3, k3, p_186709_));
            }
         }
      }

      return set;
   }

   @Nullable
   public Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(int p_207830_, int p_207831_, int p_207832_, int p_207833_, Predicate<Holder<Biome>> p_207834_, Random p_207835_, Climate.Sampler p_207836_) {
      return this.findBiomeHorizontal(p_207830_, p_207831_, p_207832_, p_207833_, 1, p_207834_, p_207835_, false, p_207836_);
   }

   @Nullable
   public Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(int p_207820_, int p_207821_, int p_207822_, int p_207823_, int p_207824_, Predicate<Holder<Biome>> p_207825_, Random p_207826_, boolean p_207827_, Climate.Sampler p_207828_) {
      int i = QuartPos.fromBlock(p_207820_);
      int j = QuartPos.fromBlock(p_207822_);
      int k = QuartPos.fromBlock(p_207823_);
      int l = QuartPos.fromBlock(p_207821_);
      Pair<BlockPos, Holder<Biome>> pair = null;
      int i1 = 0;
      int j1 = p_207827_ ? 0 : k;

      for(int k1 = j1; k1 <= k; k1 += p_207824_) {
         for(int l1 = SharedConstants.debugGenerateSquareTerrainWithoutNoise ? 0 : -k1; l1 <= k1; l1 += p_207824_) {
            boolean flag = Math.abs(l1) == k1;

            for(int i2 = -k1; i2 <= k1; i2 += p_207824_) {
               if (p_207827_) {
                  boolean flag1 = Math.abs(i2) == k1;
                  if (!flag1 && !flag) {
                     continue;
                  }
               }

               int k2 = i + i2;
               int j2 = j + l1;
               Holder<Biome> holder = this.getNoiseBiome(k2, l, j2, p_207828_);
               if (p_207825_.test(holder)) {
                  if (pair == null || p_207826_.nextInt(i1 + 1) == 0) {
                     BlockPos blockpos = new BlockPos(QuartPos.toBlock(k2), p_207821_, QuartPos.toBlock(j2));
                     if (p_207827_) {
                        return Pair.of(blockpos, holder);
                     }

                     pair = Pair.of(blockpos, holder);
                  }

                  ++i1;
               }
            }
         }
      }

      return pair;
   }

   public abstract Holder<Biome> getNoiseBiome(int p_204238_, int p_204239_, int p_204240_, Climate.Sampler p_204241_);

   public void addDebugInfo(List<String> p_207837_, BlockPos p_207838_, Climate.Sampler p_207839_) {
   }

   public List<BiomeSource.StepFeatureData> featuresPerStep() {
      return this.featuresPerStep.get();
   }

   static {
      Registry.register(Registry.BIOME_SOURCE, "fixed", FixedBiomeSource.CODEC);
      Registry.register(Registry.BIOME_SOURCE, "multi_noise", MultiNoiseBiomeSource.CODEC);
      Registry.register(Registry.BIOME_SOURCE, "checkerboard", CheckerboardColumnBiomeSource.CODEC);
      Registry.register(Registry.BIOME_SOURCE, "the_end", TheEndBiomeSource.CODEC);
      CODEC = Registry.BIOME_SOURCE.byNameCodec().dispatchStable(BiomeSource::codec, Function.identity());
   }

   public static record StepFeatureData(List<PlacedFeature> features, ToIntFunction<PlacedFeature> indexMapping) {
   }
}