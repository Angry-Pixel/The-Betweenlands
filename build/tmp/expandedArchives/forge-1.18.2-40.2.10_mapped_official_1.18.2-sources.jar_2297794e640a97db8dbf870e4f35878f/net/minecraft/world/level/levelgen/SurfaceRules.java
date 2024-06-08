package net.minecraft.world.level.levelgen;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class SurfaceRules {
   public static final SurfaceRules.ConditionSource ON_FLOOR = stoneDepthCheck(0, false, CaveSurface.FLOOR);
   public static final SurfaceRules.ConditionSource UNDER_FLOOR = stoneDepthCheck(0, true, CaveSurface.FLOOR);
   public static final SurfaceRules.ConditionSource DEEP_UNDER_FLOOR = stoneDepthCheck(0, true, 6, CaveSurface.FLOOR);
   public static final SurfaceRules.ConditionSource VERY_DEEP_UNDER_FLOOR = stoneDepthCheck(0, true, 30, CaveSurface.FLOOR);
   public static final SurfaceRules.ConditionSource ON_CEILING = stoneDepthCheck(0, false, CaveSurface.CEILING);
   public static final SurfaceRules.ConditionSource UNDER_CEILING = stoneDepthCheck(0, true, CaveSurface.CEILING);

   public static SurfaceRules.ConditionSource stoneDepthCheck(int p_202177_, boolean p_202178_, CaveSurface p_202179_) {
      return new SurfaceRules.StoneDepthCheck(p_202177_, p_202178_, 0, p_202179_);
   }

   public static SurfaceRules.ConditionSource stoneDepthCheck(int p_202172_, boolean p_202173_, int p_202174_, CaveSurface p_202175_) {
      return new SurfaceRules.StoneDepthCheck(p_202172_, p_202173_, p_202174_, p_202175_);
   }

   public static SurfaceRules.ConditionSource not(SurfaceRules.ConditionSource p_189393_) {
      return new SurfaceRules.NotConditionSource(p_189393_);
   }

   public static SurfaceRules.ConditionSource yBlockCheck(VerticalAnchor p_189401_, int p_189402_) {
      return new SurfaceRules.YConditionSource(p_189401_, p_189402_, false);
   }

   public static SurfaceRules.ConditionSource yStartCheck(VerticalAnchor p_189423_, int p_189424_) {
      return new SurfaceRules.YConditionSource(p_189423_, p_189424_, true);
   }

   public static SurfaceRules.ConditionSource waterBlockCheck(int p_189383_, int p_189384_) {
      return new SurfaceRules.WaterConditionSource(p_189383_, p_189384_, false);
   }

   public static SurfaceRules.ConditionSource waterStartCheck(int p_189420_, int p_189421_) {
      return new SurfaceRules.WaterConditionSource(p_189420_, p_189421_, true);
   }

   @SafeVarargs
   public static SurfaceRules.ConditionSource isBiome(ResourceKey<Biome>... p_189417_) {
      return isBiome(List.of(p_189417_));
   }

   private static SurfaceRules.BiomeConditionSource isBiome(List<ResourceKey<Biome>> p_189408_) {
      return new SurfaceRules.BiomeConditionSource(p_189408_);
   }

   public static SurfaceRules.ConditionSource noiseCondition(ResourceKey<NormalNoise.NoiseParameters> p_189410_, double p_189411_) {
      return noiseCondition(p_189410_, p_189411_, Double.MAX_VALUE);
   }

   public static SurfaceRules.ConditionSource noiseCondition(ResourceKey<NormalNoise.NoiseParameters> p_189413_, double p_189414_, double p_189415_) {
      return new SurfaceRules.NoiseThresholdConditionSource(p_189413_, p_189414_, p_189415_);
   }

   public static SurfaceRules.ConditionSource verticalGradient(String p_189404_, VerticalAnchor p_189405_, VerticalAnchor p_189406_) {
      return new SurfaceRules.VerticalGradientConditionSource(new ResourceLocation(p_189404_), p_189405_, p_189406_);
   }

   public static SurfaceRules.ConditionSource steep() {
      return SurfaceRules.Steep.INSTANCE;
   }

   public static SurfaceRules.ConditionSource hole() {
      return SurfaceRules.Hole.INSTANCE;
   }

   public static SurfaceRules.ConditionSource abovePreliminarySurface() {
      return SurfaceRules.AbovePreliminarySurface.INSTANCE;
   }

   public static SurfaceRules.ConditionSource temperature() {
      return SurfaceRules.Temperature.INSTANCE;
   }

   public static SurfaceRules.RuleSource ifTrue(SurfaceRules.ConditionSource p_189395_, SurfaceRules.RuleSource p_189396_) {
      return new SurfaceRules.TestRuleSource(p_189395_, p_189396_);
   }

   public static SurfaceRules.RuleSource sequence(SurfaceRules.RuleSource... p_198273_) {
      if (p_198273_.length == 0) {
         throw new IllegalArgumentException("Need at least 1 rule for a sequence");
      } else {
         return new SurfaceRules.SequenceRuleSource(Arrays.asList(p_198273_));
      }
   }

   public static SurfaceRules.RuleSource state(BlockState p_189391_) {
      return new SurfaceRules.BlockRuleSource(p_189391_);
   }

   public static SurfaceRules.RuleSource bandlands() {
      return SurfaceRules.Bandlands.INSTANCE;
   }

   static enum AbovePreliminarySurface implements SurfaceRules.ConditionSource {
      INSTANCE;

      static final Codec<SurfaceRules.AbovePreliminarySurface> CODEC = Codec.unit(INSTANCE);

      public Codec<? extends SurfaceRules.ConditionSource> codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(SurfaceRules.Context p_189437_) {
         return p_189437_.abovePreliminarySurface;
      }
   }

   static enum Bandlands implements SurfaceRules.RuleSource {
      INSTANCE;

      static final Codec<SurfaceRules.Bandlands> CODEC = Codec.unit(INSTANCE);

      public Codec<? extends SurfaceRules.RuleSource> codec() {
         return CODEC;
      }

      public SurfaceRules.SurfaceRule apply(SurfaceRules.Context p_189482_) {
         return p_189482_.system::getBand;
      }
   }

   static final class BiomeConditionSource implements SurfaceRules.ConditionSource {
      static final Codec<SurfaceRules.BiomeConditionSource> CODEC = ResourceKey.codec(Registry.BIOME_REGISTRY).listOf().fieldOf("biome_is").xmap(SurfaceRules::isBiome, (p_204620_) -> {
         return p_204620_.biomes;
      }).codec();
      private final List<ResourceKey<Biome>> biomes;
      final Predicate<ResourceKey<Biome>> biomeNameTest;

      BiomeConditionSource(List<ResourceKey<Biome>> p_189493_) {
         this.biomes = p_189493_;
         this.biomeNameTest = Set.copyOf(p_189493_)::contains;
      }

      public Codec<? extends SurfaceRules.ConditionSource> codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context p_189496_) {
         class BiomeCondition extends SurfaceRules.LazyYCondition {
            BiomeCondition() {
               super(p_189496_);
            }

            protected boolean compute() {
               return this.context.biome.get().is(BiomeConditionSource.this.biomeNameTest);
            }
         }

         return new BiomeCondition();
      }

      public boolean equals(Object p_209694_) {
         if (this == p_209694_) {
            return true;
         } else if (p_209694_ instanceof SurfaceRules.BiomeConditionSource) {
            SurfaceRules.BiomeConditionSource surfacerules$biomeconditionsource = (SurfaceRules.BiomeConditionSource)p_209694_;
            return this.biomes.equals(surfacerules$biomeconditionsource.biomes);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.biomes.hashCode();
      }

      public String toString() {
         return "BiomeConditionSource[biomes=" + this.biomes + "]";
      }
   }

   static record BlockRuleSource(BlockState resultState, SurfaceRules.StateRule rule) implements SurfaceRules.RuleSource {
      static final Codec<SurfaceRules.BlockRuleSource> CODEC = BlockState.CODEC.xmap(SurfaceRules.BlockRuleSource::new, SurfaceRules.BlockRuleSource::resultState).fieldOf("result_state").codec();

      BlockRuleSource(BlockState p_189517_) {
         this(p_189517_, new SurfaceRules.StateRule(p_189517_));
      }

      public Codec<? extends SurfaceRules.RuleSource> codec() {
         return CODEC;
      }

      public SurfaceRules.SurfaceRule apply(SurfaceRules.Context p_189523_) {
         return this.rule;
      }
   }

   public interface Condition {
      boolean test();
   }

   public interface ConditionSource extends Function<SurfaceRules.Context, SurfaceRules.Condition> {
      Codec<SurfaceRules.ConditionSource> CODEC = Registry.CONDITION.byNameCodec().dispatch(SurfaceRules.ConditionSource::codec, Function.identity());

      static Codec<? extends SurfaceRules.ConditionSource> bootstrap(Registry<Codec<? extends SurfaceRules.ConditionSource>> p_204625_) {
         Registry.register(p_204625_, "biome", SurfaceRules.BiomeConditionSource.CODEC);
         Registry.register(p_204625_, "noise_threshold", SurfaceRules.NoiseThresholdConditionSource.CODEC);
         Registry.register(p_204625_, "vertical_gradient", SurfaceRules.VerticalGradientConditionSource.CODEC);
         Registry.register(p_204625_, "y_above", SurfaceRules.YConditionSource.CODEC);
         Registry.register(p_204625_, "water", SurfaceRules.WaterConditionSource.CODEC);
         Registry.register(p_204625_, "temperature", SurfaceRules.Temperature.CODEC);
         Registry.register(p_204625_, "steep", SurfaceRules.Steep.CODEC);
         Registry.register(p_204625_, "not", SurfaceRules.NotConditionSource.CODEC);
         Registry.register(p_204625_, "hole", SurfaceRules.Hole.CODEC);
         Registry.register(p_204625_, "above_preliminary_surface", SurfaceRules.AbovePreliminarySurface.CODEC);
         return Registry.register(p_204625_, "stone_depth", SurfaceRules.StoneDepthCheck.CODEC);
      }

      Codec<? extends SurfaceRules.ConditionSource> codec();
   }

   public static final class Context {
      private static final int HOW_FAR_BELOW_PRELIMINARY_SURFACE_LEVEL_TO_BUILD_SURFACE = 8;
      private static final int SURFACE_CELL_BITS = 4;
      private static final int SURFACE_CELL_SIZE = 16;
      private static final int SURFACE_CELL_MASK = 15;
      public final SurfaceSystem system;
      final SurfaceRules.Condition temperature = new SurfaceRules.Context.TemperatureHelperCondition(this);
      final SurfaceRules.Condition steep = new SurfaceRules.Context.SteepMaterialCondition(this);
      final SurfaceRules.Condition hole = new SurfaceRules.Context.HoleCondition(this);
      final SurfaceRules.Condition abovePreliminarySurface = new SurfaceRules.Context.AbovePreliminarySurfaceCondition();
      final ChunkAccess chunk;
      private final NoiseChunk noiseChunk;
      private final Function<BlockPos, Holder<Biome>> biomeGetter;
      public final WorldGenerationContext context;
      private long lastPreliminarySurfaceCellOrigin = Long.MAX_VALUE;
      private final int[] preliminarySurfaceCache = new int[4];
      long lastUpdateXZ = -9223372036854775807L;
      public int blockX;
      public int blockZ;
      int surfaceDepth;
      private long lastSurfaceDepth2Update = this.lastUpdateXZ - 1L;
      private double surfaceSecondary;
      private long lastMinSurfaceLevelUpdate = this.lastUpdateXZ - 1L;
      private int minSurfaceLevel;
      long lastUpdateY = -9223372036854775807L;
      final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
      Supplier<Holder<Biome>> biome;
      public int blockY;
      int waterHeight;
      int stoneDepthBelow;
      int stoneDepthAbove;

      protected Context(SurfaceSystem p_189562_, ChunkAccess p_189563_, NoiseChunk p_189564_, Function<BlockPos, Holder<Biome>> p_189565_, Registry<Biome> p_189566_, WorldGenerationContext p_189567_) {
         this.system = p_189562_;
         this.chunk = p_189563_;
         this.noiseChunk = p_189564_;
         this.biomeGetter = p_189565_;
         this.context = p_189567_;
      }

      protected void updateXZ(int p_189570_, int p_189571_) {
         ++this.lastUpdateXZ;
         ++this.lastUpdateY;
         this.blockX = p_189570_;
         this.blockZ = p_189571_;
         this.surfaceDepth = this.system.getSurfaceDepth(p_189570_, p_189571_);
      }

      protected void updateY(int p_189577_, int p_189578_, int p_189579_, int p_189580_, int p_189581_, int p_189582_) {
         ++this.lastUpdateY;
         this.biome = Suppliers.memoize(() -> {
            return this.biomeGetter.apply(this.pos.set(p_189580_, p_189581_, p_189582_));
         });
         this.blockY = p_189581_;
         this.waterHeight = p_189579_;
         this.stoneDepthBelow = p_189578_;
         this.stoneDepthAbove = p_189577_;
      }

      protected double getSurfaceSecondary() {
         if (this.lastSurfaceDepth2Update != this.lastUpdateXZ) {
            this.lastSurfaceDepth2Update = this.lastUpdateXZ;
            this.surfaceSecondary = this.system.getSurfaceSecondary(this.blockX, this.blockZ);
         }

         return this.surfaceSecondary;
      }

      private static int blockCoordToSurfaceCell(int p_198281_) {
         return p_198281_ >> 4;
      }

      private static int surfaceCellToBlockCoord(int p_198283_) {
         return p_198283_ << 4;
      }

      protected int getMinSurfaceLevel() {
         if (this.lastMinSurfaceLevelUpdate != this.lastUpdateXZ) {
            this.lastMinSurfaceLevelUpdate = this.lastUpdateXZ;
            int i = blockCoordToSurfaceCell(this.blockX);
            int j = blockCoordToSurfaceCell(this.blockZ);
            long k = ChunkPos.asLong(i, j);
            if (this.lastPreliminarySurfaceCellOrigin != k) {
               this.lastPreliminarySurfaceCellOrigin = k;
               this.preliminarySurfaceCache[0] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(i), surfaceCellToBlockCoord(j));
               this.preliminarySurfaceCache[1] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(i + 1), surfaceCellToBlockCoord(j));
               this.preliminarySurfaceCache[2] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(i), surfaceCellToBlockCoord(j + 1));
               this.preliminarySurfaceCache[3] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord(i + 1), surfaceCellToBlockCoord(j + 1));
            }

            int l = Mth.floor(Mth.lerp2((double)((float)(this.blockX & 15) / 16.0F), (double)((float)(this.blockZ & 15) / 16.0F), (double)this.preliminarySurfaceCache[0], (double)this.preliminarySurfaceCache[1], (double)this.preliminarySurfaceCache[2], (double)this.preliminarySurfaceCache[3]));
            this.minSurfaceLevel = l + this.surfaceDepth - 8;
         }

         return this.minSurfaceLevel;
      }

      final class AbovePreliminarySurfaceCondition implements SurfaceRules.Condition {
         public boolean test() {
            return Context.this.blockY >= Context.this.getMinSurfaceLevel();
         }
      }

      static final class HoleCondition extends SurfaceRules.LazyXZCondition {
         HoleCondition(SurfaceRules.Context p_189591_) {
            super(p_189591_);
         }

         protected boolean compute() {
            return this.context.surfaceDepth <= 0;
         }
      }

      static class SteepMaterialCondition extends SurfaceRules.LazyXZCondition {
         SteepMaterialCondition(SurfaceRules.Context p_189594_) {
            super(p_189594_);
         }

         protected boolean compute() {
            int i = this.context.blockX & 15;
            int j = this.context.blockZ & 15;
            int k = Math.max(j - 1, 0);
            int l = Math.min(j + 1, 15);
            ChunkAccess chunkaccess = this.context.chunk;
            int i1 = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, k);
            int j1 = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, l);
            if (j1 >= i1 + 4) {
               return true;
            } else {
               int k1 = Math.max(i - 1, 0);
               int l1 = Math.min(i + 1, 15);
               int i2 = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, k1, j);
               int j2 = chunkaccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, l1, j);
               return i2 >= j2 + 4;
            }
         }
      }

      static class TemperatureHelperCondition extends SurfaceRules.LazyYCondition {
         TemperatureHelperCondition(SurfaceRules.Context p_189597_) {
            super(p_189597_);
         }

         protected boolean compute() {
            return this.context.biome.get().value().coldEnoughToSnow(this.context.pos.set(this.context.blockX, this.context.blockY, this.context.blockZ));
         }
      }
   }

   static enum Hole implements SurfaceRules.ConditionSource {
      INSTANCE;

      static final Codec<SurfaceRules.Hole> CODEC = Codec.unit(INSTANCE);

      public Codec<? extends SurfaceRules.ConditionSource> codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(SurfaceRules.Context p_189608_) {
         return p_189608_.hole;
      }
   }

   abstract static class LazyCondition implements SurfaceRules.Condition {
      protected final SurfaceRules.Context context;
      private long lastUpdate;
      @Nullable
      Boolean result;

      protected LazyCondition(SurfaceRules.Context p_189619_) {
         this.context = p_189619_;
         this.lastUpdate = this.getContextLastUpdate() - 1L;
      }

      public boolean test() {
         long i = this.getContextLastUpdate();
         if (i == this.lastUpdate) {
            if (this.result == null) {
               throw new IllegalStateException("Update triggered but the result is null");
            } else {
               return this.result;
            }
         } else {
            this.lastUpdate = i;
            this.result = this.compute();
            return this.result;
         }
      }

      protected abstract long getContextLastUpdate();

      protected abstract boolean compute();
   }

   abstract static class LazyXZCondition extends SurfaceRules.LazyCondition {
      protected LazyXZCondition(SurfaceRules.Context p_189622_) {
         super(p_189622_);
      }

      protected long getContextLastUpdate() {
         return this.context.lastUpdateXZ;
      }
   }

   public abstract static class LazyYCondition extends SurfaceRules.LazyCondition {
      protected LazyYCondition(SurfaceRules.Context p_189625_) {
         super(p_189625_);
      }

      protected long getContextLastUpdate() {
         return this.context.lastUpdateY;
      }
   }

   static record NoiseThresholdConditionSource(ResourceKey<NormalNoise.NoiseParameters> noise, double minThreshold, double maxThreshold) implements SurfaceRules.ConditionSource {
      static final Codec<SurfaceRules.NoiseThresholdConditionSource> CODEC = RecordCodecBuilder.create((p_189638_) -> {
         return p_189638_.group(ResourceKey.codec(Registry.NOISE_REGISTRY).fieldOf("noise").forGetter(SurfaceRules.NoiseThresholdConditionSource::noise), Codec.DOUBLE.fieldOf("min_threshold").forGetter(SurfaceRules.NoiseThresholdConditionSource::minThreshold), Codec.DOUBLE.fieldOf("max_threshold").forGetter(SurfaceRules.NoiseThresholdConditionSource::maxThreshold)).apply(p_189638_, SurfaceRules.NoiseThresholdConditionSource::new);
      });

      public Codec<? extends SurfaceRules.ConditionSource> codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context p_189640_) {
         final NormalNoise normalnoise = p_189640_.system.getOrCreateNoise(this.noise);

         class NoiseThresholdCondition extends SurfaceRules.LazyXZCondition {
            NoiseThresholdCondition() {
               super(p_189640_);
            }

            protected boolean compute() {
               double d0 = normalnoise.getValue((double)this.context.blockX, 0.0D, (double)this.context.blockZ);
               return d0 >= NoiseThresholdConditionSource.this.minThreshold && d0 <= NoiseThresholdConditionSource.this.maxThreshold;
            }
         }

         return new NoiseThresholdCondition();
      }
   }

   static record NotCondition(SurfaceRules.Condition target) implements SurfaceRules.Condition {
      public boolean test() {
         return !this.target.test();
      }
   }

   static record NotConditionSource(SurfaceRules.ConditionSource target) implements SurfaceRules.ConditionSource {
      static final Codec<SurfaceRules.NotConditionSource> CODEC = SurfaceRules.ConditionSource.CODEC.xmap(SurfaceRules.NotConditionSource::new, SurfaceRules.NotConditionSource::target).fieldOf("invert").codec();

      public Codec<? extends SurfaceRules.ConditionSource> codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(SurfaceRules.Context p_189674_) {
         return new SurfaceRules.NotCondition(this.target.apply(p_189674_));
      }
   }

   public interface RuleSource extends Function<SurfaceRules.Context, SurfaceRules.SurfaceRule> {
      Codec<SurfaceRules.RuleSource> CODEC = Registry.RULE.byNameCodec().dispatch(SurfaceRules.RuleSource::codec, Function.identity());

      static Codec<? extends SurfaceRules.RuleSource> bootstrap(Registry<Codec<? extends SurfaceRules.RuleSource>> p_204631_) {
         Registry.register(p_204631_, "bandlands", SurfaceRules.Bandlands.CODEC);
         Registry.register(p_204631_, "block", SurfaceRules.BlockRuleSource.CODEC);
         Registry.register(p_204631_, "sequence", SurfaceRules.SequenceRuleSource.CODEC);
         return Registry.register(p_204631_, "condition", SurfaceRules.TestRuleSource.CODEC);
      }

      Codec<? extends SurfaceRules.RuleSource> codec();
   }

   static record SequenceRule(List<SurfaceRules.SurfaceRule> rules) implements SurfaceRules.SurfaceRule {
      @Nullable
      public BlockState tryApply(int p_189694_, int p_189695_, int p_189696_) {
         for(SurfaceRules.SurfaceRule surfacerules$surfacerule : this.rules) {
            BlockState blockstate = surfacerules$surfacerule.tryApply(p_189694_, p_189695_, p_189696_);
            if (blockstate != null) {
               return blockstate;
            }
         }

         return null;
      }
   }

   static record SequenceRuleSource(List<SurfaceRules.RuleSource> sequence) implements SurfaceRules.RuleSource {
      static final Codec<SurfaceRules.SequenceRuleSource> CODEC = SurfaceRules.RuleSource.CODEC.listOf().xmap(SurfaceRules.SequenceRuleSource::new, SurfaceRules.SequenceRuleSource::sequence).fieldOf("sequence").codec();

      public Codec<? extends SurfaceRules.RuleSource> codec() {
         return CODEC;
      }

      public SurfaceRules.SurfaceRule apply(SurfaceRules.Context p_189704_) {
         if (this.sequence.size() == 1) {
            return this.sequence.get(0).apply(p_189704_);
         } else {
            Builder<SurfaceRules.SurfaceRule> builder = ImmutableList.builder();

            for(SurfaceRules.RuleSource surfacerules$rulesource : this.sequence) {
               builder.add(surfacerules$rulesource.apply(p_189704_));
            }

            return new SurfaceRules.SequenceRule(builder.build());
         }
      }
   }

   static record StateRule(BlockState state) implements SurfaceRules.SurfaceRule {
      public BlockState tryApply(int p_189721_, int p_189722_, int p_189723_) {
         return this.state;
      }
   }

   static enum Steep implements SurfaceRules.ConditionSource {
      INSTANCE;

      static final Codec<SurfaceRules.Steep> CODEC = Codec.unit(INSTANCE);

      public Codec<? extends SurfaceRules.ConditionSource> codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(SurfaceRules.Context p_189733_) {
         return p_189733_.steep;
      }
   }

   static record StoneDepthCheck(int offset, boolean addSurfaceDepth, int secondaryDepthRange, CaveSurface surfaceType) implements SurfaceRules.ConditionSource {
      static final Codec<SurfaceRules.StoneDepthCheck> CODEC = RecordCodecBuilder.create((p_189753_) -> {
         return p_189753_.group(Codec.INT.fieldOf("offset").forGetter(SurfaceRules.StoneDepthCheck::offset), Codec.BOOL.fieldOf("add_surface_depth").forGetter(SurfaceRules.StoneDepthCheck::addSurfaceDepth), Codec.INT.fieldOf("secondary_depth_range").forGetter(SurfaceRules.StoneDepthCheck::secondaryDepthRange), CaveSurface.CODEC.fieldOf("surface_type").forGetter(SurfaceRules.StoneDepthCheck::surfaceType)).apply(p_189753_, SurfaceRules.StoneDepthCheck::new);
      });

      public Codec<? extends SurfaceRules.ConditionSource> codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context p_189755_) {
         final boolean flag = this.surfaceType == CaveSurface.CEILING;

         class StoneDepthCondition extends SurfaceRules.LazyYCondition {
            StoneDepthCondition() {
               super(p_189755_);
            }

            protected boolean compute() {
               int i = flag ? this.context.stoneDepthBelow : this.context.stoneDepthAbove;
               int j = StoneDepthCheck.this.addSurfaceDepth ? this.context.surfaceDepth : 0;
               int k = StoneDepthCheck.this.secondaryDepthRange == 0 ? 0 : (int)Mth.map(this.context.getSurfaceSecondary(), -1.0D, 1.0D, 0.0D, (double)StoneDepthCheck.this.secondaryDepthRange);
               return i <= 1 + StoneDepthCheck.this.offset + j + k;
            }
         }

         return new StoneDepthCondition();
      }
   }

   protected interface SurfaceRule {
      @Nullable
      BlockState tryApply(int p_189774_, int p_189775_, int p_189776_);
   }

   static enum Temperature implements SurfaceRules.ConditionSource {
      INSTANCE;

      static final Codec<SurfaceRules.Temperature> CODEC = Codec.unit(INSTANCE);

      public Codec<? extends SurfaceRules.ConditionSource> codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(SurfaceRules.Context p_189786_) {
         return p_189786_.temperature;
      }
   }

   static record TestRule(SurfaceRules.Condition condition, SurfaceRules.SurfaceRule followup) implements SurfaceRules.SurfaceRule {
      @Nullable
      public BlockState tryApply(int p_189805_, int p_189806_, int p_189807_) {
         return !this.condition.test() ? null : this.followup.tryApply(p_189805_, p_189806_, p_189807_);
      }
   }

   static record TestRuleSource(SurfaceRules.ConditionSource ifTrue, SurfaceRules.RuleSource thenRun) implements SurfaceRules.RuleSource {
      static final Codec<SurfaceRules.TestRuleSource> CODEC = RecordCodecBuilder.create((p_189817_) -> {
         return p_189817_.group(SurfaceRules.ConditionSource.CODEC.fieldOf("if_true").forGetter(SurfaceRules.TestRuleSource::ifTrue), SurfaceRules.RuleSource.CODEC.fieldOf("then_run").forGetter(SurfaceRules.TestRuleSource::thenRun)).apply(p_189817_, SurfaceRules.TestRuleSource::new);
      });

      public Codec<? extends SurfaceRules.RuleSource> codec() {
         return CODEC;
      }

      public SurfaceRules.SurfaceRule apply(SurfaceRules.Context p_189819_) {
         return new SurfaceRules.TestRule(this.ifTrue.apply(p_189819_), this.thenRun.apply(p_189819_));
      }
   }

   static record VerticalGradientConditionSource(ResourceLocation randomName, VerticalAnchor trueAtAndBelow, VerticalAnchor falseAtAndAbove) implements SurfaceRules.ConditionSource {
      static final Codec<SurfaceRules.VerticalGradientConditionSource> CODEC = RecordCodecBuilder.create((p_189839_) -> {
         return p_189839_.group(ResourceLocation.CODEC.fieldOf("random_name").forGetter(SurfaceRules.VerticalGradientConditionSource::randomName), VerticalAnchor.CODEC.fieldOf("true_at_and_below").forGetter(SurfaceRules.VerticalGradientConditionSource::trueAtAndBelow), VerticalAnchor.CODEC.fieldOf("false_at_and_above").forGetter(SurfaceRules.VerticalGradientConditionSource::falseAtAndAbove)).apply(p_189839_, SurfaceRules.VerticalGradientConditionSource::new);
      });

      public Codec<? extends SurfaceRules.ConditionSource> codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context p_189841_) {
         final int i = this.trueAtAndBelow().resolveY(p_189841_.context);
         final int j = this.falseAtAndAbove().resolveY(p_189841_.context);
         final PositionalRandomFactory positionalrandomfactory = p_189841_.system.getOrCreateRandomFactory(this.randomName());

         class VerticalGradientCondition extends SurfaceRules.LazyYCondition {
            VerticalGradientCondition() {
               super(p_189841_);
            }

            protected boolean compute() {
               int k = this.context.blockY;
               if (k <= i) {
                  return true;
               } else if (k >= j) {
                  return false;
               } else {
                  double d0 = Mth.map((double)k, (double)i, (double)j, 1.0D, 0.0D);
                  RandomSource randomsource = positionalrandomfactory.at(this.context.blockX, k, this.context.blockZ);
                  return (double)randomsource.nextFloat() < d0;
               }
            }
         }

         return new VerticalGradientCondition();
      }
   }

   static record WaterConditionSource(int offset, int surfaceDepthMultiplier, boolean addStoneDepth) implements SurfaceRules.ConditionSource {
      static final Codec<SurfaceRules.WaterConditionSource> CODEC = RecordCodecBuilder.create((p_189874_) -> {
         return p_189874_.group(Codec.INT.fieldOf("offset").forGetter(SurfaceRules.WaterConditionSource::offset), Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(SurfaceRules.WaterConditionSource::surfaceDepthMultiplier), Codec.BOOL.fieldOf("add_stone_depth").forGetter(SurfaceRules.WaterConditionSource::addStoneDepth)).apply(p_189874_, SurfaceRules.WaterConditionSource::new);
      });

      public Codec<? extends SurfaceRules.ConditionSource> codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context p_189876_) {
         class WaterCondition extends SurfaceRules.LazyYCondition {
            WaterCondition() {
               super(p_189876_);
            }

            protected boolean compute() {
               return this.context.waterHeight == Integer.MIN_VALUE || this.context.blockY + (WaterConditionSource.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= this.context.waterHeight + WaterConditionSource.this.offset + this.context.surfaceDepth * WaterConditionSource.this.surfaceDepthMultiplier;
            }
         }

         return new WaterCondition();
      }
   }

   static record YConditionSource(VerticalAnchor anchor, int surfaceDepthMultiplier, boolean addStoneDepth) implements SurfaceRules.ConditionSource {
      static final Codec<SurfaceRules.YConditionSource> CODEC = RecordCodecBuilder.create((p_189455_) -> {
         return p_189455_.group(VerticalAnchor.CODEC.fieldOf("anchor").forGetter(SurfaceRules.YConditionSource::anchor), Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(SurfaceRules.YConditionSource::surfaceDepthMultiplier), Codec.BOOL.fieldOf("add_stone_depth").forGetter(SurfaceRules.YConditionSource::addStoneDepth)).apply(p_189455_, SurfaceRules.YConditionSource::new);
      });

      public Codec<? extends SurfaceRules.ConditionSource> codec() {
         return CODEC;
      }

      public SurfaceRules.Condition apply(final SurfaceRules.Context p_189457_) {
         class YCondition extends SurfaceRules.LazyYCondition {
            YCondition() {
               super(p_189457_);
            }

            protected boolean compute() {
               return this.context.blockY + (YConditionSource.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= YConditionSource.this.anchor.resolveY(this.context.context) + this.context.surfaceDepth * YConditionSource.this.surfaceDepthMultiplier;
            }
         }

         return new YCondition();
      }
   }
}