package net.minecraft.world.level.biome;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

public class Climate {
   private static final boolean DEBUG_SLOW_BIOME_SEARCH = false;
   private static final float QUANTIZATION_FACTOR = 10000.0F;
   @VisibleForTesting
   protected static final int PARAMETER_COUNT = 7;

   public static Climate.TargetPoint target(float p_186782_, float p_186783_, float p_186784_, float p_186785_, float p_186786_, float p_186787_) {
      return new Climate.TargetPoint(quantizeCoord(p_186782_), quantizeCoord(p_186783_), quantizeCoord(p_186784_), quantizeCoord(p_186785_), quantizeCoord(p_186786_), quantizeCoord(p_186787_));
   }

   public static Climate.ParameterPoint parameters(float p_186789_, float p_186790_, float p_186791_, float p_186792_, float p_186793_, float p_186794_, float p_186795_) {
      return new Climate.ParameterPoint(Climate.Parameter.point(p_186789_), Climate.Parameter.point(p_186790_), Climate.Parameter.point(p_186791_), Climate.Parameter.point(p_186792_), Climate.Parameter.point(p_186793_), Climate.Parameter.point(p_186794_), quantizeCoord(p_186795_));
   }

   public static Climate.ParameterPoint parameters(Climate.Parameter p_186799_, Climate.Parameter p_186800_, Climate.Parameter p_186801_, Climate.Parameter p_186802_, Climate.Parameter p_186803_, Climate.Parameter p_186804_, float p_186805_) {
      return new Climate.ParameterPoint(p_186799_, p_186800_, p_186801_, p_186802_, p_186803_, p_186804_, quantizeCoord(p_186805_));
   }

   public static long quantizeCoord(float p_186780_) {
      return (long)(p_186780_ * 10000.0F);
   }

   public static float unquantizeCoord(long p_186797_) {
      return (float)p_186797_ / 10000.0F;
   }

   public static Climate.Sampler empty() {
      DensityFunction densityfunction = DensityFunctions.zero();
      return new Climate.Sampler(densityfunction, densityfunction, densityfunction, densityfunction, densityfunction, densityfunction, List.of());
   }

   public static BlockPos findSpawnPosition(List<Climate.ParameterPoint> p_207843_, Climate.Sampler p_207844_) {
      return (new Climate.SpawnFinder(p_207843_, p_207844_)).result.location();
   }

   interface DistanceMetric<T> {
      long distance(Climate.RTree.Node<T> p_186810_, long[] p_186811_);
   }

   public static record Parameter(long min, long max) {
      public static final Codec<Climate.Parameter> CODEC = ExtraCodecs.intervalCodec(Codec.floatRange(-2.0F, 2.0F), "min", "max", (p_186833_, p_186834_) -> {
         return p_186833_.compareTo(p_186834_) > 0 ? DataResult.error("Cannon construct interval, min > max (" + p_186833_ + " > " + p_186834_ + ")") : DataResult.success(new Climate.Parameter(Climate.quantizeCoord(p_186833_), Climate.quantizeCoord(p_186834_)));
      }, (p_186841_) -> {
         return Climate.unquantizeCoord(p_186841_.min());
      }, (p_186839_) -> {
         return Climate.unquantizeCoord(p_186839_.max());
      });

      public static Climate.Parameter point(float p_186821_) {
         return span(p_186821_, p_186821_);
      }

      public static Climate.Parameter span(float p_186823_, float p_186824_) {
         if (p_186823_ > p_186824_) {
            throw new IllegalArgumentException("min > max: " + p_186823_ + " " + p_186824_);
         } else {
            return new Climate.Parameter(Climate.quantizeCoord(p_186823_), Climate.quantizeCoord(p_186824_));
         }
      }

      public static Climate.Parameter span(Climate.Parameter p_186830_, Climate.Parameter p_186831_) {
         if (p_186830_.min() > p_186831_.max()) {
            throw new IllegalArgumentException("min > max: " + p_186830_ + " " + p_186831_);
         } else {
            return new Climate.Parameter(p_186830_.min(), p_186831_.max());
         }
      }

      public String toString() {
         return this.min == this.max ? String.format("%d", this.min) : String.format("[%d-%d]", this.min, this.max);
      }

      public long distance(long p_186826_) {
         long i = p_186826_ - this.max;
         long j = this.min - p_186826_;
         return i > 0L ? i : Math.max(j, 0L);
      }

      public long distance(Climate.Parameter p_186828_) {
         long i = p_186828_.min() - this.max;
         long j = this.min - p_186828_.max();
         return i > 0L ? i : Math.max(j, 0L);
      }

      public Climate.Parameter span(@Nullable Climate.Parameter p_186837_) {
         return p_186837_ == null ? this : new Climate.Parameter(Math.min(this.min, p_186837_.min()), Math.max(this.max, p_186837_.max()));
      }
   }

   public static class ParameterList<T> {
      private final List<Pair<Climate.ParameterPoint, T>> values;
      private final Climate.RTree<T> index;

      public ParameterList(List<Pair<Climate.ParameterPoint, T>> p_186849_) {
         this.values = p_186849_;
         this.index = Climate.RTree.create(p_186849_);
      }

      public List<Pair<Climate.ParameterPoint, T>> values() {
         return this.values;
      }

      public T findValue(Climate.TargetPoint p_204253_) {
         return this.findValueIndex(p_204253_);
      }

      @VisibleForTesting
      public T findValueBruteForce(Climate.TargetPoint p_204255_) {
         Iterator<Pair<Climate.ParameterPoint, T>> iterator = this.values().iterator();
         Pair<Climate.ParameterPoint, T> pair = iterator.next();
         long i = pair.getFirst().fitness(p_204255_);
         T t = pair.getSecond();

         while(iterator.hasNext()) {
            Pair<Climate.ParameterPoint, T> pair1 = iterator.next();
            long j = pair1.getFirst().fitness(p_204255_);
            if (j < i) {
               i = j;
               t = pair1.getSecond();
            }
         }

         return t;
      }

      public T findValueIndex(Climate.TargetPoint p_186852_) {
         return this.findValueIndex(p_186852_, Climate.RTree.Node::distance);
      }

      protected T findValueIndex(Climate.TargetPoint p_186854_, Climate.DistanceMetric<T> p_186855_) {
         return this.index.search(p_186854_, p_186855_);
      }
   }

   public static record ParameterPoint(Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter depth, Climate.Parameter weirdness, long offset) {
      public static final Codec<Climate.ParameterPoint> CODEC = RecordCodecBuilder.create((p_186885_) -> {
         return p_186885_.group(Climate.Parameter.CODEC.fieldOf("temperature").forGetter((p_186905_) -> {
            return p_186905_.temperature;
         }), Climate.Parameter.CODEC.fieldOf("humidity").forGetter((p_186902_) -> {
            return p_186902_.humidity;
         }), Climate.Parameter.CODEC.fieldOf("continentalness").forGetter((p_186897_) -> {
            return p_186897_.continentalness;
         }), Climate.Parameter.CODEC.fieldOf("erosion").forGetter((p_186894_) -> {
            return p_186894_.erosion;
         }), Climate.Parameter.CODEC.fieldOf("depth").forGetter((p_186891_) -> {
            return p_186891_.depth;
         }), Climate.Parameter.CODEC.fieldOf("weirdness").forGetter((p_186888_) -> {
            return p_186888_.weirdness;
         }), Codec.floatRange(0.0F, 1.0F).fieldOf("offset").xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter((p_186881_) -> {
            return p_186881_.offset;
         })).apply(p_186885_, Climate.ParameterPoint::new);
      });

      long fitness(Climate.TargetPoint p_186883_) {
         return Mth.square(this.temperature.distance(p_186883_.temperature)) + Mth.square(this.humidity.distance(p_186883_.humidity)) + Mth.square(this.continentalness.distance(p_186883_.continentalness)) + Mth.square(this.erosion.distance(p_186883_.erosion)) + Mth.square(this.depth.distance(p_186883_.depth)) + Mth.square(this.weirdness.distance(p_186883_.weirdness)) + Mth.square(this.offset);
      }

      protected List<Climate.Parameter> parameterSpace() {
         return ImmutableList.of(this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, new Climate.Parameter(this.offset, this.offset));
      }
   }

   protected static final class RTree<T> {
      private static final int CHILDREN_PER_NODE = 10;
      private final Climate.RTree.Node<T> root;
      private final ThreadLocal<Climate.RTree.Leaf<T>> lastResult = new ThreadLocal<>();

      private RTree(Climate.RTree.Node<T> p_186913_) {
         this.root = p_186913_;
      }

      public static <T> Climate.RTree<T> create(List<Pair<Climate.ParameterPoint, T>> p_186936_) {
         if (p_186936_.isEmpty()) {
            throw new IllegalArgumentException("Need at least one value to build the search tree.");
         } else {
            int i = p_186936_.get(0).getFirst().parameterSpace().size();
            if (i != 7) {
               throw new IllegalStateException("Expecting parameter space to be 7, got " + i);
            } else {
               List<Climate.RTree.Leaf<T>> list = p_186936_.stream().map((p_186934_) -> {
                  return new Climate.RTree.Leaf<T>(p_186934_.getFirst(), p_186934_.getSecond());
               }).collect(Collectors.toCollection(ArrayList::new));
               return new Climate.RTree<>(build(i, list));
            }
         }
      }

      private static <T> Climate.RTree.Node<T> build(int p_186921_, List<? extends Climate.RTree.Node<T>> p_186922_) {
         if (p_186922_.isEmpty()) {
            throw new IllegalStateException("Need at least one child to build a node");
         } else if (p_186922_.size() == 1) {
            return p_186922_.get(0);
         } else if (p_186922_.size() <= 10) {
            p_186922_.sort(Comparator.comparingLong((p_186916_) -> {
               long i1 = 0L;

               for(int j1 = 0; j1 < p_186921_; ++j1) {
                  Climate.Parameter climate$parameter = p_186916_.parameterSpace[j1];
                  i1 += Math.abs((climate$parameter.min() + climate$parameter.max()) / 2L);
               }

               return i1;
            }));
            return new Climate.RTree.SubTree<>(p_186922_);
         } else {
            long i = Long.MAX_VALUE;
            int j = -1;
            List<Climate.RTree.SubTree<T>> list = null;

            for(int k = 0; k < p_186921_; ++k) {
               sort(p_186922_, p_186921_, k, false);
               List<Climate.RTree.SubTree<T>> list1 = bucketize(p_186922_);
               long l = 0L;

               for(Climate.RTree.SubTree<T> subtree : list1) {
                  l += cost(subtree.parameterSpace);
               }

               if (i > l) {
                  i = l;
                  j = k;
                  list = list1;
               }
            }

            sort(list, p_186921_, j, true);
            return new Climate.RTree.SubTree<>(list.stream().map((p_186919_) -> {
               return build(p_186921_, Arrays.asList(p_186919_.children));
            }).collect(Collectors.toList()));
         }
      }

      private static <T> void sort(List<? extends Climate.RTree.Node<T>> p_186938_, int p_186939_, int p_186940_, boolean p_186941_) {
         Comparator<Climate.RTree.Node<T>> comparator = comparator(p_186940_, p_186941_);

         for(int i = 1; i < p_186939_; ++i) {
            comparator = comparator.thenComparing(comparator((p_186940_ + i) % p_186939_, p_186941_));
         }

         p_186938_.sort(comparator);
      }

      private static <T> Comparator<Climate.RTree.Node<T>> comparator(int p_186924_, boolean p_186925_) {
         return Comparator.comparingLong((p_186929_) -> {
            Climate.Parameter climate$parameter = p_186929_.parameterSpace[p_186924_];
            long i = (climate$parameter.min() + climate$parameter.max()) / 2L;
            return p_186925_ ? Math.abs(i) : i;
         });
      }

      private static <T> List<Climate.RTree.SubTree<T>> bucketize(List<? extends Climate.RTree.Node<T>> p_186945_) {
         List<Climate.RTree.SubTree<T>> list = Lists.newArrayList();
         List<Climate.RTree.Node<T>> list1 = Lists.newArrayList();
         int i = (int)Math.pow(10.0D, Math.floor(Math.log((double)p_186945_.size() - 0.01D) / Math.log(10.0D)));

         for(Climate.RTree.Node<T> node : p_186945_) {
            list1.add(node);
            if (list1.size() >= i) {
               list.add(new Climate.RTree.SubTree<>(list1));
               list1 = Lists.newArrayList();
            }
         }

         if (!list1.isEmpty()) {
            list.add(new Climate.RTree.SubTree<>(list1));
         }

         return list;
      }

      private static long cost(Climate.Parameter[] p_186943_) {
         long i = 0L;

         for(Climate.Parameter climate$parameter : p_186943_) {
            i += Math.abs(climate$parameter.max() - climate$parameter.min());
         }

         return i;
      }

      static <T> List<Climate.Parameter> buildParameterSpace(List<? extends Climate.RTree.Node<T>> p_186947_) {
         if (p_186947_.isEmpty()) {
            throw new IllegalArgumentException("SubTree needs at least one child");
         } else {
            int i = 7;
            List<Climate.Parameter> list = Lists.newArrayList();

            for(int j = 0; j < 7; ++j) {
               list.add((Climate.Parameter)null);
            }

            for(Climate.RTree.Node<T> node : p_186947_) {
               for(int k = 0; k < 7; ++k) {
                  list.set(k, node.parameterSpace[k].span(list.get(k)));
               }
            }

            return list;
         }
      }

      public T search(Climate.TargetPoint p_186931_, Climate.DistanceMetric<T> p_186932_) {
         long[] along = p_186931_.toParameterArray();
         Climate.RTree.Leaf<T> leaf = this.root.search(along, this.lastResult.get(), p_186932_);
         this.lastResult.set(leaf);
         return leaf.value;
      }

      static final class Leaf<T> extends Climate.RTree.Node<T> {
         final T value;

         Leaf(Climate.ParameterPoint p_186950_, T p_186951_) {
            super(p_186950_.parameterSpace());
            this.value = p_186951_;
         }

         protected Climate.RTree.Leaf<T> search(long[] p_186953_, @Nullable Climate.RTree.Leaf<T> p_186954_, Climate.DistanceMetric<T> p_186955_) {
            return this;
         }
      }

      abstract static class Node<T> {
         protected final Climate.Parameter[] parameterSpace;

         protected Node(List<Climate.Parameter> p_186958_) {
            this.parameterSpace = p_186958_.toArray(new Climate.Parameter[0]);
         }

         protected abstract Climate.RTree.Leaf<T> search(long[] p_186961_, @Nullable Climate.RTree.Leaf<T> p_186962_, Climate.DistanceMetric<T> p_186963_);

         protected long distance(long[] p_186960_) {
            long i = 0L;

            for(int j = 0; j < 7; ++j) {
               i += Mth.square(this.parameterSpace[j].distance(p_186960_[j]));
            }

            return i;
         }

         public String toString() {
            return Arrays.toString((Object[])this.parameterSpace);
         }
      }

      static final class SubTree<T> extends Climate.RTree.Node<T> {
         final Climate.RTree.Node<T>[] children;

         protected SubTree(List<? extends Climate.RTree.Node<T>> p_186967_) {
            this(Climate.RTree.buildParameterSpace(p_186967_), p_186967_);
         }

         protected SubTree(List<Climate.Parameter> p_186969_, List<? extends Climate.RTree.Node<T>> p_186970_) {
            super(p_186969_);
            this.children = p_186970_.toArray(new Climate.RTree.Node[0]);
         }

         protected Climate.RTree.Leaf<T> search(long[] p_186972_, @Nullable Climate.RTree.Leaf<T> p_186973_, Climate.DistanceMetric<T> p_186974_) {
            long i = p_186973_ == null ? Long.MAX_VALUE : p_186974_.distance(p_186973_, p_186972_);
            Climate.RTree.Leaf<T> leaf = p_186973_;

            for(Climate.RTree.Node<T> node : this.children) {
               long j = p_186974_.distance(node, p_186972_);
               if (i > j) {
                  Climate.RTree.Leaf<T> leaf1 = node.search(p_186972_, leaf, p_186974_);
                  long k = node == leaf1 ? j : p_186974_.distance(leaf1, p_186972_);
                  if (i > k) {
                     i = k;
                     leaf = leaf1;
                  }
               }
            }

            return leaf;
         }
      }
   }

   public static record Sampler(DensityFunction temperature, DensityFunction humidity, DensityFunction continentalness, DensityFunction erosion, DensityFunction depth, DensityFunction weirdness, List<Climate.ParameterPoint> spawnTarget) {
      public Climate.TargetPoint sample(int p_186975_, int p_186976_, int p_186977_) {
         int i = QuartPos.toBlock(p_186975_);
         int j = QuartPos.toBlock(p_186976_);
         int k = QuartPos.toBlock(p_186977_);
         DensityFunction.SinglePointContext densityfunction$singlepointcontext = new DensityFunction.SinglePointContext(i, j, k);
         return Climate.target((float)this.temperature.compute(densityfunction$singlepointcontext), (float)this.humidity.compute(densityfunction$singlepointcontext), (float)this.continentalness.compute(densityfunction$singlepointcontext), (float)this.erosion.compute(densityfunction$singlepointcontext), (float)this.depth.compute(densityfunction$singlepointcontext), (float)this.weirdness.compute(densityfunction$singlepointcontext));
      }

      public BlockPos findSpawnPosition() {
         return this.spawnTarget.isEmpty() ? BlockPos.ZERO : Climate.findSpawnPosition(this.spawnTarget, this);
      }
   }

   static class SpawnFinder {
      Climate.SpawnFinder.Result result;

      SpawnFinder(List<Climate.ParameterPoint> p_207872_, Climate.Sampler p_207873_) {
         this.result = getSpawnPositionAndFitness(p_207872_, p_207873_, 0, 0);
         this.radialSearch(p_207872_, p_207873_, 2048.0F, 512.0F);
         this.radialSearch(p_207872_, p_207873_, 512.0F, 32.0F);
      }

      private void radialSearch(List<Climate.ParameterPoint> p_207875_, Climate.Sampler p_207876_, float p_207877_, float p_207878_) {
         float f = 0.0F;
         float f1 = p_207878_;
         BlockPos blockpos = this.result.location();

         while(f1 <= p_207877_) {
            int i = blockpos.getX() + (int)(Math.sin((double)f) * (double)f1);
            int j = blockpos.getZ() + (int)(Math.cos((double)f) * (double)f1);
            Climate.SpawnFinder.Result climate$spawnfinder$result = getSpawnPositionAndFitness(p_207875_, p_207876_, i, j);
            if (climate$spawnfinder$result.fitness() < this.result.fitness()) {
               this.result = climate$spawnfinder$result;
            }

            f += p_207878_ / f1;
            if ((double)f > (Math.PI * 2D)) {
               f = 0.0F;
               f1 += p_207878_;
            }
         }

      }

      private static Climate.SpawnFinder.Result getSpawnPositionAndFitness(List<Climate.ParameterPoint> p_207880_, Climate.Sampler p_207881_, int p_207882_, int p_207883_) {
         double d0 = Mth.square(2500.0D);
         int i = 2;
         long j = (long)((double)Mth.square(10000.0F) * Math.pow((double)(Mth.square((long)p_207882_) + Mth.square((long)p_207883_)) / d0, 2.0D));
         Climate.TargetPoint climate$targetpoint = p_207881_.sample(QuartPos.fromBlock(p_207882_), 0, QuartPos.fromBlock(p_207883_));
         Climate.TargetPoint climate$targetpoint1 = new Climate.TargetPoint(climate$targetpoint.temperature(), climate$targetpoint.humidity(), climate$targetpoint.continentalness(), climate$targetpoint.erosion(), 0L, climate$targetpoint.weirdness());
         long k = Long.MAX_VALUE;

         for(Climate.ParameterPoint climate$parameterpoint : p_207880_) {
            k = Math.min(k, climate$parameterpoint.fitness(climate$targetpoint1));
         }

         return new Climate.SpawnFinder.Result(new BlockPos(p_207882_, 0, p_207883_), j + k);
      }

      static record Result(BlockPos location, long fitness) {
      }
   }

   public static record TargetPoint(long temperature, long humidity, long continentalness, long erosion, long depth, long weirdness) {
      @VisibleForTesting
      protected long[] toParameterArray() {
         return new long[]{this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, 0L};
      }
   }
}